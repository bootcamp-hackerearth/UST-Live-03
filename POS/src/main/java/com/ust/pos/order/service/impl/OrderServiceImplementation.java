package com.ust.pos.order.service.impl;

import com.ust.pos.cart.CartService;
import com.ust.pos.cartentry.CartEntryService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.*;
import com.ust.pos.model.Order;
import com.ust.pos.model.OrderRepository;
import com.ust.pos.order.service.OrderService;
import com.ust.pos.orderentry.OrderEntryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderServiceImplementation
        implements OrderService {

    private final OrderRepository orderRepository;

    private final ModelMapper modelMapper;

    private final CartService cartService;

    private final CartEntryService cartEntryService;

    private final CustomerService customerService;

    private final com.ust.pos.orderentry.OrderEntryService orderEntryService;

    public OrderServiceImplementation(OrderRepository orderRepository, ModelMapper modelMapper, CartService cartService, CartEntryService cartEntryService, CustomerService customerService, OrderEntryService orderEntryService) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.cartService = cartService;
        this.cartEntryService = cartEntryService;
        this.customerService = customerService;
        this.orderEntryService = orderEntryService;
    }

    @Override
    public OrderDto save(OrderDto orderDto)
    {
        String identifier = orderDto.getIdentifier();
        Order existingOrder = orderRepository.findByIdentifier(identifier);
        if(existingOrder != null)
        {
            orderDto.setSuccess(false);
            orderDto.setMessage("Order with identifier - "+identifier+" already exists");
            return orderDto;
        }
        Order order = modelMapper.map(orderDto, Order.class);
        orderRepository.save(order);
        return orderDto;
    }

    @Override
    public OrderDto placeOrder(OrderDto orderDto)
    {
        String cartId =
                orderDto.getCartId();

        CartDto cart =
                cartService.findByIdentifier(
                        cartId
                );

        if(cart == null)
        {
            orderDto.setSuccess(false);
            orderDto.setMessage(
                    "Cart not found"
            );

            return orderDto;
        }

        List<CartEntryDto> cartEntries =
                cartEntryService.findByCartId(
                        cartId
                );

        if(cartEntries == null ||
                cartEntries.isEmpty())
        {
            orderDto.setSuccess(false);
            orderDto.setMessage(
                    "Cart is empty"
            );

            return orderDto;
        }

        CustomerDto customer =
                customerService.findByEmail(
                        cartId
                );

        if(customer == null)
        {
            orderDto.setSuccess(false);
            orderDto.setMessage(
                    "Customer not found"
            );

            return orderDto;
        }

        String orderId =
                "ORD_" +
                        System.currentTimeMillis();

        OrderDto newOrder =
                new OrderDto();

        newOrder.setIdentifier(
                orderId
        );

        newOrder.setCustomerEmail(
                customer.getEmail()
        );

        newOrder.setShippingAddress(
                customer.getAddress()
        );

        newOrder.setPaymentMethod(
                orderDto.getPaymentMethod()
        );

        newOrder.setPaymentTime(
                orderDto.getPaymentTime()
        );

        BigDecimal totalDiscount = BigDecimal.ZERO;

        BigDecimal subtotal = BigDecimal.ZERO;

        for(CartEntryDto entry: cartEntries)
        {
            BigDecimal discountPerUnit = entry.getDiscount() != null ? entry.getDiscount() : BigDecimal.ZERO;
            BigDecimal quantity = entry.getTotalPrice() != null ? entry.getQuantity() : BigDecimal.ZERO;
            BigDecimal rowDiscount = discountPerUnit.multiply(quantity);
            totalDiscount = totalDiscount.add(rowDiscount);
            subtotal = subtotal.add(entry.getTotalPrice().add(rowDiscount));
            newOrder.setSubtotal(subtotal);
            newOrder.setDiscount(totalDiscount);
            newOrder.setTotal(subtotal.subtract(totalDiscount));
        }

        Order order =
                modelMapper.map(
                        newOrder,
                        Order.class
                );

        orderRepository.save(
                order
        );

        for(CartEntryDto entry : cartEntries)
        {
            OrderEntryDto orderEntryDto =
                    new OrderEntryDto();

            orderEntryDto.setIdentifier(
                    orderId +
                            "_" +
                            entry.getProduct()
            );

            orderEntryDto.setOrderId(
                    orderId
            );

            orderEntryDto.setProduct(
                    entry.getProduct()
            );

            orderEntryDto.setQuantity(
                    entry.getQuantity()
            );

            orderEntryDto.setUnitPrice(
                    entry.getUnitPrice()
            );

            orderEntryDto.setDiscount(
                    entry.getDiscount().multiply(entry.getQuantity())
            );

            orderEntryDto.setTotalPrice(
                    entry.getTotalPrice()
            );

            orderEntryService.save(
                    orderEntryDto
            );
        }

        cartEntryService.clearCart(
                cartId
        );

        cartService.recalculateCart(
                cartId
        );

        newOrder.setSuccess(true);
        newOrder.setMessage(
                "Order placed successfully"
        );

        return newOrder;
    }

    @Override
    public OrderDto update(OrderDto orderDto) {

        String identifier =
                orderDto.getIdentifier();

        Order existingOrder =
                orderRepository.findByIdentifier(
                        identifier
                );

        if(existingOrder == null)
        {
            orderDto.setSuccess(false);

            orderDto.setMessage(
                    "Order with identifier - "
                            + identifier
                            + " not found"
            );

            return orderDto;
        }

        modelMapper.map(
                orderDto,
                existingOrder
        );

        orderRepository.save(
                existingOrder
        );

        return orderDto;
    }

    @Override
    public Page<OrderDto> findAll(String search, Pageable pageable) {
        Page<Order> rolePage;
        if(search != null && !search.trim().isEmpty())
        {
            rolePage = orderRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse(search, pageable);
        }
        else
        {
            rolePage = orderRepository.findByDeletedFalse(pageable);
        }
        return rolePage.map(order -> modelMapper.map(order, OrderDto.class));
    }

    @Override
    public void delete(String identifier) {

        orderRepository
                .deleteByIdentifier(
                        identifier
                );
    }

    @Override
    public List<OrderDto> findAll() {

        Type listType =
                new TypeToken<
                        List<OrderDto>>() {
                }.getType();

        return modelMapper.map(
                orderRepository.findAll(),
                listType
        );
    }

    @Override
    public WsDto<OrderDto> findAll(
            Pageable pageable
    ) {

        Type listType =
                new TypeToken<
                        List<OrderDto>>() {
                }.getType();

        Page<Order> orderPage =
                orderRepository.findAll(
                        pageable
                );

        WsDto<OrderDto> wsDto =
                new WsDto<>();

        wsDto.setDtoList(
                modelMapper.map(
                        orderPage.getContent(),
                        listType
                )
        );

        wsDto.setTotalRecords(
                orderPage.getTotalElements()
        );

        wsDto.setTotalPage(
                orderPage.getTotalPages()
        );

        wsDto.setSizePerPage(
                pageable.getPageSize()
        );

        wsDto.setPage(
                pageable.getPageNumber()
        );

        return wsDto;
    }

    @Override
    public OrderDto findByIdentifier(
            String identifier
    ) {

        return modelMapper.map(
                orderRepository.findByIdentifier(
                        identifier
                ),
                OrderDto.class
        );
    }
    @Override
    public List<OrderDto> findByCustomerEmail(
            String email
    )
    {
        Type listType =
                new TypeToken<List<OrderDto>>() {
                }.getType();

        return modelMapper.map(
                orderRepository.findByCustomerEmail(
                        email
                ),
                listType
        );
    }
}