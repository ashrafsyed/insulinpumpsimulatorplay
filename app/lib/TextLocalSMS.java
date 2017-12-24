package lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class TextLocalSMS {

    public static String sendSms(String message, String numbers) {
        try {
            // Construct data
            String apiKey = "apikey=" + "I6lsXRaqmNE-WWCRjjCJ88k1hBf62BI30i9IXqabC0";
            String sender = "&sender=" + "InsulinPump";
//            String message = "&message=" + "Emergency! Patient's Blood Glucose Level is too high! This is Alpha Beta Insulin Pump";
//            String numbers = "&numbers=" + "4915217500865";

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("http://api.txtlocal.com/send/?").openConnection();
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
