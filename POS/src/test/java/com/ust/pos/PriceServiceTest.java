package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    private PriceDto testDto;
    private Price testPrice;

    @BeforeEach
    void setUp() {
        testDto = new PriceDto();
        testDto.setIdentifier("PRC001");

        testPrice = new Price();
        testPrice.setIdentifier("PRC001");
        testPrice.setStatus(true);
    }

    @Test
    @DisplayName("Save Price - Success")
    void save_Success() {
        when(priceRepository.findByIdentifier("PRC001")).thenReturn(null);
        when(modelMapper.map(testDto, Price.class)).thenReturn(testPrice);

        PriceDto result = priceService.save(testDto);

        Assertions.assertNotNull(result);
        verify(priceRepository).save(testPrice);
    }

    @Test
    @DisplayName("Save Price - Error: Already Exists")
    void save_AlreadyExists() {
        when(priceRepository.findByIdentifier("PRC001")).thenReturn(testPrice);

        PriceDto result = priceService.save(testDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Price with identifier - PRC001 already exists", result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update Price - Success")
    void update_Success() {
        when(priceRepository.findByIdentifier("PRC001")).thenReturn(testPrice);

        PriceDto result = priceService.update(testDto);

        Assertions.assertNotNull(result);
        verify(modelMapper).map(testDto, testPrice);
        verify(priceRepository).save(testPrice);
    }

    @Test
    @DisplayName("Update Price - Error: Not Found")
    void update_NotFound() {
        when(priceRepository.findByIdentifier("PRC001")).thenReturn(null);

        PriceDto result = priceService.update(testDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Price with identifier - PRC001 not found", result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Find By Identifier - Success Case")
    void findByIdentifier_Success() {
        when(priceRepository.findByIdentifier("PRC001")).thenReturn(testPrice);
        when(modelMapper.map(testPrice, PriceDto.class)).thenReturn(testDto);

        PriceDto result = priceService.findByIdentifier("PRC001");

        Assertions.assertNotNull(result);
        verify(modelMapper).map(testPrice, PriceDto.class);
    }

    @Test
    @DisplayName("Find By Identifier - Not Configured Branch")
    void findByIdentifier_NotFound() {
        when(priceRepository.findByIdentifier("NOT_FOUND")).thenReturn(null);

        PriceDto result = priceService.findByIdentifier("NOT_FOUND");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("NOT_FOUND", result.getIdentifier());
        Assertions.assertEquals(BigDecimal.ZERO, result.getMrp());
        Assertions.assertEquals(BigDecimal.ZERO, result.getSellingPrice());
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Price not configured", result.getMessage());
    }

    @Test
    @DisplayName("Toggle Status - Logic Flip")
    void toggleStatus_TrueToFalse() {
        testPrice.setStatus(true);
        when(priceRepository.findByIdentifier("PRC001")).thenReturn(testPrice);
        when(modelMapper.map(testPrice, PriceDto.class)).thenReturn(testDto);

        PriceDto result = priceService.toggleStatus("PRC001");

        Assertions.assertNotNull(result);
        Assertions.assertFalse(testPrice.isStatus());
        verify(priceRepository).save(testPrice);
    }

    @Test
    @DisplayName("Find All - Paginated Success with WsDto Mapping")
    void findAll_Success() {
        Pageable pageable = PageRequest.of(2, 15);
        List<Price> prices = Collections.singletonList(testPrice);
        Page<Price> pricePage = new PageImpl<>(prices, pageable, 45);
        List<PriceDto> dtos = Collections.singletonList(testDto);

        when(priceRepository.findAll(pageable)).thenReturn(pricePage);
        when(modelMapper.map(eq(prices), any(Type.class))).thenReturn(dtos);

        WsDto<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(dtos, result.getDtoList());
        Assertions.assertEquals(45, result.getTotalRecords());
        Assertions.assertEquals(3, result.getTotalPages());
        Assertions.assertEquals(15, result.getSizePerPage());
        Assertions.assertEquals(2, result.getPage());
        verify(priceRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Delete Price - Success")
    void delete_Success() {
        boolean result = priceService.delete("PRC001");
        Assertions.assertTrue(result);
        verify(priceRepository).deleteByIdentifier("PRC001");
    }
}