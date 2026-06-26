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

import java.util.Collections;
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
        String cartIdentifier = "CART-01";
        String paymentMode = "CASH";

        Cart cart = new Cart();
        CartEntry cartEntry = new CartEntry();
        List<CartEntry> cartEntries = List.of(cartEntry);

        OrderDto orderDto = new OrderDto();
        OrderEntryDto orderEntryDto = new OrderEntryDto();
        List<OrderEntryDto> orderEntryDtos = List.of(orderEntryDto);

        Mockito.when(cartRepository.findByIdentifier(cartIdentifier)).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCart(cartIdentifier)).thenReturn(cartEntries);

        Mockito.when(modelMapper.map(Mockito.any(Cart.class), Mockito.eq(Order.class))).thenReturn(new Order());
        Mockito.when(modelMapper.map(Mockito.any(CartEntry.class), Mockito.eq(OrderEntry.class))).thenReturn(new OrderEntry());

        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(orderEntryRepository.saveAll(Mockito.anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.doReturn(true).when(cartEntryService).deleteAllByCart(cartIdentifier);

        Mockito.doAnswer(invocation -> null).when(cartService).recalculate(cartIdentifier);

        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(orderDto);
        Mockito.when(modelMapper.map(Mockito.anyList(), Mockito.any(java.lang.reflect.Type.class))).thenReturn(orderEntryDtos);

        OrderDto response = orderService.placeOrder(cartIdentifier, paymentMode);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(orderEntryDtos, response.getEntryDtoList());
        Mockito.verify(cartEntryService).deleteAllByCart(cartIdentifier);
        Mockito.verify(cartService).recalculate(cartIdentifier);
    }

    @Test
    void findByOrderIdTestSuccess() {
        String orderId = "ORD-01";
        Order order = new Order();
        order.setOrderId(orderId);
        List<Order> orders = List.of(order);

        OrderEntry entry = new OrderEntry();
        List<OrderEntry> entries = List.of(entry);

        OrderDto orderDto = new OrderDto();
        OrderEntryDto entryDto = new OrderEntryDto();
        List<OrderEntryDto> entryDtos = List.of(entryDto);

        Mockito.when(orderRepository.findByOrderId(orderId)).thenReturn(orders);
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        Mockito.when(orderEntryRepository.findByOrderId(orderId)).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(java.lang.reflect.Type.class))).thenReturn(entryDtos);

        OrderDto response = orderService.findByOrderId(orderId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(entryDtos, response.getEntryDtoList());
    }

    @Test
    void findByOrderIdTestNotFound() {
        String orderId = "ORD-INVALID";
        Mockito.when(orderRepository.findByOrderId(orderId)).thenReturn(Collections.emptyList());

        OrderDto response = orderService.findByOrderId(orderId);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Order not found", response.getMessage());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order = new Order();
        order.setOrderId("ORD-01");
        List<Order> orderList = List.of(order);
        Page<Order> orderPage = new PageImpl<>(orderList, pageable, orderList.size());

        OrderEntry entry = new OrderEntry();
        List<OrderEntry> entries = List.of(entry);

        OrderDto orderDto = new OrderDto();
        OrderEntryDto entryDto = new OrderEntryDto();
        List<OrderEntryDto> entryDtos = List.of(entryDto);

        Mockito.when(orderRepository.findAllByOrderByOrderDateDesc(pageable)).thenReturn(orderPage);
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        Mockito.when(orderEntryRepository.findByOrderId("ORD-01")).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(java.lang.reflect.Type.class))).thenReturn(entryDtos);

        WsDto<OrderDto> response = orderService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(entryDtos, response.getDtoList().get(0).getEntryDtoList());
        Assertions.assertEquals(1L, response.getTotalRecords());
    }
}