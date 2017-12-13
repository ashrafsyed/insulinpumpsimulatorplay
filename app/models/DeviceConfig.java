package models;

import io.ebean.Finder;
import io.ebean.Model;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "device_config")
public class DeviceConfig extends Model {

    @Id
    public long id;

    public String deviceId;
    public String patientId;
    public String deviceMode;
    public Double batteryLevel;
    public Double insulinLevel;
    public Double glucagonLevel;
    public Double dailyMax;
    public Double basalHourly;
    public Double bolusMax;
    public LocalDateTime lastUpdate;

    public static Finder<String, DeviceConfig> find = new Finder<>(DeviceConfig.class);

    public DeviceConfig(){
        this.deviceId = UUID.randomUUID().toString();
        this.patientId = UUID.randomUUID().toString();
        this.lastUpdate = LocalDateTime.now(ZoneId.of("Z"));
    }

    public static DeviceConfig getOrCreate () {
        DeviceConfig config = find.query().where().findUnique();
        if (config == null){
            config = new DeviceConfig();
            config.save();
        }
        return config;
    }
    public static DeviceConfig createOrUpdate (String deviceId, String patientId, String deviceMode, Double batteryLevel, Double insulinLevel,
                                               Double glucagonLevel, Double dailyMax, Double bolusMax) {
        DeviceConfig config = null;
        if (StringUtils.isEmpty(deviceId) && StringUtils.isEmpty(patientId)){
            config = new DeviceConfig();
        } else {
            config = DeviceConfig.byIds(deviceId, patientId);
        }
        config.deviceMode = deviceMode;
        config.batteryLevel = batteryLevel;
        config.insulinLevel = insulinLevel;
        config.glucagonLevel = glucagonLevel;
        config.dailyMax = dailyMax;
        config.bolusMax = bolusMax;
        config.basalHourly = Double.valueOf(dailyMax.intValue()/ 24);;
        config.save();
        return config;
    }


    public static DeviceConfig byIds(String deviceId, String patientId){
        return find.query().where().eq("deviceId",deviceId).eq("patientId",patientId).findUnique();
    }


}