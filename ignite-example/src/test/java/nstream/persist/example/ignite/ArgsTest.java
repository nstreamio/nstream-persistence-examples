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

import org.testng.Assert;
import org.testng.annotations.Test;

public class ArgsTest {

  @Test
  public void initOnly() {
    final var args = "--init-only --home /path/to/ignite --ignite /path/to/config --wait 4";
    final var params = args.split("\\s+");
    final var parsed = Args.parse(params);

    parsed.visit((initOnly) -> {
      Assert.assertEquals(initOnly.getIgniteHome(), "/path/to/ignite");
      Assert.assertEquals(initOnly.getIgniteConfigPath(), "/path/to/config");
      Assert.assertEquals(initOnly.getWaitTime(), 4);
    }, (runServer) -> {
      throw new RuntimeException("Unexpected type.");
    });
  }

  @Test
  public void runServer() {
    final var args = "--home /path/to/ignite --swim /path/to/swim";
    final var params = args.split("\\s+");
    final var parsed = Args.parse(params);

    parsed.visit((initOnly) -> {
      throw new RuntimeException("Unexpected type.");
    }, (runServer) -> {
      Assert.assertEquals(runServer.getIgniteHome(), "/path/to/ignite");
      Assert.assertEquals(runServer.getSwimConfigPath(), "/path/to/swim");
    });
  }

}
