package intregatedproject.backend.utils.Token;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import intregatedproject.backend.dtos.user.RequestRegisterDto;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.verifyEmail.InvalidVerificationTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {
    @Value("#{${app.security.jwt.token-max-interval-in-minute}*1000*60}")
    private long MAX_TOKEN_INTERVAL;
    @Value("${app.security.jwt.key-id}")
    private String KEY_ID;
    private RSAKey rsaPrivateJWK;
    private RSAKey rsaPublicJWK;
    private User user;

    public RSAKey getRsaPublicJWK() {
        return this.rsaPublicJWK;
    }

    public JwtUtils() {
        try {
            rsaPrivateJWK = new RSAKeyGenerator(2048).keyID(KEY_ID).generate();
            rsaPublicJWK = rsaPrivateJWK.toPublicJWK();
            System.out.println(rsaPublicJWK.toJSONString());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
//    public String generateToken(UserDetails user) {
//        return generateToken(user,MAX_TOKEN_INTERVAL,TokenType.ACCESS_TOKEN);
//    }

//    public String generateToken(UserDetails user
//            , long ageInMinute, TokenType tokenType) {
//        try {
//            JWSSigner signer = new RSASSASigner(rsaPrivateJWK);
//            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
//                    .subject(user.getUsername())
//                    .issuer("https://int204.sit.kmutt.ac.th")
//                    .expirationTime(new Date(new Date().getTime() + ageInMinute))
//                    .issueTime(new Date(new Date().getTime()))
//                    .claim("authorities", user.getAuthorities())
//                    .claim("typ", tokenType.toString())
//                    .claim("uid", ((AuthUserDetail) user).getId())
//                    .build();
//            SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
//                    .keyID(rsaPrivateJWK.getKeyID()).build(), claimsSet);
//            signedJWT.sign(signer);
//            return signedJWT.serialize();
//        } catch (JOSEException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public String generateEmailVerificationToken(RequestRegisterDto user, long ageInMillis) {
        try {
            // 1) ใช้ private key สำหรับเซ็น token
            JWSSigner signer = new RSASSASigner(rsaPrivateJWK);

            // 2) สร้าง claims (ข้อมูลที่จะเก็บใน token)
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .claim("userId", user.getId())
                    .claim("email", user.getEmail())
                    .claim("role", user.getRole())
                    .subject(user.getEmail()) // subject = email
                    .issuer("http://localhost:8080/itb-mshop/v2/users/register") // ออกโดยใคร
                    .expirationTime(new Date(System.currentTimeMillis() + MAX_TOKEN_INTERVAL * ageInMillis)) // วันหมดอายุ
                    .issueTime(new Date()) // เวลาออก token
                    .claim("typ", "VERIFY_EMAIL") // custom claim เอาไว้ระบุว่า token นี้ใช้ทำ email verify
                    .build();

            // 3) สร้าง JWT object พร้อม header และ payload
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(rsaPrivateJWK.getKeyID()) // ใส่ key id
                            .build(),
                    claimsSet
            );

            // 4) เซ็น token ด้วย private key (RS256)
            signedJWT.sign(signer);

            // 5) แปลงเป็น String (JWT format: header.payload.signature)
            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public SignedJWT parseToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);

            if (!signedJWT.verify(verifier)) {
                // เดิม: throw new RuntimeException("Invalid JWT signature");
                throw new InvalidVerificationTokenException("Invalid JWT signature");
            }
            return signedJWT;
        } catch (JOSEException | ParseException ex) {
            // เดิม: throw new RuntimeException(...)
            throw new InvalidVerificationTokenException("Invalid JWT (can't parse/verify)");
        }
    }

    public void verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);
            boolean passed = signedJWT.verify(verifier);
            System.out.println("Token verified: " + passed);
            if (!passed) {
                // เดิม: throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Verify token failed");
                throw new InvalidVerificationTokenException("Verify token failed");
            }
        } catch (JOSEException | ParseException ex) {
            // เดิม: ResponseStatusException
            throw new InvalidVerificationTokenException("Verified Error, Invalid JWT");
        }
    }

    public Map<String, Object> getJWTClaimsSet(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getClaims();
        } catch (ParseException ex) {
            // เดิม: ResponseStatusException
            throw new InvalidVerificationTokenException("Invalid JWT (Can't parsed)");
        }
    }

    public boolean isExpired(Map<String, Object> jwtClaims) {
        Date expDate = (Date) jwtClaims.get("exp");
        return expDate.before(new Date());
    }

    public boolean isValidClaims(Map<String, Object> jwtClaims) {
        System.out.println(jwtClaims);
        return jwtClaims.containsKey("iat") && "http://localhost:8080/itb-mshop/v2/users/register"
                .equals(jwtClaims.get("iss")) && jwtClaims.containsKey("uid") &&
                (Long) jwtClaims.get("uid") > 0;
    }
}