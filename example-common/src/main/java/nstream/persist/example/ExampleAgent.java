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

package nstream.persist.example;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import swim.api.SwimLane;
import swim.api.agent.AbstractAgent;
import swim.api.lane.CommandLane;
import swim.api.lane.MapLane;
import swim.api.lane.ValueLane;
import swim.structure.Value;

public class ExampleAgent extends AbstractAgent {

  private static final Logger LOG = Logger.getLogger(ExampleAgent.class.getName());

  @SwimLane("map")
  MapLane<String, Integer> mapLane = super.mapLane();

  @SwimLane("value")
  ValueLane<String> valueLane = super.<String>valueLane()
      .didSet((v, old) -> {
        final var n = this.mapLane.getOrDefault(v, 0) + 1;
        this.mapLane.put(v, n);
      });

  @SuppressWarnings("unused")
  @SwimLane("wake")
  CommandLane<Value> wake = super.<Value>commandLane().onCommand((v) -> LOG.info("Commanded with: " + v));

  private static final String[] COLOURS = new String[] {
    "red",
    "green",
    "blue",
    "orange",
    "yellow",
    "purple",
    "beige",
    "chartreuse"
  };

  private static final int LEN = COLOURS.length;

  private static String nextColour() {
    final var rand = ThreadLocalRandom.current();
    final int i = rand.nextInt(LEN);
    return COLOURS[i];
  }

  private void update() {
    final var id = super.agentContext().getProp("id").stringValue();
    final var c = nextColour();
    LOG.info(String.format("id = %s, Setting colour to %s.", id, c));
    this.valueLane.set(c);
    super.schedule().setTimer(2000, this::update);
  }

  @Override
  public void didStart() {
    final var uri = super.agentContext().nodeUri();
    final var id = super.agentContext().getProp("id").stringValue();
    LOG.info(String.format("Starting agent at: %s", uri));
    final var snapshot = this.mapLane.snapshot();
    if (snapshot.isEmpty()) {
      LOG.info(String.format("id = %s, Restored map was empty.", id));
    } else {
      for (var entry : snapshot.entrySet()) {
        LOG.info(String.format("id = %s, key = %s, count = %d", id, entry.getKey(), entry.getValue()));
      }
    }
    this.update();
  }

}
