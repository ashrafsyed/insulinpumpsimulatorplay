package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "patient")
public class Patient extends Model {

    @Id
    public long id;

    @Column(length = 255, unique = true, nullable = false)
    @Constraints.MaxLength(255)
    @Constraints.Required
    @Constraints.Email
    public String email;

    public String userUuid;
    public String firstName;
    public String lastName;

    @Column(length = 64, nullable = false)
    public String shaPassword;

    public Timestamp lastUpdate;

    public static Finder<String, Patient> find = new Finder<>(Patient.class);

    public Patient(String email, String firstName, String lastName){
        this.email = email;
        this.userUuid = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Patient byUserName(String firstName){
        return find.query().where().eq("firstName",firstName).findUnique();
    }

}