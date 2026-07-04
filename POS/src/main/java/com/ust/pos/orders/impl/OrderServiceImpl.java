package com.ust.pos.orders.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.*;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.model.Orders;
import com.ust.pos.model.OrdersRepository;
import com.ust.pos.orders.OrdersService;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderServiceImpl extends BaseService implements OrdersService {

    private final OrdersRepository ordersRepository;

    private final OrderEntryRepository orderEntryRepository;

    private final CartEntryService cartEntryService;

    private final CartService cartService;

    private final ModelMapper modelMapper;

    private final StockServiceImpl stockService;

    public OrderServiceImpl(OrdersRepository ordersRepository, OrderEntryRepository orderEntryRepository, StockServiceImpl stockService, CartEntryService cartEntryService, CartService cartService, ModelMapper modelMapper) {
        this.ordersRepository = ordersRepository;
        this.orderEntryRepository = orderEntryRepository;
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
        this.stockService = stockService;
    }

    @Override
    public OrdersDto findByIdentifier(String identifier) {

        Orders orders = ordersRepository.findByIdentifier(identifier);

        if (orders == null) {
            throw new ResourseNotFoundException("Data cannot found");
        }

        return modelMapper.map(orders, OrdersDto.class);
    }

    @Override
    public OrdersDto save(OrdersDto ordersDto) {

        String cartId = ordersDto.getCustomerId();
        List<CartEntryDto> cartEntryList = cartEntryService.findByCartId(cartId);
        String orderID = generateOrderId(cartId);

        for (CartEntryDto cartEntry : cartEntryList) {
            OrderEntry orderEntry = modelMapper.map(cartEntry, OrderEntry.class);
            orderEntry.setOrderId(orderID);
            orderEntry.setId(null);
            setCreatedDetails(orderEntry);
            StockDto stockDto = new StockDto();
            stockDto.setProduct(cartEntry.getProduct());
            stockDto.setQuantity((orderEntry.getQuantity()).longValue());
            stockService.updateQuantity(stockDto);
            orderEntryRepository.save(orderEntry);
        }

        CartDto cartDto = cartService.findByIdentifier(cartId);
        modelMapper.map(cartDto, ordersDto);
        ordersDto.setIdentifier(orderID);
        ordersDto.setId(null);
        setCreatedDetails(modelMapper.map(ordersDto, Orders.class));
        ordersRepository.save(modelMapper.map(ordersDto, Orders.class));
        cartEntryService.deleteAllByCartId(cartId);

        return ordersDto;
    }

    @Override
    public WsDto<OrdersDto> findAll(Pageable pageable) {

        Page<Orders> ordersPage = ordersRepository.findAll(pageable);

        WsDto<OrdersDto> ordersDto = new WsDto<>();

        List<OrdersDto> ordersDtos = ordersPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, OrdersDto.class))
                .toList();

        ordersDto.setContent(ordersDtos);
        ordersDto.setPage(ordersPage.getNumber());
        ordersDto.setSizePerPage(ordersPage.getSize());
        ordersDto.setTotalPages(ordersPage.getTotalPages());
        ordersDto.setTotalRecords(ordersPage.getTotalElements());

        return ordersDto;
    }

    @Override
    public WsDto<OrdersDto> findAll(Specification<Orders> example, Pageable pageable) {

        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        Page<Orders> page = ordersRepository.findAll(example, pageable);

        WsDto<OrdersDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    private String generateOrderId(String cartId) {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String shortCartId = cartId.substring(0, Math.min(5, cartId.length()));
        return "ORD-" + date + "-" + shortCartId;
    }
}
