package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
    void createPriceProductNotFoundTest() {

        PriceDto dto = new PriceDto();
        dto.setProductId(1L);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        PriceDto response = priceService.createPrice(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product not found", response.getMessage());

        Mockito.verify(priceRepository, Mockito.never()).save(any());
    }

    @Test
    void createPriceAlreadyExistsTest() {

        PriceDto dto = new PriceDto();
        dto.setProductId(1L);

        Product product = new Product();
        product.setIdentifier("SKU001");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("SKU001");

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Mockito.when(priceRepository.findByProductId(1L)).thenReturn(existingPrice);

        PriceDto response = priceService.createPrice(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price for product - SKU001 already exists", response.getMessage());

        Mockito.verify(priceRepository, Mockito.never()).save(any());
    }

    @Test
    void createPriceSoftDeletedTest() {

        PriceDto dto = new PriceDto();
        dto.setProductId(1L);

        Product product = new Product();
        product.setIdentifier("SKU001");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("SKU001");
        existingPrice.setDeleted(true);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Mockito.when(priceRepository.findByProductId(1L)).thenReturn(existingPrice);

        PriceDto response = priceService.createPrice(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Price for product - SKU001 has been soft deleted.(Rollback by changing status)", response.getMessage());

        Mockito.verify(priceRepository, Mockito.never()).save(any());
    }

    @Test
    void updatePriceNotFoundTest() {

        PriceDto dto = new PriceDto();
        dto.setId(1L);

        Mockito.when(priceRepository.findById(1L)).thenReturn(Optional.empty());

        PriceDto response = priceService.updatePrice(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price record not found", response.getMessage());

        Mockito.verify(priceRepository, Mockito.never()).save(any());
    }

    @Test
    void createPriceSuccessTest() {

        PriceDto dto = new PriceDto();
        dto.setProductId(1L);

        Product product = new Product();
        product.setProductName("Samsung");
        product.setIdentifier("SKU001");

        Price price = new Price();

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Mockito.when(priceRepository.findByProductId(1L)).thenReturn(null);

        Mockito.when(modelMapper.map(dto, Price.class)).thenReturn(price);

        PriceDto response = priceService.createPrice(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Price created successfully", response.getMessage());
        Assertions.assertEquals("Samsung", response.getProductName());
        Assertions.assertEquals("SKU001", response.getIdentifier());

        Mockito.verify(priceRepository).save(price);
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

        Mockito.when(priceRepository.findById(1L)).thenReturn(Optional.of(price));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Mockito.doAnswer(invocation -> {
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
        Assertions.assertEquals("Samsung", response.getProductName());
        Assertions.assertEquals("SKU001", response.getIdentifier());

        Mockito.verify(priceRepository).save(price);
    }

    @Test
    void updatePriceWithoutProductTest() {
        PriceDto dto = new PriceDto();
        dto.setId(1L);

        Price price = new Price();
        price.setId(1L);
        price.setProductId(1L);

        Mockito.when(priceRepository.findById(1L)).thenReturn(Optional.of(price));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        PriceDto response = priceService.updatePrice(dto);

        Assertions.assertNotNull(response);

        Mockito.verify(priceRepository).save(price);
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

        Mockito.when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(any(Price.class), eq(PriceDto.class))).thenReturn(dto);
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        WsDto<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Samsung", response.getDtoList().get(0).getProductName());
        Assertions.assertEquals("SKU001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutProductTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();
        price.setProductId(1L);

        PriceDto dto = new PriceDto();

        Page<Price> page = new PageImpl<>(List.of(price));

        Mockito.when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(any(Price.class), eq(PriceDto.class))).thenReturn(dto);
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        WsDto<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void deletePriceSuccessTest() {
        Price price = new Price();
        price.setId(1L);

        Mockito.when(priceRepository.findById(1L)).thenReturn(Optional.of(price));

        boolean response = priceService.deletePrice(1L);

        Assertions.assertTrue(response);

        Mockito.verify(priceRepository).save(price);
    }

    @Test
    void deletePriceFailureTest() {
        Mockito.when(priceRepository.findById(1L)).thenReturn(Optional.empty());

        boolean response = priceService.deletePrice(1L);

        Assertions.assertFalse(response);

        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getPriceByIdSuccessTest() {
        Price price = new Price();
        price.setProductId(1L);

        Product product = new Product();
        product.setProductName("Samsung");
        product.setIdentifier("SKU001");

        Mockito.when(priceRepository.findById(1L)).thenReturn(Optional.of(price));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Mockito.doAnswer(invocation -> {
            PriceDto target = invocation.getArgument(1);
            target.setProductId(1L);
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

        Mockito.when(priceRepository.findById(1L)).thenReturn(Optional.of(price));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        PriceDto response = priceService.getPriceById(1L);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void getPriceByIdNotFoundTest() {
        Mockito.when(priceRepository.findById(1L)).thenReturn(Optional.empty());

        PriceDto response = priceService.getPriceById(1L);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price not found", response.getMessage());
    }

}
