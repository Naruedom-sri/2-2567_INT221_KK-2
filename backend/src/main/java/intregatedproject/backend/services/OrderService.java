package intregatedproject.backend.services;

import intregatedproject.backend.dtos.orders.OrderItemDto;
import intregatedproject.backend.dtos.orders.RequestOrderDto;
import intregatedproject.backend.entities.*;
import intregatedproject.backend.exceptions.saleitems.InsufficientQuantityException;
import intregatedproject.backend.exceptions.users.ForbiddenException;
import intregatedproject.backend.repositories.OrderItemRepository;
import intregatedproject.backend.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Objects;

@Service
public class OrderService {
    @Autowired
    private SaleItemService saleItemService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    public Order createOrder(RequestOrderDto requestOrderDto) {
        User buyer = userService.getUserById(requestOrderDto.getBuyerId());
        User seller = userService.getUserById(requestOrderDto.getSellerId());

        if (buyer == null) {
            throw new ResourceNotFoundException("Buyer not found.");
        }
        if (seller == null) {
            throw new ResourceNotFoundException("Seller not found.");
        }
        if (Objects.equals(buyer.getId(), seller.getId())) {
            throw new ForbiddenException("User can't buy own sale items.");
        }
        Order order = new Order();
        covertOrderDtoToEntity(requestOrderDto, order, buyer, seller);
        orderRepository.save(order);
        requestOrderDto.getOrderItems().forEach(item -> {
            OrderItem orderItem = new OrderItem();
            SaleItem saleItem = saleItemService.getSaleItemById(Math.toIntExact(item.getSaleItemId()));
            if (saleItem.getQuantity() < item.getQuantity()) {
                throw new InsufficientQuantityException("Insufficient quantity.");
            }
            covertOrderItemDtoToEntity(item, orderItem, order, saleItem);
            orderItemRepository.save(orderItem);
            order.getOrderItems().add(orderItem);
        });
        return order;
    }

    private void covertOrderDtoToEntity(RequestOrderDto requestOrderDto, Order order, User buyer, User seller) {
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setOrderDate(requestOrderDto.getOrderDate());
        order.setShippingAddress(requestOrderDto.getShippingAddress());
        order.setOrderNote(requestOrderDto.getOrderNote());
        order.setOrderStatus(requestOrderDto.getOrderStatus());
    }

    private void covertOrderItemDtoToEntity(OrderItemDto item, OrderItem orderItem, Order order, SaleItem saleItem) {
        orderItem.setSaleItem(saleItem);
        orderItem.setOrder(order);
        orderItem.setQuantity(item.getQuantity());
        orderItem.setPrice(item.getPrice());
        orderItem.setDescription(item.getDescription());
    }

}
