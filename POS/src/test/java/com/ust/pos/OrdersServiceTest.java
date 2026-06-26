package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.model.*;
import com.ust.pos.orders.service.impl.OrdersServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

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

    @InjectMocks
    private OrdersServiceImpl ordersService;

    @Test
    void generateOrderId_withCartIdentifier_stripsNonDigits() {
        String orderId = ordersService.generateOrderId("CART-123");

        Assertions.assertTrue(orderId.startsWith("ORD-123-"));
    }

    @Test
    void generateOrderId_withNoCartIdentifier_usesWalkin() {
        String orderId = ordersService.generateOrderId(null);

        Assertions.assertTrue(orderId.startsWith("ORD-WALKIN-"));
    }

    @Test
    void generateOrderId_withBlankCartIdentifier_usesWalkin() {
        String orderId = ordersService.generateOrderId("   ");

        Assertions.assertTrue(orderId.startsWith("ORD-WALKIN-"));
    }

    @Test
    void placeOrder_createsOrderAndEntries_andClearsCart() {
        Cart cart = new Cart();
        cart.setIdentifier("CART-123");

        CartEntry entry = new CartEntry();
        entry.setProduct("PROD1");

        Orders order = new Orders();
        OrderEntry orderEntry = new OrderEntry();

        Mockito.when(cartRepository.findByIdentifier("CART-123")).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCart("CART-123")).thenReturn(List.of(entry));
        Mockito.when(modelMapper.map(cart, Orders.class)).thenReturn(order);
        Mockito.when(modelMapper.map(entry, OrderEntry.class)).thenReturn(orderEntry);

        OrdersDto orderDto = new OrdersDto();
        Mockito.when(modelMapper.map(order, OrdersDto.class)).thenReturn(orderDto);

        Type listType = new org.modelmapper.TypeToken<List<OrderEntryDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(Mockito.anyList(), Mockito.eq(listType)))
                .thenReturn(List.of(new OrderEntryDto()));

        OrdersDto response = ordersService.placeOrder("CART-123", "Cash");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Cash", order.getPaymentMode());
        Assertions.assertNull(order.getId());
        Assertions.assertNotNull(order.getOrderId());
        Assertions.assertTrue(order.getOrderId().startsWith("ORD-123-"));

        Mockito.verify(ordersRepository, Mockito.times(1)).save(order);
        Mockito.verify(orderEntryRepository, Mockito.times(1)).saveAll(Mockito.anyList());
        Mockito.verify(cartEntryService, Mockito.times(1)).deleteAllByCart("CART-123");
        Mockito.verify(cartService, Mockito.times(1)).recalculate("CART-123");
        Assertions.assertEquals(1, response.getOrderEntryDtoList().size());
    }

    @Test
    void findAll_returnsOrdersWithEntries() {
        Orders order = new Orders();
        order.setOrderId("ORD-1");

        OrdersDto orderDto = new OrdersDto();
        OrderEntry entry = new OrderEntry();

        Mockito.when(ordersRepository.findAllByOrderByOrderDateDesc()).thenReturn(List.of(order));
        Mockito.when(modelMapper.map(order, OrdersDto.class)).thenReturn(orderDto);
        Mockito.when(orderEntryRepository.findByOrderId("ORD-1")).thenReturn(List.of(entry));

        Type listType = new org.modelmapper.TypeToken<List<OrderEntryDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(Mockito.anyList(), Mockito.eq(listType)))
                .thenReturn(List.of(new OrderEntryDto()));

        List<OrdersDto> response = ordersService.findAll();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(1, response.get(0).getOrderEntryDtoList().size());
    }

    @Test
    void findAll_noOrders_returnsEmptyList() {
        Mockito.when(ordersRepository.findAllByOrderByOrderDateDesc()).thenReturn(List.of());

        List<OrdersDto> response = ordersService.findAll();

        Assertions.assertTrue(response.isEmpty());
    }
}