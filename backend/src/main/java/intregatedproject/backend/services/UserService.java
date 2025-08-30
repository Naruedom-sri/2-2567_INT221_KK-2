package intregatedproject.backend.services;

import intregatedproject.backend.dtos.user.RequestRegisterDto;
import intregatedproject.backend.entities.Buyer;
import intregatedproject.backend.entities.Seller;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.user.InvalidRoleException;
import intregatedproject.backend.exceptions.user.RequiredFileMissingException;
import intregatedproject.backend.exceptions.user.UserAlreadyExistsException;
import intregatedproject.backend.repositories.EmailVerificationTokenRepository;
import intregatedproject.backend.repositories.SaleItemRepository;
import intregatedproject.backend.repositories.SellerRepository;
import intregatedproject.backend.repositories.UserRepository;
//import intregatedproject.backend.utils.Token.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private FileService fileService;
//    @Autowired
//    private JwtUtils jwtUtils;
//    @Autowired
//    private EmailService emailService;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;



    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
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


//    public Map<String, Object> authenticateUser(JwtRequestUser user) {
//        UsernamePasswordAuthenticationToken upat = new
//                UsernamePasswordAuthenticationToken(
//                user.getUsername(), user.getPassword());
//        authenticationManager.authenticate(upat);
//        //Exception occurred (401) if failed
//        UserDetails userDetails = jwtUserDetailsService
//                .loadUserByUsername(user.getUsername());
//        long refreshTokenAgeInMinute = 8 * 60 * 60 * 1000;
//        return Map.of(
//                "access_token", jwtUtils.generateToken(userDetails)
//                , "refresh_token", jwtUtils.generateToken(
//                        userDetails, refreshTokenAgeInMinute, TokenType.REFRESH_TOKEN)
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
//        UserDetails userDetails = jwtUserDetailsService.loadUserById((Long) claims.get("uid"));
//        return Map.of("access_token", jwtUtils.generateToken(userDetails));
//    }

//    @Transactional
//    public User registerSeller(RequestSellerDto sellerDto, List<MultipartFile> files) {
//        // 1) Validate role
//        if (!"seller".equalsIgnoreCase(sellerDto.getRole())) {
//            throw new RuntimeException("Role must be 'seller'");
//        }
//
//        // 2) Validate รูปบัตร (บังคับ 2 ไฟล์: front, back)
//        List<MultipartFile> safeFiles = (files == null) ? List.of() : files;
//        if (safeFiles.size() < 2) {
//            throw new RuntimeException("National ID front and back photos are required (2 files).");
//        }
//
//        // 3) ตรวจ email/nickname ไม่ซ้ำ (ตัวอย่างคร่าว ๆ)
//        if (userRepository.existsByEmail(sellerDto.getEmail())) {
//            throw new RuntimeException("Email already in use.");
//        }
//        if (userRepository.existsByNickname(sellerDto.getNickname())) {
//            throw new RuntimeException("Nickname already in use.");
//        }
//
//        // 4) สร้าง entity และ map ฟิลด์
//        var newUser   = new User();
//        var newSeller = new Seller();
//        convertToEntitySeller(sellerDto, newUser, newSeller);
//
//        // 5) เข้ารหัสรหัสผ่าน + บังคับค่า role/status ฝั่งเซิร์ฟเวอร์
//        newUser.setPassword(passwordEncoder.encode(sellerDto.getPassword()));
//        newUser.setRole("SELLER");
//        newUser.setStatus("INACTIVE");
//
//        // 6) เซฟ User ก่อน (เพื่อให้มี id ใช้เป็น FK)
//        User savedUser = userRepository.save(newUser);
//
//        // 7) อัปโหลดไฟล์ (คืนชื่อไฟล์ใหม่แบบ UUID)
//        List<String> storedNames = fileService.multiStore(safeFiles);
//        // สมมติ FE ส่งลำดับเป็น [front, back]
//        String front = storedNames.get(0);
//        String back  = storedNames.get(1);
//
//        newSeller.setUser(savedUser);
//        newSeller.setNationalIdPhotoFront(front);
//        newSeller.setNationalIdPhotoBack(back);
//
//        sellerRepository.save(newSeller);
//
//        // 8) สร้าง email verification token (อายุ 1 ชม.)
//        String tokenStr = UUID.randomUUID().toString();
//        EmailVerificationToken token = new EmailVerificationToken();
//        token.setUserId(savedUser.getId());
//        token.setToken(tokenStr);
//        token.setExpiryTime(Timestamp.from(Instant.now().plus(1, ChronoUnit.HOURS)));
//        tokenRepository.save(token);
//
//        // 9) ส่งอีเมลยืนยัน
//        emailService.sendVerification(savedUser.getEmail(), tokenStr);
//
//        // 10) คืนผล (หรือ map ไปเป็น Response DTO ตามต้องการ)
//        return savedUser;
//    }

//    private void sendVerificationToken(User user) {
//        String token = UUID.randomUUID().toString();
//        EmailVerificationToken newToken = new EmailVerificationToken();
//        newToken.setToken(token);
//        newToken.setUser(user);
//        newToken.setExpiryTime(LocalDateTime.now().plusHours(24));
//        emailVerificationTokenRepository.save(newToken);
//
//        emailService.sendVerificationEmail(user.getEmail(), token);
//    }

//    private boolean verifyToken(String token) {
//        Optional<EmailVerificationToken> tokenFromOtp = emailVerificationTokenRepository.findByToken(token);
//
//    }
}
