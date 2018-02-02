package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lib.BrowserPush;
import models.PushNotificationType;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import views.html.gcm_main_js;
import views.html.gcm_manifest_json;
import views.html.gcm_serviceworker_js;
import views.html.index;

import javax.persistence.PersistenceException;
import java.util.HashMap;
import java.util.Map;

public class AlertNotificationHandler extends Controller {

    /***********************Browserpush Alert Notification Code Starts*******************************/
    public Result mainJs(){
        return Results.ok (gcm_main_js.render()).as("text/javascript");
    }

    public Result gcmManifest(){
        return Results.ok (gcm_manifest_json.render()).as("application/json");
    }

    public Result gcmServiceWorker(){
        response().setHeader("cache-control", "no-cache");
        return ok (gcm_serviceworker_js.render()).as("text/javascript");
    }

    public Result registerSubscriptionPreflight() {
        response().setHeader("Access-Control-Allow-Headers", "Content-Type");
        return ok("Allowed").withHeader("Access-Control-Allow-Origin", "*").withHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
    }

    public Result registerSubscription() {
        Gson gson = new Gson();
        HashMap<String, Object> res = new HashMap<>();
        HashMap jsonData = gson.fromJson(request().body().asJson().toString(), new TypeToken<HashMap<String, Object>>() {
        }.getType());
        String registrationId = (String) jsonData.getOrDefault("registrationId", "");
        String authSecret = (String) jsonData.getOrDefault("authSecret", "");
        String endpoint = (String) jsonData.getOrDefault("endpoint", "");

        if ((StringUtils.isEmpty(registrationId))) {
            res.put("status", "error");
            res.put("message", "Required parameters missing");
        } else {
            HashMap<String, Object> responseData = new HashMap<String, Object>();

            try {
            } catch (PersistenceException pe) {
            }

            res.put("status", "success");
            res.put("message", "Registration completed");
            res.put("data", responseData);
        }

        response().setHeader("Access-Control-Allow-Origin", "*");

        return ok(gson.toJson(res)).as("application/json");
    }

    public Result fetchNotification() {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader("Access-Control-Allow-Methods", "GET");
        response().setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");

        Gson gson = new Gson();
        HashMap<String, Object> responseData = new HashMap<String, Object>();

        String registrationId = request().getQueryString("registrationId");
        PushNotificationType notification = PushNotificationType.getFetchedTrue();
        if (StringUtils.isNotEmpty(registrationId)){

            if (notification != null){
                if (0 == notification.pushType.compareToIgnoreCase("needleCheck")){
                    responseData.put("body","Needle Assembly not attached!");
                }else if (0 == notification.pushType.compareToIgnoreCase("insulinResCheck")){
                    responseData.put("body","Insulin Reservoir not attached!");
                }else if (0 == notification.pushType.compareToIgnoreCase("glucagonResCheck")){
                    responseData.put("body","Glucagon Reservoir not attached!");

                }else if (0 == notification.pushType.compareToIgnoreCase("batteryCheck")){
                    responseData.put("body","Please recharge the battery");

                }else if (0 == notification.pushType.compareToIgnoreCase("pumpFailCheck")){
                    responseData.put("body","Pump is unable to inject Insulin. Kindly Check!");

                }
                PushNotificationType.setFetchedFalse(notification.pushType);
            }else {
                responseData.put("body", "SOS!! The Patient needs assistance. Please check on him/her");
                responseData.put("image","assets/images/warning_sign.png");
            }
            responseData.put("title","Alpha-Beta Pump Simulator");
            responseData.put("icon","assets/images/syringe.png");
            responseData.put("sound","assets/misc/Alert.mp3");
            responseData.put("url","http://www.frankfurt-university.de/");

        }

        return ok(gson.toJson(responseData)).as("application/json");
    }

    public Result selectPushType(){
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();

        String notiftype = request().getQueryString("notiftype");

        if (StringUtils.isNotEmpty(notiftype)){
            PushNotificationType.setFetchedTrue(notiftype);
            dispatchPushNotification();
            resMap.put("status", "success");
            return ok(gson.toJson(resMap)).as("application/json");
        }
        return ok(gson.toJson(resMap)).as("application/json");
    }

    public static void dispatchPushNotification(){
        BrowserPush.dispatchPushNotification("fKzm67mVNSw:APA91bFk3RowZnyobLBQuQDhtGX0_LASJhUgRC6puflxQZvljSQwdTI6qMLgwtMFjM-Uk2JXefCaQq89IvuMatbGebeWm4Fcx_bJosc5bhvDS3ZvuX8rUNTKPIwPqT9lSxBe31zRyDvK");
    }

    /***********************Browserpush Alert Notification Code Ends*******************************/
}
