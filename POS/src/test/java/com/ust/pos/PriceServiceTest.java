package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PriceServiceImpl priceService;

    private Price price;
    private PriceDto priceDto;

    @BeforeEach
    void setUp() {

        priceDto = new PriceDto();
        priceDto.setProduct("PROD1");
        priceDto.setPriceType("MRP");
        priceDto.setPriceAmount(100.0);
        priceDto.setIdentifier("PROD1_MRP");
        priceDto.setSuccess(true);

        price = new Price();
        price.setId(1L);
        price.setProduct("PROD1");
        price.setPriceType("MRP");
        price.setPriceAmount(100.0);
        price.setIdentifier("PROD1_MRP");

    }

    @Test
    void testFindByIdentifier_Found() {

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto result = priceService.findByIdentifier("PROD1_MRP");

        assertNotNull(result);
        assertEquals("PROD1_MRP", result.getIdentifier());

    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(null);

        PriceDto result = priceService.findByIdentifier("PROD1_MRP");

        assertNull(result);

    }

    @Test
    void testSave_PriceAlreadyExists() {

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(priceRepository, never()).save(any());

    }

    @Test
    void testSave_NewPrice() {

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(null);
        when(modelMapper.map(priceDto, Price.class)).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        verify(priceRepository).save(price);
        assertEquals("PROD1_MRP", result.getIdentifier());

    }

    @Test
    void testUpdate_PriceNotFound() {

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(null);

        PriceDto result = priceService.update(priceDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Price not found"));

    }

    @Test
    void testUpdate_DuplicatePriceExists() {

        Price duplicate = new Price();
        duplicate.setId(2L);
        duplicate.setIdentifier("PROD1_SALE");

        priceDto.setPriceType("SALE");

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);
        when(priceRepository.findByIdentifier("PROD1_SALE")).thenReturn(duplicate);

        PriceDto result = priceService.update(priceDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Another price already exists"));
        verify(priceRepository, never()).save(any());

    }

    @Test
    void testUpdate_Success() {

        priceDto.setPriceType("SALE");
        priceDto.setPriceAmount(120.0);

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);
        when(priceRepository.findByIdentifier("PROD1_SALE")).thenReturn(null);

        PriceDto result = priceService.update(priceDto);

        assertEquals("PROD1_SALE", result.getIdentifier());
        verify(priceRepository).save(price);

    }

    @Test
    void testDelete() {

        doNothing().when(priceRepository).deleteByIdentifier("PROD1_MRP");

        priceService.delete("PROD1_MRP");
        verify(priceRepository).deleteByIdentifier("PROD1_MRP");

    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 50);

        List<Price> priceList = Collections.singletonList(price);
        List<PriceDto> priceDtoList = Collections.singletonList(priceDto);
        Page<Price> pricePage = new PageImpl<>(priceList, pageable, priceList.size());

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        when(priceRepository.findAll(pageable)).thenReturn(pricePage);
        when(modelMapper.map(pricePage.getContent(), listType)).thenReturn(priceDtoList);

        List<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(priceRepository, times(1)).findAll(pageable);
        verify(modelMapper, times(1)).map(pricePage.getContent(), listType);

    }

}