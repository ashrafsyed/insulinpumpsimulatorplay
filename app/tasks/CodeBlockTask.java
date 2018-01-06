package tasks;

import akka.actor.ActorSystem;
import com.google.inject.Inject;
import lib.Sensor;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class CodeBlockTask {

    private final ActorSystem actorSystem;
    private final ExecutionContext executionContext;

    @Inject
    public CodeBlockTask(ActorSystem actorSystem, ExecutionContext executionContext) {
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
//        this.initialize();
    }

    private void initialize() {
        this.actorSystem.scheduler().schedule(
                Duration.create(1, TimeUnit.SECONDS), // initialDelay
                Duration.create(1, TimeUnit.SECONDS), // interval
                () -> Sensor.bglCalculator(),
                this.executionContext
        );
    }
}