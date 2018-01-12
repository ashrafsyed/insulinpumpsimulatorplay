package tasks;

import akka.actor.ActorSystem;
//import com.google.inject.Inject;
import lib.RealtimeSimulator;
import lib.Sensor;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class SimulatorTask {

    private final ActorSystem actorSystem;
    private final ExecutionContext executionContext;

    @Inject
    RealtimeSimulator realtimeSimulator;

    @Inject
    public SimulatorTask(ActorSystem actorSystem, ExecutionContext executionContext) {
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
        this.initialize();
    }

    private void initialize() {
        this.actorSystem.scheduler().schedule(
                Duration.create(1, TimeUnit.SECONDS), // initialDelay
                Duration.create(1, TimeUnit.SECONDS), // interval
                () -> realtimeSimulator.tick(),
                this.executionContext
        );
    }
}