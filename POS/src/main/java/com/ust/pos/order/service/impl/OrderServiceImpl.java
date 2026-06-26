package com.ust.pos.order.service.impl;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.OrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEntryRepository orderEntryRepository;
    private final CartRepository cartRepository;
    private final CartEntryRepository cartEntryRepository;
    private final CartService cartService;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEntryRepository orderEntryRepository, CartRepository cartRepository, CartEntryRepository cartEntryRepository, CartService cartService, CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.orderEntryRepository = orderEntryRepository;
        this.cartRepository = cartRepository;
        this.cartEntryRepository = cartEntryRepository;
        this.cartService = cartService;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    private OrderDto mapToOrderDto(Order order) {
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        List<OrderEntry> orderEntries = orderEntryRepository.findAllByOrder(order.getIdentifier());
        Type listType = new TypeToken<List<OrderEntryDto>>() {}.getType();
        orderDto.setOrderEntryDtoList(modelMapper.map(orderEntries, listType));
        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto placeOrder(String cartIdentifier) {
        Cart cart = cartRepository.findByIdentifier(cartIdentifier);
        Order order = orderRepository.findByIdentifier(cartIdentifier);
        if (order == null) {
            order = new Order();
        }
        String customerPhone = cartIdentifier.contains("-") ? cartIdentifier.substring(cartIdentifier.indexOf("-") + 1) : cartIdentifier;
        Customer customerEntity = customerRepository.findByIdentifier(customerPhone);
        String customerName = customerEntity != null ? customerEntity.getName() : customerPhone;
        order.setIdentifier(cartIdentifier);
        order.setOrderId("ORD-" + System.currentTimeMillis());
        order.setCustomer(customerName);
        order.setTotalPrice(cart.getTotalPrice() != null ? cart.getTotalPrice() : BigDecimal.ZERO);
        order.setTotalDiscount(cart.getTotalDiscount() != null ? cart.getTotalDiscount() : BigDecimal.ZERO);
        order.setOrderStatus("PLACED");
        orderRepository.save(order);
        List<CartEntry> cartEntries = cartEntryRepository.findAllByCart(cartIdentifier);
        for (CartEntry cartEntry : cartEntries) {
            OrderEntry existing = orderEntryRepository.findByIdentifier(cartEntry.getIdentifier());
            OrderEntry orderEntry = existing != null ? existing : new OrderEntry();
            orderEntry.setIdentifier(cartEntry.getIdentifier());
            orderEntry.setOrder(cartIdentifier);
            orderEntry.setProduct(cartEntry.getProduct());
            orderEntry.setQuantity(cartEntry.getQuantity());
            orderEntry.setPrice(cartEntry.getPrice());
            orderEntry.setSellingPrice(cartEntry.getSellingPrice());
            orderEntry.setTotalPrice(cartEntry.getTotalPrice());
            orderEntry.setDiscount(cartEntry.getDiscount());
            orderEntry.setCouponCode(cartEntry.getCouponCode());
            orderEntryRepository.save(orderEntry);
        }
        cartEntryRepository.deleteAll(cartEntries);
        cartService.recalculate(cartIdentifier);
        return findByIdentifier(cartIdentifier);
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(String identifier) {
        Order order = orderRepository.findByIdentifier(identifier);
        List<OrderEntry> orderEntries = orderEntryRepository.findAllByOrder(identifier);
        for (OrderEntry orderEntry : orderEntries) {
            CartEntry cartEntry = cartEntryRepository.findByIdentifier(orderEntry.getIdentifier());
            if (cartEntry != null) {
                cartEntry.setQuantity(cartEntry.getQuantity().add(orderEntry.getQuantity()));
            } else {
                cartEntry = new CartEntry();
                cartEntry.setIdentifier(orderEntry.getIdentifier());
                cartEntry.setCart(identifier);
                cartEntry.setProduct(orderEntry.getProduct());
                cartEntry.setQuantity(orderEntry.getQuantity());
                cartEntry.setPrice(orderEntry.getPrice());
                cartEntry.setSellingPrice(orderEntry.getSellingPrice());
                cartEntry.setTotalPrice(orderEntry.getTotalPrice());
                cartEntry.setDiscount(orderEntry.getDiscount());
                cartEntry.setCouponCode(orderEntry.getCouponCode());
            }
            cartEntryRepository.save(cartEntry);
        }
        cartService.recalculate(identifier);
        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);
        return findByIdentifier(identifier);
    }

    @Override
    public OrderDto findByIdentifier(String identifier) {
        Order order = orderRepository.findByIdentifier(identifier);
        return mapToOrderDto(order);
    }

    @Override
    public List<OrderDto> findAllByCustomer(String customer) {
        return orderRepository.findAllByCustomer(customer).stream()
                .map(this::mapToOrderDto)
                .toList();
    }

    @Override
    public List<OrderDto> findAll() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderDto)
                .toList();
    }

    @Override
    public void delete(String identifier) {
        orderRepository.deleteByIdentifier(identifier);
    }
}