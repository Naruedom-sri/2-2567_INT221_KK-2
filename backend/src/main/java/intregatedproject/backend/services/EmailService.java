package intregatedproject.backend.services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.verifyEmail.EmailAlreadyVerifiedException;
import intregatedproject.backend.exceptions.verifyEmail.InvalidVerificationTokenException;
import intregatedproject.backend.repositories.UserRepository;
import intregatedproject.backend.utils.token.JwtUtils;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Value("${app.base-url}")
    private String baseUrl;
    @Value("${app.base-url2}")
    private String resetBaseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        String encoded = URLEncoder.encode(token, StandardCharsets.UTF_8);
         String verificationUrl = baseUrl + encoded;
        // เตรียม context สำหรับ Thymeleaf
        Context context = new Context();
        context.setVariable("verificationUrl", verificationUrl);
        context.setVariable("token", encoded);
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

    @Async
    public void sendResetPWEmail(String toEmail, String token) {
        String encoded = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String resetUrl = resetBaseUrl + encoded;

        Context context = new Context();
        context.setVariable("resetUrl", resetUrl);
        context.setVariable("token", encoded);

        String htmlContent = templateEngine.process("resetPasswordEmail", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Reset Your Password");
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send reset password email", e);
        }
    }


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
