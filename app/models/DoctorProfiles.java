package models;

import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.*;

import io.ebean.*;
import play.data.validation.*;
import java.sql.Timestamp;

@Entity
public class DoctorProfiles extends Model {

    @Id
    public long id;

    public String email;
    public String doctorId;
    public String password;
    public String gender;
    public String mobileNumber;
    public String firstName;
    public String lastName;
    public LocalDateTime lastUpdate;

    public static Finder<String, DoctorProfiles> find = new Finder<>(DoctorProfiles.class);

    public DoctorProfiles(String email, String doctorId, String password, String firstName, String lastName,
                          String gender, String mobileNumber){
        this.email = email;
        this.doctorId = doctorId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.lastUpdate = LocalDateTime.now();
    }

    public static DoctorProfiles createOrUpdate(String email, String doctorId, String password, String firstName, String lastName,
                                                String gender, String mobileNumber){
        DoctorProfiles doctor = DoctorProfiles.byDoctorIdPassword(doctorId, password);
        if (null == doctor){
            doctor = new DoctorProfiles(email, doctorId, password, firstName, lastName, gender, mobileNumber);
            doctor.save();
        } else {
            doctor.email = email;
            doctor.doctorId = doctorId;
            doctor.password = password;
            doctor.firstName = firstName;
            doctor.lastName = lastName;
            doctor.gender = gender;
            doctor.mobileNumber = mobileNumber;
            doctor.lastUpdate = LocalDateTime.now();
            doctor.save();
        }
        return doctor;
    }
    public static DoctorProfiles byDoctorIdPassword(String doctorId, String password){
        DoctorProfiles doctor = null;
        doctor = find.query().where().eq("doctorId",doctorId).eq("password",password).findUnique();
        if (null == doctor){
            doctor = find.query().where().eq("email",doctorId).eq("password",password).findUnique();
        }
        return doctor;
    }

    public static DoctorProfiles byProfileId(Long doctorId){
        DoctorProfiles doctor = null;
        doctor = find.query().where().eq("id",doctorId).findUnique();
        return doctor;
    }

}