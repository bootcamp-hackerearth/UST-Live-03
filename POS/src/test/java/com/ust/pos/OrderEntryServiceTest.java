package com.ust.pos;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.orderentry.impl.OrderEntryServiceImpl;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEntryServiceTest {

    @InjectMocks
    private OrderEntryServiceImpl orderEntryService;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {

        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setIdentifier("ORD001");

        OrderEntryDto dto = new OrderEntryDto();
        dto.setIdentifier("ORD001");

        when(orderEntryRepository.findByIdentifier("ORD001"))
                .thenReturn(orderEntry);

        when(modelMapper.map(orderEntry, OrderEntryDto.class))
                .thenReturn(dto);

        OrderEntryDto response =
                orderEntryService.findByIdentifier("ORD001");

        assertNotNull(response);
        assertEquals("ORD001", response.getIdentifier());

        verify(orderEntryRepository)
                .findByIdentifier("ORD001");
    }

    @Test
    void findByIdentifierFailureTest() {

        when(orderEntryRepository.findByIdentifier("ORD001"))
                .thenReturn(null);

        OrderEntryDto response =
                orderEntryService.findByIdentifier("ORD001");

        assertNull(response);

        verify(modelMapper, never())
                .map(any(), eq(OrderEntryDto.class));
    }

    @Test
    void findOrderEntryByOrderIdSuccessTest() {

        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setOrderId("ORD001");

        OrderEntryDto dto = new OrderEntryDto();
        dto.setOrderId("ORD001");

        when(orderEntryRepository.findByOrderId("ORD001"))
                .thenReturn(List.of(orderEntry));

        when(modelMapper.map(orderEntry, OrderEntryDto.class))
                .thenReturn(dto);

        List<OrderEntryDto> response =
                orderEntryService.findOrderEntryByOrderId("ORD001");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("ORD001",
                response.get(0).getOrderId());

        verify(orderEntryRepository)
                .findByOrderId("ORD001");
    }

    @Test
    void findOrderEntryByOrderIdNullTest() {

        when(orderEntryRepository.findByOrderId("ORD001"))
                .thenReturn(null);

        List<OrderEntryDto> response =
                orderEntryService.findOrderEntryByOrderId("ORD001");

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(modelMapper, never())
                .map(any(), eq(OrderEntryDto.class));
    }

    @Test
    void findOrderEntryByOrderIdEmptyListTest() {

        when(orderEntryRepository.findByOrderId("ORD001"))
                .thenReturn(List.of());

        List<OrderEntryDto> response =
                orderEntryService.findOrderEntryByOrderId("ORD001");

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        OrderEntry orderEntry = new OrderEntry();
        OrderEntryDto dto = new OrderEntryDto();

        Page<OrderEntry> page =
                new PageImpl<>(List.of(orderEntry), pageable, 1);

        when(orderEntryRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(orderEntry, OrderEntryDto.class))
                .thenReturn(dto);

        List<OrderEntryDto> result =
                orderEntryService.findAll(pageable)
                        .getContent();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(orderEntryRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(orderEntry, OrderEntryDto.class);
    }
}