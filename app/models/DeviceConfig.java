package models;

import io.ebean.Finder;
import io.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "device_config")
public class DeviceConfig extends Model {

    @Id
    public long id;

    public String patientFirstName;
    public String patientLastName;
    public String patientGender;
    public LocalDate patientDob;
    public int patientWeight;
    public int patientHeight;
    public String deviceId;
    public String patientId;
    public int  basalDailyMax;
    public int  basalHourly;
    public int  bolusMax;
    public LocalDateTime lastUpdate;

    public static Finder<String, DeviceConfig> find = new Finder<>(DeviceConfig.class);

    public DeviceConfig(String patientFirstName, String patientLastName, String patientGender,
                        LocalDate patientDob, int basalDailyMax, int bolusMax){
        this.deviceId = UUID.randomUUID().toString();
        this.patientId = UUID.randomUUID().toString();
        this.basalDailyMax = basalDailyMax;
        this.basalHourly = Math.floorDiv(basalDailyMax,24);
        this.bolusMax = bolusMax;
        this.lastUpdate = LocalDateTime.now(ZoneId.of("Z"));
    }

/*
    public static DeviceConfig byUserName(Long firstName){
        return find.query().where().eq("firstName",firstName).findUnique();
    }
*/

}