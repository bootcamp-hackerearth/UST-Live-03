package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
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
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Test
    void generateOrderId_withValidCartIdentifier() {

        String result = orderService.generateOrderId("CART-123");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.startsWith("ORD-123-"));
    }

    @Test
    void generateOrderId_withNullCartIdentifier() {

        String result = orderService.generateOrderId(null);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.startsWith("ORD-WALKIN-"));
    }

    @Test
    void generateOrderId_withEmptyCartIdentifier() {

        String result = orderService.generateOrderId("   ");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.startsWith("ORD-WALKIN-"));
    }

    @Test
    void placeOrderTest() {

        String cartIdentifier = "CART-001";
        String paymentMode = "CASH";

        Cart cart = new Cart();
        cart.setIdentifier(cartIdentifier);
        cart.setTotalPrice(new BigDecimal("500"));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setCart(cartIdentifier);

        Order order = new Order();
        order.setIdentifier(cartIdentifier);

        OrderEntry orderEntry = new OrderEntry();

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier(cartIdentifier);

        OrderEntryDto orderEntryDto = new OrderEntryDto();

        Type listType = new TypeToken<List<OrderEntryDto>>() {}.getType();

        Mockito.when(cartRepository.findByIdentifier(cartIdentifier))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCart(cartIdentifier))
                .thenReturn(List.of(cartEntry));

        Mockito.when(modelMapper.map(cart, Order.class))
                .thenReturn(order);

        Mockito.when(orderRepository.save(order))
                .thenReturn(order);

        Mockito.when(modelMapper.map(cartEntry, OrderEntry.class))
                .thenReturn(orderEntry);

        Mockito.when(orderEntryRepository.saveAll(Mockito.anyList()))
                .thenReturn(List.of(orderEntry));

        Mockito.when(modelMapper.map(order, OrderDto.class))
                .thenReturn(orderDto);

        Mockito.when(modelMapper.map(Mockito.anyList(), Mockito.eq(listType)))
                .thenReturn(List.of(orderEntryDto));

        OrderDto result = orderService.placeOrder(cartIdentifier, paymentMode);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(cartIdentifier, result.getIdentifier());
        Assertions.assertEquals(1, result.getOrderEntryDtoList().size());

        Mockito.verify(orderRepository).save(order);
        Mockito.verify(orderEntryRepository).saveAll(Mockito.anyList());
        Mockito.verify(cartEntryService).deleteAllByCart(cartIdentifier);
        Mockito.verify(cartService).recalculate(cartIdentifier);
    }

    @Test
    void placeOrderTest_setsOrderIdAndPaymentMode() {

        String cartIdentifier = "9876543210";
        String paymentMode = "CARD";

        Cart cart = new Cart();
        cart.setIdentifier(cartIdentifier);

        Order order = new Order();

        OrderDto orderDto = new OrderDto();

        Type listType = new TypeToken<List<OrderEntryDto>>() {}.getType();

        Mockito.when(cartRepository.findByIdentifier(cartIdentifier))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCart(cartIdentifier))
                .thenReturn(List.of());

        Mockito.when(modelMapper.map(cart, Order.class))
                .thenReturn(order);

        Mockito.when(orderRepository.save(order))
                .thenReturn(order);

        Mockito.when(modelMapper.map(order, OrderDto.class))
                .thenReturn(orderDto);

        Mockito.when(modelMapper.map(Mockito.anyList(), Mockito.eq(listType)))
                .thenReturn(List.of());

        orderService.placeOrder(cartIdentifier, paymentMode);

        Assertions.assertNull(order.getId());
        Assertions.assertNotNull(order.getOrderId());
        Assertions.assertTrue(order.getOrderId().startsWith("ORD-9876543210-"));
        Assertions.assertEquals("CARD", order.getPaymentMode());
        Assertions.assertNotNull(order.getOrderDate());
    }

    @Test
    void findAllTest() {

        Order order = new Order();
        order.setOrderId("ORD-001");

        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setOrderId("ORD-001");

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId("ORD-001");

        OrderEntryDto orderEntryDto = new OrderEntryDto();

        Type listType = new TypeToken<List<OrderEntryDto>>() {}.getType();

        Mockito.when(orderRepository.findAllByOrderByOrderDateDesc())
                .thenReturn(List.of(order));

        Mockito.when(modelMapper.map(order, OrderDto.class))
                .thenReturn(orderDto);

        Mockito.when(orderEntryRepository.findByOrderId("ORD-001"))
                .thenReturn(List.of(orderEntry));

        Mockito.when(modelMapper.map(List.of(orderEntry), listType))
                .thenReturn(List.of(orderEntryDto));

        List<OrderDto> result = orderService.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("ORD-001", result.get(0).getOrderId());
        Assertions.assertEquals(1, result.get(0).getOrderEntryDtoList().size());

        Mockito.verify(orderRepository).findAllByOrderByOrderDateDesc();
        Mockito.verify(orderEntryRepository).findByOrderId("ORD-001");
    }

    @Test
    void findAllTest_empty() {

        Mockito.when(orderRepository.findAllByOrderByOrderDateDesc())
                .thenReturn(List.of());

        List<OrderDto> result = orderService.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(orderRepository).findAllByOrderByOrderDateDesc();
        Mockito.verify(orderEntryRepository, Mockito.never())
                .findByOrderId(Mockito.anyString());
    }
}