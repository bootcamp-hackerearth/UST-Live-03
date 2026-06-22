package com.ust.pos.order.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.*;
import com.ust.pos.model.CustomerRepository;
import com.ust.pos.model.OrderRepository;
import com.ust.pos.model.Orders;
import com.ust.pos.order.service.OrderService;
import com.ust.pos.orderentry.service.OrderEntryService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OrderServiceImpl extends CommonService implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderEntryService orderEntryService;
    private final CartService cartService;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEntryService orderEntryService,
                            CartService cartService, CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.orderEntryService = orderEntryService;
        this.cartService = cartService;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    private String generateOrderId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uniquePart = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + uniquePart;
    }

    @Override
    public OrderDto placeOrder(PlaceOrderRequestDto request) {
        OrderDto errorDto = new OrderDto();

        String cartIdentifier = request.getCartIdentifier();
        CartDto cart = cartService.findByIdentifier(cartIdentifier);

        if (cart == null) {
            errorDto.setMessage("Cart with identifier - " + cartIdentifier + " not found");
            errorDto.setSuccess(false);
            return errorDto;
        }

        List<CartEntryDto> cartEntries = cart.getCartEntries();
        if (cartEntries == null || cartEntries.isEmpty()) {
            errorDto.setMessage("Cart is empty. Cannot place order.");
            errorDto.setSuccess(false);
            return errorDto;
        }

        String customerIdentifier = cart.getUsername() != null ? cart.getUsername() : cart.getIdentifier();
        com.ust.pos.model.Customer customer = customerRepository.findByIdentifier(customerIdentifier);

        Orders orders = new Orders();
        orders.setIdentifier(java.util.UUID.randomUUID().toString());
        orders.setOrderId(generateOrderId());
        orders.setCartIdentifier(cartIdentifier);
        orders.setCustomerIdentifier(customerIdentifier);
        orders.setPaymentMode(request.getPaymentMode());
        orders.setOrderDate(LocalDateTime.now());
        orders.setStatus(true);

        BigDecimal totalPrice = cartEntries.stream().map(CartEntryDto::getTotalPrice).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscount = cartEntries.stream().map(CartEntryDto::getDiscount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        orders.setTotalPrice(totalPrice);
        orders.setDiscount(totalDiscount);
        orders.setTaxAmount(BigDecimal.ZERO);
        orders.setGrandTotal(totalPrice);
        orders.setCoupon(cart.getCoupon());
        orderRepository.save(orders);

        for (CartEntryDto cartEntry : cartEntries) {
            OrderEntryDto orderEntryDto = new OrderEntryDto();
            orderEntryDto.setOrderIdentifier(orders.getIdentifier());
            orderEntryDto.setProductIdentifier(cartEntry.getProductIdentifier());
            orderEntryDto.setQuantity(cartEntry.getQuantity());
            orderEntryDto.setSellingPrice(cartEntry.getSellingPrice());
            orderEntryDto.setMrpPrice(cartEntry.getMrpPrice());
            orderEntryDto.setTotalPrice(cartEntry.getTotalPrice());
            orderEntryDto.setDiscount(cartEntry.getDiscount());
            orderEntryService.save(orderEntryDto);
        }

        cartService.delete(cartIdentifier);

        OrderDto orderDto = modelMapper.map(orders, OrderDto.class);
        if (customer != null) {
            orderDto.setCustomer(modelMapper.map(customer, CustomerDto.class));
        }
        List<OrderEntryDto> savedEntries = orderEntryService.findAllByOrderIdentifier(orders.getIdentifier());
        orderDto.setOrderEntries(savedEntries);
        orderDto.setSuccess(true);
        orderDto.setMessage("Order placed successfully.");
        return orderDto;
    }

    @Override
    public OrderDto findByIdentifier(String identifier) {
        Orders orders = orderRepository.findByIdentifier(identifier);
        if (orders == null) {
            return null;
        }
        return buildOrderDto(orders);
    }

    @Override
    public OrderDto findByOrderId(String orderId) {
        Orders orders = orderRepository.findByOrderId(orderId);
        if (orders == null) {
            return null;
        }
        return buildOrderDto(orders);
    }

    @Override
    public List<OrderDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();
        Page<Orders> orderPage = orderRepository.findAll(pageable);
        List<OrderDto> orderDtos = modelMapper.map(orderPage.getContent(), listType);
        orderDtos.forEach(this::enrichOrderDto);
        return orderDtos;
    }

    @Override
    public List<OrderDto> findByCustomerIdentifier(String customerIdentifier) {
        List<Orders> orders = orderRepository.findAllByCustomerIdentifierOrderByOrderDateDesc(customerIdentifier);
        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();
        List<OrderDto> orderDtos = modelMapper.map(orders, listType);
        orderDtos.forEach(this::enrichOrderDto);
        return orderDtos;
    }

    @Override
    public boolean delete(String identifier) {
        Orders orders = orderRepository.findByIdentifier(identifier);
        if (orders == null) {
            return false;
        }
        orderEntryService.deleteByOrderIdentifier(identifier);
        orderRepository.deleteByIdentifier(identifier);
        return true;
    }

    private OrderDto buildOrderDto(Orders orders) {
        OrderDto dto = modelMapper.map(orders, OrderDto.class);
        enrichOrderDto(dto);
        return dto;
    }

    private void enrichOrderDto(OrderDto dto) {
        if (dto.getCustomerIdentifier() != null) {
            com.ust.pos.model.Customer customer = customerRepository.findByIdentifier(dto.getCustomerIdentifier());
            if (customer != null) {
                dto.setCustomer(modelMapper.map(customer, CustomerDto.class));
            }
        }
        List<OrderEntryDto> entries = orderEntryService.findAllByOrderIdentifier(dto.getIdentifier());
        dto.setOrderEntries(entries);
    }
}