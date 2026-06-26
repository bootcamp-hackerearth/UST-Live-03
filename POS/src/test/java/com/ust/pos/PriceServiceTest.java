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

    private PriceDto priceDto;
    private Price price;

    @BeforeEach
    void setUp() {
        priceDto = new PriceDto();
        priceDto.setIdentifier("PRC-100");
        priceDto.setMrp(BigDecimal.valueOf(100));
        priceDto.setSellingPrice(BigDecimal.valueOf(80));

        price = new Price();
        price.setIdentifier("PRC-100");
        price.setMrp(BigDecimal.valueOf(100));
        price.setSellingPrice(BigDecimal.valueOf(80));
        price.setStatus(true);
        price.setDeleted(false);
    }

    @Test
    @DisplayName("Save Price - Success")
    void save_Success() {
        when(priceRepository.findByIdentifier("PRC-100")).thenReturn(null);
        when(modelMapper.map(priceDto, Price.class)).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Price created successfully", result.getMessage());
        verify(priceRepository).save(price);
    }

    @Test
    @DisplayName("Save Price - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        price.setDeleted(false);
        when(priceRepository.findByIdentifier("PRC-100")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Save Price - Failure: Previously Deleted")
    void save_Failure_PreviouslyDeleted() {
        price.setDeleted(true);
        when(priceRepository.findByIdentifier("PRC-100")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Update Price - Success")
    void update_Success() {
        when(priceRepository.findByIdentifier("PRC-100")).thenReturn(price);

        PriceDto result = priceService.update(priceDto);

        Assertions.assertNotNull(result);
        verify(priceRepository).save(price);
    }

    @Test
    @DisplayName("Update Price - Failure: Not Found")
    void update_Failure_NotFound() {
        when(priceRepository.findByIdentifier("PRC-100")).thenReturn(null);

        PriceDto result = priceService.update(priceDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Delete Price - Success")
    void delete_Success() {
        when(priceRepository.findByIdentifier("PRC-100")).thenReturn(price);

        boolean result = priceService.delete("PRC-100");

        Assertions.assertTrue(result);
        verify(priceRepository).save(price);
    }

    @Test
    @DisplayName("Delete Price - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(priceRepository.findByIdentifier("PRC-100")).thenReturn(null);

        boolean result = priceService.delete("PRC-100");

        Assertions.assertFalse(result);
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Find All Prices - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> pricePage = new PageImpl<>(List.of(price));

        when(priceRepository.findAll(pageable)).thenReturn(pricePage);
        when(modelMapper.map(eq(pricePage.getContent()), any(Type.class))).thenReturn(List.of(priceDto));

        WsDto<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    @DisplayName("Find All Active Prices - Success")
    void findAllActive_Success() {
        List<Price> activePrices = List.of(price);
        when(priceRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activePrices);
        when(modelMapper.map(eq(activePrices), any(Type.class))).thenReturn(List.of(priceDto));

        List<PriceDto> result = priceService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(priceRepository.findByIdentifier("PRC-100")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto result = priceService.findByIdentifier("PRC-100");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find By Identifier - Failure: Price Not Configured")
    void findByIdentifier_NotFound() {
        when(priceRepository.findByIdentifier("PRC-100")).thenReturn(null);

        PriceDto result = priceService.findByIdentifier("PRC-100");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(BigDecimal.ZERO, result.getMrp());
        Assertions.assertEquals(BigDecimal.ZERO, result.getSellingPrice());
        Assertions.assertEquals("Price not configured", result.getMessage());
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(priceRepository.findByIdentifier("PRC-100")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto result = priceService.toggleStatus("PRC-100");

        Assertions.assertFalse(price.isStatus());
        verify(priceRepository).save(price);
    }
}