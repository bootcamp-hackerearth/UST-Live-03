package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl service;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Price> page = new PageImpl<>(List.of(new Price()), pageable, 1);

        when(priceRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new PriceDto()));

        WsDto<PriceDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierTest() {
        Price price = new Price();
        PriceDto dto = new PriceDto();

        when(priceRepository.findByIdentifier("P1")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);

        PriceDto result = service.findByIdentifier("P1");

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD");
        dto.setPriceType("MRP");
        dto.setPriceAmount(new BigDecimal("100"));

        when(priceRepository.findByIdentifier("PRODMRP")).thenReturn(null);
        when(modelMapper.map(dto, Price.class)).thenReturn(new Price());

        PriceDto result = service.save(dto);

        assertTrue(result.isSuccess());
        assertEquals("PRODMRP", result.getIdentifier());
        verify(priceRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD");
        dto.setPriceType("MRP");

        Price existing = new Price();
        existing.setDeleted(false);

        when(priceRepository.findByIdentifier("PRODMRP")).thenReturn(existing);

        PriceDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(priceRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD");
        dto.setPriceType("MRP");

        Price existing = new Price();
        existing.setDeleted(true);

        when(priceRepository.findByIdentifier("PRODMRP")).thenReturn(existing);

        PriceDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("ID1");

        Price existing = new Price();

        when(priceRepository.findByIdentifier("ID1")).thenReturn(existing);

        PriceDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(priceRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("ID1");

        when(priceRepository.findByIdentifier("ID1")).thenReturn(null);

        PriceDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(priceRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Price price = new Price();
        price.setDeleted(false);

        when(priceRepository.findByIdentifier("ID1")).thenReturn(price);

        String result = service.delete("ID1");

        assertEquals("ID1", result);
        assertTrue(price.isDeleted());
    }
}
