package com.ust.pos.order.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.OrderService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl extends BaseService implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEntryRepository orderEntryRepository;
    private final CartRepository cartRepository;
    private final CartEntryRepository cartEntryRepository;
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEntryRepository orderEntryRepository, CartRepository cartRepository, CartEntryRepository cartEntryRepository, StockRepository stockRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.orderEntryRepository = orderEntryRepository;
        this.cartRepository = cartRepository;
        this.cartEntryRepository = cartEntryRepository;
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderDto checkout(OrderDto orderDto) {
        Cart cart = cartRepository.findByIdentifier(orderDto.getCustomer());
        if (cart == null) {
            orderDto.setSuccess(false);
            orderDto.setMessage("Cart not found");
            return orderDto;
        }
        List<CartEntry> cartEntryList = cartEntryRepository.findByCart(cart.getIdentifier());
        if (cartEntryList == null || cartEntryList.isEmpty()) {
            orderDto.setSuccess(false);
            orderDto.setMessage("Cart is empty");
            return orderDto;
        }
        for (CartEntry cartEntry : cartEntryList) {

            Stock stock = stockRepository.findByProduct(cartEntry.getProduct());

            if (stock == null) {
                orderDto.setSuccess(false);
                orderDto.setMessage(
                        "Stock not found for product: " + cartEntry.getProduct()
                );
                return orderDto;
            }

            if (BigDecimal.valueOf(stock.getQuantity())
                    .compareTo(cartEntry.getQuantity()) < 0) {
                orderDto.setSuccess(false);
                orderDto.setMessage("Insufficient stock for product: " + cartEntry.getProduct());
                return orderDto;
            }
        }
        String orderIdentifier = "ORD-" + System.currentTimeMillis();
        Order order = new Order();
        order.setIdentifier(orderIdentifier);
        order.setCustomer(cart.getIdentifier());
        order.setOriginalPrice(cart.getTotalOriginalPrice());
        order.setDiscount(cart.getTotalDiscount());
        order.setTotalPrice(cart.getTotalPrice());
        order.setPaymentMethod(orderDto.getPaymentMethod());

        if ("CASH".equalsIgnoreCase(orderDto.getPaymentMethod())
                && orderDto.getReceivedAmount().compareTo(cart.getTotalPrice()) < 0) {

            orderDto.setSuccess(false);
            orderDto.setMessage("Received amount is less than total price");

            return orderDto;
        } else {
            order.setReceivedAmount(cart.getTotalPrice());
            order.setChangeAmount(BigDecimal.ZERO);
        }
        setCreatedDetails(order);
        orderRepository.save(order);

        for (CartEntry cartEntry : cartEntryList) {

            Stock stock = stockRepository.findByProduct(cartEntry.getProduct());

            long updatedQuantity =
                    BigDecimal.valueOf(stock.getQuantity())
                            .subtract(cartEntry.getQuantity())
                            .longValue();

            stock.setQuantity(updatedQuantity);

            setModifiedDetails(stock);

            stockRepository.save(stock);
        }

        for (CartEntry cartEntry : cartEntryList) {
            OrderEntry orderEntry = new OrderEntry();
            orderEntry.setIdentifier(orderIdentifier + "-" + cartEntry.getProduct());
            orderEntry.setOrderIdentifier(orderIdentifier);
            orderEntry.setProduct(cartEntry.getProduct());
            orderEntry.setMrp(cartEntry.getOriginalPrice());
            orderEntry.setUnitDiscount(cartEntry.getDiscount());
            orderEntry.setUnitPrice(cartEntry.getUnitPrice());
            orderEntry.setQuantity(cartEntry.getQuantity());
            orderEntry.setTotalPrice(cartEntry.getTotalPrice());
            setCreatedDetails(orderEntry);
            orderEntryRepository.save(orderEntry);
        }
        cartEntryRepository.deleteAll(cartEntryList);
        cart.setTotalOriginalPrice(BigDecimal.ZERO);
        cart.setTotalDiscount(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.ZERO);
        setModifiedDetails(cart);
        cartRepository.save(cart);
        OrderDto responseDto = modelMapper.map(order, OrderDto.class);
        Type entryListType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();

        List<OrderEntry> orderEntryList = orderEntryRepository.findByOrderIdentifier(orderIdentifier);

        responseDto.setEntryList(modelMapper.map(orderEntryList, entryListType));
        responseDto.setSuccess(true);
        responseDto.setMessage("Order placed successfully");

        return responseDto;
    }

    @Override
    public OrderDto get(String identifier) {
        Order order = orderRepository.findByIdentifier(identifier);

        if (order == null) {
            OrderDto orderDto = new OrderDto();
            orderDto.setSuccess(false);
            orderDto.setMessage("Order not found");
            return orderDto;
        }

        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        List<OrderEntry> orderEntryList =
                orderEntryRepository.findByOrderIdentifier(identifier);
        Type entryListType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        orderDto.setEntryList(modelMapper.map(orderEntryList, entryListType));

        return orderDto;
    }

    @Override
    public PaginationResponseDto<OrderDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();
        if (pageable == null) {

            List<OrderDto> orderDtoList =
                    modelMapper.map(
                            orderRepository.findAll(),
                            listType
                    );

            PaginationResponseDto<OrderDto> response =
                    new PaginationResponseDto<>();
            response.setDtoList(orderDtoList);
            response.setTotalRecords(orderDtoList.size());

            return response;
        }
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderDto> orderDtoList = modelMapper.map(orderPage.getContent(), listType);

        PaginationResponseDto<OrderDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(orderDtoList);
        paginationResponseDto.setPage(orderPage.getNumber());
        paginationResponseDto.setSizePerPage(orderPage.getSize());
        paginationResponseDto.setTotalPages(orderPage.getTotalPages());
        paginationResponseDto.setTotalRecords(orderPage.getTotalElements());

        return paginationResponseDto;
    }
}
