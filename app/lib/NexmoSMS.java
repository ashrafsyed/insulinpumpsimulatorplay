package lib;

import com.google.gson.Gson;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import play.Logger;
import play.libs.ws.*;

import java.util.*;

public class NexmoSMS {
    public static final String NEXMO_END_POINT = ConfigFactory.load().getString("nexmo.endpoint");
    public static final String NEXMO_API_KEY = ConfigFactory.load().getString("nexmo.apikey");
    public static final String NEXMO_API_SECRET = ConfigFactory.load().getString("nexmo.apisecret");
    public static WSClient ws = null;

    public static void sendSms(String message, String phoneNumber) {
        try {
            // Construct data
//            String message = "&message=" + "Emergency! Patient's Blood Glucose Level is too high! This is Alpha Beta Insulin Pump";
//            String numbers = "&numbers=" + "4915217158915";

            Map<String, Object> nexmoQueryParams = new HashMap<>();
            nexmoQueryParams.put("api_key", NEXMO_API_KEY);
            nexmoQueryParams.put("api_secret", NEXMO_API_SECRET);
            nexmoQueryParams.put("to", "49"+phoneNumber);
            nexmoQueryParams.put("from", "Alpha-Beta Pump");
            nexmoQueryParams.put("text", message);

            String json = new Gson().toJson(nexmoQueryParams);
            WSRequest wsRequest = ws.url(NEXMO_END_POINT).setContentType("application/json");
            wsRequest.setContentType("application/json").post(json).whenComplete((wsResponse, ex) -> {
                Logger.debug ("NexmoResponse:getBody: " + wsResponse.getBody());
            });

        } catch (Exception e) {
            System.out.println("Error SMS "+e);
        }
    }

}
