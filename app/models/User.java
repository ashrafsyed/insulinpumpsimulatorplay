package models;

import java.util.*;
import javax.persistence.*;

import io.ebean.*;
import play.data.validation.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static play.mvc.Controller.request;

@Entity
@Table(name = "user_data")
public class User extends Model {

    @Id
    public long id;

    @Column(length = 255, unique = true, nullable = false)
    @Constraints.MaxLength(255)
    @Constraints.Required
    @Constraints.Email
    public String email;

    public String userType;
    public String userUuid;
    public String firstName;
    public String lastName;

    @Column(length = 64, nullable = false)
    public String shaPassword;

    public Timestamp lastUpdate;

    public static Finder<String, User> find = new Finder<>(User.class);

    public User(String email, String userType, String firstName, String lastName){
        this.email = email;
        this.userType = userType;
        this.userUuid = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static User byUserName(String firstName){
        return find.query().where().eq("firstName",firstName).findUnique();
    }

}