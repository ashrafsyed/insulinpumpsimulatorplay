package akka.protocol;

public class SensorMessageProtocol {

    public static class SensorMessage {
        public final String chunkMessage;

        public SensorMessage (String chunkMessage) {
            this.chunkMessage = chunkMessage;
        }

    }
}
