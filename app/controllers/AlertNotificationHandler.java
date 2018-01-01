package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lib.BrowserPush;
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
        if (StringUtils.isNotEmpty(registrationId)){
            //TODO fetch notification data from table
            responseData.put("title","Alpha-Beta Pump Simulator");
            responseData.put("body","Danger!! The BGL level is too high. The patient may die");
            responseData.put("icon","assets/images/warning_sign.png");
            responseData.put("sound","assets/misc/Alert.mp3");
            responseData.put("url","https://youtube.com");
        }

        return ok(gson.toJson(responseData)).as("application/json");
    }

    public static void dispatchPushNotification(){
        BrowserPush.dispatchPushNotification("BEL0DKcnKmqHEl5tOTSmKh6DtXsiFU3irlEhmZGoiTqG+i4enw6oeNmE1SyhUY5XO3C5WdvARokWryFiQMaxhBw=");
    }

    /***********************Browserpush Alert Notification Code Ends*******************************/
}
