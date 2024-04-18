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

interface Args {

  String getIgniteHome();

  void visit(Consumer<InitOnly> onInit, Consumer<RunServer> onRun);

  static Args parse(String[] args) {
    State state = State.Param;
    boolean initOnly = false;
    String ignitePath = null;
    String igniteHomePath = null;
    String swimPath = null;
    long waitTime = 10;
    for (String arg : args) {
      switch (state) {
        case Param:
          switch (arg) {
            case "--init-only":
              initOnly = true;
              break;
            case "--ignite":
              if (ignitePath != null) {
                throw new RuntimeException("Ignite configuration provided twice.");
              }
              state = State.IgniteConfigPath;
              break;
            case "--home":
              if (igniteHomePath != null) {
                throw new RuntimeException("Ignite home path provided twice.");
              }
              state = State.IgniteHomePath;
              break;
            case "--swim":
              if (swimPath != null) {
                throw new RuntimeException("Swim configuration provided twice.");
              }
              state = State.SwimPath;
              break;
            case "--wait":
              state = State.Wait;
              break;
            default:
              throw new RuntimeException("Unrecognized argument: " + arg);
          }
          break;
        case IgniteConfigPath:
          ignitePath = arg;
          state = State.Param;
          break;
        case IgniteHomePath:
          igniteHomePath = arg;
          state = State.Param;
          break;
        case Wait:
          try {
            final var t = Long.parseLong(arg);
            if (t < 0) {
              throw new RuntimeException("Wait time must be non-negative.");
            }
            waitTime = t;
          } catch (NumberFormatException ex) {
            throw new RuntimeException(arg + " is not a valid wait time.");
          }
          state = State.Param;
          break;
        default:
          swimPath = arg;
          state = State.Param;
          break;
      }
    }
    if (state != State.Param) {
      throw new RuntimeException("A path was expected.");
    }
    if (initOnly && ignitePath != null && igniteHomePath != null) {
      return new InitOnly(ignitePath, igniteHomePath, waitTime);
    } else if (!initOnly && igniteHomePath != null && swimPath != null) {
      return new RunServer(igniteHomePath, swimPath);
    } else {
      throw new RuntimeException("Invalid arguments.");
    }
  }

}

enum State {

  Param,
  IgniteConfigPath,
  IgniteHomePath,
  SwimPath,
  Wait,

}
