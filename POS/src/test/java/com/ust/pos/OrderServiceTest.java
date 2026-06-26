package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.order.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderDto orderDto;
    private Cart cart;
    private CartEntry cartEntry;
    private OrderEntry orderEntry;
    private OrderEntryDto orderEntryDto;

    @BeforeEach
    void setUp() {
        orderDto = new OrderDto();
        orderDto.setCustomerIdentifier("CUST-01");
        orderDto.setPaymentMethod("CASH");
        orderDto.setReceivedAmount(new BigDecimal("100.00"));

        cart = new Cart();
        cart.setIdentifier("CUST-01");
        cart.setOriginalPrice(new BigDecimal("90.00"));
        cart.setDiscount(new BigDecimal("10.00"));
        cart.setTotalPrice(new BigDecimal("80.00"));

        cartEntry = new CartEntry();
        cartEntry.setProduct("PROD-100");
        cartEntry.setOriginalPrice(new BigDecimal("90.00"));
        cartEntry.setDiscount(new BigDecimal("10.00"));
        cartEntry.setUnitPrice(new BigDecimal("80.00"));
        cartEntry.setQuantity(new BigDecimal(1L));
        cartEntry.setTotalPrice(new BigDecimal("80.00"));

        order = new Order();
        order.setIdentifier("ORD-SAMPLE");
        order.setCustomerIdentifier("CUST-01");
        order.setTotalPrice(new BigDecimal("80.00"));

        orderEntry = new OrderEntry();
        orderEntry.setIdentifier("ORD-SAMPLE-PROD-100");

        orderEntryDto = new OrderEntryDto();
    }

    @Test
    void testCheckout_CartNotFound() {
        when(cartRepository.findByIdentifier("CUST-01")).thenReturn(null);

        OrderDto result = orderService.checkout(orderDto);

        assertFalse(result.isSuccess());
        assertEquals("Cart not found", result.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCheckout_CartEntriesNull() {
        when(cartRepository.findByIdentifier("CUST-01")).thenReturn(cart);
        when(cartEntryRepository.findAllByCartId("CUST-01")).thenReturn(null);

        OrderDto result = orderService.checkout(orderDto);

        assertFalse(result.isSuccess());
        assertEquals("Cart is empty", result.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCheckout_CartEntriesEmpty() {
        when(cartRepository.findByIdentifier("CUST-01")).thenReturn(cart);
        when(cartEntryRepository.findAllByCartId("CUST-01")).thenReturn(Collections.emptyList());

        OrderDto result = orderService.checkout(orderDto);

        assertFalse(result.isSuccess());
        assertEquals("Cart is empty", result.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCheckout_Success_CashPayment() {
        List<CartEntry> entries = List.of(cartEntry);
        List<OrderEntry> orderEntries = List.of(orderEntry);
        OrderDto responseMock = new OrderDto();

        when(cartRepository.findByIdentifier("CUST-01")).thenReturn(cart);
        when(cartEntryRepository.findAllByCartId("CUST-01")).thenReturn(entries);
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(responseMock);
        when(orderEntryRepository.findByOrderIdentifier(anyString())).thenReturn(orderEntries);
        when(modelMapper.map(eq(orderEntries), any(Type.class))).thenReturn(List.of(orderEntryDto));

        OrderDto result = orderService.checkout(orderDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Order placed successfully", result.getMessage());

        verify(orderRepository, times(1)).save(argThat(o ->
                o.getChangeAmount().compareTo(new BigDecimal("20.00")) == 0 &&
                        o.getReceivedAmount().compareTo(new BigDecimal("100.00")) == 0
        ));
        verify(orderEntryRepository, times(1)).save(any(OrderEntry.class));
        verify(cartEntryRepository, times(1)).deleteAll(entries);

        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testCheckout_Success_CardPayment() {
        orderDto.setPaymentMethod("CARD");
        List<CartEntry> entries = List.of(cartEntry);
        List<OrderEntry> orderEntries = List.of(orderEntry);
        OrderDto responseMock = new OrderDto();

        when(cartRepository.findByIdentifier("CUST-01")).thenReturn(cart);
        when(cartEntryRepository.findAllByCartId("CUST-01")).thenReturn(entries);
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(responseMock);
        when(orderEntryRepository.findByOrderIdentifier(anyString())).thenReturn(orderEntries);
        when(modelMapper.map(eq(orderEntries), any(Type.class))).thenReturn(List.of(orderEntryDto));

        OrderDto result = orderService.checkout(orderDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());

        verify(orderRepository, times(1)).save(argThat(o ->
                o.getChangeAmount().compareTo(BigDecimal.ZERO) == 0 &&
                        o.getReceivedAmount().compareTo(new BigDecimal("80.00")) == 0
        ));
    }

    @Test
    void testGet_OrderNotFound() {
        when(orderRepository.findByIdentifier("ORD-001")).thenReturn(null);

        OrderDto result = orderService.get("ORD-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Order not found", result.getMessage());
    }

    @Test
    void testGet_Success() {
        List<OrderEntry> orderEntries = List.of(orderEntry);
        OrderDto expectedDto = new OrderDto();
        expectedDto.setCustomerIdentifier("CUST-01");

        when(orderRepository.findByIdentifier("ORD-001")).thenReturn(order);
        when(modelMapper.map(order, OrderDto.class)).thenReturn(expectedDto);
        when(orderEntryRepository.findByOrderIdentifier("ORD-001")).thenReturn(orderEntries);
        when(modelMapper.map(eq(orderEntries), any(Type.class))).thenReturn(List.of(orderEntryDto));

        OrderDto result = orderService.get("ORD-001");

        assertNotNull(result);
        assertEquals("CUST-01", result.getCustomerIdentifier());
        assertNotNull(result.getEntryList());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);
        List<OrderDto> dtoList = List.of(orderDto);

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(dtoList);

        WsDto<OrderDto> result = orderService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testDelete() {
        String identifier = "ORD-001";

        boolean result = orderService.delete(identifier);

        assertTrue(result);
        verify(orderEntryRepository, times(1)).deleteByOrderIdentifier(identifier);
        verify(orderRepository, times(1)).deleteByIdentifier(identifier);
    }
}
