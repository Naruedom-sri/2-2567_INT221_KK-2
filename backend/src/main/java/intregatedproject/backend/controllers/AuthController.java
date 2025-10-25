package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.authentications.RequestLogin;
import intregatedproject.backend.dtos.authentications.ResponseToken;
import intregatedproject.backend.dtos.users.RequestUserRegisterDto;
import intregatedproject.backend.dtos.users.ResponseBuyerRegisterDto;
import intregatedproject.backend.dtos.users.ResponseSellerRegisterDto;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.users.ForbiddenException;
import intregatedproject.backend.exceptions.users.UnauthorizedException;
import intregatedproject.backend.repositories.UserRepository;
import intregatedproject.backend.services.EmailService;
import intregatedproject.backend.services.UserService;
import intregatedproject.backend.utils.token.JwtUtils;
import intregatedproject.backend.utils.token.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/itb-mshop")
public class AuthController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/v2/users/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String jwtToken) {
        User user = emailService.verifyEmail(jwtToken);

        if ("SELLER".equalsIgnoreCase(user.getRole())) {
            ResponseSellerRegisterDto response = modelMapper.map(user.getSeller(), ResponseSellerRegisterDto.class);
            response.setNickname(user.getNickname());
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRole(user.getRole());
            response.setStatus(user.getStatus());
            return ResponseEntity.ok(response);
        } else {
            ResponseBuyerRegisterDto response = modelMapper.map(user, ResponseBuyerRegisterDto.class);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping(value = "/v2/auth/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@ModelAttribute RequestUserRegisterDto userDto,
                                      @RequestPart(value = "front", required = false) MultipartFile front,
                                      @RequestPart(value = "back", required = false) MultipartFile back) {
        String token = jwtUtil.generateToken(userDto, 48, TokenType.ACCESS_TOKEN);

        if ("seller".equalsIgnoreCase(userDto.getRole())) {
            userDto.setRole("seller");
            User newUser = userService.registerSeller(userDto, front, back);
            ResponseSellerRegisterDto responseSellerDto = modelMapper.map(newUser.getSeller(), ResponseSellerRegisterDto.class);
            responseSellerDto.setNickname(newUser.getNickname());
            responseSellerDto.setEmail(newUser.getEmail());
            responseSellerDto.setFullName(newUser.getFullName());
            responseSellerDto.setRole(newUser.getRole());
            responseSellerDto.setStatus(newUser.getStatus());

            emailService.sendVerificationEmail(newUser.getEmail(), token);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseSellerDto);
        }
        if ("buyer".equalsIgnoreCase(userDto.getRole())) {
            User newUser = userService.registerBuyer(userDto);
            ResponseBuyerRegisterDto responseUserDto = modelMapper.map(newUser, ResponseBuyerRegisterDto.class);
            emailService.sendVerificationEmail(newUser.getEmail(), token);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseUserDto);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


        @PostMapping("/v2/auth/login")
        public ResponseEntity<ResponseToken> loginUser(@Valid @RequestBody RequestLogin requestLogin, HttpServletRequest httpServletRequest) {
            List<User> userList = userService.getAllUsers();
            ResponseToken responseToken = new ResponseToken();
            for (User user : userList) {
                if (user.getEmail().equals(requestLogin.getEmail())
                        && user.getPassword().equals(requestLogin.getPassword())) {

                    if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
                        String access_token = jwtUtil.generateAccessToken(user, httpServletRequest);
                        String refresh_token = jwtUtil.generateRefreshToken(user, httpServletRequest);

                        responseToken.setAccess_token(access_token);

                        // refresh token อยู่ใน HttpOnly cookie
                        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refresh_token)
                                .httpOnly(true)       // JS อ่านไม่ได้
                                .secure(false)         // เฉพาะ HTTPS (true)
                                .path("/") // จะส่ง cookie เฉพาะตอนเรียก refresh endpoint
                                .maxAge(30 * 24 * 60 * 60) // อายุ 30 วัน
                                .sameSite("Strict")    // ป้องกัน CSRF
                                .build();

                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                                .body(responseToken);
                    } else {
                        throw new ForbiddenException("You need to activate your account before signing in.");
                    }
                }
            }
            throw new UnauthorizedException("Email or password is incorrect.");
        }

    @PostMapping("/v2/auth/logout")
    public ResponseEntity<ResponseToken> logoutUser(Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException("invalid token.");
        }
        Integer userIdFromToken = Integer.valueOf((String) authentication.getPrincipal());
        User user = userService.getUserById(userIdFromToken);
        if (user == null) {
            throw new UnauthorizedException("User not found.");
        }
        if (user.getStatus().equals("INACTIVE")) {
            throw new ForbiddenException("Account is not active.");
        }
        // ใช้การตั้งค่าเดียวกับตอน login
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)        // ต้องเหมือนกับตอน login
                .path("/")           // ต้องเหมือนกับตอน login
                .maxAge(0)           // delete immediately
                .sameSite("Strict")     // ต้องเหมือนกับตอน login
                .build();

        // ใช้วิธีเดียวกับตอน login
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();

    }

    @PostMapping("/v2/auth/refresh")
    public ResponseEntity<ResponseToken> refreshToken(HttpServletRequest request) throws BadRequestException {
        ResponseToken responseToken = new ResponseToken();
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            throw new BadRequestException("No refresh token.");
        }

        Claims claims = jwtUtil.validateToken(refreshToken);
        if (claims == null) {
            throw new UnauthorizedException("Invalid refresh token.");
        }

        User user = userService.getUserById(Integer.valueOf(claims.getSubject()));
        if (user == null) {
            throw new UnauthorizedException("User not found.");
        }
        if (user.getStatus().equals("INACTIVE")) {
            throw new ForbiddenException("User is not active.");
        }
        String newAccessToken = jwtUtil.generateAccessToken(user, request);
        responseToken.setAccess_token(newAccessToken);
        return ResponseEntity.ok(responseToken);
    }

    @PostMapping("/v2/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Email not found."));
        String token = jwtUtil.generateTokenForResetPW(email);
        emailService.sendResetPWEmail(email, token);
        return ResponseEntity.ok("send email success");
    }

    @PutMapping("/v2/auth/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String password, @RequestParam("token") String jwtToken){
        String email = jwtUtil.extractEmail(jwtToken);

        userService.forgotPassword(password, email);

        return ResponseEntity.noContent().build();
    }
}

