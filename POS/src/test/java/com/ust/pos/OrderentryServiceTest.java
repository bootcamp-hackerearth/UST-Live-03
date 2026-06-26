package com.ust.pos;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.orderentry.service.impl.OrderentryServiceImpl;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderentryServiceTest {

    @InjectMocks
    private OrderentryServiceImpl service;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<OrderEntry> page = new PageImpl<>(List.of(new OrderEntry()), pageable, 1);

        when(orderEntryRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        WsDto<OrderEntryDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierFoundTest() {
        OrderEntry entity = new OrderEntry();
        OrderEntryDto dto = new OrderEntryDto();

        when(orderEntryRepository.findByIdentifier("E1")).thenReturn(entity);
        when(modelMapper.map(entity, OrderEntryDto.class)).thenReturn(dto);

        OrderEntryDto result = service.findByIdentifier("E1");

        assertNotNull(result);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        when(orderEntryRepository.findByIdentifier("E1")).thenReturn(null);

        OrderEntryDto result = service.findByIdentifier("E1");

        assertNull(result);
    }

    @Test
    void findByOrderIdTest() {
        when(orderEntryRepository.findByOrderId("O1")).thenReturn(List.of(new OrderEntry()));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        List<OrderEntryDto> result = service.findByOrderId("O1");

        assertEquals(1, result.size());
    }
}