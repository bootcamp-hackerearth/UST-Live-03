package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
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
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    void findByIdentifierTest() {
        Price price = new Price();
        price.setIdentifier("P-T");
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P-T");

        when(repository.findByIdentifier("P-T")).thenReturn(price);
        when(mapper.map(price, PriceDto.class)).thenReturn(dto);

        PriceDto result = service.findByIdentifier("P-T");
        assertEquals("P-T", result.getIdentifier());

        when(repository.findByIdentifier("X")).thenReturn(null);
        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void saveTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P");
        dto.setType("T");

        Price price = new Price();

        when(repository.findByIdentifier("P-T")).thenReturn(null);
        when(mapper.map(dto, Price.class)).thenReturn(price);

        PriceDto result = service.save(dto);
        verify(repository).save(price);
        assertTrue(result.isSuccess());
        assertEquals("P-T", result.getIdentifier());

        when(repository.findByIdentifier("P-T")).thenReturn(price);

        PriceDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertEquals("Price already exists for product + type", duplicate.getMessage());
    }

    @Test
    void updateTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("OLD");
        dto.setProduct("P");
        dto.setType("T");
        dto.setPriceAmount(new BigDecimal("100.0"));

        Price existing = new Price();
        existing.setId(1L);
        existing.setIdentifier("OLD");

        when(repository.findByIdentifier("OLD")).thenReturn(existing);
        when(repository.findByIdentifier("P-T")).thenReturn(null);
        when(mapper.map(existing, PriceDto.class)).thenReturn(dto);

        PriceDto updated = service.update(dto);
        assertEquals("P", updated.getProduct());
        verify(repository).save(existing);

        when(repository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");

        PriceDto notFound = service.update(dto);
        assertFalse(notFound.isSuccess());
        assertEquals("Price not found", notFound.getMessage());

        Price duplicate = new Price();
        duplicate.setId(2L);

        when(repository.findByIdentifier("OLD")).thenReturn(existing);
        when(repository.findByIdentifier("P-T")).thenReturn(duplicate);

        dto.setIdentifier("OLD");

        PriceDto dup = service.update(dto);
        assertFalse(dup.isSuccess());
        assertEquals("Price already exists for this product and type", dup.getMessage());
    }

    @Test
    void deleteTest() {
        service.delete("P-T");
        verify(repository).deleteByIdentifier("P-T");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<PriceDto>>(){}.getType();

        Price price = new Price();
        price.setIdentifier("P-T");
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P-T");

        Page<Price> page = new PageImpl<>(List.of(price), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<PriceDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<Price> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(emptyPage.getContent(), type)).thenReturn(List.of());

        WsDto<PriceDto> empty = service.findAll(pageable);
        assertTrue(empty.getDtoList().isEmpty());
    }
}