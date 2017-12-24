package lib;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class MailgunEmailGateway {
    public static final String API_KEY = "mailgun.apikey";
    public static final String DOMAIN_NAME = "mailgun.domain";

    private final Config config;

    @javax.inject.Inject
    public MailgunEmailGateway(Config config) {
        this.config = config;
    }

    public String getApiKey() {
        if (config.hasPath(API_KEY)) {
            return config.getString(API_KEY);
        } else {
            throw new ConfigException.Missing(API_KEY);
        }
    }

    public String getDomainName() {
        if (config.hasPath(DOMAIN_NAME)) {
            return config.getString(DOMAIN_NAME);
        } else {
            throw new ConfigException.Missing(DOMAIN_NAME);
        }
    }

    public void sendSimpleMessage() throws UnirestException {

        com.mashape.unirest.http.HttpResponse<com.mashape.unirest.http.JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + DOMAIN_NAME + "/messages")
                .basicAuth("api", this.getApiKey())
                .queryString("from", "Excited User <USER@YOURDOMAIN.COM>")
                .queryString("to", "ashraf.skycom@gmail.com")
                .queryString("subject", "hello")
                .queryString("text", "testing")
                .asJson();

    }
}
