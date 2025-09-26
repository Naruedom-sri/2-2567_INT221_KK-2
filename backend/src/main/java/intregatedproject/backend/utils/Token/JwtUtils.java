package intregatedproject.backend.utils.Token;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import intregatedproject.backend.dtos.users.RequestUserRegisterDto;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.verifyEmail.InvalidVerificationTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtils {
    @Value("#{${app.security.jwt.token-max-interval-in-minute}*1000*60}")
    private long MAX_TOKEN_INTERVAL;
    @Value("${app.security.jwt.key-id}")
    private String KEY_ID;
    private RSAKey rsaPrivateJWK;
    @Getter
    private RSAKey rsaPublicJWK;
    private final Key key;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }


    public String generateAccessToken(User user, HttpServletRequest request) {
        String issuer = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return Jwts.builder()
                .setId(user.getId().toString())
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .claim("role", user.getRole())
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(User user, HttpServletRequest request) {
        String issuer = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return Jwts.builder()
                .setId(UUID.randomUUID().toString()) // unique id สำหรับ refresh token
                .setSubject(user.getId().toString()) // ใช้ userId หรือ email ก็ได้
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(key)
                .compact();
    }


    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            System.out.println("JWT validate error: " + e.getMessage());
            return null;
        }
    }

    @PostConstruct
    private void initKeys() {
        try {
            rsaPrivateJWK = new RSAKeyGenerator(2048).keyID(KEY_ID).generate();
            rsaPublicJWK = rsaPrivateJWK.toPublicJWK();
            System.out.println("Public JWK: " + rsaPublicJWK.toJSONString());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }


    public String generateToken(RequestUserRegisterDto user, long ageInMillis, TokenType tokenType) {
        try {
            // 1) ใช้ private key สำหรับเซ็น token
            JWSSigner signer = new RSASSASigner(rsaPrivateJWK);
//            TokenType tokenType = TokenType.ACCESS_TOKEN;
            // 2) สร้าง claims (ข้อมูลที่จะเก็บใน token)
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .claim("userId", user.getId())
                    .claim("email", user.getEmail())
                    .claim("role", user.getRole())
                    .claim("typ", tokenType.toString())
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

            // 4) เซ็น token ด้วย private key
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
                throw new InvalidVerificationTokenException("Invalid JWT signature");
            }
            return signedJWT;
        } catch (JOSEException | ParseException ex) {
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