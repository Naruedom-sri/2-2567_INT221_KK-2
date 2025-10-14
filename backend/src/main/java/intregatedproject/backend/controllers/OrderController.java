package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.orders.RequestOrderDto;
import intregatedproject.backend.dtos.orders.ResponseOrderDto;
import intregatedproject.backend.entities.Order;
import intregatedproject.backend.entities.User;
import intregatedproject.backend.exceptions.users.ForbiddenException;
import intregatedproject.backend.exceptions.users.UnauthorizedException;

import intregatedproject.backend.services.OrderService;
import intregatedproject.backend.services.UserService;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/itb-mshop")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @PostMapping("/v2/orders")
    public ResponseEntity<List<ResponseOrderDto>> createOrder(Authentication authentication, @RequestBody List<RequestOrderDto> requestOrderDto) throws BadRequestException {
//        if (authentication == null) {
//            throw new UnauthorizedException("invalid token.");
//        }
        if (requestOrderDto == null || requestOrderDto.isEmpty()) {
            throw new BadRequestException("Missing request parameters or invalid request parameter.");
        }
        List<Order> orderList = new ArrayList<>();
        requestOrderDto.forEach(orderDto -> {
            Order order = orderService.createOrder(orderDto);
            orderList.add(order);
        });
        List<ResponseOrderDto> responseOrderDtoList = orderList.stream()
                .map(order -> modelMapper.map(order, ResponseOrderDto.class)).toList();
        return ResponseEntity.status(201).body(responseOrderDtoList);
    }

    @GetMapping("/v2/orders/{id}")
    public ResponseEntity<ResponseOrderDto> getBuyerOrderById(Authentication authentication, @PathVariable int id) {
        if (authentication == null) {
            throw new UnauthorizedException("Invalid token.");
        }
        Order order = orderService.getOrderByOrderId(id);
        Integer userIdFromToken = Integer.valueOf((String) authentication.getPrincipal());
        User user = userService.getUserById(userIdFromToken);
        System.out.println(user);
        if (user == null) {
            throw new UnauthorizedException("User not found.");
        }

        if (Objects.equals(user.getStatus(), "INACTIVE")) {
            throw new ForbiddenException("Account is not active.");
        }

        if (!user.getId().equals(order.getBuyer().getId()) && !user.getId().equals(order.getSeller().getId())) {
            throw new ForbiddenException("User id not an owner (seller/buyer) of the order.");
        }
        ResponseOrderDto dto = modelMapper.map(order, ResponseOrderDto.class);
        return ResponseEntity.ok(dto);
    }
}
