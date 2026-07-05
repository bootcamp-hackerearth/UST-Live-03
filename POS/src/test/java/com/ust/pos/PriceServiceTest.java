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
import org.springframework.data.jpa.domain.Specification;

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
        priceDto.setIdentifier("PRC-001");
        priceDto.setMrp(new BigDecimal("100.00"));
        priceDto.setSellingPrice(new BigDecimal("80.00"));

        price = new Price();
        price.setIdentifier("PRC-001");
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));
        price.setStatus(true);
        price.setDeleted(false);
    }

    @Test
    @DisplayName("Save Price - Success")
    void save_Success() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(null);
        when(modelMapper.map(priceDto, Price.class)).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("successfully"));
        verify(priceRepository).save(price);
    }

    @Test
    @DisplayName("Save Price - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        price.setDeleted(false);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Save Price - Failure: Previously Deleted")
    void save_Failure_PreviouslyDeleted() {
        price.setDeleted(true);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("previously deleted"));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Update Price - Success")
    void update_Success() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);

        PriceDto result = priceService.update(priceDto);

        Assertions.assertNotNull(result);
        verify(priceRepository).save(price);
        verify(modelMapper).map(priceDto, price);
    }

    @Test
    @DisplayName("Update Price - Failure: Not Found")
    void update_Failure_NotFound() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(null);

        PriceDto result = priceService.update(priceDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Delete Price - Success")
    void delete_Success() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);

        boolean result = priceService.delete("PRC-001");

        Assertions.assertTrue(result);
        verify(priceRepository).save(price);
    }

    @Test
    @DisplayName("Delete Price - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(null);

        boolean result = priceService.delete("PRC-001");

        Assertions.assertFalse(result);
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Find All Prices - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> pricePage = new PageImpl<>(List.of(price), pageable, 1);

        when(priceRepository.findAll(pageable)).thenReturn(pricePage);
        when(modelMapper.map(eq(pricePage.getContent()), any(Type.class))).thenReturn(List.of(priceDto));

        WsDto<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
    }

    @Test
    @DisplayName("Find All Prices with Specification - Success")
    void findAll_WithSpecification_Success() {
        Specification<Price> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> pricePage = new PageImpl<>(List.of(price), pageable, 1);

        when(priceRepository.findAll(spec, pageable)).thenReturn(pricePage);
        when(modelMapper.map(eq(pricePage.getContent()), any(Type.class))).thenReturn(List.of(priceDto));

        WsDto<PriceDto> result = priceService.findAll(spec, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
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
    @DisplayName("Find By Identifier - Success Configuration Found")
    void findByIdentifier_Success() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto result = priceService.findByIdentifier("PRC-001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(new BigDecimal("100.00"), result.getMrp());
    }

    @Test
    @DisplayName("Find By Identifier - Not Configured Fallback")
    void findByIdentifier_NotConfigured() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(null);

        PriceDto result = priceService.findByIdentifier("PRC-001");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(BigDecimal.ZERO, result.getMrp());
        Assertions.assertEquals(BigDecimal.ZERO, result.getSellingPrice());
        Assertions.assertEquals("Price not configured", result.getMessage());
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto result = priceService.toggleStatus("PRC-001");

        Assertions.assertFalse(price.isStatus());
        verify(priceRepository).save(price);
    }
}