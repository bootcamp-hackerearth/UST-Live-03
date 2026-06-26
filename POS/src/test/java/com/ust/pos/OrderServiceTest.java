package com.ust.pos;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderItemDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

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

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {

        order = new Order();
        order.setIdentifier("ORD001");

        orderDto = new OrderDto();
        orderDto.setIdentifier("ORD001");
    }

    @Test
    void createOrderCartNotFoundTest() {

        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(null);

        OrderDto response =
                orderService.createOrder("CART001", "CASH");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Cart is empty or not found",
                response.getMessage());
    }

    @Test
    void createOrderSuccessTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        cart.setTotalPrice(BigDecimal.valueOf(100));
        cart.setDiscount(BigDecimal.valueOf(10));

        CartEntryDto entry = new CartEntryDto();
        entry.setProduct("PROD001");
        entry.setQuantity(BigDecimal.ONE);
        entry.setUnitPrice(BigDecimal.valueOf(90));
        entry.setTotalPrice(BigDecimal.valueOf(90));

        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        Mockito.when(cartEntryService.findByCartId("CART001"))
                .thenReturn(List.of(entry));

        Mockito.when(modelMapper.map(
                        Mockito.any(Order.class),
                        Mockito.eq(OrderDto.class)))
                .thenReturn(orderDto);

        Mockito.when(modelMapper.map(
                        Mockito.anyList(),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(new OrderItemDto()));

        OrderDto response =
                orderService.createOrder("CART001", "CASH");

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(orderRepository)
                .save(Mockito.any(Order.class));

        Mockito.verify(orderItemRepository)
                .saveAll(Mockito.anyList());

        Mockito.verify(cartEntryService)
                .deleteAll("CART001");

        Mockito.verify(cartRepository)
                .deleteByIdentifier("CART001");
    }

    @Test
    void updateStatusOrderNotFoundTest() {

        Mockito.when(
                        orderRepository.findByIdentifierAndDeletedFalse(
                                "ORD001"))
                .thenReturn(null);

        OrderDto response =
                orderService.updateStatus(
                        "ORD001",
                        "CONFIRMED");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(
                response.getMessage().contains("Order not found"));
    }

    @Test
    void updateStatusSuccessTest() {

        Mockito.when(
                        orderRepository.findByIdentifierAndDeletedFalse(
                                "ORD001"))
                .thenReturn(order);

        Mockito.when(
                        modelMapper.map(order, OrderDto.class))
                .thenReturn(orderDto);

        OrderDto response =
                orderService.updateStatus(
                        "ORD001",
                        "DELIVERED");

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(orderRepository)
                .save(order);
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(
                        orderRepository.findByIdentifierAndDeletedFalse(
                                "ORD001"))
                .thenReturn(null);

        OrderDto response =
                orderService.findByIdentifier("ORD001");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Order not found",
                response.getMessage());
    }

    @Test
    void findByIdentifierSuccessTest() {

        List<OrderItem> items =
                List.of(new OrderItem());

        Mockito.when(
                        orderRepository.findByIdentifierAndDeletedFalse(
                                "ORD001"))
                .thenReturn(order);

        Mockito.when(
                        modelMapper.map(order, OrderDto.class))
                .thenReturn(orderDto);

        Mockito.when(
                        orderItemRepository
                                .findByOrderIdentifierAndDeletedFalse(
                                        "ORD001"))
                .thenReturn(items);

        Mockito.when(
                        modelMapper.map(
                                Mockito.eq(items),
                                Mockito.any(Type.class)))
                .thenReturn(List.of(new OrderItemDto()));

        OrderDto response =
                orderService.findByIdentifier("ORD001");

        Assertions.assertNotNull(response);
    }

    @Test
    void findAllTest() {

        List<Order> orders =
                List.of(order);

        List<OrderDto> dtos =
                List.of(orderDto);

        Type listType =
                new TypeToken<List<OrderDto>>() {
                }.getType();

        Mockito.when(orderRepository.findAll())
                .thenReturn(orders);

        Mockito.when(
                        modelMapper.map(
                                orders,
                                listType))
                .thenReturn(dtos);

        List<OrderDto> response =
                orderService.findAll();

        Assertions.assertEquals(
                1,
                response.size());
    }

    @Test
    void findAllWithSearchTest() {

        Page<Order> page =
                new PageImpl<>(List.of(order));

        Mockito.when(
                        orderRepository
                                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                                        Mockito.eq("ORD"),
                                        Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(
                        modelMapper.map(
                                order,
                                OrderDto.class))
                .thenReturn(orderDto);

        Page<OrderDto> response =
                orderService.findAll(
                        "ORD",
                        PageRequest.of(0, 10));

        Assertions.assertEquals(
                1,
                response.getContent().size());
    }

    @Test
    void findAllWithoutSearchTest() {

        Page<Order> page =
                new PageImpl<>(List.of(order));

        Mockito.when(
                        orderRepository.findAll(
                                Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(
                        modelMapper.map(
                                order,
                                OrderDto.class))
                .thenReturn(orderDto);

        Page<OrderDto> response =
                orderService.findAll(
                        null,
                        PageRequest.of(0, 10));

        Assertions.assertEquals(
                1,
                response.getContent().size());
    }

    @Test
    void deleteSuccessTest() {

        List<OrderItem> items =
                List.of(new OrderItem());

        Mockito.when(
                        orderItemRepository
                                .findByOrderIdentifierAndDeletedFalse(
                                        "ORD001"))
                .thenReturn(items);

        Mockito.when(
                        orderRepository
                                .findByIdentifierAndDeletedFalse(
                                        "ORD001"))
                .thenReturn(order);

        orderService.delete("ORD001");

        Mockito.verify(orderItemRepository)
                .deleteAll(items);

        Mockito.verify(orderRepository)
                .save(order);

        Assertions.assertTrue(order.isDeleted());
    }

    @Test
    void deleteOrderNotFoundTest() {

        Mockito.when(
                        orderItemRepository
                                .findByOrderIdentifierAndDeletedFalse(
                                        "ORD001"))
                .thenReturn(List.of());

        Mockito.when(
                        orderRepository
                                .findByIdentifierAndDeletedFalse(
                                        "ORD001"))
                .thenReturn(null);

        orderService.delete("ORD001");

        Mockito.verify(orderItemRepository)
                .deleteAll(Mockito.anyList());
    }
}