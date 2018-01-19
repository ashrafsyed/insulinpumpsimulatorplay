package lib;

import models.DeviceConfig;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
public class SimulationContext {


    public static class Patient {
        public String patientId;
        public String deviceId;
        public double weight;
        public double glucoseSensitivity;
        public double bloodVol;

        public Patient(String patientId, String deviceId, double weight, double glucoseSensitivity, double bloodVol) {
            this.patientId = patientId;
            this.weight = weight;
            this.deviceId = deviceId;
            this.glucoseSensitivity = glucoseSensitivity;
            this.bloodVol = bloodVol;
        }
    }

    public static class DeviceConfig {
        public double targetBgl;
    }

    public static class Meal {
        public boolean tookInsulin;
        public int mealTime;
        public double carbs;
        public double gI;

        public Meal(int mealTime, double carbs, double gI) {
            this.mealTime = mealTime;
            this.carbs = carbs;
            this.gI = gI;
        }
    }

    public String simulationId;
    public final int maxSafeBgl = 200; //Change these
    public final int targetBgl = 20;  // These too
    public final int minSafeBgl = 20;  // These too
    public final int insulinShotInterval = 20;  // These too

    public boolean started;
    public String mode = "AUTO"; // or MANUAL
    public int currentTime;
    public int lapseSpeed;

    public double currentBgl;
    public double currentInsulin;
    public int lastInsulinTime;

    public Patient patient;
    public DeviceConfig config;
    public List<Meal> meals;

    public void reset() {
        started = false;
        currentTime = 0;
        lapseSpeed = 0;
        currentBgl = 0.0;
        currentInsulin = 0.0;
        lastInsulinTime = - insulinShotInterval; // Always less than interval, so that insulin shot can be taken start.
        patient = null;
        config = new DeviceConfig();
        meals = new ArrayList<>();
    }

    public void stopSimulation() {
        this.reset();
    }

    /**
     * Always call stopSimulation() before startSimulation
     *
     * simulationContext.stopSimulation();
     * simulationContext.startSimulation();
     */
    public String startSimulation() {
        this.simulationId = UUID.randomUUID().toString();
        this.started = true;
        this.lapseSpeed = 1;
        return this.simulationId;
    }

    public int speedUp() {
        return this.lapseSpeed < 9 ? ++this.lapseSpeed : 9;
    }

    public int speedDown() {
        return this.lapseSpeed > 0 ? --this.lapseSpeed : 0;
    }
}
