package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.saleitems.*;
import intregatedproject.backend.entities.SaleItem;

import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.users.ForbiddenException;
import intregatedproject.backend.exceptions.users.UnauthorizedException;
import intregatedproject.backend.services.SaleItemService;

import intregatedproject.backend.services.UserService;
import intregatedproject.backend.utils.Token.JwtUtils;

import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip24kk2.sit.kmutt.ac.th"})
public class UserController {
    @Autowired
    private SaleItemService saleItemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/v2/sellers/{id}/sale-items")
    public ResponseEntity<PageSellerDto> getSaleItemsOfSeller(@PathVariable int id,
                                                              @RequestParam("token") String accessToken,
                                                              @RequestParam Integer page,
                                                              @RequestParam(defaultValue = "10") Integer size,
                                                              @RequestParam(defaultValue = "createdOn") String sortField,
                                                              @RequestParam(defaultValue = "asc") String sortDirection) {
        Claims claims = jwtUtils.validateToken(accessToken);
        if (claims == null) {
            throw new UnauthorizedException("Invalid token.");
        }
        User user = userService.getUserById(Integer.valueOf(claims.getId()));
        if (!user.getId().equals(id)) {
            throw new ForbiddenException("Request seller id not matched with id in access token.");
        }
        if (Objects.equals(user.getStatus(), "INACTIVE")) {
            throw new ForbiddenException("Account is not active.");
        }



//        Page<SaleItem> pageResult = saleItemService.getAllSortedAndFiltered(null, null, null, null, null, sortField, sortDirection, page, size);
//        List<SaleItemSellerDto> saleItemsDto = pageResult.getContent().stream()
//                .map(saleItem -> modelMapper.map(saleItem, SaleItemSellerDto.class))
//                .collect(Collectors.toList());
//        PageSellerDto dto = new PageSellerDto();
//        dto.setContent(saleItemsDto);
//        dto.setPage(page);
//        dto.setSize(pageResult.getSize());
//        dto.setSort(sortField + ": " + sortDirection.toUpperCase());
//        dto.setFirst(pageResult.isFirst());
//        dto.setLast(pageResult.isLast());
//        dto.setTotalPages(pageResult.getTotalPages());
//        dto.setTotalElements((int) pageResult.getTotalElements());
        return null;
    }
}   
