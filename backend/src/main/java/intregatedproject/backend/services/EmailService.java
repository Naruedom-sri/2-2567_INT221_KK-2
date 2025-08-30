package intregatedproject.backend.services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.verifyEmail.EmailAlreadyVerifiedException;
import intregatedproject.backend.exceptions.verifyEmail.InvalidVerificationTokenException;
import intregatedproject.backend.repositories.UserRepository;
import intregatedproject.backend.utils.Token.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JwtUtils jwtUtil;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendVerificationEmail(String toEmail,String token) {
        String subject = "Verification Email";
        String verificationUrl = "http://localhost:5173/kk2/verify-email/?token="+token ;
        //ใช้ตอน deploy จริง
//        String verificationUrl = "http    ://intproj24.sit.kmutt.ac.th/kk2/verify-email/?token="+token ;
        String body = "Click the link to verify your account:\n" + verificationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public User verifyEmail(String jwtToken) {
        try {
            SignedJWT signedJWT = jwtUtil.parseToken(jwtToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            Integer userId = claims.getIntegerClaim("userId");
            String email = claims.getStringClaim("email");

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new InvalidVerificationTokenException("User not found."));

            if (!user.getEmail().equals(email)) {
                throw new InvalidVerificationTokenException("Email does not match.");
            }

            if ("ACTIVE".equals(user.getStatus())) {
                throw new EmailAlreadyVerifiedException("Email is already verified.");
            }

            user.setStatus("ACTIVE");
            return userRepository.save(user);

        } catch (Exception e) {
            throw new InvalidVerificationTokenException("Invalid verification token.");
        }
    }
}
