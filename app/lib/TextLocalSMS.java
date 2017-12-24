package lib;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class TextLocalSMS {
    public static final String API_KEY = "textlocal.apikey";
    public static final String TEXTLOCAL_ENDPOINT = "textlocal.endpoint";

    private final Config config;

    @javax.inject.Inject
    public TextLocalSMS(Config config) {
        this.config = config;
    }

    public String getApiKey() {
        if (config.hasPath(API_KEY)) {
            return config.getString(API_KEY);
        } else {
            throw new ConfigException.Missing(API_KEY);
        }
    }


    public static String sendSms(String message, String numbers) {
        try {
            // Construct data
            String apiKey = "apikey=" + API_KEY;
            String sender = "&sender=" + "InsulinPump";
//            String message = "&message=" + "Emergency! Patient's Blood Glucose Level is too high! This is Alpha Beta Insulin Pump";
//            String numbers = "&numbers=" + "4915217500865";

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL(TEXTLOCAL_ENDPOINT).openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();
            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("Error SMS "+e);
            return "Error "+e;
        }
    }

}
