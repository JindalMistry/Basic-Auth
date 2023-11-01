package com.basicauth.BasicAuth.Integration.EmailIntegration;

public interface EmailRepository {
    String sendSimpleMail(EmailDetails emailDetails);
}
