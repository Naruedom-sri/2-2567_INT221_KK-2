package intregatedproject.backend.services;

import intregatedproject.backend.dtos.user.RequestJwtUser;
import intregatedproject.backend.dtos.user.RequestRegisterDto;
import intregatedproject.backend.dtos.user.ResponseSellerDto;
import intregatedproject.backend.entities.Buyer;
import intregatedproject.backend.entities.Seller;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.user.InvalidRoleException;
import intregatedproject.backend.exceptions.user.RequiredFileMissingException;
import intregatedproject.backend.exceptions.user.UserAlreadyExistsException;
import intregatedproject.backend.exceptions.verifyEmail.InvalidVerificationTokenException;
import intregatedproject.backend.repositories.EmailVerificationTokenRepository;
import intregatedproject.backend.repositories.SaleItemRepository;
import intregatedproject.backend.repositories.SellerRepository;
import intregatedproject.backend.repositories.UserRepository;
//import intregatedproject.backend.utils.Token.JwtUtils;
import intregatedproject.backend.utils.Token.JwtUtils;
import intregatedproject.backend.utils.Token.TokenType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private FileService fileService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ModelMapper modelMapper;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        if (user.getRole().equalsIgnoreCase("seller")) {
            ResponseSellerDto sellerDto = modelMapper.map(user.getSeller(), ResponseSellerDto.class);
            sellerDto.setNickname(user.getNickname());
            sellerDto.setEmail(user.getEmail());
            sellerDto.setFullname(user.getFullname());
            sellerDto.setRole(user.getRole());
            sellerDto.setStatus(user.getStatus());
            return user;
        } else if (user.getRole().equalsIgnoreCase("buyer")) {
            return user;
        }

        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    private void convertToEntityBuyer(RequestRegisterDto userDto, User user, Buyer buyer) {
        user.setNickname(userDto.getNickname());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setFullname(userDto.getFullname());
        user.setRole(userDto.getRole());
        user.setStatus(userDto.getStatus());
        user.setBuyer(buyer);
        buyer.setUser(user);
        userDto.setRole("buyer");
        userDto.setMobileNumber(null);
        userDto.setBankAccountNumber(null);
        userDto.setBankName(null);
        userDto.setNationalIdNumber(null);
    }

    private void convertToEntitySeller(RequestRegisterDto userDto, User user, Seller seller) {
        user.setNickname(userDto.getNickname());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setFullname(userDto.getFullname());
        user.setRole(userDto.getRole());
        user.setStatus(userDto.getStatus());
        user.setSeller(seller);
        seller.setUser(user);
        seller.setMobileNumber(userDto.getMobileNumber());
        seller.setBankAccountNumber(userDto.getBankAccountNumber());
        seller.setBankName(userDto.getBankName());
        seller.setNationalIdNumber(userDto.getNationalIdNumber());
    }

//    public User registerBuyer(RequestRegisterDto userDto) {
//        if (userDto.getId() != null && userRepository.existsById(userDto.getId())) {
//            throw new RuntimeException("Buyer with id " + userDto.getId() + " already exists");
//        }
//        if (!"buyer".equalsIgnoreCase(userDto.getRole())) {
//            throw new RuntimeException("Role must be 'buyer'");
//        }
//        var newUser = new User();
//        var newBuyer = new Buyer();
//        convertToEntityBuyer(userDto,newUser,newBuyer);
//
//        User savedUser = userRepository.save(newUser);
//        newBuyer.setUser(savedUser);
//        return savedUser;
//    }
//
//    public User registerSeller(RequestRegisterDto userDto, MultipartFile frontFile, MultipartFile backFile) {
//        if (userDto.getId() != null && sellerRepository.existsById(userDto.getId())) {
//            throw new RuntimeException("Seller with id " + userDto.getId() + " already exists");
//        }
//
//        if (!"seller".equalsIgnoreCase(userDto.getRole())) {
//            throw new RuntimeException("Role must be 'seller'");
//
//        }if (frontFile == null || backFile == null) {
//            throw new RuntimeException("National ID front/back files are required");
//        }
//
//        var newUser = new User();
//        var newSeller = new Seller();
//        convertToEntitySeller(userDto,newUser,newSeller);
//
//        String storedFront = fileService.store(frontFile);
//        String storedBack = fileService.store(backFile);
//
//
//        newSeller.setNationalIdPhotoFront(storedFront);
//        newSeller.setNationalIdPhotoBack(storedBack);
//
//        User savedUser = userRepository.save(newUser);
//
//
//        newSeller.setUser(savedUser);
//        sellerRepository.save(newSeller);
//
//        return savedUser;
//    }

    public User registerBuyer(RequestRegisterDto userDto) {
        if (userDto.getId() != null && userRepository.existsById(userDto.getId())) {
            throw new UserAlreadyExistsException("Buyer with id " + userDto.getId() + " already exists");
        }
        if (!"buyer".equalsIgnoreCase(userDto.getRole())) {
            throw new InvalidRoleException("Role must be 'buyer'");
        }

        var newUser = new User();
        var newBuyer = new Buyer();
        convertToEntityBuyer(userDto, newUser, newBuyer);

        User savedUser = userRepository.save(newUser);
        newBuyer.setUser(savedUser);
        return savedUser;
    }

    public User registerSeller(RequestRegisterDto userDto, MultipartFile frontFile, MultipartFile backFile) {
        if (userDto.getId() != null && sellerRepository.existsById(userDto.getId())) {
            throw new UserAlreadyExistsException("Seller with id " + userDto.getId() + " already exists");
        }
        if (!"seller".equalsIgnoreCase(userDto.getRole())) {
            throw new InvalidRoleException("Role must be 'seller'");
        }
        if (frontFile == null || backFile == null) {
            throw new RequiredFileMissingException("National ID front/back files are required");
        }

        var newUser = new User();
        var newSeller = new Seller();
        convertToEntitySeller(userDto, newUser, newSeller);

        String storedFront = fileService.store(frontFile);
        String storedBack = fileService.store(backFile);

        newSeller.setNationalIdPhotoFront(storedFront);
        newSeller.setNationalIdPhotoBack(storedBack);

        User savedUser = userRepository.save(newUser);
        newSeller.setUser(savedUser);
        sellerRepository.save(newSeller);

        return savedUser;
    }


//    public Map<String, Object> authenticateUser(RequestJwtUser user) {
//        UsernamePasswordAuthenticationToken upat = new
//                UsernamePasswordAuthenticationToken(
//                user.getEmail(), user.getPassword());
//        authenticationManager.authenticate(upat);
//        //Exception occurred (401) if failed
//        User user1 = userRepository.findByEmail(user.getEmail())
//                .orElseThrow(() -> new InvalidVerificationTokenException("User not found for email: " + user.getEmail()));
//        RequestRegisterDto userDto = modelMapper.map(user1, RequestRegisterDto.class);
//        long refreshTokenAgeInMinute = 8 * 60 * 60 * 1000;
//        return Map.of(
//                "access_token", jwtUtils.generateToken(userDto, 48,TokenType.ACCESS_TOKEN)
//                , "refresh_token", jwtUtils.generateToken(
//                        userDto, refreshTokenAgeInMinute, TokenType.REFRESH_TOKEN)
//        );
//    }
//
//    public Map<String, Object> refreshToken(String refreshToken) {
//        jwtUtils.verifyToken(refreshToken);
//        Map<String, Object> claims = jwtUtils.getJWTClaimsSet(refreshToken);
//        jwtUtils.isExpired(claims);
//        if (!jwtUtils.isValidClaims(claims) || !"REFRESH_TOKEN".equals(claims.get("typ"))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
//        }
//        User user = getUserById((Integer) claims.get("uid"));
//        RequestRegisterDto userDetails = modelMapper.map(user, RequestRegisterDto.class);
//        return Map.of("access_token", jwtUtils.generateToken(userDetails,48,TokenType.ACCESS_TOKEN));
//    }
}
