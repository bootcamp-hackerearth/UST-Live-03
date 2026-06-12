package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import com.ust.pos.product.service.ProductService;
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
import java.math.BigDecimal;
import java.util.List;

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
        dto.setPriceType("Retail");

        ProductDto productDto = new ProductDto();
        productDto.setProductName("Laptop");

        Price price = new Price();

        when(priceRepository.findByIdentifier("P1_Retail"))
                .thenReturn(null);

        when(productService.findByIdentifier("P1"))
                .thenReturn(productDto);

        when(modelMapper.map(any(PriceDto.class), eq(Price.class)))
                .thenReturn(price);

        PriceDto response = priceService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Price saved successfully", response.getMessage());

        verify(priceRepository).save(price);
    }

    @Test
    void save_failure_priceExists() {

        PriceDto dto = new PriceDto();
        dto.setProductId("P1");
        dto.setPriceType("Retail");

        when(priceRepository.findByIdentifier("P1_Retail"))
                .thenReturn(new Price());

        PriceDto response = priceService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price already exists", response.getMessage());

        verify(priceRepository, never()).save(any());
    }

    @Test
    void update_success() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1_Retail");
        dto.setProductId("P1");
        dto.setPriceType("Retail");
        dto.setValue(BigDecimal.valueOf(100));

        Price existing = new Price();

        ProductDto productDto = new ProductDto();
        productDto.setProductName("Laptop");

        when(priceRepository.findByIdentifier("P1_Retail"))
                .thenReturn(existing);

        when(productService.findByIdentifier("P1"))
                .thenReturn(productDto);

        PriceDto response = priceService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Price updated successfully", response.getMessage());

        verify(priceRepository).save(existing);
    }

    @Test
    void findByIdentifier_success() {

        Price price = new Price();
        PriceDto dto = new PriceDto();

        when(priceRepository.findByIdentifier("P1_Retail"))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto response = priceService.findByIdentifier("P1_Retail");

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void findByIdentifier_failure() {

        when(priceRepository.findByIdentifier("P1_Retail"))
                .thenReturn(null);

        PriceDto response = priceService.findByIdentifier("P1_Retail");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price not found", response.getMessage());
    }

    @Test
    void findAll_success() {

        List<Price> prices = List.of(new Price());
        Page<Price> page = new PageImpl<>(prices);

        List<PriceDto> dtoList = List.of(new PriceDto());

        when(priceRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(eq(prices), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<PriceDto> response =
                priceService.findAll(PageRequest.of(0, 5));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void delete_success() {

        priceService.delete("P1_Retail");

        verify(priceRepository).deleteByIdentifier("P1_Retail");
    }
}