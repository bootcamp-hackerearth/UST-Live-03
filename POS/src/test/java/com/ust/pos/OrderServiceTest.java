package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderDto inputOrderDto;
    private Order orderEntity;
    private OrderEntryDto entryDto;
    private OrderEntry entryEntity;

    @BeforeEach
    void setUp() {
        inputOrderDto = new OrderDto();
        inputOrderDto.setCustomerIdentifier("CUST-123");

        entryDto = new OrderEntryDto();
        List<OrderEntryDto> entryList = new ArrayList<>();
        entryList.add(entryDto);
        inputOrderDto.setEntryList(entryList);

        orderEntity = new Order();
        orderEntity.setIdentifier("ORDER-UUID");

        entryEntity = new OrderEntry();
        entryEntity.setIdentifier("ENTRY-UUID");
    }

    @Test
    void processCheckout_Success() {
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(orderEntity);
        when(orderEntryRepository.saveAndFlush(any(OrderEntry.class))).thenReturn(entryEntity);
        doNothing().when(cartRepository).deleteByIdentifier("CUST-123");

        OrderDto result = orderService.processCheckout(inputOrderDto);

        assertNotNull(result);
        assertEquals("ORDER-UUID", result.getIdentifier());
        assertNotNull(result.getEntryList());
        assertEquals(1, result.getEntryList().size());

        verify(orderRepository, times(1)).saveAndFlush(any(Order.class));
        verify(orderEntryRepository, times(1)).saveAndFlush(any(OrderEntry.class));
        verify(cartRepository, times(1)).deleteByIdentifier("CUST-123");
    }

    @Test
    void processCheckout_WithNullEntryList() {
        inputOrderDto.setEntryList(null);
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(orderEntity);

        OrderDto result = orderService.processCheckout(inputOrderDto);

        assertNotNull(result);
        assertTrue(result.getEntryList().isEmpty());
        verify(orderEntryRepository, never()).saveAndFlush(any(OrderEntry.class));
        verify(cartRepository, times(1)).deleteByIdentifier("CUST-123");
    }

    @Test
    void getOrderDetails_Success() {
        String orderId = "ORDER-UUID";
        when(orderRepository.findByIdentifier(orderId)).thenReturn(orderEntity);
        when(orderEntryRepository.findByOrderIdentifier(orderId)).thenReturn(Collections.singletonList(entryEntity));

        OrderDto result = orderService.getOrderDetails(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getIdentifier());
        assertEquals(1, result.getEntryList().size());
    }

    @Test
    void getOrderDetails_ThrowsException_WhenOrderNotFound() {
        String orderId = "NON-EXISTENT";
        when(orderRepository.findByIdentifier(orderId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrderDetails(orderId);
        });

        assertEquals("Requested invoice does not exist.", exception.getMessage());
        verify(orderEntryRepository, never()).findByOrderIdentifier(anyString());
    }

    @Test
    void getAllOrdersList_Success() {
        List<Order> orders = new ArrayList<>();
        orders.add(orderEntity);
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderDto> result = orderService.getAllOrdersList();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }
}