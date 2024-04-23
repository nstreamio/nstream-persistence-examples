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

import java.util.logging.Logger;
import nstream.persist.api.PersistenceException;
import nstream.persist.store.ignite.init.InitCluster;
import swim.api.plane.AbstractPlane;
import swim.kernel.Kernel;
import swim.server.ServerLoader;
import swim.structure.Text;

public class ExamplePlane extends AbstractPlane {

  private static final Logger LOG = Logger.getLogger(ExamplePlane.class.getName());

  public ExamplePlane() {
  }

  public static void main(String[] args) {
    final var appParams = Args.parse(args);
    System.setProperty("IGNITE_HOME", appParams.getIgniteHome());
    Args.parse(args).visit((initOnly) -> {
      try {
        InitCluster.initializeCluster(initOnly.getIgniteConfigPath(), initOnly.getWaitTime());
      } catch (PersistenceException ex) {
        ex.printStackTrace(System.err);
      }
    }, (runServer) -> {
      try {
        System.setProperty("swim.config.file", runServer.getSwimConfigPath());

        final Kernel kernel = ServerLoader.loadServer();
        final var space = kernel.getSpace("example");

        LOG.info("Running Example application ...");
        kernel.start();

        for (int i = 1; i <= 10; ++i) {
          space.command(String.format("/example/%d", i), "wake", Text.from("start"));
        }

        kernel.run();
      } catch (Exception ex) {
        ex.printStackTrace(System.err);
      }
    });

  }

}
