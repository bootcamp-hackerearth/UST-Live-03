package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void createPriceSuccessTest() {

        PriceDto dto = new PriceDto();
        dto.setProductId(1L);

        Product product = new Product();
        product.setProductName("Samsung");
        product.setIdentifier("SKU001");

        Price price = new Price();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(priceRepository.existsByProductId(1L)).thenReturn(false);

        when(modelMapper.map(dto, Price.class)).thenReturn(price);

        PriceDto response = priceService.createPrice(dto);

        Assertions.assertEquals("Samsung", response.getProductName());
        Assertions.assertEquals("SKU001", response.getIdentifier());

        verify(priceRepository).save(price);
    }

    @Test
    void createPriceProductNotFoundTest() {

        PriceDto dto = new PriceDto();
        dto.setProductId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> priceService.createPrice(dto));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        Assertions.assertEquals("404 NOT_FOUND \"Product not found\"", exception.getMessage());
    }

    @Test
    void createPriceAlreadyExistsTest() {

        PriceDto dto = new PriceDto();
        dto.setProductId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));

        when(priceRepository.existsByProductId(1L)).thenReturn(true);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> priceService.createPrice(dto));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        Assertions.assertEquals("409 CONFLICT \"Price already exists for this product\"", exception.getMessage());
    }

    @Test
    void updatePriceSuccessTest() {

        PriceDto dto = new PriceDto();
        dto.setId(1L);
        dto.setSellingPrice(BigDecimal.valueOf(500));
        dto.setCostPrice(BigDecimal.valueOf(300));

        Price price = new Price();
        price.setId(1L);
        price.setProductId(1L);

        Product product = new Product();
        product.setProductName("Samsung");
        product.setIdentifier("SKU001");

        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        doAnswer(invocation -> {

            Price source = invocation.getArgument(0);
            PriceDto target = invocation.getArgument(1);

            target.setId(source.getId());
            target.setSellingPrice(source.getSellingPrice());
            target.setCostPrice(source.getCostPrice());
            target.setProductName(source.getProductName());
            target.setIdentifier(source.getIdentifier());

            return null;

        }).when(modelMapper).map(any(Price.class), any(PriceDto.class));

        PriceDto response = priceService.updatePrice(dto);

        Assertions.assertEquals(1L, response.getId());

        Assertions.assertEquals(BigDecimal.valueOf(500), response.getSellingPrice());

        Assertions.assertEquals(BigDecimal.valueOf(300), response.getCostPrice());

        Assertions.assertEquals("Samsung", response.getProductName());

        Assertions.assertEquals("SKU001", response.getIdentifier());

        verify(priceRepository).save(price);
    }

    @Test
    void updatePriceWithoutProductTest() {

        PriceDto dto = new PriceDto();
        dto.setId(1L);
        dto.setSellingPrice(BigDecimal.valueOf(100));
        dto.setCostPrice(BigDecimal.valueOf(50));

        Price price = new Price();
        price.setId(1L);
        price.setProductId(1L);

        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        doNothing().when(modelMapper).map(any(Price.class), any(PriceDto.class));

        PriceDto response = priceService.updatePrice(dto);

        Assertions.assertNull(response.getProductName());
        Assertions.assertNull(response.getIdentifier());

        verify(priceRepository).save(price);
    }

    @Test
    void updatePriceNotFoundTest() {

        PriceDto dto = new PriceDto();
        dto.setId(1L);

        when(priceRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> priceService.updatePrice(dto));

        Assertions.assertEquals("Price record not found", exception.getMessage());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();
        price.setProductId(1L);

        Product product = new Product();
        product.setProductName("Samsung");
        product.setIdentifier("SKU001");

        PriceDto dto = new PriceDto();

        Page<Price> page = new PageImpl<>(List.of(price));

        when(priceRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(any(Price.class), eq(PriceDto.class))).thenReturn(dto);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        List<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("Samsung", response.get(0).getProductName());

        Assertions.assertEquals("SKU001", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutProductTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();
        price.setProductId(1L);

        PriceDto dto = new PriceDto();

        Page<Price> page = new PageImpl<>(List.of(price));

        when(priceRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(any(Price.class), eq(PriceDto.class))).thenReturn(dto);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        List<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Assertions.assertNull(response.get(0).getProductName());
    }

    @Test
    void deletePriceSuccessTest() {

        when(priceRepository.existsById(1L)).thenReturn(true);

        boolean response = priceService.deletePrice(1L);

        Assertions.assertTrue(response);

        verify(priceRepository).deleteById(1L);
    }

    @Test
    void deletePriceFailureTest() {

        when(priceRepository.existsById(1L)).thenReturn(false);

        boolean response = priceService.deletePrice(1L);

        Assertions.assertFalse(response);

        verify(priceRepository, never()).deleteById(anyLong());
    }

    @Test
    void getPriceByIdSuccessTest() {

        Price price = new Price();
        price.setProductId(1L);

        Product product = new Product();
        product.setProductName("Samsung");
        product.setIdentifier("SKU001");

        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        doAnswer(invocation -> {

            Price source = invocation.getArgument(0);
            PriceDto target = invocation.getArgument(1);

            target.setProductId(source.getProductId());

            return null;

        }).when(modelMapper).map(any(Price.class), any(PriceDto.class));

        PriceDto response = priceService.getPriceById(1L);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals("Samsung", response.getProductName());

        Assertions.assertEquals("SKU001", response.getIdentifier());
    }

    @Test
    void getPriceByIdWithoutProductTest() {

        Price price = new Price();
        price.setProductId(1L);

        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        doNothing().when(modelMapper).map(any(Price.class), any(PriceDto.class));

        PriceDto response = priceService.getPriceById(1L);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertNull(response.getProductName());
        Assertions.assertNull(response.getIdentifier());
    }

    @Test
    void getPriceByIdNotFoundTest() {

        when(priceRepository.findById(1L)).thenReturn(Optional.empty());

        PriceDto response = priceService.getPriceById(1L);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Price not found", response.getMessage());
    }
}