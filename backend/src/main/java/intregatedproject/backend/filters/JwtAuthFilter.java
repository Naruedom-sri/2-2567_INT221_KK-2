package intregatedproject.backend.filters;

import intregatedproject.backend.utils.Token.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

//    @Autowired
//    private JwtUtils jwtUtils;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//        String header = request.getHeader("Authorization");
//
//        if (header != null && header.startsWith("Bearer ")) {
//            String token = header.substring(7);
//
//            try {
//                Map<String, Object> claims = jwtUtils.getJWTClaimsSet(token);
//
//                if (!jwtUtils.isExpired(claims)) {
//                    String username = (String) claims.get("sub");
//
//                    UsernamePasswordAuthenticationToken auth =
//                            new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
//
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }
//            } catch (Exception e) {
//                System.out.println("Invalid JWT: " + e.getMessage());
//            }
//        }
//        filterChain.doFilter(request, response);
//    }

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            Claims claims = jwtUtils.validateToken(token); // ✅ ใช้ method ที่คุณให้มา
            if (claims != null) {
                try {
                    // ดึง userId จาก claims
                    // สมมติว่าตอน generate JWT คุณ setId(userId) เอาไว้
                    String userId = claims.getId();

                    if (userId != null) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (Exception e) {
                    System.out.println("Invalid JWT claims: " + e.getMessage());
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
