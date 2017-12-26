package utils.commons;

import io.ebean.annotation.EnumValue;


/**
 * Created by Ashraf on 27-11-2017.
 */
public class Enums {
    public enum CapacityStatus {
        @EnumValue(value = "VERY_LOW")
        VERY_LOW(10),

        @EnumValue(value = "LOW")
        LOW(20),

        @EnumValue(value = "MEDIUM")
        MEDIUM(50),

        @EnumValue(value = "HIGH")
        HIGH(50),

        @EnumValue(value = "FULL")
        FULL(100);

        public int statusValue;
        CapacityStatus(int capacityStatus) {
            this.statusValue = capacityStatus;
        }
    }

    public enum deviceStatus {
        @EnumValue(value = "SWITCH_OFF")
        SWITCH_OFF("SWITCH_OFF"),

        @EnumValue(value = "SWITCH_ON")
        SWITCH_ON("SWITCH_ON");

        public String value;
        deviceStatus(String deviceStatus) { this.value = deviceStatus; }
    }

    public enum deviceMode {
        @EnumValue(value = "MANUAL")
        MANUAL("MANUAL"),

        @EnumValue(value = "AUTO")
        AUTO("AUTO");

        public String value;
        deviceMode (String deviceMode) { this.value = deviceMode;}
    }

}
