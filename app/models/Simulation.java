package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;

@Entity
public class Simulation extends Model {

    @Id
    public long id;

    public String simulationId;
    public String deviceId;
    public String patientId;

    public Map<Integer, Double> bglList;
    public Double batteryStatus;
    public Double insulinStatus;
    public Double glucagonStatus;

    public static Finder<String, Simulation> find = new Finder<>(Simulation.class);

    public Simulation(){
    }

}