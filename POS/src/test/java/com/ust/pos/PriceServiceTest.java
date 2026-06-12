package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
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
    private final String EXPECTED_IDENTIFIER = "ProdA_Retail";

    @BeforeEach
    void setUp() {
        price = new Price();
        price.setIdentifier(EXPECTED_IDENTIFIER);
        price.setProduct("ProdA");
        price.setPriceType("Retail");

        priceDto = new PriceDto();
        priceDto.setIdentifier(EXPECTED_IDENTIFIER);
        priceDto.setProduct("ProdA");
        priceDto.setPriceType("Retail");
    }

    @Test
    void testFindByIdentifier_Found() {
        when(priceRepository.findByIdentifier(EXPECTED_IDENTIFIER)).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto result = priceService.findByIdentifier(EXPECTED_IDENTIFIER);

        assertNotNull(result);
        assertEquals(EXPECTED_IDENTIFIER, result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(priceRepository.findByIdentifier(EXPECTED_IDENTIFIER)).thenReturn(null);

        PriceDto result = priceService.findByIdentifier(EXPECTED_IDENTIFIER);

        assertNull(result);
    }

    @Test
    void testFindByProductAndPriceType() {
        when(priceRepository.findByProductAndPriceType("ProdA", "Retail")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto result = priceService.findByProductAndPriceType("ProdA", "Retail");

        assertNotNull(result);
        assertEquals(EXPECTED_IDENTIFIER, result.getIdentifier());
    }

    @Test
    void testSave_AlreadyExists() {
        when(priceRepository.findByIdentifier(EXPECTED_IDENTIFIER)).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(priceRepository, never()).save(any());
    }

    @Test
    void testSave_Success() {
        when(priceRepository.findByIdentifier(EXPECTED_IDENTIFIER)).thenReturn(null);
        when(modelMapper.map(priceDto, Price.class)).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        verify(priceRepository).save(price);
    }

    @Test
    void testUpdate_NotFound() {
        when(priceRepository.findByIdentifier(EXPECTED_IDENTIFIER)).thenReturn(null);

        PriceDto result = priceService.update(priceDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(priceRepository, never()).save(any());
    }

    @Test
    void testUpdate_Success() {
        when(priceRepository.findByIdentifier(EXPECTED_IDENTIFIER)).thenReturn(price);

        PriceDto result = priceService.update(priceDto);

        verify(modelMapper).map(priceDto, price);
        verify(priceRepository).save(price);
        assertNotNull(result);
    }

    @Test
    void testDelete() {
        doNothing().when(priceRepository).deleteByIdentifier(EXPECTED_IDENTIFIER);

        priceService.delete(EXPECTED_IDENTIFIER);

        verify(priceRepository).deleteByIdentifier(EXPECTED_IDENTIFIER);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Price> priceList = Collections.singletonList(price);
        Page<Price> pricePage = new PageImpl<>(priceList, pageable, 1);

        List<PriceDto> dtoList = Collections.singletonList(priceDto);
        Type listType = new TypeToken<List<PriceDto>>() {}.getType();

        when(priceRepository.findAll(pageable)).thenReturn(pricePage);
        when(modelMapper.map(pricePage.getContent(), listType)).thenReturn(dtoList);

        WsDto<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertEquals(dtoList, result.getDtoList());
    }
}