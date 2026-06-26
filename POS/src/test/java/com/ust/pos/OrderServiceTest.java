package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Test
    void placeOrderTest() {
        Cart cart = new Cart();
        cart.setTotalPrice(BigDecimal.valueOf(100));
        cart.setOriginalPrice(BigDecimal.valueOf(120));
        cart.setDiscount(BigDecimal.valueOf(20));
        CartEntry entry = new CartEntry();
        entry.setProductIdentifier("P1");
        entry.setQuantity(BigDecimal.ONE);
        entry.setUnitPrice(BigDecimal.valueOf(100));
        entry.setOriginalPrice(BigDecimal.valueOf(120));
        entry.setDiscount(BigDecimal.valueOf(20));
        entry.setTotalPrice(BigDecimal.valueOf(100));
        Mockito.when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCartIdentifier("C1")).thenReturn(List.of(entry));
        OrderDto dto = new OrderDto();
        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(dto);
        OrderDto response = orderService.placeOrder("C1", "U1", "COD");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Order placed successfully", response.getMessage());
        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
        Mockito.verify(orderEntryRepository).save(Mockito.any(OrderEntry.class));
        Mockito.verify(cartRepository).deleteByIdentifier("C1");
    }

    @Test
    void placeOrderCartNotFoundTest() {
        Mockito.when(cartRepository.findByIdentifier("C1")).thenReturn(null);
        OrderDto response = orderService.placeOrder("C1", "U1", "COD");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart not found", response.getMessage());
    }

    @Test
    void findAllTest() {
        Order order = new Order();
        OrderDto dto = new OrderDto();
        List<Order> list = List.of(order);
        List<OrderDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Order> page = new PageImpl<>(list);
        Mockito.when(orderRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<OrderDto> response = orderService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Order> page = new PageImpl<>(List.of());
        Mockito.when(orderRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        WsDto<OrderDto> response = orderService.findAll(pageable);
        Assertions.assertTrue(response.getDtoList().isEmpty());
    }

    @Test
    void findByIdentifierTest() {
        Order order = new Order();
        order.setIdentifier("O1");
        OrderEntry entry = new OrderEntry();
        OrderDto dto = new OrderDto();
        Mockito.when(orderRepository.findByIdentifier("O1")).thenReturn(order);
        Mockito.when(orderEntryRepository.findByOrderIdentifier("O1")).thenReturn(List.of(entry));
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.eq(List.of(entry)), Mockito.any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));
        OrderDto response = orderService.findByIdentifier("O1");
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getEntryList());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(orderRepository.findByIdentifier("O1")).thenReturn(null);
        OrderDto response = orderService.findByIdentifier("O1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Order not found", response.getMessage());
    }
}