package lib;

import com.google.gson.Gson;
import com.typesafe.config.ConfigFactory;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.util.HashMap;
import java.util.Map;

public class BrowserPush {
    public static final String GCM_API_KEY = ConfigFactory.load().getString("gcm.api_key");
    public static final String GCM_ENDPOINT = ConfigFactory.load().getString("gcm.end_point");

    public static WSClient ws = null;

    public static void dispatchPushNotification(String registrationId) {
        try {
            Map<String, Object> gcmPushQueryParams = new HashMap<>();

            gcmPushQueryParams.put("registration_ids", registrationId);
            gcmPushQueryParams.put("time_to_live", 24 * 60 * 60);

            String json = new Gson().toJson(gcmPushQueryParams);
            WSRequest wsRequest = ws.url(GCM_ENDPOINT).addHeader("Authorization", String.format("key=%s", GCM_API_KEY));;
            wsRequest.setContentType("application/json").post(json).whenComplete((wsResponse, ex) -> {
                Logger.debug ("GCMResponse:getBody: " + wsResponse.getBody());
            });

        } catch (Exception e) {
            System.out.println("Error dispatching Push Notif "+e);
        }
    }

}
