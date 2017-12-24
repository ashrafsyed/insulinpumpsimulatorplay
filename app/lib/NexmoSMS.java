package lib;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NexmoSMS {
    public static final String END_POINT = "nexmo.endpoint";
    public static final String API_KEY = "nexmo.apikey";
    public static final String API_SECRET = "nexmo.apisecret";

    private final Config config;

    @javax.inject.Inject
    public NexmoSMS (Config config) {
        this.config = config;
    }

    public String getEndPoint() {
        if (config.hasPath(END_POINT)) {
            return config.getString(END_POINT);
        } else {
            throw new ConfigException.Missing(END_POINT);
        }
    }

    public String getApiKey() {
        if (config.hasPath(API_KEY)) {
            return config.getString(API_KEY);
        } else {
            throw new ConfigException.Missing(API_KEY);
        }
    }

    public String getApiSecret() {
        if (config.hasPath(API_SECRET)) {
            return config.getString(API_SECRET);
        } else {
            throw new ConfigException.Missing(API_SECRET);
        }
    }


    public static void sendSms(String message, String phoneNumber) {
        try {
            // Construct data
            String sender = "InsulinPump";
//            String message = "&message=" + "Emergency! Patient's Blood Glucose Level is too high! This is Alpha Beta Insulin Pump";
//            String numbers = "&numbers=" + "4915217158915";

            Map<String, String> nexmoQueryParams = new LinkedHashMap<>();
            nexmoQueryParams.put("api_key", API_KEY);
            nexmoQueryParams.put("api_secret", API_SECRET);
            nexmoQueryParams.put("to", "49"+phoneNumber);
            nexmoQueryParams.put("from", sender);
            nexmoQueryParams.put("text", message);

            List<Pair<String, String>> extraHeaders =  new ArrayList<>();
            Pair<String, String> pair = new MutablePair<>("Content-Type", "application/x-www-form-urlencoded");
            extraHeaders.add(pair);

            //TODO nexmo integration with post method
/*
            String nexmoResponse = Play.net().postValues(END_POINT, nexmoQueryParams, extraHeaders);
            Logger.info(nexmoResponse);
*/


        } catch (Exception e) {
            System.out.println("Error SMS "+e);
        }
    }

}
