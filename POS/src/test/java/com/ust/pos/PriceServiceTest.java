package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {

        Price price = new Price();
        price.setIdentifier("P1.RETAIL");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1.RETAIL");

        Mockito.when(priceRepository.findByIdentifier("P1.RETAIL"))
                .thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto response = priceService.findByIdentifier("P1.RETAIL");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("P1.RETAIL", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(priceRepository.findByIdentifier("P1.RETAIL"))
                .thenReturn(null);
        PriceDto response = priceService.findByIdentifier("P1.RETAIL");

        Assertions.assertNull(response);
    }

    @Test
    void saveTestSuccess() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("RETAIL");

        Price price = new Price();

        Mockito.when(priceRepository.findByIdentifier(anyString()))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(price);
        PriceDto response = priceService.save(dto);

        Assertions.assertEquals("P1.RETAIL", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(priceRepository).save(any(Price.class));
    }

    @Test
    void saveTestFailure_duplicate() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("RETAIL");

        Price existing = new Price();

        Mockito.when(priceRepository.findByIdentifier(anyString()))
                .thenReturn(existing);
        PriceDto response = priceService.save(dto);

        Assertions.assertEquals("P1.RETAIL", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("RETAIL");

        Price existing = new Price();

        Mockito.when(priceRepository.findByIdentifier(anyString()))
                .thenReturn(existing);
        Mockito.when(priceRepository.save(any(Price.class)))
                .thenReturn(existing);

        PriceDto response = priceService.update(dto);
        Assertions.assertEquals("P1.RETAIL", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(priceRepository).save(existing);
    }

    @Test
    void updateTestFailure_notFound() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("RETAIL");

        Mockito.when(priceRepository.findByIdentifier(anyString()))
                .thenReturn(null);
        PriceDto response = priceService.update(dto);

        Assertions.assertEquals("P1.RETAIL", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        priceService.delete("P1.RETAIL");
        verify(priceRepository).deleteByIdentifier("P1.RETAIL");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Price> prices = List.of(new Price());
        Page<Price> page = new PageImpl<>(prices);
        List<PriceDto> dtos = List.of(new PriceDto());

        when(priceRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(
                eq(prices),
                any(Type.class)
        )).thenReturn(dtos);

        List<PriceDto> result = priceService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(priceRepository).findAll(pageable);
    }
}