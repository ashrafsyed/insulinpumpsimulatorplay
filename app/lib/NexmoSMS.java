package lib;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import play.Logger;
import play.api.Play;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NexmoSMS {
    public static String endPoint = "https://rest.nexmo.com/sms/json";
    public static String apiKey = "edff98d0";
    public static String apiSecret = "203244039e3c89fe";

    public static void sendSms(String message, String phoneNumber) {
        try {
            // Construct data
            String sender = "InsulinPump";
//            String message = "&message=" + "Emergency! Patient's Blood Glucose Level is too high! This is Alpha Beta Insulin Pump";
//            String numbers = "&numbers=" + "4915217500865";

            Map<String, String> nexmoQueryParams = new LinkedHashMap<>();
            nexmoQueryParams.put("api_key", apiKey);
            nexmoQueryParams.put("api_secret", apiSecret);
            nexmoQueryParams.put("to", "49"+phoneNumber);
            nexmoQueryParams.put("from", sender);
            nexmoQueryParams.put("text", message);

            List<Pair<String, String>> extraHeaders =  new ArrayList<>();
            Pair<String, String> pair = new MutablePair<>("Content-Type", "application/x-www-form-urlencoded");
            extraHeaders.add(pair);

            //TODO nexmo integration with post method
/*
            String nexmoResponse = Play.net().postValues(endPoint, nexmoQueryParams, extraHeaders);
            Logger.info(nexmoResponse);
*/


        } catch (Exception e) {
            System.out.println("Error SMS "+e);
        }
    }

}
