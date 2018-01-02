package models;

import io.ebean.Finder;
import io.ebean.Model;
import lib.CarbohydrateModule;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "patient")
public class Patient extends Model {

    @Id
    public long id;

    public String deviceId;
    public String patientId;
    public String patientFirstName;
    public String patientLastName;
    public String patientGender;
    public Integer patientAge;
    public Double patientWeight;
    public Double patientHeight;
    public Double bloodVolume;
    public Double glucoseSensitivity;
    public String emailId;
    public String mobileNumber;
    public LocalDateTime lastUpdate;

    public static Finder<String, Patient> find = new Finder<>(Patient.class);
    public Patient (String deviceId, String patientId, String patientFirstName, String patientLastName, String patientGender,
                    String emailId, String mobileNumber, Integer patientAge, Double patientHeight, Double patientWeight){
        this.deviceId = deviceId;
        this.patientId = patientId;
        this.lastUpdate = LocalDateTime.now(ZoneId.of("Z"));
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.patientGender = patientGender;
        this.emailId = emailId;
        this.mobileNumber = mobileNumber;
        this.patientAge = patientAge;
        this.patientWeight = patientWeight;
        this.patientHeight = patientHeight;
        this.bloodVolume = Math.pow((0.3669 * (patientHeight/100)),3.0) + 0.03219 * patientWeight + 0.6041;
        this.glucoseSensitivity = CarbohydrateModule.glucoseSensitivity(patientWeight);
        this.save();

    }

    public static Patient createOrUpdatePatientData (String deviceId, String patientId, String patientFirstName, String patientLastName, String patientGender,
                                                                 String emailId, String mobileNumber, Integer patientAge, Double patientHeight, Double patientWeight) {
        if (StringUtils.isNotEmpty(deviceId) && StringUtils.isNotEmpty(patientId)){
            Patient patient = Patient.byIds(deviceId,patientId);
            if (patient == null) {
                patient = new Patient(deviceId, patientId, patientFirstName, patientLastName, patientGender, emailId, mobileNumber,
                            patientAge, patientHeight, patientWeight);
            } else {
                patient.deviceId = deviceId;
                patient.patientId = patientId;
                patient.lastUpdate = LocalDateTime.now(ZoneId.of("Z"));
                patient.patientFirstName = patientFirstName;
                patient.patientLastName = patientLastName;
                patient.patientGender = patientGender;
                patient.emailId = emailId;
                patient.mobileNumber = mobileNumber;
                patient.patientAge = patientAge;
                patient.patientWeight = patientWeight;
                patient.patientHeight = patientHeight;
                patient.bloodVolume = Math.pow((0.3669 * (patientHeight/100)),3.0) + 0.03219 * patientWeight + 0.6041;
                patient.glucoseSensitivity = CarbohydrateModule.glucoseSensitivity(patientWeight);

            }
            patient.save();
            return patient;
        }else {
            return null;
        }
    }


    public static Patient byIds(String deviceId, String patientId){
        return find.query().where().eq("deviceId",deviceId).eq("patientId",patientId).findUnique();
    }

    public static Patient getPatient(){
        Patient patient = null;
        patient = find.query().where().findUnique();
        if (null == patient){
            return null;
        }
        return patient;
    }
}