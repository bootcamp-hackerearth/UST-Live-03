package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    public static final String CART_1 = "CART1";
    @InjectMocks
    private OrderServiceImpl service;

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

    @Test
    void checkoutCartNotFoundTest() {

        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier(CART_1);

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(null);

        OrderDto result = service.checkout(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Cart not found matching customer details",
                result.getMessage()
        );
    }

    @Test
    void checkoutCartEmptyTest() {

        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier(CART_1);

        Cart cart = new Cart();
        cart.setIdentifier(CART_1);

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(Collections.emptyList());

        OrderDto result = service.checkout(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Cart is currently empty",
                result.getMessage()
        );
    }

    @Test
    void checkoutCashPaymentTest() {

        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier(CART_1);
        dto.setPaymentMethod("CASH");
        dto.setReceivedAmount(BigDecimal.valueOf(1000));

        Cart cart = new Cart();
        cart.setIdentifier(CART_1);
        cart.setOriginalPrice(BigDecimal.valueOf(900));
        cart.setDiscount(BigDecimal.valueOf(100));
        cart.setTotalPrice(BigDecimal.valueOf(800));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setProductIdentifier("P1");
        cartEntry.setQuantity(2);
        cartEntry.setUnitPrice(BigDecimal.valueOf(400));
        cartEntry.setOriginalPrice(BigDecimal.valueOf(500));
        cartEntry.setDiscount(BigDecimal.valueOf(100));
        cartEntry.setTotalPrice(BigDecimal.valueOf(800));

        OrderDto responseDto = new OrderDto();

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(cartEntry));

        when(orderEntryRepository.findByOrderIdentifierAndDeletedFalse(anyString()))
                .thenReturn(List.of(new OrderEntry()));

        when(modelMapper.map(any(Order.class), eq(OrderDto.class)))
                .thenReturn(responseDto);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        OrderDto result = service.checkout(dto);

        assertTrue(result.isSuccess());

        verify(orderRepository).save(any(Order.class));
        verify(orderEntryRepository).save(any(OrderEntry.class));
        verify(cartEntryRepository).save(cartEntry);
        verify(cartRepository).save(cart);
    }

    @Test
    void checkoutCardPaymentTest() {

        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier(CART_1);
        dto.setPaymentMethod("CARD");

        Cart cart = new Cart();
        cart.setIdentifier(CART_1);
        cart.setTotalPrice(BigDecimal.valueOf(500));
        cart.setOriginalPrice(BigDecimal.valueOf(600));
        cart.setDiscount(BigDecimal.valueOf(100));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setProductIdentifier("P1");
        cartEntry.setQuantity(1);
        cartEntry.setUnitPrice(BigDecimal.valueOf(500));
        cartEntry.setOriginalPrice(BigDecimal.valueOf(600));
        cartEntry.setDiscount(BigDecimal.valueOf(100));
        cartEntry.setTotalPrice(BigDecimal.valueOf(500));

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(cartEntry));

        when(orderEntryRepository.findByOrderIdentifierAndDeletedFalse(anyString()))
                .thenReturn(List.of(new OrderEntry()));

        when(modelMapper.map(any(Order.class), eq(OrderDto.class)))
                .thenReturn(new OrderDto());

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        service.checkout(dto);

        ArgumentCaptor<Order> captor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(captor.capture());

        Order savedOrder = captor.getValue();

        assertEquals(
                BigDecimal.valueOf(500),
                savedOrder.getReceivedAmount()
        );

        assertEquals(
                BigDecimal.ZERO,
                savedOrder.getChangeAmount()
        );
    }

    @Test
    void getOrderSuccessTest() {

        Order order = new Order();

        OrderDto dto = new OrderDto();

        when(orderRepository.findByIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(order);

        when(modelMapper.map(order, OrderDto.class))
                .thenReturn(dto);

        when(orderEntryRepository.findByOrderIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(List.of(new OrderEntry()));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        OrderDto result = service.get("ORD1");

        assertNotNull(result);
    }

    @Test
    void getOrderNotFoundTest() {

        when(orderRepository.findByIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(null);

        OrderDto result = service.get("ORD1");

        assertFalse(result.isSuccess());

        assertEquals(
                "Order registry trace not found",
                result.getMessage()
        );
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Order order = new Order();
        OrderDto dto = new OrderDto();

        Page<Order> page =
                new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<OrderDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> page =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(orderRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<OrderDto> result =
                service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }

    @Test
    void deleteSuccessTest() {

        Order order = new Order();

        OrderEntry entry = new OrderEntry();

        when(orderRepository.findByIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(order);

        when(orderEntryRepository.findByOrderIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(List.of(entry));

        boolean result = service.delete("ORD1");

        assertTrue(result);

        verify(orderRepository).save(order);
        verify(orderEntryRepository).save(entry);
    }

    @Test
    void deleteOrderNotFoundTest() {

        when(orderRepository.findByIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(null);

        boolean result = service.delete("ORD1");

        assertFalse(result);

        verify(orderRepository, never()).save(any());
        verify(orderEntryRepository, never()).save(any());
    }
}

