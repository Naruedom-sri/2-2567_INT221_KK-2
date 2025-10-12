package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.orders.PageBuyerOrder;
import intregatedproject.backend.dtos.orders.PageSellerOrder;
import intregatedproject.backend.dtos.orders.ResponseOrderDto;
import intregatedproject.backend.dtos.saleitems.*;
import intregatedproject.backend.dtos.users.RequestUserEditDto;
import intregatedproject.backend.dtos.users.ResponseBuyerDto;
import intregatedproject.backend.dtos.users.ResponseSellerDto;

import intregatedproject.backend.entities.Order;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.entities.Seller;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.users.ForbiddenException;
import intregatedproject.backend.exceptions.users.UnauthorizedException;
import intregatedproject.backend.repositories.OrderRepository;
import intregatedproject.backend.services.OrderService;
import intregatedproject.backend.services.SaleItemService;
import intregatedproject.backend.services.UserService;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/itb-mshop")
public class UserController {
    @Autowired
    private SaleItemService saleItemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;


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

    @PostMapping(value = "/v2/sellers/{id}/sale-items",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseSaleItemImageDtoV2> createSaleItem(
            Authentication authentication,
            @PathVariable int id,
            @ModelAttribute RequestSaleItemDto saleItemCreateDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws BadRequestException {

        if (id <= 0) {
            throw new BadRequestException("Missing or invalid request parameters.");
        }
        if (authentication == null) {
            throw new UnauthorizedException("invalid token.");
        }

        Integer userIdFromToken = Integer.valueOf((String) authentication.getPrincipal());
        User user = userService.getUserById(id);
        if (user == null) {
            throw new UnauthorizedException("User not found.");
        }
        if (!user.getId().equals(userIdFromToken)) {
            throw new ForbiddenException("Request seller id not matched with id in access token.");
        }
        if (Objects.equals(user.getStatus(), "INACTIVE")) {
            throw new ForbiddenException("Account is not active.");
        }
        if (!"SELLER".equalsIgnoreCase(user.getRole())) {
            throw new ForbiddenException("User is not a seller.");
        }

        SaleItem saleitem = saleItemService.createSaleItemImage(saleItemCreateDTO, images, id);
        ResponseSaleItemImageDtoV2 response = modelMapper.map(saleitem, ResponseSaleItemImageDtoV2.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/v2/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id,
                                              Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException("Invalid token.");
        }

        Integer userIdFromToken = Integer.valueOf((String) authentication.getPrincipal());
        User user = userService.getUserById(id);
        if (user == null) {
            throw new UnauthorizedException("User not found.");
        }
        if (!user.getId().equals(userIdFromToken)) {
            throw new ForbiddenException("Request user id not matched with id in access token.");
        }
        if (Objects.equals(user.getStatus(), "INACTIVE")) {
            throw new ForbiddenException("Account is not active.");
        }

        if ("seller".equalsIgnoreCase(user.getRole())) {
            Seller seller = userService.getUserById(id).getSeller();
            ResponseSellerDto sellerDto = new ResponseSellerDto();
            modelMapper.map(user, sellerDto);     // map ข้อมูลจาก User
            modelMapper.map(seller, sellerDto); // map ข้อมูลจาก Seller
            return ResponseEntity.ok(sellerDto);

        } else {
            ResponseBuyerDto buyerDto = modelMapper.map(user, ResponseBuyerDto.class);
            return ResponseEntity.ok(buyerDto);
        }
    }

    @PutMapping("/v2/users/{id}")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Integer id,
            Authentication authentication,
            @RequestBody RequestUserEditDto request) {

        if (authentication == null) {
            throw new UnauthorizedException("Invalid token.");
        }

        // หา user จาก DB ก่อน
        User user = userService.getUserById(id);
        if (user == null) {
            throw new UnauthorizedException("User not found.");
        }
        // เช็ค role จาก DB ไม่ใช่จาก request
        if ("buyer".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.ok(userService.updateBuyerProfile(id, request));
        } else if ("seller".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.ok(userService.updateSellerProfile(id, request));
        } else {
            return ResponseEntity.badRequest().body("Unsupported role");
        }
    }


    @GetMapping("/v2/users/{id}/orders")
    public ResponseEntity<PageBuyerOrder> getOrdersByUserId(@PathVariable int id,
                                                    Authentication authentication,
                                                    @RequestParam(required = false) Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    @RequestParam(defaultValue = "id") String sortField,
                                                    @RequestParam(defaultValue = "asc") String sortDirection) throws BadRequestException {
        if (authentication == null) {
            throw new UnauthorizedException("Invalid token.");
        }

        Integer userIdFromToken = Integer.valueOf((String) authentication.getPrincipal());
        User user = userService.getUserById(id);
        if (user == null) {
            throw new UnauthorizedException("User not found.");
        }
        if (!user.getId().equals(userIdFromToken)) {
            throw new ForbiddenException("Request user id not matched with id in access token.");
        }
        if (Objects.equals(user.getStatus(), "INACTIVE")) {
            throw new ForbiddenException("Account is not active.");
        }
        Page<Order> resultPage = orderService.getAllOrdersFilter(id, null,  sortField, sortDirection, page, size);
        List<ResponseOrderDto> ordersDto = resultPage.getContent().stream()
                .map(order -> modelMapper.map(order, ResponseOrderDto.class))
                .collect(Collectors.toList());
        PageBuyerOrder dto = new PageBuyerOrder();
        dto.setContent(ordersDto);
        dto.setPage(page);
        dto.setSize(resultPage.getSize());
        dto.setSort(sortField + ": " + sortDirection.toUpperCase());
        dto.setFirst(resultPage.isFirst());
        dto.setLast(resultPage.isLast());
        dto.setTotalPages(resultPage.getTotalPages());
        dto.setTotalElements((int) resultPage.getTotalElements());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/v2/sellers/{id}/orders")
    public ResponseEntity<PageSellerOrder> getOrdersBySellerId(@PathVariable int id,
                                                               Authentication authentication,
                                                               @RequestParam(required = false) Integer page,
                                                               @RequestParam(defaultValue = "10") Integer size,
                                                               @RequestParam(defaultValue = "id") String sortField,
                                                               @RequestParam(defaultValue = "asc") String sortDirection) throws BadRequestException {
        if (authentication == null) {
            throw new UnauthorizedException("Invalid token.");
        }

        Integer userIdFromToken = Integer.valueOf((String) authentication.getPrincipal());
        User user = userService.getUserById(id);
        if (user == null) {
            throw new UnauthorizedException("User not found.");
        }
        if (!user.getId().equals(userIdFromToken)) {
            throw new ForbiddenException("Request user id not matched with id in access token.");
        }
        if (Objects.equals(user.getStatus(), "INACTIVE")) {
            throw new ForbiddenException("Account is not active.");
        }
        Page<Order> resultPage = orderService.getAllOrdersFilter(id, null,  sortField, sortDirection, page, size);
        List<ResponseSellerDto> ordersDto = resultPage.getContent().stream()
                .map(order -> modelMapper.map(order, ResponseOrderDto.class))
                .collect(Collectors.toList());
        PageSellerOrder dto = new PageSellerOrder();
        dto.setContent(ordersDto);
        dto.setPage(page);
        dto.setSize(resultPage.getSize());
        dto.setSort(sortField + ": " + sortDirection.toUpperCase());
        dto.setFirst(resultPage.isFirst());
        dto.setLast(resultPage.isLast());
        dto.setTotalPages(resultPage.getTotalPages());
        dto.setTotalElements((int) resultPage.getTotalElements());
        return ResponseEntity.ok(dto);
    }
}