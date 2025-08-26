package intregatedproject.backend.services;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendVerificationEmail(String toEmail,String token) {
        String subject = "Verification Email";
        String verificationUrl = "http://localhost:8080/itb-mshop/v2/verify-email?token="+token ;
        String body = "Click the link to verify your account:\n" + verificationUrl;


    }
}
