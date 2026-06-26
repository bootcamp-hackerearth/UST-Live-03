package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Price;
import com.ust.pos.modell.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        PriceDto dto = new PriceDto();

        when(repository.findByIdentifierAndDeletedFalse("P-T"))
                .thenReturn(price);

        when(mapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result = service.findByIdentifier("P-T");

        assertNotNull(result);
    }

    @Test
    void findByIdentifierNotFoundTest() {

        when(repository.findByIdentifierAndDeletedFalse("P-T"))
                .thenReturn(null);

        assertNull(service.findByIdentifier("P-T"));
    }

    @Test
    void saveSuccessTest() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P");
        dto.setType("T");

        Price price = new Price();

        when(repository.findByIdentifier("P-T"))
                .thenReturn(null);

        when(mapper.map(dto, Price.class))
                .thenReturn(price);

        PriceDto result = service.save(dto);

        verify(repository).save(price);

        assertTrue(result.isSuccess());
        assertEquals("P-T", result.getIdentifier());
    }

    @Test
    void saveDuplicatePriceTest() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P");
        dto.setType("T");

        Price price = new Price();
        price.setDeleted(false);

        when(repository.findByIdentifier("P-T"))
                .thenReturn(price);

        PriceDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Price already exists for product + type",
                result.getMessage()
        );
    }

    @Test
    void saveSoftDeletedPriceTest() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P");
        dto.setType("T");

        Price price = new Price();
        price.setDeleted(true);

        when(repository.findByIdentifier("P-T"))
                .thenReturn(price);

        PriceDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Price with Identifier P-T already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("OLD");
        dto.setProduct("P");
        dto.setType("T");
        dto.setPriceAmount(BigDecimal.valueOf(100));

        Price existing = new Price();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        existing.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdentifierAndDeletedFalse("OLD"))
                .thenReturn(existing);

        when(repository.findByIdentifierAndDeletedFalse("P-T"))
                .thenReturn(existing);

        when(mapper.map(existing, PriceDto.class))
                .thenReturn(dto);

        PriceDto result = service.update(dto);

        assertNotNull(result);

        verify(repository).save(existing);
    }

    @Test
    void updatePriceNotFoundTest() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("OLD");

        when(repository.findByIdentifierAndDeletedFalse("OLD"))
                .thenReturn(null);

        PriceDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertEquals("Price not found", result.getMessage());
    }

    @Test
    void updateDuplicatePriceTest() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("OLD");
        dto.setProduct("P");
        dto.setType("T");

        Price existing = new Price();
        existing.setId(1L);

        Price duplicate = new Price();
        duplicate.setId(2L);

        when(repository.findByIdentifierAndDeletedFalse("OLD"))
                .thenReturn(existing);

        when(repository.findByIdentifierAndDeletedFalse("P-T"))
                .thenReturn(duplicate);

        PriceDto result = service.update(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Price already exists for this product and type",
                result.getMessage()
        );
    }

    @Test
    void deleteSuccessTest() {

        Price price = new Price();

        when(repository.findByIdentifierAndDeletedFalse("P-T"))
                .thenReturn(price);

        service.delete("P-T");

        verify(repository).save(price);
    }

    @Test
    void deletePriceNotFoundTest() {

        when(repository.findByIdentifierAndDeletedFalse("P-T"))
                .thenReturn(null);

        service.delete("P-T");

        verify(repository, never()).save(any());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();
        PriceDto dto = new PriceDto();

        Page<Price> page =
                new PageImpl<>(List.of(price), pageable, 1);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<PriceDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Price> page =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<PriceDto> result = service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }
}

