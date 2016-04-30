package email;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Based on Jee Vang blog:
 * https://vangjee.wordpress.com/2010/11/02/how-to-create-an-in-memory-pdf-report-and-send-as-an-email-attachment-using-itext-and-java/
 *
 *
 * Uses javax library
 */

public class MailClient {

    /**
     * Sends an email with a PDF attachment.
     */
    public void email(String content, byte[] pdfAsBytes) throws URISyntaxException {
        try {
            Properties prop = new Properties();
            // toURI solves problem with filepaths that contain spaces or UTF-8 characters
            prop.loadFromXML(new FileInputStream(new File(MailClient.class.getClassLoader().getResource("mail.xml").toURI())));


            String smtpHost = prop.getProperty("SMTP_host");

            Properties properties = new Properties();
            properties.put("mail.smtp.host", smtpHost);
            properties.put("mail.smtp.port", Integer.parseInt(prop.getProperty("SMTP_port")));
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.trust", smtpHost);


            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(prop.getProperty("SMTP_username"), prop.getProperty("SMTP_password"));
                }
            });


            //construct the text body part
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(content);


            //construct the pdf body part
            DataSource dataSource = new ByteArrayDataSource(pdfAsBytes, "application/pdf");
            MimeBodyPart pdfBodyPart = new MimeBodyPart();
            pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            pdfBodyPart.setFileName("results.pdf");


            //construct the mime multi part
            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(textBodyPart);
            mimeMultipart.addBodyPart(pdfBodyPart);


            //construct the mime message
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setSender(new InternetAddress(prop.getProperty("sender")));
            mimeMessage.setSubject(prop.getProperty("subject"));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(prop.getProperty("receiver")));
            mimeMessage.setContent(mimeMultipart);


            //send off the email
            Transport.send(mimeMessage);
        } catch (NullPointerException | MessagingException | IOException ex) {
            ex.printStackTrace();
        }
    }
}