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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        Price price = new Price();
        price.setIdentifier("PROD1MRP");

        when(priceRepository.findByIdentifier("PROD1MRP")).thenReturn(null);
        when(modelMapper.map(dto, Price.class)).thenReturn(price);

        PriceDto response = priceService.save(dto);

        assertEquals("PROD1MRP", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        verify(priceRepository).save(price);
    }

    @Test
    void saveFailureTest() {
        Price existingPrice = new Price();
        existingPrice.setIdentifier("PROD1MRP");

        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        when(priceRepository.findByIdentifier("PROD1MRP")).thenReturn(existingPrice);

        PriceDto response = priceService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        Price price = new Price();
        price.setIdentifier("PROD1MRP");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PROD1MRP");

        when(priceRepository.findByIdentifier("PROD1MRP")).thenReturn(price);

        PriceDto response = priceService.update(dto);

        assertEquals("PROD1MRP", response.getIdentifier());
        verify(modelMapper).map(dto, price);
        verify(priceRepository).save(price);
    }

    @Test
    void updateFailureTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("PROD1MRP");

        when(priceRepository.findByIdentifier("PROD1MRP")).thenReturn(null);

        PriceDto response = priceService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        priceService.delete("PROD1MRP");
        verify(priceRepository).deleteByIdentifier("PROD1MRP");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Price price = new Price();
        price.setIdentifier("PROD1MRP");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PROD1MRP");

        when(priceRepository.findByIdentifier("PROD1MRP")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);

        PriceDto response = priceService.findByIdentifier("PROD1MRP");

        assertNotNull(response);
        assertEquals("PROD1MRP", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(priceRepository.findByIdentifier("PROD1MRP")).thenReturn(null);

        PriceDto response = priceService.findByIdentifier("PROD1MRP");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Price> prices = List.of(new Price(), new Price());
        Page<Price> page = new PageImpl<>(prices);

        List<PriceDto> dtoList = List.of(new PriceDto(), new PriceDto());

        when(priceRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(prices),
                any(Type.class)
        )).thenReturn(dtoList);

        List<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(priceRepository).findAll(pageable);
    }
}