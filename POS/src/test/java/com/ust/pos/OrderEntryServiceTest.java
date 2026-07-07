package com.ust.pos;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
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

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<OrderEntry> specification = mock(Specification.class);

        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setIdentifier("OE001");

        OrderEntryDto orderEntryDto = new OrderEntryDto();
        orderEntryDto.setIdentifier("OE001");

        List<OrderEntry> orderEntryList = List.of(orderEntry);

        Page<OrderEntry> page =
                new PageImpl<>(orderEntryList, pageable, 1);

        when(orderEntryRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(orderEntryList), any(Type.class)))
                .thenReturn(List.of(orderEntryDto));

        WsDto<OrderEntryDto> result =
                orderEntryService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("OE001",
                result.getContent().get(0).getIdentifier());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(orderEntryRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(orderEntryList), any(Type.class));
    }

    @Test
    void findAllWithSpecificationEmptyResultTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<OrderEntry> specification = mock(Specification.class);

        Page<OrderEntry> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(orderEntryRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(List.of()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<OrderEntryDto> result =
                orderEntryService.findAll(specification, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(orderEntryRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(List.of()), any(Type.class));
    }
}