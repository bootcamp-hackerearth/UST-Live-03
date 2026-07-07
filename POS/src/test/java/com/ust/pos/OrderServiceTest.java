package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.model.Order;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

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
    void checkoutSuccessTest() {
        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier("CART1");
        dto.setPaymentMethod("CASH");
        dto.setReceivedAmount(new BigDecimal("200"));

        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        cart.setTotalPrice(new BigDecimal("150"));
        cart.setTotalOriginalPrice(new BigDecimal("200"));
        cart.setTotalDiscount(new BigDecimal("50"));

        CartEntry entry = new CartEntry();
        entry.setProductIdentifier("PROD1");
        entry.setQuantity(new BigDecimal("2"));
        entry.setUnitPrice(new BigDecimal("75"));
        entry.setOriginalPrice(new BigDecimal("150"));
        entry.setDiscount(new BigDecimal("20"));
        entry.setTotalPrice(new BigDecimal("150"));
        entry.setCartIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCartIdentifier("CART1"))
                .thenReturn(List.of(entry));

        Mockito.when(orderRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));
        Mockito.when(orderEntryRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        Mockito.when(orderEntryRepository.findByOrderIdentifier(Mockito.any()))
                .thenReturn(List.of(new OrderEntry()));

        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class)))
                .thenReturn(dto);

        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        OrderDto response = orderService.checkout(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Order placed successfully", response.getMessage());

        Mockito.verify(cartEntryRepository).deleteAll(Mockito.any());
        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void checkoutFailure_cartNotFound() {
        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier("INVALID");

        Mockito.when(cartRepository.findByIdentifier("INVALID")).thenReturn(null);
        OrderDto response = orderService.checkout(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart not found", response.getMessage());
    }

    @Test
    void checkoutFailure_emptyCart() {
        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier("CART1");

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCartIdentifier("CART1")).thenReturn(List.of());

        OrderDto response = orderService.checkout(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart is empty", response.getMessage());
    }

    @Test
    void getSuccessTest() {
        Order order = new Order();
        order.setIdentifier("ORD1");
        Mockito.when(orderRepository.findByIdentifier("ORD1")).thenReturn(order);
        Mockito.when(orderEntryRepository.findByOrderIdentifier("ORD1")).thenReturn(List.of(new OrderEntry()));
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(new OrderDto());
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of(new OrderEntryDto()));
        OrderDto response = orderService.get("ORD1");
        Assertions.assertNotNull(response);
    }

    @Test
    void getFailureTest() {
        Mockito.when(orderRepository.findByIdentifier("ORD1")).thenReturn(null);
        OrderDto response = orderService.get("ORD1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Order not found", response.getMessage());
    }

    @Test
    void findAllTest() {
        Order order = new Order();
        order.setIdentifier("ORD1");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        Mockito.when(orderRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of(new OrderDto()));

        WsDto<OrderDto> response = orderService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(orderEntryRepository).deleteByOrderIdentifier("ORD1");
        Mockito.doNothing().when(orderRepository).deleteByIdentifier("ORD1");
        boolean result = orderService.delete("ORD1");
        Assertions.assertTrue(result);
        Mockito.verify(orderEntryRepository).deleteByOrderIdentifier("ORD1");
        Mockito.verify(orderRepository).deleteByIdentifier("ORD1");
    }
}