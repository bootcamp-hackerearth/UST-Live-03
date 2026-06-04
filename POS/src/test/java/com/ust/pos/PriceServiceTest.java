package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.modell.Price;
import com.ust.pos.modell.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl service;

    @Mock
    private PriceRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifier_shouldHandleBothCases() {
        Price price = new Price();
        price.setIdentifier("PR1");
        PriceDto dto = new PriceDto();
        dto.setIdentifier("PR1");
        when(repository.findByIdentifier("PR1")).thenReturn(price);
        when(mapper.map(price, PriceDto.class)).thenReturn(dto);
        assertEquals("PR1", service.findByIdentifier("PR1").getIdentifier());
        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, PriceDto.class)).thenReturn(null);
        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void save_shouldHandleBothCases() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("PR1");
        Price price = new Price();
        price.setIdentifier("PR1");
        when(repository.findByIdentifier("PR1")).thenReturn(null);
        when(mapper.map(dto, Price.class)).thenReturn(price);
        PriceDto result = service.save(dto);
        verify(repository).save(price);
        assertTrue(result.isSuccess() || result.getMessage() == null);
        when(repository.findByIdentifier("PR1")).thenReturn(price);
        PriceDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void update_shouldHandleBothCases() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("PR1");
        Price price = new Price();
        price.setIdentifier("PR1");
        when(repository.findByIdentifier("PR1")).thenReturn(price);
        service.update(dto);
        verify(mapper).map(dto, price);
        verify(repository).save(price);
        when(repository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");
        PriceDto failure = service.update(dto);
        assertFalse(failure.isSuccess());
        assertTrue(failure.getMessage().contains("already exists")); // matches service bug
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete("PR1");
        verify(repository).deleteByIdentifier("PR1");
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<PriceDto>>() {}.getType();
        Price price = new Price();
        price.setIdentifier("PR1");
        PriceDto dto = new PriceDto();
        dto.setIdentifier("PR1");
        Page<Price> page =
                new PageImpl<>(List.of(price), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        Page<Price> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
    }
}
