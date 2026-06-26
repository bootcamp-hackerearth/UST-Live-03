package com.ust.pos;


import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

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
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private StockRepository stockRepository;

    @Test
    void testCheckout_Cash_Success() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerIdentifier("CUST-001");
        orderDto.setPaymentMethod("CASH");
        orderDto.setReceivedAmount(BigDecimal.valueOf(1000));

        Cart cart = new Cart();
        cart.setIdentifier("CUST-001");
        cart.setOriginalPrice(BigDecimal.valueOf(1200));
        cart.setDiscount(BigDecimal.valueOf(200));
        cart.setTotalPrice(BigDecimal.valueOf(1000));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setProductIdentifier("PROD-001");
        cartEntry.setMrp(BigDecimal.valueOf(1200));
        cartEntry.setUnitDiscount(BigDecimal.valueOf(200));
        cartEntry.setUnitPrice(BigDecimal.valueOf(1000));
        cartEntry.setQuantity(BigDecimal.ONE);
        cartEntry.setTotalPrice(BigDecimal.valueOf(1000));

        Stock stock = new Stock();
        stock.setProduct("PROD-001");
        stock.setQuantity(10L);

        OrderDto mappedOrderDto = new OrderDto();
        mappedOrderDto.setIdentifier("ORD-TEST");

        when(cartRepository.findByIdentifier("CUST-001")).thenReturn(cart);
        when(cartEntryRepository.findByCartIdentifier("CUST-001"))
                .thenReturn(List.of(cartEntry));

        when(stockRepository.findByProductAndDeletedFalse("PROD-001"))
                .thenReturn(stock);

        when(modelMapper.map(any(Order.class), eq(OrderDto.class)))
                .thenReturn(mappedOrderDto);

        when(orderEntryRepository.findByOrderIdentifier(anyString()))
                .thenReturn(List.of(new OrderEntry()));

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        OrderDto result = orderService.checkout(orderDto);

        assertTrue(result.isSuccess());
        assertEquals("Order placed successfully", result.getMessage());

        assertEquals(9L, stock.getQuantity());
        assertEquals("Low Stock", stock.getStockStatus());

        assertEquals(BigDecimal.ZERO, cart.getOriginalPrice());
        assertEquals(BigDecimal.ZERO, cart.getDiscount());
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());

        verify(orderRepository).save(any(Order.class));
        verify(orderEntryRepository).save(any(OrderEntry.class));
        verify(stockRepository, times(2)).findByProductAndDeletedFalse("PROD-001");
        verify(stockRepository).save(stock);
        verify(cartEntryRepository).deleteAll(List.of(cartEntry));
        verify(cartRepository).save(cart);
    }

    @Test
    void testCheckout_CartNotFound() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerIdentifier("CUST-001");

        when(cartRepository.findByIdentifier("CUST-001")).thenReturn(null);

        OrderDto result = orderService.checkout(orderDto);

        assertFalse(result.isSuccess());
        assertEquals("Cart not found", result.getMessage());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCheckout_CartIsEmpty() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerIdentifier("CUST-001");

        Cart cart = new Cart();
        cart.setIdentifier("CUST-001");

        when(cartRepository.findByIdentifier("CUST-001")).thenReturn(cart);
        when(cartEntryRepository.findByCartIdentifier("CUST-001")).thenReturn(List.of());

        OrderDto result = orderService.checkout(orderDto);

        assertFalse(result.isSuccess());
        assertEquals("Cart is empty", result.getMessage());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGet_Success() {
        String identifier = "ORD-001";

        Order order = new Order();
        order.setIdentifier(identifier);

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier(identifier);

        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setOrderIdentifier(identifier);

        OrderEntryDto orderEntryDto = new OrderEntryDto();

        when(orderRepository.findByIdentifier(identifier)).thenReturn(order);
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);
        when(orderEntryRepository.findByOrderIdentifier(identifier)).thenReturn(List.of(orderEntry));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(orderEntryDto));

        OrderDto result = orderService.get(identifier);

        assertEquals(identifier, result.getIdentifier());
        assertEquals(1, result.getEntryList().size());
    }

    @Test
    void testGet_NotFound() {
        when(orderRepository.findByIdentifier("ORD-001")).thenReturn(null);

        OrderDto result = orderService.get("ORD-001");

        assertFalse(result.isSuccess());
        assertEquals("Order not found", result.getMessage());
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Order order = new Order();
        order.setIdentifier("ORD-001");

        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("ORD-001");

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(orderDto));

        WsDto<OrderDto> result = orderService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testDelete_Success() {
        boolean result = orderService.delete("ORD-001");

        assertTrue(result);

        verify(orderEntryRepository).deleteByOrderIdentifier("ORD-001");
        verify(orderRepository).deleteByIdentifier("ORD-001");
    }
}
