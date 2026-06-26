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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
        cart.setIdentifier("CART-123");

        cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD-123");

        order = new Order();
        order.setOrderId("ORD-123-20260101000000");

        orderEntry = new OrderEntry();
        orderEntry.setOrderId("ORD-123-20260101000000");

        orderDto = new OrderDto();
        orderDto.setOrderId("ORD-123-20260101000000");

        orderEntryDto = new OrderEntryDto();
    }

    @Test
    @DisplayName("Generate Order ID - Numeric Cart Identifier")
    void generateOrderId_WithNumericCart() {
        String orderId = orderService.generateOrderId("CART-999");

        Assertions.assertNotNull(orderId);
        Assertions.assertTrue(orderId.startsWith("ORD-999-"));
    }

    @Test
    @DisplayName("Generate Order ID - Null or Empty Cart Identifier Defaults to WALK IN")
    void generateOrderId_WithEmptyCart() {
        String orderIdNull = orderService.generateOrderId(null);
        String orderIdBlank = orderService.generateOrderId("   ");

        Assertions.assertTrue(orderIdNull.startsWith("ORD-WALK IN-"));
        Assertions.assertTrue(orderIdBlank.startsWith("ORD-WALK IN-"));
    }

    @Test
    @DisplayName("Place Order - Success Workflow")
    void placeOrder_Success() {
        String cartIdentifier = "CART-123";
        String paymentMode = "CASH";

        when(cartRepository.findByIdentifier(cartIdentifier)).thenReturn(cart);
        when(cartEntryRepository.findByCart(cartIdentifier)).thenReturn(List.of(cartEntry));
        when(modelMapper.map(cart, Order.class)).thenReturn(order);
        when(modelMapper.map(cartEntry, OrderEntry.class)).thenReturn(orderEntry);
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(orderEntryDto));

        OrderDto result = orderService.placeOrder(cartIdentifier, paymentMode);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ORD-123-20260101000000", result.getOrderId());

        verify(orderRepository).save(order);
        verify(orderEntryRepository).saveAll(anyList());
        verify(cartEntryService).deleteAllByCart(cartIdentifier);
        verify(cartService).recalculate(cartIdentifier);
    }

    @Test
    @DisplayName("Find All Orders - Success Workflow")
    void findAll_Success() {
        when(orderRepository.findAllByOrderByOrderDateDesc()).thenReturn(List.of(order));
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        when(orderEntryRepository.findByOrderId(order.getOrderId())).thenReturn(List.of(orderEntry));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(orderEntryDto));

        List<OrderDto> result = orderService.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("ORD-123-20260101000000", result.getFirst().getOrderId());
    }

    @Test
    @DisplayName("Find All Orders - Return Empty List when No Orders Found")
    void findAll_Empty() {
        when(orderRepository.findAllByOrderByOrderDateDesc()).thenReturn(Collections.emptyList());

        List<OrderDto> result = orderService.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        verify(orderEntryRepository, never()).findByOrderId(anyString());
    }
}