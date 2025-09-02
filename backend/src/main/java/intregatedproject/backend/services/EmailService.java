package intregatedproject.backend.services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.verifyEmail.EmailAlreadyVerifiedException;
import intregatedproject.backend.exceptions.verifyEmail.InvalidVerificationTokenException;
import intregatedproject.backend.repositories.UserRepository;
import intregatedproject.backend.utils.Token.JwtUtils;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.MessagingException;


@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

//    @Async
//    public void sendVerificationEmail(String toEmail,String token) {
//        String encoded = URLEncoder.encode(token, StandardCharsets.UTF_8);
//        String verificationUrl = "http://localhost:5173/kk2/verify-email/?token=" + encoded;
//        String body = "Click the link to verify your account:\n" + verificationUrl;
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("Verification Email");
//        message.setText(body);
//        mailSender.send(message);
//    }

    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        String encoded = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String verificationUrl = "http://localhost:5173/kk2/verify-email/?token=" + encoded;

        // เตรียม context สำหรับ Thymeleaf
        Context context = new Context();
        context.setVariable("verificationUrl", verificationUrl);
        context.setVariable("token",encoded);
        // ประมวลผล template
        String htmlContent = templateEngine.process("verificationEmail", context);

        // ส่งอีเมลแบบ HTML
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Verification Email");
            helper.setText(htmlContent, true); // true = HTML
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    //ใช้ตอน deploy จริง
//        String verificationUrl = "http    ://intproj24.sit.kmutt.ac.th/kk2/verify-email/?token="+token ;

    public User verifyEmail(String jwtToken) {
        try {
            if (jwtToken == null || jwtToken.isBlank()) {
                throw new InvalidVerificationTokenException("Missing token");
            }

            SignedJWT signedJWT = jwtUtil.parseToken(jwtToken); // parseToken จะโยน Exception ถ้าล้ม
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            System.out.println("verifyEmail - claims: " + claims.getClaims());

            String email = claims.getStringClaim("email");
            if (email == null || email.isBlank()) {
                throw new InvalidVerificationTokenException("Token does not contain email claim");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new InvalidVerificationTokenException("User not found for email: " + email));

            if ("ACTIVE".equals(user.getStatus())) {
                throw new EmailAlreadyVerifiedException("Email is already verified.");
            }

            user.setStatus("ACTIVE");
            return userRepository.save(user);

        } catch (InvalidVerificationTokenException | EmailAlreadyVerifiedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidVerificationTokenException("Invalid verification token.");
        }
    }
}
