package lib;

import com.google.gson.Gson;
import com.typesafe.config.ConfigFactory;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BrowserPush {
    public static final String FCM_API_KEY = ConfigFactory.load().getString("fcm.api_key");
    public static final String FCM_ENDPOINT = ConfigFactory.load().getString("fcm.end_point");

    public static WSClient ws = null;

    public static void dispatchPushNotification(String registrationId) {
        try {
            Map<String, Object> gcmPushQueryParams = new HashMap<>();
            Map<String, Object> gcmPushData = new HashMap<>();

            gcmPushQueryParams.put("to", registrationId.trim());
            gcmPushQueryParams.put("priority", "high");

            gcmPushData.put("title", "FCM Notification Title"); // Notification title
            gcmPushData.put("body", "Hello First Test notification"); // Notification body
            gcmPushQueryParams.put("data", gcmPushData);


            String pushMessage = new Gson().toJson(gcmPushQueryParams);
            System.out.println(pushMessage);
/*
            WSRequest wsRequest = ws.url(FCM_ENDPOINT).addHeader("Authorization", String.format("key=%s", FCM_API_KEY));;
            wsRequest.setContentType("application/json").post(pushMessage).whenComplete((wsResponse, ex) -> {
                Logger.debug ("FCMResponse:getBody: " + wsResponse.getBody());
            });

*/

            URL url = new URL(FCM_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + FCM_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");


            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(pushMessage);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


        } catch (Exception e) {
            System.out.println("Error dispatching Push Notif "+e);
        }
    }

}
