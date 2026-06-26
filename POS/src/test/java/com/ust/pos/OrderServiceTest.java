package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private CartEntryRepository cartEntryRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Cart cart;
    private CartEntry cartEntry;
    private Order order;
    private OrderDto orderDto;
    private OrderEntry orderEntry;
    private OrderEntryDto orderEntryDto;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setIdentifier("C1");

        cartEntry = new CartEntry();
        cartEntry.setIdentifier("CE1");

        order = new Order();
        order.setOrderId("ORD-C1-123");

        orderDto = new OrderDto();

        orderEntry = new OrderEntry();
        orderEntryDto = new OrderEntryDto();
    }

    // ✅ GENERATE ORDER ID
    @Test
    void testGenerateOrderId() {
        String result = orderService.generateOrderId("C1");

        assertTrue(result.startsWith("ORD-"));
        assertTrue(result.contains("C1".replaceAll("[^0-9]", "")));
    }

    // ✅ PLACE ORDER
    @Test
    void testPlaceOrder() {
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        when(cartEntryRepository.findByCart("C1"))
                .thenReturn(Collections.singletonList(cartEntry));

        when(modelMapper.map(cart, Order.class)).thenReturn(order);
        when(modelMapper.map(cartEntry, OrderEntry.class)).thenReturn(orderEntry);
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(orderEntryDto));

        OrderDto result = orderService.placeOrder("C1", "CASH");

        assertNotNull(result);

        verify(orderRepository).save(order);
        verify(orderEntryRepository).saveAll(anyList());
        verify(cartEntryService).deleteAllByCart("C1");
        verify(cartService).recalculate("C1");
    }

    // ✅ FIND ALL ORDERS
    @Test
    void testFindAll() {
        when(orderRepository.findAllByOrderByOrderDateDesc())
                .thenReturn(Collections.singletonList(order));

        when(orderEntryRepository.findByOrderId("ORD-C1-123"))
                .thenReturn(Collections.singletonList(orderEntry));

        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(orderEntryDto));

        List<OrderDto> result = orderService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
