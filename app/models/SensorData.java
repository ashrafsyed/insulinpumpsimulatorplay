package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
public class SensorData extends Model {

    @Id
    public long id;

    public Long deviceId;
    public Long patientId;
    public int  bloodSugarLevel;
    public LocalDateTime recordingTimeStamp;

    public static Finder<String, SensorData> find = new Finder<>(SensorData.class);

    public SensorData(Long deviceId, Long patientId, int bloodSugarLevel){
        this.deviceId = deviceId;
        this.patientId = patientId;
        this.bloodSugarLevel = bloodSugarLevel;
        this.recordingTimeStamp = LocalDateTime.now(ZoneId.of("Z"));
    }

    public static void storeBGL(Long deviceId, Long patientId, int bloodSugarLevel){
        SensorData sensorData = new SensorData(deviceId, patientId, bloodSugarLevel);
        sensorData.save();
    }

    public static void getCurrentBGL(Long deviceId, Long patientId, int bloodSugarLevel){
        SensorData sensorData = new SensorData(deviceId, patientId, bloodSugarLevel);
        sensorData.save();
    }

}