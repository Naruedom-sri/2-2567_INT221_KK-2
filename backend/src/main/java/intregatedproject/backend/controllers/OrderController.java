package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.orders.RequestOrderDto;
import intregatedproject.backend.dtos.orders.ResponseOrderDto;
import intregatedproject.backend.entities.Order;
import intregatedproject.backend.exceptions.users.UnauthorizedException;

import intregatedproject.backend.services.OrderService;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/itb-mshop")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/v2/orders")
    public ResponseEntity<List<ResponseOrderDto>> createOrder(Authentication authentication, @RequestBody List<RequestOrderDto> requestOrderDto) throws BadRequestException {
        if (authentication == null) {
            throw new UnauthorizedException("invalid token.");
        }
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
}
