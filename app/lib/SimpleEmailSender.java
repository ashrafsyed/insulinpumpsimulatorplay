package lib;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

// Java program to send simple email using apache commons email
// Uses the Gmail SMTP servers
public class SimpleEmailSender {
    private static final String EMAIL_USERNAME = ConfigFactory.load().getString("gmail.username");
    private static final String EMAIL_PASSWORD = ConfigFactory.load().getString("gmail.password");
    private static final String HOST = "smtp.gmail.com";
    private static final int PORT = 465;
    private static final boolean SSL_FLAG = true;

    public static void sendSimpleEmail(String toAddress, String subject, String message) {

        try {
            Email email = new SimpleEmail();
            email.setHostName(HOST);
            email.setSmtpPort(PORT);
            email.setAuthenticator(new DefaultAuthenticator(EMAIL_USERNAME, EMAIL_PASSWORD));
            email.setSSLOnConnect(SSL_FLAG);
            email.setFrom(EMAIL_USERNAME);
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(toAddress);
            email.send();
        }catch(Exception ex){
            System.out.println("Unable to send email");
            System.out.println(ex);
        }
    }
}