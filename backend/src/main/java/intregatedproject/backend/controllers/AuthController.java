package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.user.ResponseBuyerDto;
import intregatedproject.backend.dtos.user.ResponseSellerDto;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.services.EmailService;
import intregatedproject.backend.utils.Token.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip24kk2.sit.kmutt.ac.th"})
public class AuthController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/v2/users/verify-email/")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String jwtToken) {
//        try {
            User user = emailService.verifyEmail(jwtToken);

            if ("SELLER".equalsIgnoreCase(user.getRole())) {
                ResponseSellerDto response = modelMapper.map(user.getSeller(), ResponseSellerDto.class);
                response.setNickname(user.getNickname());
                response.setEmail(user.getEmail());
                response.setFullname(user.getFullname());
                response.setRole(user.getRole());
                response.setStatus(user.getStatus());
                return ResponseEntity.ok(response);
            } else {
                ResponseBuyerDto response = modelMapper.map(user, ResponseBuyerDto.class);
                return ResponseEntity.ok(response);
            }

//        } catch (EmailAlreadyVerifiedException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("Email already verified");
//        } catch (InvalidVerificationTokenException e) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", "Invalid verification token"));
//        }
    }
}

