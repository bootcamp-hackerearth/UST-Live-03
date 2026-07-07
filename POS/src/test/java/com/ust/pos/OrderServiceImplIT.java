package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceImplIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEntryRepository orderEntryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @MockitoBean
    private CartEntryService cartEntryService;

    @MockitoBean
    private CartService cartService;

    @BeforeEach
    void cleanUp() {
        orderEntryRepository.deleteAll();
        orderRepository.deleteAll();
        cartEntryRepository.deleteAll();
        cartRepository.deleteAll();
    }

    private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        assertNotNull(actual, "BigDecimal value should not be null");
        assertEquals(0, expected.compareTo(actual), "Expected " + expected + " but got " + actual);
    }

    @Test
    void placeOrder_shouldConvertCartToOrderSuccessfully() {
        Cart cart = new Cart();
        cart.setIdentifier("CART01");
        cart.setTotalPrice(new BigDecimal("500.00"));
        cart.setTotalDiscount(new BigDecimal("50.00"));
        cartRepository.save(cart);

        CartEntry entry = new CartEntry();
        entry.setIdentifier("PROD01-CART01");
        entry.setCart("CART01");
        entry.setProduct("PROD01");
        entry.setQuantity(new BigDecimal("2"));
        cartEntryRepository.save(entry);

        OrderDto response = orderService.placeOrder("CART01", "CASH");

        assertNotNull(response);
        assertTrue(response.isSuccess());

        List<Order> savedOrders = orderRepository.findAll();
        assertEquals(1, savedOrders.size());
        Order savedOrder = savedOrders.get(0);
        assertBigDecimalEquals(new BigDecimal("500.00"), savedOrder.getTotalPrice());
        assertBigDecimalEquals(new BigDecimal("50.00"), savedOrder.getTotalDiscount());

        List<OrderEntry> savedEntries = orderEntryRepository.findByOrderId(savedOrder.getOrderId());
        assertEquals(1, savedEntries.size());
        assertEquals("PROD01", savedEntries.get(0).getProduct());

        Mockito.verify(cartEntryService, Mockito.times(1)).deleteAllByCart("CART01");
        Mockito.verify(cartService, Mockito.times(1)).recalculate("CART01");
    }

    @Test
    void findByOrderId_shouldReturnCompleteOrderDetails() {
        Order order = new Order();
        order.setOrderId("ORD-100");
        order.setTotalPrice(new BigDecimal("200.00"));
        orderRepository.save(order);

        OrderEntry entry = new OrderEntry();
        entry.setOrderId("ORD-100");
        entry.setProduct("PROD02");
        orderEntryRepository.save(entry);

        OrderDto result = orderService.findByOrderId("ORD-100");

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("ORD-100", result.getOrderId());
        assertNotNull(result.getEntryDtoList());
        assertEquals(1, result.getEntryDtoList().size());
    }

    @Test
    void findByOrderId_shouldReturnFailureIfOrderDoesNotExist() {
        OrderDto result = orderService.findByOrderId("NON-EXISTENT");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Order not found", result.getMessage());
    }

    @Test
    void findAll_shouldReturnPaginatedOrders() {
        Order order = new Order();
        order.setOrderId("ORD-200");
        orderRepository.save(order);

        Pageable pageable = PageRequest.of(0, 50);
        WsDto<OrderDto> result = orderService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getDtoList().size());
        assertEquals("ORD-200", result.getDtoList().get(0).getOrderId());
    }
}