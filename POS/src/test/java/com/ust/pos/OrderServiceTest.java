package com.ust.pos;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderItemDto;
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
import org.modelmapper.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void createOrderTest() {
        Cart cart = new Cart();
        cart.setTotalPrice(new BigDecimal("100"));
        cart.setDiscount(new BigDecimal("10"));
        cart.setCoupon("SAVE10");
        CartEntryDto entry = new CartEntryDto();
        entry.setProduct("P1");
        entry.setQuantity(new BigDecimal("2"));
        entry.setUnitPrice(new BigDecimal("50"));
        entry.setTotalPrice(new BigDecimal("100"));
        OrderDto responseDto = new OrderDto();
        Type itemListType = new TypeToken<List<OrderItemDto>>() {
        }.getType();
        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);
        Mockito.when(cartEntryService.findByCartId("C1"))
                .thenReturn(List.of(entry));
        Mockito.when(modelMapper.map(
                        Mockito.any(Order.class),
                        Mockito.eq(OrderDto.class)))
                .thenReturn(responseDto);
        Mockito.when(modelMapper.map(Mockito.anyList(), Mockito.eq(itemListType)))
                .thenReturn(List.of(new OrderItemDto()));
        OrderDto response = orderService.createOrder("C1", "CARD");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Order created successfully", response.getMessage());
        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
        Mockito.verify(orderItemRepository).saveAll(Mockito.anyList());
        Mockito.verify(cartEntryService).delete("C1");
        Mockito.verify(cartRepository).deleteByIdentifier("C1");
    }

    @Test
    void createOrderEmptyCartTest() {
        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);
        Mockito.when(cartEntryService.findByCartId("C1"))
                .thenReturn(null);
        OrderDto response = orderService.createOrder("C1", "CARD");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart is empty or not found", response.getMessage());
        Mockito.verify(orderRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateStatusTest() {
        Order order = new Order();
        Mockito.when(orderRepository.findByIdentifier("ORD1"))
                .thenReturn(order);
        Mockito.when(modelMapper.map(order, OrderDto.class))
                .thenReturn(new OrderDto());
        OrderDto response = orderService.updateStatus("ORD1", "PENDING");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Order status updated", response.getMessage());
        Mockito.verify(orderRepository).save(order);
    }

    @Test
    void updateStatusFailureTest() {
        Mockito.when(orderRepository.findByIdentifier("ORD1"))
                .thenReturn(null);
        OrderDto response = orderService.updateStatus("ORD1", "PENDING");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Order not found - ORD1", response.getMessage());
        Mockito.verify(orderRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Order order = new Order();
        List<OrderItem> items = List.of(new OrderItem());
        Type itemListType = new TypeToken<List<OrderItemDto>>() {
        }.getType();
        Mockito.when(orderRepository.findByIdentifier("ORD1"))
                .thenReturn(order);
        Mockito.when(orderItemRepository.findByOrderIdentifier("ORD1"))
                .thenReturn(items);
        Mockito.when(modelMapper.map(
                        Mockito.any(Order.class),
                        Mockito.eq(OrderDto.class)))
                .thenReturn(new OrderDto());
        Mockito.when(modelMapper.map(items, itemListType))
                .thenReturn(List.of(new OrderItemDto()));
        OrderDto response = orderService.findByIdentifier("ORD1");
        Assertions.assertNotNull(response);
        Mockito.verify(orderItemRepository)
                .findByOrderIdentifier("ORD1");
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(orderRepository.findByIdentifier("ORD1"))
                .thenReturn(null);
        OrderDto response = orderService.findByIdentifier("ORD1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Order not found", response.getMessage());
    }

    @Test
    void findAllTest() {
        List<Order> orders = List.of(new Order());
        List<OrderDto> dtos = List.of(new OrderDto());
        Type type = new TypeToken<List<OrderDto>>() {
        }.getType();
        Mockito.when(orderRepository.findAll())
                .thenReturn(orders);
        Mockito.when(modelMapper.map(orders, type))
                .thenReturn(dtos);
        List<OrderDto> response = orderService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTest() {
        List<OrderItem> items = List.of(new OrderItem());
        Mockito.when(orderItemRepository.findByOrderIdentifier("ORD1"))
                .thenReturn(items);
        orderService.delete("ORD1");
        Mockito.verify(orderItemRepository)
                .deleteAll(items);
        Mockito.verify(orderRepository)
                .deleteByIdentifier("ORD1");
    }
}