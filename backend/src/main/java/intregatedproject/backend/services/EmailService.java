package intregatedproject.backend.services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.verifyEmail.EmailAlreadyVerifiedException;
import intregatedproject.backend.exceptions.verifyEmail.InvalidVerificationTokenException;
import intregatedproject.backend.repositories.UserRepository;
import intregatedproject.backend.utils.Token.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

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
        String encoded = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String verificationUrl = "http://localhost:5173/kk2/verify-email/?token=" + encoded;
        String body = "Click the link to verify your account:\n" + verificationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verification Email");
        message.setText(body);
        mailSender.send(message);
    }
    //ใช้ตอน deploy จริง
//        String verificationUrl = "http    ://intproj24.sit.kmutt.ac.th/kk2/verify-email/?token="+token ;

    public User verifyEmail(String jwtToken) {
        try {
            if (jwtToken == null || jwtToken.isBlank()) {
                throw new InvalidVerificationTokenException("Missing token");
            }

            SignedJWT signedJWT = jwtUtil.parseToken(jwtToken); // parseToken จะโยน InvalidVerificationTokenException ถ้าล้ม
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            System.out.println("verifyEmail - claims: " + claims.getClaims());

            String email = claims.getStringClaim("email");
            if (email == null || email.isBlank()) {
                throw new InvalidVerificationTokenException("Token does not contain email claim");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new InvalidVerificationTokenException("User not found for email: " + email));

            if ("ACTIVE".equals(user.getStatus())) {
                // ให้โยน EmailAlreadyVerifiedException ตรงนี้ และอย่าไปกลบมันใน catch ข้างล่าง
                throw new EmailAlreadyVerifiedException("Email is already verified.");
            }

            user.setStatus("ACTIVE");
            return userRepository.save(user);

        } catch (InvalidVerificationTokenException | EmailAlreadyVerifiedException e) {
            // ให้ rethrow เพื่อให้ controller หรือ exception handler จัดการ
            throw e;
        } catch (Exception e) {
            // log stacktrace เพื่อดีบัก แล้วส่ง InvalidVerificationTokenException
            e.printStackTrace();
            throw new InvalidVerificationTokenException("Invalid verification token.");
        }
    }
}
