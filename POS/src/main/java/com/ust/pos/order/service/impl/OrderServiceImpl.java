package com.ust.pos.order.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.OrderService;
import com.ust.pos.stock.service.StockService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ust.pos.dto.StockDto;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl extends BaseService implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEntryRepository orderEntryRepository;
    private final CartRepository cartRepository;
    private final CartEntryRepository cartEntryRepository;
    private final ModelMapper modelMapper;
    private final StockService stockService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEntryRepository orderEntryRepository, CartRepository cartRepository, CartEntryRepository cartEntryRepository, ModelMapper modelMapper, StockService stockService) {
        this.orderRepository = orderRepository;
        this.orderEntryRepository = orderEntryRepository;
        this.cartRepository = cartRepository;
        this.cartEntryRepository = cartEntryRepository;
        this.modelMapper = modelMapper;
        this.stockService = stockService;
    }

    @Override
    public OrderDto checkout(OrderDto orderDto) {

        Cart cart = cartRepository.findByIdentifier(orderDto.getCustomer());

        if (cart == null) {
            orderDto.setSuccess(false);
            orderDto.setMessage("Cart not found");
            return orderDto;
        }

        List<CartEntry> cartEntries =cartEntryRepository.findByCartId(cart.getIdentifier());

        if (cartEntries == null || cartEntries.isEmpty()) {
            orderDto.setSuccess(false);
            orderDto.setMessage("Cart is empty");
            return orderDto;
        }

        for (CartEntry ce : cartEntries) {
            boolean available = stockService.isStockAvailable(ce.getProductId(), ce.getQuantity().intValue());
            if (!available) {
                orderDto.setSuccess(false);
                orderDto.setMessage("Insufficient stock for product: " + ce.getProductName());
                return orderDto;
            }
        }

        String orderId = "ORD-" + System.currentTimeMillis();
        Order order = new Order();
        order.setIdentifier(orderId);
        order.setCustomer(cart.getIdentifier());
        order.setOriginalPrice(cart.getOriginalPrice());
        order.setDiscount(cart.getDiscount());
        order.setTotalPrice(cart.getTotalPrice());
        order.setPaymentMethod(orderDto.getPaymentMethod());
        if ("CASH".equalsIgnoreCase(orderDto.getPaymentMethod())) {

            BigDecimal received = orderDto.getReceivedAmount() != null
                    ? orderDto.getReceivedAmount() : BigDecimal.ZERO;
            order.setReceivedAmount(received);
            order.setChangeAmount(received.subtract(cart.getTotalPrice()));

        } else {
            order.setReceivedAmount(cart.getTotalPrice());
            order.setChangeAmount(BigDecimal.ZERO);
        }

        setCreatedDetails(order);
        orderRepository.save(order);

        for (CartEntry ce : cartEntries) {
            OrderEntry oe = new OrderEntry();
            oe.setIdentifier(orderId + "-" + ce.getProductId());
            oe.setOrderIdentifier(orderId);
            oe.setProduct(ce.getProductName());
            oe.setMrp(ce.getMrp());
            oe.setUnitPrice(ce.getSellingPrice());
            oe.setUnitDiscount(ce.getDiscount());
            oe.setQuantity(ce.getQuantity().intValue());
            oe.setTotalPrice(ce.getTotalPrice());
            setCreatedDetails(oe);
            orderEntryRepository.save(oe);
            StockDto stockResult = stockService.reduceStock(ce.getProductId(), ce.getQuantity().intValue());

            if (Boolean.FALSE.equals(stockResult.isSuccess())) {
                orderDto.setSuccess(false);
                orderDto.setMessage("Stock reduction failed for product: " + ce.getProductName());
                return orderDto;
            }
        }

        cartEntryRepository.deleteAll(cartEntries);
        cart.setOriginalPrice(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.ZERO);
        setModifiedDetails(cart);
        cartRepository.save(cart);
        OrderDto response = modelMapper.map(order, OrderDto.class);

        Type listType = new TypeToken<List<OrderEntryDto>>() {}.getType();
        List<OrderEntry> savedEntries =orderEntryRepository.findByOrderIdentifier(orderId);
        response.setEntryList(modelMapper.map(savedEntries, listType));
        response.setSuccess(true);
        response.setMessage("Order placed successfully");
        return response;
    }
    @Override
    public OrderDto get(String identifier) {

        Order order = orderRepository.findByIdentifier(identifier);

        if (order == null) {
            OrderDto dto = new OrderDto();
            dto.setSuccess(false);
            dto.setMessage("Order not found");
            return dto;
        }

        OrderDto dto = modelMapper.map(order, OrderDto.class);
        List<OrderEntry> entries =orderEntryRepository.findByOrderIdentifier(identifier);
        Type type = new TypeToken<List<OrderEntryDto>>() {}.getType();
        dto.setEntryList(modelMapper.map(entries, type));
        dto.setSuccess(true);
        return dto;
    }

    @Override
    public WsDto<OrderDto> findAll(Pageable pageable) {

        Type type = new TypeToken<List<OrderDto>>() {}.getType();

        if (pageable == null) {
            List<OrderDto> list =modelMapper.map(orderRepository.findAll(), type);
            WsDto<OrderDto> res = new WsDto<>();
            res.setDtoList(list);
            res.setTotalRecords(list.size());
            return res;
        }

        Page<Order> page = orderRepository.findAll(pageable);
        List<OrderDto> list =modelMapper.map(page.getContent(), type);
        WsDto<OrderDto> res = new WsDto<>();
        res.setDtoList(list);
        res.setPage(page.getNumber());
        res.setSizePerPage(page.getSize());
        res.setTotalPages(page.getTotalPages());
        res.setTotalRecords(page.getTotalElements());
        return res;
    }

    @Override
    public List<OrderDto> search(String query) {

        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Order> orders = orderRepository.searchOrders(query);
        Type type = new TypeToken<List<OrderDto>>() {}.getType();
        return modelMapper.map(orders, type);
    }
}