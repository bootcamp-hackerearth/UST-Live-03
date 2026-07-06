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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

    private Page<Order> buildOrderPage(Pageable pageable, String orderId) {
        Order order = new Order();
        order.setOrderId(orderId);
        return new PageImpl<>(List.of(order), pageable, 1);
    }

    private void mockOrderPageCommon(Page<Order> orderPage, String orderId) {
        OrderDto orderDto = new OrderDto();
        OrderEntry orderEntry = new OrderEntry();
        List<OrderEntry> entries = List.of(orderEntry);
        OrderEntryDto entryDto = new OrderEntryDto();
        List<OrderEntryDto> entryDtos = List.of(entryDto);

        Mockito.when(modelMapper.map(orderPage.getContent().get(0), OrderDto.class)).thenReturn(orderDto);
        Mockito.when(orderEntryRepository.findByOrderId(orderId)).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(entryDtos);
    }

    private void assertWsDto(WsDto<OrderDto> response) {
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void placeOrderTest() {
        String cartId = "CART01";
        String paymentMode = "CASH";

        Cart cart = new Cart();
        CartEntry cartEntry = new CartEntry();
        List<CartEntry> cartEntries = List.of(cartEntry);

        Order order = new Order();
        OrderEntry orderEntry = new OrderEntry();
        OrderDto orderDto = new OrderDto();
        OrderEntryDto orderEntryDto = new OrderEntryDto();
        List<OrderEntryDto> orderEntryDtos = List.of(orderEntryDto);

        Mockito.when(cartRepository.findByIdentifier(cartId)).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCart(cartId)).thenReturn(cartEntries);
        Mockito.when(modelMapper.map(cart, Order.class)).thenReturn(order);
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
        Mockito.when(modelMapper.map(cartEntry, OrderEntry.class)).thenReturn(orderEntry);
        Mockito.when(orderEntryRepository.saveAll(Mockito.anyList())).thenReturn(new ArrayList<>());
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        Mockito.when(modelMapper.map(Mockito.anyList(), Mockito.any(Type.class))).thenReturn(orderEntryDtos);

        OrderDto response = orderService.placeOrder(cartId, paymentMode);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(orderEntryDtos, response.getEntryDtoList());
        Mockito.verify(cartEntryService, Mockito.times(1)).deleteAllByCart(cartId);
        Mockito.verify(cartService, Mockito.times(1)).recalculate(cartId);
    }

    @Test
    void findByOrderIdTestSuccess() {
        String orderId = "ORD-01";
        Order order = new Order();
        order.setOrderId(orderId);
        List<Order> orders = List.of(order);

        OrderDto orderDto = new OrderDto();
        OrderEntry orderEntry = new OrderEntry();
        List<OrderEntry> entries = List.of(orderEntry);
        OrderEntryDto entryDto = new OrderEntryDto();
        List<OrderEntryDto> entryDtos = List.of(entryDto);

        Mockito.when(orderRepository.findByOrderId(orderId)).thenReturn(orders);
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        Mockito.when(orderEntryRepository.findByOrderId(orderId)).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(entryDtos);

        OrderDto response = orderService.findByOrderId(orderId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(entryDtos, response.getEntryDtoList());
    }

    @Test
    void findByOrderIdTestFailure() {
        String orderId = "ORD-01";
        Mockito.when(orderRepository.findByOrderId(orderId)).thenReturn(List.of());

        OrderDto response = orderService.findByOrderId(orderId);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Order not found", response.getMessage());
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Page<Order> orderPage = buildOrderPage(pageable, "ORD-01");
        mockOrderPageCommon(orderPage, "ORD-01");
        Mockito.when(orderRepository.findAllByOrderByOrderDateDesc(pageable)).thenReturn(orderPage);

        WsDto<OrderDto> response = orderService.findAll(pageable);

        assertWsDto(response);
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Order> specification = Mockito.mock(Specification.class);
        Page<Order> orderPage = buildOrderPage(pageable, "ORD-01");
        mockOrderPageCommon(orderPage, "ORD-01");
        Mockito.when(orderRepository.findAll(specification, pageable)).thenReturn(orderPage);

        WsDto<OrderDto> response = orderService.findAll(specification, pageable);

        assertWsDto(response);
    }
}