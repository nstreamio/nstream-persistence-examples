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

package nstream.persist.example.ignite.standalone;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

final class ExampleHome {

  final File home;
  final File serverRecon;

  String getHome() {
    return this.home.getAbsolutePath();
  }

  String getNStreamConfig() {
    return this.serverRecon.getAbsolutePath();
  }

  ExampleHome() throws IOException {
    final var h = Files.createTempDirectory("igniteHome").toFile();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if (!(clearDir(h) && h.delete())) {
        System.err.println("Failed to delete temporary files.");
      }
    }));
    this.home = h;
    this.serverRecon = setup(h);
  }

  static boolean clearDir(File f) {
    final var children = f.listFiles();
    if (children != null) {
      for (var child : children) {
        clearDir(child);
        if (!child.delete()) {
          return false;
        }
      }
    }
    return true;
  }

  private static File setup(File home) throws IOException {

    final File work = new File(home, "work");
    if (!work.mkdir()) {
      throw new IOException("Failed to create work directory.");
    }
    final File config = new File(home, "config");
    if (!config.mkdir()) {
      throw new IOException("Failed to create config directory.");
    }
    try (var in = ExampleHome.class.getResourceAsStream("java.util.logging.properties")) {
      if (in != null) {
        Files.copy(in, new File(config, "java.util.logging.properties").toPath());
      } else {
        throw new IOException("Cannot find logging properties file.");
      }
    }
    final var igniteConfig = new File(home, "ignite.xml");
    try (var in = ExampleHome.class.getResourceAsStream("ignite.xml")) {

      if (in != null) {
        final var bytes = in.readAllBytes();
        var body = new String(bytes, StandardCharsets.UTF_8);
        body = body.replace("$WORK_DIR", work.getAbsolutePath());
        Files.write(igniteConfig.toPath(), body.getBytes(StandardCharsets.UTF_8));
      } else {
        throw new IOException("Cannot find logging properties file.");
      }
    }
    final var serverRecon = new File(home, "server.recon");
    try (var in = ExampleHome.class.getResourceAsStream("server.recon")) {

      if (in != null) {
        final var bytes = in.readAllBytes();
        var body = new String(bytes, StandardCharsets.UTF_8);
        body = body.replace("$IGNITE_CONFIG", igniteConfig.getAbsolutePath());
        Files.write(serverRecon.toPath(), body.getBytes(StandardCharsets.UTF_8));
      } else {
        throw new IOException("Cannot find logging properties file.");
      }
    }
    return serverRecon;
  }

}
