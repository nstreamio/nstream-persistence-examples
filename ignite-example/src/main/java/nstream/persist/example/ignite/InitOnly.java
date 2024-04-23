// Copyright 2015-2023 Nstream, inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package nstream.persist.example.ignite;

import java.util.function.Consumer;

final class InitOnly implements Args {

  private final String igniteConfigPath;
  private final String workPath;

  private final long waitTime;

  InitOnly(String igniteConfigPath, String workPath, long waitTime) {
    this.igniteConfigPath = igniteConfigPath;
    this.workPath = workPath;
    this.waitTime = waitTime;
  }

  @Override
  public String getIgniteHome() {
    return this.workPath;
  }

  public String getIgniteConfigPath() {
    return this.igniteConfigPath;
  }

  public long getWaitTime() {
    return this.waitTime;
  }

  @Override
  public void visit(Consumer<InitOnly> onInit, Consumer<RunServer> onRun) {
    onInit.accept(this);
  }

}
