package intregatedproject.backend.services;

import intregatedproject.backend.dtos.users.*;
import intregatedproject.backend.dtos.users.RequestUserRegisterDto;
import intregatedproject.backend.entities.Seller;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.users.InvalidRoleException;
import intregatedproject.backend.exceptions.users.RequiredFileMissingException;
import intregatedproject.backend.exceptions.users.UnauthorizedException;
import intregatedproject.backend.exceptions.users.UserAlreadyExistsException;
import intregatedproject.backend.exceptions.verifyEmail.EmailAlreadyVerifiedException;
import intregatedproject.backend.repositories.SellerRepository;
import intregatedproject.backend.repositories.UserRepository;
import org.apache.coyote.BadRequestException;
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
    private FileService fileService;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    private void convertToEntityBuyer(RequestUserRegisterDto userDto, User user) {
        user.setNickname(userDto.getNickname());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setFullName(userDto.getFullName());
        user.setRole(userDto.getRole());
        user.setStatus(userDto.getStatus());
        userDto.setRole("buyer");
        userDto.setMobileNumber(null);
        userDto.setBankAccountNumber(null);
        userDto.setBankName(null);
        userDto.setNationalIdNumber(null);
    }

    private void convertToEntitySeller(RequestUserRegisterDto userDto, User user, Seller seller) {
        user.setNickname(userDto.getNickname());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setFullName(userDto.getFullName());
        user.setRole(userDto.getRole());
        user.setStatus(userDto.getStatus());
        user.setSeller(seller);
        seller.setUser(user);
        seller.setMobileNumber(userDto.getMobileNumber());
        seller.setBankAccountNumber(userDto.getBankAccountNumber());
        seller.setBankName(userDto.getBankName());
        seller.setNationalIdNumber(userDto.getNationalIdNumber());
    }

    public User registerBuyer(RequestUserRegisterDto userDto) {
        List<User> users = getAllUsers();
        users.forEach(user -> {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new EmailAlreadyVerifiedException("Email Already Exists");
            }
        });
        if (userDto.getId() != null && userRepository.existsById(userDto.getId())) {
            throw new UserAlreadyExistsException("Buyer with id " + userDto.getId() + " already exists");
        }
        if (!"buyer".equalsIgnoreCase(userDto.getRole())) {
            throw new InvalidRoleException("Role must be 'buyer'");
        }
        var newUser = new User();
        convertToEntityBuyer(userDto, newUser);

        return userRepository.save(newUser);
    }

    public User registerSeller(RequestUserRegisterDto userDto, MultipartFile frontFile, MultipartFile backFile) {
        List<User> users = getAllUsers();
        users.forEach(user -> {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new EmailAlreadyVerifiedException("Email Already Exists");
            }
        });
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

    public ResponseBuyerDto updateBuyerProfile(Integer id, RequestUserEditDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setNickname(request.getNickname());
        user.setFullName(request.getFullName());
        // role ไม่ต้องแก้ ให้ใช้ของเดิม (buyer)

        User saved = userRepository.save(user);

        ResponseBuyerDto dto = new ResponseBuyerDto();
        dto.setNickname(saved.getNickname());
        dto.setEmail(saved.getEmail());
        dto.setFullName(saved.getFullName());
        dto.setRole(saved.getRole());
        return dto;
    }

    public ResponseSellerDto updateSellerProfile(Integer id, RequestUserEditDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setNickname(request.getNickname());
        user.setFullName(request.getFullName());

        User saved = userRepository.save(user);

        ResponseSellerDto dto = new ResponseSellerDto();
        dto.setNickname(saved.getNickname());
        dto.setEmail(saved.getEmail());
        dto.setFullName(saved.getFullName());
        dto.setRole(saved.getRole());
        dto.setMobileNumber(saved.getSeller().getMobileNumber());
        dto.setBankAccountNumber(saved.getSeller().getBankAccountNumber());
        dto.setBankName(saved.getSeller().getBankName());
        return dto;
    }


    public void changePassword(Integer id, RequestChangePasswordDto request) throws BadRequestException {
        User user = getUserById(id);
        if(user == null) {
            throw new UnauthorizedException("User not found.");
        }
        if(!user.getPassword().equals(request.getOldPassword())) {
            throw new BadRequestException("Current password is incorrect.");
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }


    public void forgotPassword(String password,String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found."));
        user.setPassword(password);
        userRepository.save(user);
    }
}
