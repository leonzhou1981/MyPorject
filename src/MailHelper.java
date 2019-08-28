
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;


public class MailHelper {

    public static void main(String[] args) {
        try {
            Email email = new SimpleEmail();

            email.setHostName("submit.notes.na.collabserv.com");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator("tmfftest1@farrow.com", "farrow123"));
            email.setSSLOnConnect(true);
//            email.setStartTLSEnabled(false);
            email.setFrom("tmfftest1@farrow.com");
            email.setSubject("TestMail");
            email.setMsg("This is a test mail ... :-)");
            email.addTo("tmfftest2@farrow.com");

            /*email.setHostName("outlook.office365.com");
            email.setSmtpPort(587);
            email.setAuthenticator(new DefaultAuthenticator("liang.zhou@blujaysolutions.com", ""));
            email.setStartTLSEnabled(true);
            email.setFrom("liang.zhou@blujaysolutions.com");
            email.setSubject("TestMail");
            email.setMsg("This is a test mail ... :-)");
            email.addTo("tmfftest2@farrow.com");*/

            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}
