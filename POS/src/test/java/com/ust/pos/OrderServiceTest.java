package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import com.ust.pos.stock.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl service;

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
    @Mock
    private StockService stockService;

    @Test
    void checkout_success() {

        OrderDto dto = new OrderDto();
        dto.setCustomer("C1");
        dto.setPaymentMethod("CASH");
        dto.setReceivedAmount(BigDecimal.valueOf(500));

        Cart cart = new Cart();
        cart.setIdentifier("C1");
        cart.setTotalPrice(BigDecimal.valueOf(300));
        cart.setDiscount(BigDecimal.valueOf(50));
        cart.setOriginalPrice(BigDecimal.valueOf(350));

        CartEntry ce = new CartEntry();
        ce.setProductId("P1");
        ce.setProductName("Pen");
        ce.setQuantity(BigDecimal.valueOf(2));
        ce.setSellingPrice(BigDecimal.valueOf(100));
        ce.setDiscount(BigDecimal.valueOf(10));
        ce.setTotalPrice(BigDecimal.valueOf(200));

        Order savedOrder = new Order();
        savedOrder.setIdentifier("ORD-1");

        OrderDto mapped = new OrderDto();
        mapped.setIdentifier("ORD-1");

        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of(ce));

        when(stockService.isStockAvailable("P1", 2)).thenReturn(true);
        when(stockService.reduceStock("P1", 2)).thenReturn(new StockDto() {{
            setSuccess(true);
        }});
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderEntryRepository.findByOrderIdentifier(anyString())).thenReturn(List.of(new OrderEntry()));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(mapped);
        Type type = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        when(modelMapper.map(anyList(), eq(type))).thenReturn(List.of(new OrderEntryDto()));
        OrderDto result = service.checkout(dto);

        assertTrue(result.isSuccess());
        assertEquals("Order placed successfully", result.getMessage());

        verify(orderRepository).save(any(Order.class));
        verify(orderEntryRepository).save(any(OrderEntry.class));
        verify(cartEntryRepository).deleteAll(anyList());
    }

    @Test
    void checkout_cart_not_found() {

        OrderDto dto = new OrderDto();
        dto.setCustomer("C1");

        when(cartRepository.findByIdentifier("C1")).thenReturn(null);
        OrderDto result = service.checkout(dto);

        assertFalse(result.isSuccess());
        assertEquals("Cart not found", result.getMessage());
    }

    @Test
    void checkout_empty_cart() {

        OrderDto dto = new OrderDto();
        dto.setCustomer("C1");

        Cart cart = new Cart();
        cart.setIdentifier("C1");

        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of());
        OrderDto result = service.checkout(dto);

        assertFalse(result.isSuccess());
        assertEquals("Cart is empty", result.getMessage());
    }

    @Test
    void get_success() {

        Order order = new Order();
        order.setIdentifier("ORD-1");

        when(orderRepository.findByIdentifier("ORD-1")).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(new OrderDto());
        when(orderEntryRepository.findByOrderIdentifier("ORD-1")).thenReturn(List.of(new OrderEntry()));
        Type type = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        when(modelMapper.map(anyList(), eq(type))).thenReturn(List.of(new OrderEntryDto()));

        OrderDto result = service.get("ORD-1");
        assertTrue(result.isSuccess());
    }

    @Test
    void get_not_found() {

        when(orderRepository.findByIdentifier("ORD-1")).thenReturn(null);
        OrderDto result = service.get("ORD-1");
        assertFalse(result.isSuccess());
        assertEquals("Order not found", result.getMessage());
    }

    @Test
    void findAll_paged() {

        Order order = new Order();

        Page<Order> page = new PageImpl<>(List.of(order));
        Pageable pageable = PageRequest.of(0, 5);

        when(orderRepository.findAll(pageable)).thenReturn(page);

        Type type = new TypeToken<List<OrderDto>>() {
        }.getType();
        when(modelMapper.map(anyList(), eq(type))).thenReturn(List.of(new OrderDto()));
        WsDto<OrderDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void search_success() {

        Order order = new Order();

        when(orderRepository.searchOrders("test")).thenReturn(List.of(order));

        Type type = new TypeToken<List<OrderDto>>() {
        }.getType();
        when(modelMapper.map(anyList(), eq(type))).thenReturn(List.of(new OrderDto()));
        List<OrderDto> result = service.search("test");

        assertEquals(1, result.size());
    }

    @Test
    void search_empty_query() {

        List<OrderDto> result = service.search("   ");
        assertTrue(result.isEmpty());
    }
}