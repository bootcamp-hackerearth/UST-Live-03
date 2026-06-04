package com.ust.pos;

import com.ust.pos.dto.PriceDto;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Price> prices = List.of(new Price());
        Page<Price> page = new PageImpl<>(prices);
        List<PriceDto> dtoList = List.of(new PriceDto());

        when(priceRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(prices), any(Type.class))).thenReturn(dtoList);

        List<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(priceRepository).findAll(pageable);
    }

    @Test
    void testFindByIdentifier() {
        String identifier = "PROD1TYPE1";

        Price price = new Price();
        PriceDto dto = new PriceDto();

        when(priceRepository.findByIdentifier(identifier)).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);

        PriceDto result = priceService.findByIdentifier(identifier);

        assertNotNull(result);
    }

    @Test
    void testSaveSuccess() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("TYPE1");

        when(priceRepository.findByIdentifier("P1TYPE1")).thenReturn(null);

        Price mappedPrice = new Price();
        when(modelMapper.map(dto, Price.class)).thenReturn(mappedPrice);

        PriceDto result = priceService.save(dto);

        assertNotNull(result);
        assertEquals("P1TYPE1", result.getIdentifier());

        verify(priceRepository).save(mappedPrice);
    }

    @Test
    void testSaveDuplicate() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("TYPE1");

        when(priceRepository.findByIdentifier("P1TYPE1"))
                .thenReturn(new Price());

        PriceDto result = priceService.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));

        verify(priceRepository, never()).save(any());
    }

    @Test
    void testDelete() {
        String identifier = "TEST1";

        String result = priceService.delete(identifier);

        assertEquals(identifier, result);
        verify(priceRepository).deleteByIdentifier(identifier);
    }

    @Test
    void testUpdateSuccess() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1TYPE1");

        Price existing = new Price();

        when(priceRepository.findByIdentifier("P1TYPE1"))
                .thenReturn(existing);

        PriceDto result = priceService.update(dto);

        assertNotNull(result);

        verify(modelMapper).map(dto, existing);
        verify(priceRepository).save(existing);
    }

    @Test
    void testUpdateNotFound() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("NOT_FOUND");

        when(priceRepository.findByIdentifier("NOT_FOUND"))
                .thenReturn(null);

        PriceDto result = priceService.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));

        verify(priceRepository, never()).save(any());
    }
}