package lib;

import models.SensorData;
import utils.commons.Enums;

public class Sensor {

    private static int currentReading = 0;
    private static boolean sensorWorking = true;

    public static void recordBGL(long deviceId, long patientId, int bloodSugarLevel)
    {
        if (Enums.deviceStatus.SWITCH_ON.value.equalsIgnoreCase("ON")) {

            SensorData.storeBGL(deviceId, patientId, bloodSugarLevel);
        }
    }

    public static int getReading()
    {
        //At this point a simulated reading is supplied
        //if the hardware was present, it would produce blood
        //sugar value.
        return currentReading;
    }

    public static void setSensorWorking(boolean working)
    {
        sensorWorking = working;
    }

    public static boolean isSensorWorking()
    {
        return sensorWorking;
    }

    public static void bglCalculator(){
        System.out.println("Calculating BGL");
    }
}
