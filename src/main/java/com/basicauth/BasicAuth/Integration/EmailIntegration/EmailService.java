package com.basicauth.BasicAuth.Integration.EmailIntegration;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailRepository{
    @Autowired
    private JavaMailSender javaMailSender;



    @Value("${spring.mail.username}")
    private String sender;
    @Override
    public String sendSimpleMail(EmailDetails emailDetails) {
        try{
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMsgBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return "Error to send mail";
    }
    public String sendComplicatedMail(EmailDetails details){
        try{
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper;

            System.out.println("Email details" + details.getRecipient());

            mimeMessageHelper = new MimeMessageHelper(msg ,true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody(), true);
            mimeMessageHelper.setSubject(
                    details.getSubject());

            javaMailSender.send(msg);
            return "Mail sent successfully.,..";
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
