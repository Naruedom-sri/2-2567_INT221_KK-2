package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.authentications.RequestLogin;
import intregatedproject.backend.dtos.authentications.ResponseLogin;
import intregatedproject.backend.dtos.users.ResponseBuyerDto;
import intregatedproject.backend.dtos.users.ResponseSellerDto;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.users.UnauthorizedException;
import intregatedproject.backend.services.EmailService;
import intregatedproject.backend.services.UserService;
import intregatedproject.backend.utils.Token.JwtUtils;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip24kk2.sit.kmutt.ac.th"})
public class AuthController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/v2/users/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String jwtToken) {
        User user = emailService.verifyEmail(jwtToken);

        if ("SELLER".equalsIgnoreCase(user.getRole())) {
            ResponseSellerDto response = modelMapper.map(user.getSeller(), ResponseSellerDto.class);
            response.setNickname(user.getNickname());
            response.setEmail(user.getEmail());
            response.setFullname(user.getFullName());
            response.setRole(user.getRole());
            response.setStatus(user.getStatus());
            return ResponseEntity.ok(response);
        } else {
            ResponseBuyerDto response = modelMapper.map(user, ResponseBuyerDto.class);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/v2/users/authentications")
    public ResponseEntity<ResponseLogin> loginUser(@Valid @RequestBody RequestLogin requestLogin) {
        List<User> userList = userService.getAllUsers();
        ResponseLogin responseLogin = new ResponseLogin();
        userList.forEach(user -> {

//            System.out.println(PasswordUtils.matches(requestLogin.getPassword(), user.getPassword()));
//            if (PasswordUtils.matches(requestLogin.getPassword(), user.getPassword()) && user.getEmail().equals(requestLogin.getEmail())) {
//                String access_token = jwtUtil.generateAccessToken(user.getNickname());
//                String refresh_token = jwtUtil.generateRefreshToken(user.getNickname());
//                responseLogin.setAccess_token(access_token);
//                responseLogin.setRefresh_token(refresh_token);
//            }
            if (user.getPassword().equals(requestLogin.getPassword()) && user.getEmail().equals(requestLogin.getEmail())) {
                String access_token = jwtUtil.generateAccessToken(user.getNickname());
                String refresh_token = jwtUtil.generateRefreshToken(user.getNickname());
                responseLogin.setAccess_token(access_token);
                responseLogin.setRefresh_token(refresh_token);
            }
        });
        if (responseLogin.getAccess_token() == null || responseLogin.getRefresh_token() == null) {
            throw new UnauthorizedException("Username or password is incorrect");
        }
        return ResponseEntity.ok(responseLogin);
    }
}

