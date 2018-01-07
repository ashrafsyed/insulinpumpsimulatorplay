package lib;

import models.DeviceConfig;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class SimulationContext {
    public class Patient {
        public double glucoseSensitivity;
        public double weight;
        public double bloodVol;
    }
    public class DeviceConfig {
        public double targetBgl;
    }
    public class Meal {
        public double gI;
        public double carbs;
    }

    public final int maxSafeBgl = 200; //Change these
    public final int minSafeBgl = 20;  // These too
    public final int insulinShotInterval = 20;  // These too

    public boolean started;
    public String mode = "AUTO"; // or MANUAL
    public int currentTime;
    public int lapseSpeed;

    public double startBgl;
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
        patient = new Patient();
        config = new DeviceConfig();
        meals = new ArrayList<>();
    }

}
