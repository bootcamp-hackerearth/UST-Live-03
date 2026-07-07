package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Order;
import com.ust.pos.models.OrderEntry;
import com.ust.pos.models.OrderEntryRepository;
import com.ust.pos.models.OrderRepository;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkoutTest() {
        OrderDto emptyDto = new OrderDto();
        emptyDto.setEntryList(new ArrayList<>());
        OrderDto result = orderService.checkout(emptyDto);
        assertFalse(result.isSuccess());
        assertEquals("Cannot process checkout: Transaction item list is empty", result.getMessage());
        OrderEntryDto entry = new OrderEntryDto();
        entry.setProductIdentifier("P1");
        entry.setQuantity(1);
        entry.setUnitPrice(BigDecimal.valueOf(100));
        entry.setTotalPrice(BigDecimal.valueOf(100));
        OrderDto cashDto = new OrderDto();
        cashDto.setEntryList(List.of(entry));
        cashDto.setCustomerIdentifier("C1");
        cashDto.setTotalPrice(BigDecimal.valueOf(100));
        cashDto.setPaymentMethod("CASH");
        cashDto.setReceivedAmount(BigDecimal.valueOf(150));
        Order savedOrder = new Order();
        OrderDto mappedDto = new OrderDto();
        when(orderRepository.save(any())).thenReturn(savedOrder);
        when(orderEntryRepository.findByOrderIdentifier(any())).thenReturn(List.of(new OrderEntry()));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(mappedDto);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(new OrderEntryDto()));
        result = orderService.checkout(cashDto);
        assertTrue(result.isSuccess());
        OrderDto cardDto = new OrderDto();
        cardDto.setEntryList(List.of(entry));
        cardDto.setTotalPrice(BigDecimal.valueOf(100));
        cardDto.setPaymentMethod("CARD");
        result = orderService.checkout(cardDto);
        assertTrue(result.isSuccess());
        verify(orderRepository, times(2)).save(any());
        verify(orderEntryRepository, times(2)).save(any());
    }

    @Test
    void getTest() {
        Order order = new Order();
        order.setIdentifier("ORD-1");
        OrderDto dto = new OrderDto();
        when(orderRepository.findByIdentifier("ORD-1")).thenReturn(order);
        when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);
        when(orderEntryRepository.findByOrderIdentifier("ORD-1")).thenReturn(List.of(new OrderEntry()));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(new OrderEntryDto()));
        OrderDto result = orderService.get("ORD-1");
        assertNotNull(result);
        when(orderRepository.findByIdentifier("X")).thenReturn(null);
        result = orderService.get("X");
        assertFalse(result.isSuccess());
        assertEquals("Order registry trace not found", result.getMessage());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Order> orders = List.of(new Order());
        Page<Order> page = new PageImpl<>(orders, pageable, 1);
        when(orderRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(orders), any(Type.class))).thenReturn(List.of(new OrderDto()));
        WsDto<OrderDto> result = orderService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(5, result.getSizePerPage());
    }

    @Test
    void deleteTest() {
        boolean result = orderService.delete("ORD-1");
        assertTrue(result);
        verify(orderEntryRepository).deleteByOrderIdentifier("ORD-1");
        verify(orderRepository).deleteByIdentifier("ORD-1");
    }
}