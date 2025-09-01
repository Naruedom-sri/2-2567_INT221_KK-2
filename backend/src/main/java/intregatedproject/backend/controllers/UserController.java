package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.users.RequestRegisterDto;
import intregatedproject.backend.dtos.users.ResponseBuyerDto;
import intregatedproject.backend.dtos.users.ResponseSellerDto;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.services.EmailService;
import intregatedproject.backend.services.UserService;
import intregatedproject.backend.utils.Token.JwtUtils;
import intregatedproject.backend.utils.Token.TokenType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

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


    @PostMapping(value = "/v2/users/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@ModelAttribute RequestRegisterDto userDto,
                                      @RequestPart(value = "front", required = false) MultipartFile front,
                                      @RequestPart(value = "back", required = false) MultipartFile back) {
        String token = jwtUtils.generateToken(userDto, 48, TokenType.ACCESS_TOKEN);
        System.out.println("Generated token: " + token);

        if ("seller".equalsIgnoreCase(userDto.getRole())) {
            userDto.setRole("seller");
            User newUser = userService.registerSeller(userDto, front, back);
            ResponseSellerDto responseSellerDto = modelMapper.map(newUser.getSeller(), ResponseSellerDto.class);
            responseSellerDto.setNickname(newUser.getNickname());
            responseSellerDto.setEmail(newUser.getEmail());
            responseSellerDto.setFullname(newUser.getFullName());
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



}
