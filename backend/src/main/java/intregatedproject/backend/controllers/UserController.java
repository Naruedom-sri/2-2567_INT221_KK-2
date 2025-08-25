package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.register.RequestRegisterDto;
import intregatedproject.backend.dtos.register.ResponseBuyerDto;
import intregatedproject.backend.dtos.register.ResponseSellerDto;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.services.UserService;
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

//    @GetMapping("/V2/sale-items/register/{id}")
//    public ResponseEntity<ResponseUserDto> getUserById(@PathVariable Integer id) {
//        User user = userService.getUserById(id);
//        ResponseUserDto userDto = modelMapper.map(user, ResponseUserDto.class);
//        return ResponseEntity.ok(userDto);
//    }

    @PostMapping(value = "v2/user/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@ModelAttribute RequestRegisterDto userDto,
                                                         @RequestPart(value = "front",required = false) MultipartFile front,
                                                         @RequestPart(value = "back" ,required = false)  MultipartFile back) {
        if("seller".equalsIgnoreCase(userDto.getRole())){
            userDto.setRole("seller");
            User newUser = userService.registerSeller(userDto,front,back);
            ResponseSellerDto responseSellerDto = modelMapper.map(newUser.getSeller(), ResponseSellerDto.class);
            responseSellerDto.setNickname(newUser.getNickname());
            responseSellerDto.setPassword(newUser.getPassword());
            responseSellerDto.setEmail(newUser.getEmail());
            responseSellerDto.setFullname(newUser.getFullname());
            responseSellerDto.setRole(newUser.getRole());
            responseSellerDto.setStatus(newUser.getStatus());
            return ResponseEntity.ok(responseSellerDto);
        }
        if("buyer".equalsIgnoreCase(userDto.getRole())){
            userDto.setRole("buyer");
            userDto.setMobileNumber(null);
            userDto.setBankAccountNumber(null);
            userDto.setBankName(null);
            userDto.setNationalIdNumber(null);
            User newUser = userService.registerBuyer(userDto);
            ResponseBuyerDto responseUserDto = modelMapper.map(newUser, ResponseBuyerDto.class);
            return ResponseEntity.ok(responseUserDto);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

//    @PostMapping("v2/user/verify-email")
//    public ResponseEntity<?> verifyEmail()

//    @PostMapping(value = "/v2/sale-items/register/seller", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ResponseSellerDto> registerSeller(
//            @RequestPart("seller") RequestSellerDto sellerDto,
//            @RequestPart("front") MultipartFile front,
//            @RequestPart("back")  MultipartFile back) {
//        User user = userService.registerSeller(sellerDto, front, back);
//        ResponseSellerDto resp = modelMapper.map(user.getSeller(), ResponseSellerDto.class);
//        resp.setNickname(user.getNickname());
//        resp.setPassword(user.getPassword());
//        resp.setEmail(user.getEmail());
//        resp.setFullname(user.getFullname());
//        resp.setRole(user.getRole());
//        resp.setStatus(user.getStatus());
//        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
//    }
}
