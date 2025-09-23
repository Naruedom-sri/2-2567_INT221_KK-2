package intregatedproject.backend.services;

import intregatedproject.backend.dtos.users.RequestRegisterDto;
import intregatedproject.backend.entities.Buyer;
import intregatedproject.backend.entities.Seller;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.users.InvalidRoleException;
import intregatedproject.backend.exceptions.users.RequiredFileMissingException;
import intregatedproject.backend.exceptions.users.UnauthorizedException;
import intregatedproject.backend.exceptions.users.UserAlreadyExistsException;
import intregatedproject.backend.exceptions.verifyEmail.EmailAlreadyVerifiedException;
import intregatedproject.backend.repositories.SellerRepository;
import intregatedproject.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
            return userRepository.findById(id).orElseThrow(() -> new UnauthorizedException("User with id " + id + " not found"));
        }

    private void convertToEntityBuyer(RequestRegisterDto userDto, User user, Buyer buyer) {
        user.setNickname(userDto.getNickname());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setFullName(userDto.getFullName());
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

    public User registerBuyer(RequestRegisterDto userDto) {
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
        var newBuyer = new Buyer();
        convertToEntityBuyer(userDto, newUser, newBuyer);
        User savedUser = userRepository.save(newUser);
        newBuyer.setUser(savedUser);
        return savedUser;
    }

    public User registerSeller(RequestRegisterDto userDto, MultipartFile frontFile, MultipartFile backFile) {
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


}
