package akka.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.protocol.SensorMessageProtocol;

public class SensorActor extends AbstractActor {

    public static Props props = Props.create(SensorMessageProtocol.class, "arg");

    public void currentBGL (String chunkMessage) {
        // Sending chunks.
        System.out.println(chunkMessage);
    }

    @Override
    public Receive createReceive () {
        return receiveBuilder()
                .match(SensorMessageProtocol.class, s -> {
                    currentBGL(s.toString());
                })
                .matchAny(o -> System.out.println("error"))
                .build();
    }


}
