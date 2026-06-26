package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import com.ust.pos.product.service.ProductService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_success() {

        PriceDto dto = new PriceDto();
        dto.setProductId("P1");
        dto.setPriceType("Retail Price");

        ProductDto productDto = new ProductDto();
        productDto.setProductName("Laptop");

        Price priceEntity = new Price();

        String expectedId = "P1_Retail_Price";

        when(priceRepository.findByIdentifier(expectedId)).thenReturn(null);
        when(productService.findByIdentifier("P1")).thenReturn(productDto);
        when(modelMapper.map(any(PriceDto.class), eq(Price.class))).thenReturn(priceEntity);

        PriceDto result = priceService.save(dto);

        assertTrue(result.isSuccess());
        assertEquals("Price saved successfully", result.getMessage());
        assertEquals(expectedId, result.getIdentifier());

        verify(priceRepository).save(priceEntity);
    }

    @Test
    void save_failure_duplicate() {

        PriceDto dto = new PriceDto();
        dto.setProductId("P1");
        dto.setPriceType("Retail Price");

        String expectedId = "P1_Retail_Price";

        when(priceRepository.findByIdentifier(expectedId)).thenReturn(new Price());
        PriceDto result = priceService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Price already exists", result.getMessage());

        verify(priceRepository, never()).save(any());
    }

    @Test
    void update_success() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1_Retail_Price");
        dto.setProductId("P1");
        dto.setPriceType("Retail Price");
        dto.setValue(BigDecimal.valueOf(100));

        Price existing = new Price();

        ProductDto productDto = new ProductDto();
        productDto.setProductName("Laptop");

        when(priceRepository.findByIdentifier("P1_Retail_Price")).thenReturn(existing);
        when(productService.findByIdentifier("P1")).thenReturn(productDto);
        PriceDto result = priceService.update(dto);

        assertTrue(result.isSuccess());
        assertEquals("Price updated successfully", result.getMessage());

        verify(priceRepository).save(existing);
    }

    @Test
    void findByIdentifier_success() {

        Price price = new Price();
        PriceDto mapped = new PriceDto();

        when(priceRepository.findByIdentifier("P1_Retail_Price")).thenReturn(price);

        when(modelMapper.map(price, PriceDto.class)).thenReturn(mapped);

        PriceDto result = priceService.findByIdentifier("P1_Retail_Price");

        assertTrue(result.isSuccess());
        assertNotNull(result);
    }

    @Test
    void findByIdentifier_failure() {

        when(priceRepository.findByIdentifier("P1_Retail_Price")).thenReturn(null);

        PriceDto result = priceService.findByIdentifier("P1_Retail_Price");

        assertFalse(result.isSuccess());
        assertEquals("Price not found", result.getMessage());
    }

    @Test
    void findAll_success() {

        List<Price> list = List.of(new Price());
        Page<Price> page = new PageImpl<>(list);
        Pageable pageable = PageRequest.of(0, 5);

        List<PriceDto> dtoList = List.of(new PriceDto());

        when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(dtoList);
        WsDto<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(0, result.getPage());
    }

    @Test
    void delete_success() {

        Price price = new Price();
        price.setDeleted(false);

        when(priceRepository.findByIdentifier("P1_Retail_Price")).thenReturn(price);
        priceService.delete("P1_Retail_Price");

        assertTrue(price.getDeleted());
        verify(priceRepository).save(price);
    }

    @Test
    void delete_null_case() {

        when(priceRepository.findByIdentifier("P1_Retail_Price")).thenReturn(null);
        priceService.delete("P1_Retail_Price");

        verify(priceRepository, never()).save(any());
    }

    @Test
    void find_active_prices() {

        List<Price> list = List.of(new Price());
        List<PriceDto> dtoList = List.of(new PriceDto());

        when(priceRepository.findByStatusTrueAndDeletedFalse()).thenReturn(list);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(dtoList);

        List<PriceDto> result = priceService.findActivePrices();

        assertEquals(1, result.size());
    }

    @Test
    void get_price_types() {

        List<String> types = priceService.getPriceTypes();

        assertEquals(3, types.size());
        assertTrue(types.contains("Selling Price"));
    }
}