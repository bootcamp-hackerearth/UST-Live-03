package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartentryService;
import com.ust.pos.dto.*;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.model.OrderRepository;
import com.ust.pos.model.Orders;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl service;

    @Mock
    private OrderRepository ordersRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private CartentryService cartEntryService;

    @Mock
    private CartService cartService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        OrdersDto dto = new OrdersDto();
        dto.setCustomerId("CART1");

        CartEntryDto cartEntry = new CartEntryDto();
        cartEntry.setProduct("P1");
        cartEntry.setQuantity(new BigDecimal("2"));
        cartEntry.setUnitPrice(new BigDecimal("50"));
        cartEntry.setTotalPrice(new BigDecimal("100"));
        cartEntry.setDiscount(new BigDecimal("10"));
        cartEntry.setOriginalPrice(new BigDecimal("120"));

        CartDto cartDto = new CartDto();
        cartDto.setTotalPrice(new BigDecimal("100"));
        cartDto.setDiscount(new BigDecimal("10"));
        cartDto.setOriginalPrice(new BigDecimal("120"));
        cartDto.setCoupon("DISC");

        when(cartEntryService.findByCartId("CART1")).thenReturn(List.of(cartEntry));
        when(cartService.findByIdentifier("CART1")).thenReturn(cartDto);
        when(ordersRepository.findByIdentifier(any())).thenReturn(null);
        when(modelMapper.map(any(), eq(Orders.class))).thenReturn(new Orders());

        OrdersDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(orderEntryRepository).save(any());
        verify(ordersRepository).save(any());
        verify(cartEntryService).deleteAllByCartId("CART1");
    }

    @Test
    void saveDuplicateActiveTest() {
        OrdersDto dto = new OrdersDto();
        dto.setCustomerId("CART1");

        Orders existing = new Orders();
        existing.setDeleted(false);

        when(ordersRepository.findByIdentifier(any())).thenReturn(existing);

        OrdersDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(ordersRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        OrdersDto dto = new OrdersDto();
        dto.setCustomerId("CART1");

        Orders existing = new Orders();
        existing.setDeleted(true);

        when(ordersRepository.findByIdentifier(any())).thenReturn(existing);

        OrdersDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Orders> page = new PageImpl<>(List.of(new Orders()), pageable, 1);

        when(ordersRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrdersDto()));

        WsDto<OrdersDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierFoundTest() {
        Orders order = new Orders();
        OrdersDto dto = new OrdersDto();

        OrderEntry entry = new OrderEntry();

        when(ordersRepository.findByIdentifier("O1")).thenReturn(order);
        when(modelMapper.map(order, OrdersDto.class)).thenReturn(dto);
        when(orderEntryRepository.findByOrderId("O1")).thenReturn(List.of(entry));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        OrdersDto result = service.findByIdentifier("O1");

        assertNotNull(result);
        assertEquals(1, result.getOrderEntryDtoList().size());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        when(ordersRepository.findByIdentifier("O1")).thenReturn(null);

        assertNull(service.findByIdentifier("O1"));
    }

    @Test
    void getOrderEntriesTest() {
        when(orderEntryRepository.findByOrderId("O1")).thenReturn(List.of(new OrderEntry()));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        List<OrderEntryDto> result = service.getOrderEntries("O1");

        assertEquals(1, result.size());
    }

    @Test
    void generateOrderIdTest() {
        String result = service.generateOrderId("CART123");

        assertTrue(result.startsWith("ORD-"));
        assertTrue(result.contains("CART1"));
    }

    @Test
    void deleteTest() {
        service.delete("O1");

        verify(ordersRepository).deleteByIdentifier("O1");
    }
}