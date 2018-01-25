package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;

@Entity
public class SimulationLog extends Model {

    @Id
    public long id;

    public String simulationId;
    public String deviceId;
    public String patientId;

    public Double currentBgl;
    public Double currentInsulin;
    public Integer currentTick;

    public static Finder<String, SimulationLog> find = new Finder<>(SimulationLog.class);

    public SimulationLog(String simulationId, String deviceId, String patientId, Double currentBgl, Double currentInsulin, Integer currentTick) {
        this.simulationId = simulationId;
        this.deviceId = deviceId;
        this.patientId = patientId;
        this.currentBgl = currentBgl;
        this.currentInsulin = currentInsulin;
        this.currentTick = currentTick;
    }

    public static SimulationLog createLog(String simulationId, String deviceId, String patientId, Double currentBgl, Double currentInsulin, Integer currentTick) {
        SimulationLog simulationLog = new SimulationLog(simulationId, deviceId, patientId, currentBgl, currentInsulin, currentTick);
        simulationLog.save();
        return simulationLog;
    }

}