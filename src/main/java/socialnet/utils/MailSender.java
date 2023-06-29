package socialnet.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class MailSender {
    @Value("${spring.mail.host}")
    private String mailSmtpHost;

    @Value("${spring.mail.port}")
    private String mailSmtpPort;

    @Value("${spring.mail.username}")
    private String mailAuthUser;

    @Value("${spring.mail.password}")
    private String mailAuthPass;

    public void send(String to, String subject, String text) throws UnsupportedEncodingException, MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", mailSmtpHost);
        props.put("mail.smtp.port", mailSmtpPort);
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.socketFactory.port", mailSmtpPort);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailAuthUser, mailAuthPass);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mailAuthUser, "Zerone SocialNet"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(text, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
