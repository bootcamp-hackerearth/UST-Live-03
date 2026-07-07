package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Cart cart;
    private CartEntry cartEntry;
    private Order order;
    private OrderEntry orderEntry;
    private OrderDto orderDto;
    private OrderEntryDto orderEntryDto;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setIdentifier("CUST-123");
        cart.setTotalPrice(new BigDecimal("500.00"));

        cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD-001-CUST-123");
        cartEntry.setQuantity(new BigDecimal("2"));

        order = new Order();
        order.setOrderId("ORD-123-20260705");
        order.setTotalPrice(new BigDecimal("500.00"));

        orderEntry = new OrderEntry();
        orderEntry.setOrderId("ORD-123-20260705");

        orderDto = new OrderDto();
        orderDto.setOrderId("ORD-123-20260705");

        orderEntryDto = new OrderEntryDto();
    }

    @Test
    @DisplayName("Generate Order ID - Valid Numeric String in Identifier")
    void generateOrderId_ValidIdentifier() {
        String result = orderService.generateOrderId("CUST-123");

        Assertions.assertTrue(result.startsWith("ORD-123-"));
        Assertions.assertEquals(22, result.length());
    }

    @Test
    @DisplayName("Generate Order ID - Empty or Null Identifier Defaults to WALK IN")
    void generateOrderId_EmptyOrNullIdentifier() {
        String resultNull = orderService.generateOrderId(null);
        String resultBlank = orderService.generateOrderId("   ");

        Assertions.assertTrue(resultNull.startsWith("ORD-WALK IN-"));
        Assertions.assertTrue(resultBlank.startsWith("ORD-WALK IN-"));
    }

    @Test
    @DisplayName("Place Order - Success")
    void placeOrder_Success() {
        String cartIdentifier = "CUST-123";
        String paymentMode = "CASH";

        when(cartRepository.findByIdentifier(cartIdentifier)).thenReturn(cart);
        when(cartEntryRepository.findByCart(cartIdentifier)).thenReturn(List.of(cartEntry));
        when(modelMapper.map(cart, Order.class)).thenReturn(order);
        when(modelMapper.map(cartEntry, OrderEntry.class)).thenReturn(orderEntry);
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        when(modelMapper.map(eq(List.of(orderEntry)), any(Type.class))).thenReturn(List.of(orderEntryDto));

        OrderDto result = orderService.placeOrder(cartIdentifier, paymentMode);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(paymentMode, order.getPaymentMode());
        Assertions.assertNotNull(order.getOrderDate());

        verify(orderRepository).save(order);
        verify(orderEntryRepository).saveAll(anyList());
        verify(cartEntryService).deleteAllByCart(cartIdentifier);
        verify(cartService).recalculate(cartIdentifier);
    }

    @Test
    @DisplayName("Find All Orders - Success with Entries")
    void findAll_Success() {
        when(orderRepository.findAllByOrderByOrderDateDesc()).thenReturn(List.of(order));
        when(orderEntryRepository.findByOrderId(order.getOrderId())).thenReturn(List.of(orderEntry));
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        when(modelMapper.map(eq(List.of(orderEntry)), any(Type.class))).thenReturn(List.of(orderEntryDto));

        List<OrderDto> result = orderService.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.getFirst().getOrderEntryDtoList().size());
        verify(orderRepository).findAllByOrderByOrderDateDesc();
        verify(orderEntryRepository).findByOrderId(order.getOrderId());
    }

    @Test
    @DisplayName("Find All Orders - Empty List")
    void findAll_EmptyList() {
        when(orderRepository.findAllByOrderByOrderDateDesc()).thenReturn(Collections.emptyList());

        List<OrderDto> result = orderService.findAll();

        Assertions.assertTrue(result.isEmpty());
        verify(orderEntryRepository, never()).findByOrderId(anyString());
    }
}