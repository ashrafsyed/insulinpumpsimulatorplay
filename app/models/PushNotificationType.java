package models;

import io.ebean.Finder;
import io.ebean.Model;
import utils.commons.Enums.CapacityStatus;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PushNotificationType extends Model {

    @Id
    public long id;

    public boolean toBeFetched;
    public String pushType;

    public static Finder<String, PushNotificationType> find = new Finder<>(PushNotificationType.class);


    public static PushNotificationType getFetchedTrue(){
        PushNotificationType pushNotificationType = find.query().where().eq("toBeFetched", Boolean.TRUE).findUnique();
        if (pushNotificationType != null){
            return pushNotificationType;
        } else {
            return null;
        }
    }

    public static void setFetchedFalse(String pushType){
        PushNotificationType pushNotificationType = find.query().where().eq("pushType", pushType).findUnique();
        pushNotificationType.toBeFetched = Boolean.FALSE;
        pushNotificationType.save();
    }

    public static void setFetchedTrue(String pushType){
        PushNotificationType pushNotificationType = find.query().where().eq("pushType", pushType).findUnique();
        pushNotificationType.toBeFetched = Boolean.TRUE;
        pushNotificationType.save();
    }

}