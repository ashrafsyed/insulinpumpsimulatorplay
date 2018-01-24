package models;

import io.ebean.Finder;
import io.ebean.Model;
import lib.CarbohydrateModule;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity
public class PatientList extends Model {

    @Id
    public long id;

    public String doctorId;
    public String deviceId;
    public String patientId;
    public String patientFirstName;
    public String patientLastName;
    public String patientGender;
    public Integer patientAge;
    public String patientEmailId;
    public String patientMobileNumber;
    public LocalDateTime lastUpdate;

    public static Finder<String, PatientList> find = new Finder<>(PatientList.class);
    public PatientList(String doctorId, String deviceId, String patientId, String patientFirstName, String patientLastName,
                       String patientGender, Integer patientAge, String patientEmailId, String patientMobileNumber){
        this.doctorId = doctorId;
        this.deviceId = deviceId;
        this.patientId = patientId;
        this.lastUpdate = LocalDateTime.now(ZoneId.of("Z"));
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.patientGender = patientGender;
        this.patientAge = patientAge;
        this.patientEmailId = patientEmailId;
        this.patientMobileNumber = patientMobileNumber;
        this.save();

    }

    public static PatientList createOrUpdatePatientData (String doctorId, String deviceId, String patientId, String patientFirstName, String patientLastName, String patientGender,
                                                         String patientEmailId, String patientMobileNumber, Integer patientAge, Double patientHeight, Double patientWeight) {
        if (StringUtils.isNotEmpty(deviceId) && StringUtils.isNotEmpty(patientId)){
            PatientList patient = PatientList.byPatientId(deviceId,patientId);
            if (patient == null) {
                patient = new PatientList(doctorId, deviceId, patientId, patientFirstName, patientLastName, patientGender, patientAge, patientEmailId, patientMobileNumber);
            } else {
                patient.deviceId = deviceId;
                patient.patientId = patientId;
                patient.lastUpdate = LocalDateTime.now(ZoneId.of("Z"));
                patient.patientFirstName = patientFirstName;
                patient.patientLastName = patientLastName;
                patient.patientGender = patientGender;
                patient.patientAge = patientAge;
                patient.patientEmailId = patientEmailId;
                patient.patientMobileNumber = patientMobileNumber;

            }
            patient.save();
            return patient;
        }else {
            return null;
        }
    }


    public static PatientList byPatientId(String deviceId, String patientId){
        return find.query().where().eq("deviceId",deviceId).eq("patientId",patientId).findUnique();
    }

    public static List<PatientList> byDoctorId(String doctorId){
        return find.query().where().eq("doctorId", doctorId).findList();
    }
}