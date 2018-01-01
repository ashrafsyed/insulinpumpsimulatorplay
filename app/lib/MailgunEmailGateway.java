package lib;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

public class MailgunEmailGateway {
    public static final String MAILGUN_API_KEY = ConfigFactory.load().getString("mailgun.apikey");
    public static final String MAILGUN_DOMAIN_NAME = ConfigFactory.load().getString("mailgun.domain");

    public static void sendSimpleMessage() throws UnirestException {

        com.mashape.unirest.http.HttpResponse<com.mashape.unirest.http.JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + MAILGUN_DOMAIN_NAME + "/messages")
                .basicAuth("api", MAILGUN_API_KEY)
                .queryString("from", MAILGUN_DOMAIN_NAME)
                .queryString("to", "ashraf.skycom@gmail.com")
                .queryString("subject", "hello")
                .queryString("text", "testing")
                .asJson();

    }
}
