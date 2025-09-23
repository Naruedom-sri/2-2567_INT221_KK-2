package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.saleitems.*;
import intregatedproject.backend.dtos.users.ResponseBuyerDto;
import intregatedproject.backend.dtos.users.ResponseSellerDto;

import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.users.ForbiddenException;
import intregatedproject.backend.exceptions.users.UnauthorizedException;
import intregatedproject.backend.services.SaleItemService;
import intregatedproject.backend.services.UserService;
import intregatedproject.backend.utils.Token.JwtUtils;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                                                              Authentication authentication,
                                                              @RequestParam(required = false) Integer page,
                                                              @RequestParam(defaultValue = "10") Integer size,
                                                              @RequestParam(defaultValue = "id") String sortField,
                                                              @RequestParam(defaultValue = "asc") String sortDirection) throws BadRequestException {
        if (id <= 0) {
            throw new BadRequestException("Missing or invalid request parameters.");
        }
        if (authentication == null) {
            throw new UnauthorizedException("invalid token.");
        }

        Integer userIdFromToken = Integer.valueOf((String) authentication.getPrincipal());
        User user = userService.getUserById(id);

        if (!user.getId().equals(userIdFromToken)) {
            throw new ForbiddenException("Request seller id not matched with id in access token.");
        }
        if (Objects.equals(user.getStatus(), "INACTIVE")) {
            throw new ForbiddenException("Account is not active.");
        }
        Page<SaleItem> pageResult = saleItemService.getAllSortedAndFiltered(user.getId(), null, null, null, null, null, sortField, sortDirection, page, size);
        List<ResponseSaleItemDto> saleItemsDto = pageResult.getContent().stream()
                .map(saleItem -> modelMapper.map(saleItem, ResponseSaleItemDto.class))
                .collect(Collectors.toList());
        PageSellerDto dto = new PageSellerDto();
        dto.setContent(saleItemsDto);
        dto.setPage(page);
        dto.setSize(pageResult.getSize());
        dto.setSort(sortField + ": " + sortDirection.toUpperCase());
        dto.setFirst(pageResult.isFirst());
        dto.setLast(pageResult.isLast());
        dto.setTotalPages(pageResult.getTotalPages());
        dto.setTotalElements((int) pageResult.getTotalElements());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/v2/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id,
                                              Authentication authentication) {

//        if (id <= 0) {
//            throw new BadRequestException("Missing or invalid request parameters.");
//        }
        if (authentication == null) {
            throw new UnauthorizedException("Invalid token.");
        }

        Integer userIdFromToken = Integer.valueOf((String) authentication.getPrincipal());
        User user = userService.getUserById(id);

        if (!user.getId().equals(userIdFromToken)) {
            throw new ForbiddenException("Request user id not matched with id in access token.");
        }
        if (Objects.equals(user.getStatus(), "INACTIVE")) {
            throw new ForbiddenException("Account is not active.");
        }

        if ("seller".equalsIgnoreCase(user.getRole())) {
            ResponseSellerDto sellerDto = modelMapper.map(user, ResponseSellerDto.class);
            return ResponseEntity.ok(sellerDto);
        } else {
            ResponseBuyerDto buyerDto = modelMapper.map(user, ResponseBuyerDto.class);
            return ResponseEntity.ok(buyerDto);
        }
    }
}