package lib;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MailgunEmailGateway {
    public static final String API_KEY = "key-40e1fa9b735f105be2b3173d87ea9804";
    public static final String YOUR_DOMAIN_NAME = "sandboxf87c1d68b657436394c08bffce23e9da.mailgun.org";

    public static void sendSimpleMessage() throws UnirestException {

        com.mashape.unirest.http.HttpResponse<com.mashape.unirest.http.JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .queryString("from", "Excited User <USER@YOURDOMAIN.COM>")
                .queryString("to", "ashraf.skycom@gmail.com")
                .queryString("subject", "hello")
                .queryString("text", "testing")
                .asJson();

    }
}
