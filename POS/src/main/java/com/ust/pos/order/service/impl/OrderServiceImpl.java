package com.ust.pos.order.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Order;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.model.OrderRepository;
import com.ust.pos.order.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderServiceImpl extends BaseService implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final CartService cartService;
    private final CartEntryService cartEntryService;
    private final OrderEntryRepository orderEntryRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ModelMapper modelMapper, CartService cartService, CartEntryService cartEntryService, OrderEntryRepository orderEntryRepository) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.cartService = cartService;
        this.cartEntryService = cartEntryService;
        this.orderEntryRepository = orderEntryRepository;
    }

    private String generateOrderId(String cartId) {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String shortCartId = cartId.substring(0, Math.min(5, cartId.length()));
        return "ORD-" + date + "-" + shortCartId;
    }

    @Override
    public OrderDto findByIdentifier(String identifier) {
        Order order = orderRepository.findByIdentifier(identifier);

        if (order == null) {
            throw new ResourceNotFoundException(
                    "Order with identifier '" + identifier + "' not found");
        }

        return modelMapper.map(order, OrderDto.class);
    }

    public OrderDto save(OrderDto orderDto) {
        String cartId = orderDto.getCustomerId();
        List<CartEntryDto> cartEntryList = cartEntryService.findByCartId(cartId);

        String orderID = generateOrderId(cartId);

        for (CartEntryDto cartEntry : cartEntryList) {
            OrderEntry orderEntry = modelMapper.map(cartEntry, OrderEntry.class);
            orderEntry.setOrderId(orderID);
            orderEntry.setId(null);
            setCreatedDetails(orderEntry);
            orderEntryRepository.save(orderEntry);
        }

        CartDto cartDto = cartService.findByIdentifier(cartId);
        modelMapper.map(cartDto, orderDto);
        orderDto.setIdentifier(orderID);
        orderDto.setId(null);
        setModifiedDetails(modelMapper.map(orderDto, Order.class));
        orderRepository.save(modelMapper.map(orderDto, Order.class));
        cartEntryService.deleteAllByCartId(cartId);
        return orderDto;
    }

    @Override
    public WsDto<OrderDto> findAll(Pageable pageable) {

        Page<Order> orderPage = orderRepository.findByIsDeletedFalse(pageable);

        WsDto<OrderDto> orderDto = new WsDto<>();

        List<OrderDto> orderDtos = orderPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, OrderDto.class))
                .toList();

        orderDto.setContent(orderDtos);
        orderDto.setPage(orderPage.getNumber());
        orderDto.setSizePerPage(orderPage.getSize());
        orderDto.setTotalPages(orderPage.getTotalPages());
        orderDto.setTotalRecords(orderPage.getTotalElements());

        return orderDto;
    }
}
