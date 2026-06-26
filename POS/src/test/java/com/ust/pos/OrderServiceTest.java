package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
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
    void checkoutCartNotFoundTest() {
        OrderDto request = new OrderDto();
        request.setCustomerIdentifier("CART001");
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(null);
        OrderDto response = orderService.checkout(request);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart not found", response.getMessage());
    }

    @Test
    void checkoutCartEmptyTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        OrderDto request = new OrderDto();
        request.setCustomerIdentifier("CART001");
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCartId("CART001")).thenReturn(Collections.emptyList());
        OrderDto response = orderService.checkout(request);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart is empty", response.getMessage());
    }

    @Test
    void checkoutCashSuccessTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        cart.setOriginalPrice(new BigDecimal("100"));
        cart.setDiscount(new BigDecimal("10"));
        cart.setTotalPrice(new BigDecimal("90"));
        CartEntry cartEntry = new CartEntry();
        cartEntry.setProductId("PROD001");
        cartEntry.setMrp(new BigDecimal("100"));
        cartEntry.setSellingPrice(new BigDecimal("90"));
        cartEntry.setDiscount(new BigDecimal("10"));
        cartEntry.setQuantity(BigDecimal.ONE);
        cartEntry.setTotalPrice(new BigDecimal("90"));
        OrderDto request = new OrderDto();
        request.setCustomerIdentifier("CART001");
        request.setPaymentMethod("CASH");
        request.setReceivedAmount(new BigDecimal("100"));
        OrderDto mappedDto = new OrderDto();
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCartId("CART001")).thenReturn(List.of(cartEntry));
        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(mappedDto);
        Mockito.when(orderEntryRepository.findByOrderIdentifier(Mockito.anyString()))
                .thenReturn(Collections.emptyList());
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class)))
                .thenReturn(Collections.emptyList());
        OrderDto response = orderService.checkout(request);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Order placed successfully", response.getMessage());
        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
        Mockito.verify(orderEntryRepository).save(Mockito.any(OrderEntry.class));
        Mockito.verify(cartRepository).save(cart);
        Mockito.verify(cartEntryRepository).deleteAll(Mockito.anyList());
    }

    @Test
    void checkoutCardSuccessTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        cart.setOriginalPrice(new BigDecimal("200"));
        cart.setDiscount(new BigDecimal("20"));
        cart.setTotalPrice(new BigDecimal("180"));
        CartEntry cartEntry = new CartEntry();
        cartEntry.setProductId("PROD001");
        cartEntry.setQuantity(BigDecimal.ONE);
        cartEntry.setMrp(new BigDecimal("200"));
        cartEntry.setSellingPrice(new BigDecimal("180"));
        cartEntry.setDiscount(new BigDecimal("20"));
        cartEntry.setTotalPrice(new BigDecimal("180"));
        OrderDto request = new OrderDto();
        request.setCustomerIdentifier("CART001");
        request.setPaymentMethod("CARD");
        OrderDto mappedDto = new OrderDto();
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCartId("CART001")).thenReturn(List.of(cartEntry));
        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(mappedDto);
        Mockito.when(orderEntryRepository.findByOrderIdentifier(Mockito.anyString())).thenReturn(Collections.emptyList());
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(Collections.emptyList());
        OrderDto response = orderService.checkout(request);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Order placed successfully", response.getMessage());
    }

    @Test
    void getSuccessTest() {
        Order order = new Order();
        order.setIdentifier("ORD001");
        OrderDto dto = new OrderDto();
        dto.setIdentifier("ORD001");
        OrderEntry entry = new OrderEntry();
        OrderEntryDto entryDto = new OrderEntryDto();
        Mockito.when(orderRepository.findByIdentifier("ORD001")).thenReturn(order);
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);
        Mockito.when(orderEntryRepository.findByOrderIdentifier("ORD001")).thenReturn(List.of(entry));
        Mockito.when(modelMapper.map(Mockito.eq(List.of(entry)), Mockito.any(Type.class))).thenReturn(List.of(entryDto));
        OrderDto response = orderService.get("ORD001");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("ORD001", response.getIdentifier());
        Assertions.assertEquals(1, response.getEntryList().size());
    }

    @Test
    void getOrderNotFoundTest() {
        Mockito.when(orderRepository.findByIdentifier("ORD001")).thenReturn(null);
        OrderDto response = orderService.get("ORD001");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Order not found", response.getMessage());
    }

    @Test
    void findAllTest() {
        Order order = new Order();
        order.setIdentifier("ORD001");
        OrderDto dto = new OrderDto();
        dto.setIdentifier("ORD001");
        Pageable pageable = PageRequest.of(0, 5);
        Page<Order> page = new PageImpl<>(List.of(order));
        Mockito.when(orderRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(List.of(order)), Mockito.any(Type.class))).thenReturn(List.of(dto));
        PaginationResponseDto<OrderDto> response = orderService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(orderEntryRepository).deleteByOrderIdentifier("ORD001");
        Mockito.doNothing().when(orderRepository).deleteByIdentifier("ORD001");
        boolean result = orderService.delete("ORD001");
        Assertions.assertTrue(result);
        Mockito.verify(orderEntryRepository).deleteByOrderIdentifier("ORD001");
        Mockito.verify(orderRepository).deleteByIdentifier("ORD001");
    }
}