package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
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

import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private CartService cartService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void placeOrderTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        CartEntry cartEntry = new CartEntry();
        List<CartEntry> cartEntries = List.of(cartEntry);
        Order order = new Order();
        OrderEntry orderEntry = new OrderEntry();
        List<OrderEntry> orderEntries = List.of(orderEntry);
        OrderDto orderDto = new OrderDto();
        List<OrderEntryDto> orderEntryDtos = List.of(new OrderEntryDto());
        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCart("CART1")).thenReturn(cartEntries);
        Mockito.when(modelMapper.map(cart, Order.class)).thenReturn(order);
        Mockito.when(modelMapper.map(cartEntry, OrderEntry.class)).thenReturn(orderEntry);
        Mockito.when(orderRepository.save(order)).thenReturn(order);
        Mockito.when(orderEntryRepository.saveAll(orderEntries)).thenReturn(orderEntries);
        Mockito.doNothing().when(cartEntryService).deleteAllByCart("CART1");
        Mockito.lenient().when(cartService.recalculate("CART1")).thenReturn(null);
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        Mockito.when(modelMapper.map(
                Mockito.eq(orderEntries),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(orderEntryDtos);
        OrderDto response = orderService.placeOrder("CART1", "CASH");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(orderEntryDtos, response.getEntryDtoList());
        Mockito.verify(orderRepository).save(order);
        Mockito.verify(orderEntryRepository).saveAll(orderEntries);
        Mockito.verify(cartEntryService).deleteAllByCart("CART1");
        Mockito.verify(cartService).recalculate("CART1");
    }

    @Test
    void findByOrderIdTest() {
        Order order = new Order();
        order.setOrderId("ORD-1");
        OrderEntry orderEntry = new OrderEntry();
        List<OrderEntry> entries = List.of(orderEntry);
        OrderDto orderDto = new OrderDto();
        List<OrderEntryDto> orderEntryDtos = List.of(new OrderEntryDto());
        Mockito.when(orderRepository.findByOrderId("ORD-1")).thenReturn(List.of(order));
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        Mockito.when(orderEntryRepository.findByOrderId("ORD-1")).thenReturn(entries);
        Mockito.when(modelMapper.map(
                Mockito.eq(entries),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(orderEntryDtos);
        OrderDto response = orderService.findByOrderId("ORD-1");
        Assertions.assertEquals(orderEntryDtos, response.getEntryDtoList());
    }

    @Test
    void findByOrderIdNotFoundTest() {
        Mockito.when(orderRepository.findByOrderId("ORD-MISSING")).thenReturn(List.of());
        OrderDto response = orderService.findByOrderId("ORD-MISSING");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Order not found", response.getMessage());
    }

    @Test
    void findAllTest() {
        Order order = new Order();
        order.setOrderId("ORD-1");
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId("ORD-1");
        List<Order> orders = List.of(order);
        List<OrderDto> orderDtos = List.of(orderDto);
        OrderEntry orderEntry = new OrderEntry();
        List<OrderEntry> entries = List.of(orderEntry);
        List<OrderEntryDto> orderEntryDtos = List.of(new OrderEntryDto());
        Page<Order> orderPage = new PageImpl<>(orders, PageRequest.of(0, 2), orders.size());
        Pageable pageable = PageRequest.of(0, 50);
        Mockito.when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        Mockito.when(modelMapper.map(Mockito.eq(orders), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(orderDtos);
        Mockito.when(orderEntryRepository.findByOrderId("ORD-1")).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(orderEntryDtos);
        WsDto<OrderDto> response = orderService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(orderEntryDtos, response.getDtoList().get(0).getEntryDtoList());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}