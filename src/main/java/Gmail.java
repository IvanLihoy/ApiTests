
import org.testng.log4testng.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;

public class Gmail {

        public static WebDriver webDriver;
        public static final String host = "pop.gmail.com";// change accordingly
        public static final String mailStoreType = "pop3s";
        public static final String username = "lihoy@singree.com";// change accordingly
        public static final String password = "lihoy92ivan";// change accordingly
        private static final Logger log = Logger.getLogger(Gmail.class);


        public static String getActivationLink() throws Exception {
            String reference = null;
            Properties properties = new Properties();
            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore(mailStoreType);
            store.connect(host, username, password);
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length--" + messages.length);
            int i = 0;
            for (Message message : messages) {
                System.out.println("---------------------------------");
                i++;
                if (message.getSubject().contains("Reset password")) {
                    System.out.println("---------------------------------");
                    System.out.println("Email Number " + (i + 1));
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("From: " + message.getFrom()[0]);
                    System.out.println(getTextFromMessage(message));
//                    System.out.println("Text: " + message.getContent().toString());
//                    String messageBody = message.getContent().toString();
//                    Document document = Jsoup.parse(messageBody);
//                    Elements link = document.body().getElementsByClass("yj6qo");
//                    System.out.println(link);
//                    reference = link.attr("href").toString();
//                    System.out.println(reference);
                }
            }
            return reference;
        }

        public void openActivationLink() throws Exception {

            String code = getActivationLink();
            Thread.sleep(5000);
        }

        @Test
        public void testGMail() throws Exception {
            //openActivationLink();
            deleteActivationMessage();

        }
        public static String getTextFromMessage(Message message) throws Exception {
            String result = "";
            if (message.isMimeType("text/plain")) {
                result = message.getContent().toString();
            } else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                result = getTextFromMimeMultipart(mimeMultipart);
            }
            return result;
        }

        public static String getTextFromMimeMultipart(
                MimeMultipart mimeMultipart) throws Exception{
            String result = "";
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result = result + "\n" + bodyPart.getContent();
                    break; // without break same text appears twice in my tests
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
                    result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
                } else if (bodyPart.getContent() instanceof MimeMultipart){
                    result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
                }
            }
            return result;
        }

    public void deleteActivationMessage() throws Exception {
        Properties properties = new Properties();
        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);
        Store store = emailSession.getStore(mailStoreType);
        store.connect(host, username, password);
        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_WRITE);
        Message[] messages = emailFolder.getMessages();
        int i =0;
        for (Message message:messages){
            i++;
            System.out.println(message.getSubject());
            if (message.getSubject().contains("Reset password")){
                message.setFlag(Flags.Flag.DELETED, true);
                emailFolder.close(true);
                System.out.println("Message: Registration successfull - WAS DELETED!");
                log.info("Registration successfull - WAS DELETED!");
                break;
            }

        }

    }
}
