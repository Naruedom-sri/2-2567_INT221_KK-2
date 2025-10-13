package intregatedproject.backend.services;

import intregatedproject.backend.dtos.orders.OrderItemDto;
import intregatedproject.backend.dtos.orders.RequestOrderDto;
import intregatedproject.backend.dtos.saleitems.RequestSaleItemDto;
import intregatedproject.backend.entities.*;
import intregatedproject.backend.exceptions.saleitems.InsufficientQuantityException;
import intregatedproject.backend.exceptions.users.ForbiddenException;
import intregatedproject.backend.repositories.OrderItemRepository;
import intregatedproject.backend.repositories.OrderRepository;
import intregatedproject.backend.utils.specifications.OrderSpecification;
import jakarta.persistence.EntityManager;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private EntityManager entityManager;

    public Page<Order> getAllOrdersFilter(
            Integer userId,
            String sortField,
            String sortDirection,
            Integer page,
            Integer size
    ) throws BadRequestException {
        size = size <= 0 ? 10 : size;

        if (page == null || page < 0) {
            throw new BadRequestException("request parameter 'page' must be greater than or equal to zero.");
        }

        Sort sort;
        if ("orderDate".equalsIgnoreCase(sortField) ||
                "paymentDate".equalsIgnoreCase(sortField) ||
                "orderStatus".equalsIgnoreCase(sortField) ||
                "shippingDate".equalsIgnoreCase(sortField) ||
                "orderNote".equalsIgnoreCase(sortField) ||
                "id".equalsIgnoreCase(sortField)) {

            sort = (sortDirection.equalsIgnoreCase("asc"))
                    ? Sort.by(Sort.Order.asc(sortField))
                    : Sort.by(Sort.Order.desc(sortField));
        } else {
            sort = Sort.by(Sort.Order.desc("id"));
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Order> filterSpec = Specification.where(OrderSpecification.hasUser(userId));

        return orderRepository.findAll(filterSpec, pageable);
    }

    public Order getOrderByOrderId(int id) {
        try {
            return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found."));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while fetching Order", e);
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
        boolean hasInsufficientStock = false;

        for (OrderItemDto item : requestOrderDto.getOrderItems()) {
            SaleItem saleItem = saleItemService.getSaleItemById(Math.toIntExact(item.getSaleItemId()));
            if (saleItem.getQuantity() < item.getQuantity()) {
                hasInsufficientStock = true;
            }
        }


        for (OrderItemDto item : requestOrderDto.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            RequestSaleItemDto saleItemDto = new RequestSaleItemDto();
            SaleItem saleItem = saleItemService.getSaleItemById(Math.toIntExact(item.getSaleItemId()));
            covertOrderItemDtoToEntity(item, orderItem, order, saleItem);
            if (!hasInsufficientStock) {
                saleItemDto.setModel(saleItem.getModel());
                saleItemDto.setBrandId(saleItem.getBrand().getId());
                saleItemDto.setPrice(saleItem.getPrice());
                saleItemDto.setColor(saleItem.getColor());
                saleItemDto.setQuantity(saleItem.getQuantity() - item.getQuantity());
                saleItemDto.setDescription(saleItem.getDescription());
                saleItemDto.setScreenSizeInch(saleItem.getScreenSizeInch());
                saleItemDto.setStorageGb(saleItem.getStorageGb());
                saleItemDto.setRamGb(saleItem.getRamGb());
                saleItemService.updateSaleItem(saleItem.getId(), saleItemDto);
            }
            orderItemRepository.save(orderItem);
            order.getOrderItems().add(orderItem);
        }
        if (hasInsufficientStock) {
            order.setOrderStatus("CANCELLED");
            order = orderRepository.save(order);
        }
        orderRepository.flush();
        entityManager.refresh(order);
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
