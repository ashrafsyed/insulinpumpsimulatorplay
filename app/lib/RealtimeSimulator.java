package lib;

import models.SimulationLog;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ankit on 13/1/18.
 */
@Singleton
public class RealtimeSimulator {

    @Inject
    SimulationContext simulationContext;

    public double consumeMeal(SimulationContext.Meal meal) {
        double riseInBglCarb = 0;
        SimulationContext.Patient patient = simulationContext.patient;
        if (simulationContext.currentTime >= meal.mealTime) {
            int consumedDuration = simulationContext.currentTime - meal.mealTime;
            riseInBglCarb = CarbohydrateModule.riseInBGL(meal.carbs, meal.gI, consumedDuration, patient.glucoseSensitivity);

        }
        return riseInBglCarb;
    }

    public boolean takeInsulin(SimulationContext.Meal meal) {
        boolean tookDose = false;
        SimulationContext.Patient patient = simulationContext.patient;
        if (! meal.tookInsulin) {
            meal.tookInsulin = true;
            if (true) {
                tookDose = true;
                simulationContext.currentInsulin = InsulinModule.computeInsulinDose(meal.carbs, meal.gI, patient.glucoseSensitivity,
                        simulationContext.currentBgl, simulationContext.targetBgl);
            }
        }
        return tookDose;
    }

    public void tick() {
        if (simulationContext.started) {
            for (int j = 0; j < simulationContext.lapseSpeed; j++, simulationContext.currentTime++) {
                SimulationContext.Patient patient = simulationContext.patient;
                SimulationContext.DeviceConfig config = simulationContext.config;
                double riseInBglCarb = 0.0;
                for (SimulationContext.Meal meal: simulationContext.meals) {
                    consumeMeal(meal);
                    takeInsulin(meal);
                }
                double bglChangeByInsulin = InsulinModule.changeInBgl(simulationContext.currentTime, simulationContext.currentInsulin, patient.weight, patient.bloodVol);
                simulationContext.currentBgl = simulationContext.currentBgl - bglChangeByInsulin + riseInBglCarb;

                SimulationLog.createLog(simulationContext.simulationId, simulationContext.patient.deviceId,
                        simulationContext.patient.patientId, simulationContext.currentBgl, simulationContext.currentInsulin, simulationContext.currentTime);

                System.out.println("BGL Breakfast:" + simulationContext.currentBgl + " at time: " + simulationContext.currentTime + " bglChangeByInsulin " + bglChangeByInsulin + " riseCarb " + riseInBglCarb);
            }
        }
    }
}
