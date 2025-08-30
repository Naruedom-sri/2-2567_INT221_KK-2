package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.user.RequestRegisterDto;
import intregatedproject.backend.dtos.user.ResponseBuyerDto;
import intregatedproject.backend.dtos.user.ResponseSellerDto;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.services.EmailService;
import intregatedproject.backend.services.UserService;
import intregatedproject.backend.utils.Token.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip24kk2.sit.kmutt.ac.th"})
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtils jwtUtils;
//    @GetMapping("/V2/sale-items/register/{id}")
//    public ResponseEntity<ResponseUserDto> getUserById(@PathVariable Integer id) {
//        User user = userService.getUserById(id);
//        ResponseUserDto userDto = modelMapper.map(user, ResponseUserDto.class);
//        return ResponseEntity.ok(userDto);
//    }

    @PostMapping(value = "/v2/users/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@ModelAttribute RequestRegisterDto userDto,
                                      @RequestPart(value = "front", required = false) MultipartFile front,
                                      @RequestPart(value = "back", required = false) MultipartFile back) {
        String token = jwtUtils.generateEmailVerificationToken(userDto, 48);
        if ("seller".equalsIgnoreCase(userDto.getRole())) {
            userDto.setRole("seller");
            User newUser = userService.registerSeller(userDto, front, back);
            ResponseSellerDto responseSellerDto = modelMapper.map(newUser.getSeller(), ResponseSellerDto.class);
            responseSellerDto.setNickname(newUser.getNickname());
            responseSellerDto.setEmail(newUser.getEmail());
            responseSellerDto.setFullname(newUser.getFullname());
            responseSellerDto.setRole(newUser.getRole());
            responseSellerDto.setStatus(newUser.getStatus());

            emailService.sendVerificationEmail(newUser.getEmail(), token);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseSellerDto);
        }
        if ("buyer".equalsIgnoreCase(userDto.getRole())) {
            User newUser = userService.registerBuyer(userDto);
            ResponseBuyerDto responseUserDto = modelMapper.map(newUser, ResponseBuyerDto.class);

            emailService.sendVerificationEmail(newUser.getEmail(), token);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseUserDto);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }



//    @PostMapping("v2/users/verify-email")
//    public ResponseEntity<?> verifyEmail(String jwtToken) {
//
//
//
//
//        return null;
//    }

}
