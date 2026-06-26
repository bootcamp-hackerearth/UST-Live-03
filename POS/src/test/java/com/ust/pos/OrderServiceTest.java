package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.*;
import com.ust.pos.modell.Order;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

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
    void testCheckout_EmptyEntries() {
        OrderDto dto = new OrderDto();
        dto.setEntryList(new ArrayList<>());
        OrderDto result = orderService.checkout(dto);
        assertFalse(result.isSuccess());
        assertEquals("Cannot process checkout: Transaction item list is empty", result.getMessage());
    }

    @Test
    void testCheckout_CashPayment_WithChange() {
        OrderEntryDto entry = new OrderEntryDto();
        entry.setProductIdentifier("P1");
        entry.setQuantity(1);
        entry.setUnitPrice(BigDecimal.valueOf(100));
        entry.setTotalPrice(BigDecimal.valueOf(100));
        OrderDto dto = new OrderDto();
        dto.setEntryList(List.of(entry));
        dto.setCustomerIdentifier("C1");
        dto.setTotalPrice(BigDecimal.valueOf(100));
        dto.setPaymentMethod("CASH");
        dto.setReceivedAmount(BigDecimal.valueOf(150));
        Order order = new Order();
        OrderDto mappedDto = new OrderDto();
        when(orderRepository.save(any())).thenReturn(order);
        when(orderEntryRepository.findByOrderIdentifier(any())).thenReturn(List.of(new OrderEntry()));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(mappedDto);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(new OrderEntryDto()));
        OrderDto result = orderService.checkout(dto);
        assertTrue(result.isSuccess());
        verify(orderRepository).save(any());
        verify(orderEntryRepository).save(any());
    }

    @Test
    void testCheckout_CardPayment_NoChange() {
        OrderEntryDto entry = new OrderEntryDto();
        entry.setProductIdentifier("P1");
        entry.setQuantity(2);
        entry.setUnitPrice(BigDecimal.valueOf(50));
        entry.setTotalPrice(BigDecimal.valueOf(100));
        OrderDto dto = new OrderDto();
        dto.setEntryList(List.of(entry));
        dto.setTotalPrice(BigDecimal.valueOf(100));
        dto.setPaymentMethod("CARD");
        Order order = new Order();
        OrderDto mapped = new OrderDto();
        when(orderRepository.save(any())).thenReturn(order);
        when(orderEntryRepository.findByOrderIdentifier(any())).thenReturn(List.of(new OrderEntry()));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(mapped);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(new OrderEntryDto()));
        OrderDto result = orderService.checkout(dto);
        assertTrue(result.isSuccess());
    }

    @Test
    void testGet_Success() {
        Order order = new Order();
        order.setIdentifier("ORD-1");
        OrderDto dto = new OrderDto();
        when(orderRepository.findByIdentifier("ORD-1")).thenReturn(order);
        when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);
        when(orderEntryRepository.findByOrderIdentifier("ORD-1")).thenReturn(List.of(new OrderEntry()));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(new OrderEntryDto()));
        OrderDto result = orderService.get("ORD-1");
        assertNotNull(result);
    }

    @Test
    void testGet_NotFound() {
        when(orderRepository.findByIdentifier("X")).thenReturn(null);
        OrderDto result = orderService.get("X");
        assertFalse(result.isSuccess());
        assertEquals("Order registry trace not found", result.getMessage());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Order> orders = List.of(new Order());
        Page<Order> page = new PageImpl<>(orders, pageable, 1);
        List<OrderDto> dtoList = List.of(new OrderDto());
        when(orderRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(orders), any(Type.class))).thenReturn(dtoList);
        WsDto<OrderDto> result = orderService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(5, result.getSizePerPage());
    }

    @Test
    void testDelete() {
        boolean result = orderService.delete("ORD-1");
        assertTrue(result);
        verify(orderEntryRepository).deleteByOrderIdentifier("ORD-1");
        verify(orderRepository).deleteByIdentifier("ORD-1");
    }
}
