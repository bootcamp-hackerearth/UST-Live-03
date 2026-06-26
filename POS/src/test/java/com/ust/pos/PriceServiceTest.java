package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
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
import java.math.BigDecimal;
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
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    private Price price;
    private PriceDto priceDto;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        priceDto = new PriceDto();
        priceDto.setProduct("PROD1");
        priceDto.setPriceType("MRP");
        priceDto.setPriceAmount(BigDecimal.valueOf(100.0));
        priceDto.setIdentifier("PROD1_MRP");
        priceDto.setSuccess(true);

        price = new Price();
        price.setId(1L);
        price.setProduct("PROD1");
        price.setPriceType("MRP");
        price.setPriceAmount(BigDecimal.valueOf(100.0));
        price.setIdentifier("PROD1_MRP");
        price.setDeleted(false);

        mockProduct = new Product();
        mockProduct.setIdentifier("PROD1");
        mockProduct.setName("PROD1");
    }

    @Test
    void testFindByIdentifier_Found() {
        when(priceRepository.findByIdentifierAndDeletedFalse("PROD1_MRP")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto result = priceService.findByIdentifier("PROD1_MRP");

        assertNotNull(result);
        assertEquals("PROD1_MRP", result.getIdentifier());
        verify(priceRepository, times(1)).findByIdentifierAndDeletedFalse("PROD1_MRP");
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(priceRepository.findByIdentifierAndDeletedFalse("PROD1_MRP")).thenReturn(null);

        PriceDto result = priceService.findByIdentifier("PROD1_MRP");

        assertNull(result);
        verify(priceRepository, times(1)).findByIdentifierAndDeletedFalse("PROD1_MRP");
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testSave_PriceAlreadyExists_Active() {
        price.setDeleted(false);
        when(productRepository.findByIdentifier("PROD1")).thenReturn(mockProduct);
        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Price with identifier - PROD1_MRP already exists", result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void testSave_PriceAlreadyExists_SoftDeleted() {
        price.setDeleted(true);
        when(productRepository.findByIdentifier("PROD1")).thenReturn(mockProduct);
        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Price identifier - PROD1_MRP not available", result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void testSave_NewPrice() {
        when(productRepository.findByIdentifier("PROD1")).thenReturn(mockProduct);
        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(null);
        when(modelMapper.map(priceDto, Price.class)).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(priceRepository, times(1)).save(price);
    }

    @Test
    void testUpdate_PriceNotFound() {
        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(null);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Price not found for Product 'PROD1' with Price Type 'MRP'", result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void testUpdate_DuplicatePriceExists() {
        Price duplicate = new Price();
        duplicate.setId(2L);
        duplicate.setProduct("PROD1");
        duplicate.setIdentifier("PROD1_SALE");

        priceDto.setPriceType("SALE");

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);
        when(priceRepository.findByIdentifier("PROD1_SALE")).thenReturn(duplicate);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Another price already exists for this Product + Price Type", result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void testUpdate_DuplicateSameId_ShouldUpdateSuccessfully() {
        Price duplicate = new Price();
        duplicate.setId(1L);
        duplicate.setProduct("PROD1");
        duplicate.setIdentifier("PROD1_SALE");

        priceDto.setPriceType("SALE");
        priceDto.setPriceAmount(BigDecimal.valueOf(100.0));

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);
        when(priceRepository.findByIdentifier("PROD1_SALE")).thenReturn(duplicate);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertEquals("PROD1_SALE", result.getIdentifier());
        verify(priceRepository, times(1)).save(price);
    }

    @Test
    void testUpdate_Success_NoDuplicate() {
        priceDto.setPriceType("SALE");
        priceDto.setPriceAmount(BigDecimal.valueOf(100.0));

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);
        when(priceRepository.findByIdentifier("PROD1_SALE")).thenReturn(null);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertEquals("PROD1_SALE", result.getIdentifier());
        verify(priceRepository, times(1)).save(price);
    }

    @Test
    void testUpdate_ProductContainsComma() {
        price.setProduct("PROD1,EXTRA");
        priceDto.setPriceType("WHOLESALE");

        when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);
        when(priceRepository.findByIdentifier("PROD1_WHOLESALE")).thenReturn(null);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertEquals("PROD1_WHOLESALE", result.getIdentifier());
        assertEquals("PROD1", price.getProduct());
        verify(priceRepository, times(1)).save(price);
    }

    @Test
    void testDelete_Success() {
        String identifier = "PROD1_MRP";
        when(priceRepository.findByIdentifier(identifier)).thenReturn(price);

        assertDoesNotThrow(() -> priceService.delete(identifier));

        verify(priceRepository, times(1)).findByIdentifier(identifier);
        assertTrue(price.getDeleted());
    }

    @Test
    void testFindAll_WithData() {
        Pageable pageable = PageRequest.of(0, 50);
        List<Price> priceList = Collections.singletonList(price);
        List<PriceDto> priceDtoList = Collections.singletonList(priceDto);
        Page<Price> pricePage = new PageImpl<>(priceList, pageable, priceList.size());
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        when(priceRepository.findAllByDeletedFalse(pageable)).thenReturn(pricePage);
        when(modelMapper.map(pricePage.getContent(), listType)).thenReturn(priceDtoList);

        WsDto<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        verify(priceRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void testFindAll_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        when(priceRepository.findAllByDeletedFalse(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(emptyPage.getContent(), listType)).thenReturn(Collections.emptyList());

        WsDto<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
        assertEquals(0, result.getTotalRecords());
        verify(priceRepository, times(1)).findAllByDeletedFalse(pageable);
    }
}