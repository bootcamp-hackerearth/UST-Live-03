package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private Product product;

    @BeforeEach
    void setUp() {
        price = new Price();
        price.setId(1L);
        price.setProduct("SKU1");
        price.setPriceType("MRP");
        price.setIdentifier("SKU1MRP");

        priceDto = new PriceDto();
        priceDto.setId(1L);
        priceDto.setProduct("SKU1");
        priceDto.setPriceType("MRP");
        priceDto.setIdentifier("SKU1MRP");

        product = new Product();
        product.setIdentifier("SKU1");
        product.setName("Apple");
    }

    @Test
    void findAllWithoutPageableTest() {

        Price price = new Price();
        price.setProduct("SKU1");

        PriceDto priceDto = new PriceDto();

        Product product = new Product();
        product.setName("Apple");

        Mockito.when(priceRepository.findAll())
                .thenReturn(List.of(price));

        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        Mockito.when(productRepository.existsByIdentifier("SKU1"))
                .thenReturn(true);

        Mockito.when(productRepository.findByIdentifier("SKU1"))
                .thenReturn(product);

        List<PriceDto> result = priceService.findAll(null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Apple", result.get(0).getProduct());
    }

    @Test
    void findAllWithPageableTest() {

        Price price = new Price();
        price.setProduct("SKU1");

        PriceDto priceDto = new PriceDto();

        Product product = new Product();
        product.setName("Apple");

        Pageable pageable = PageRequest.of(0, 5);
        Page<Price> pricePage = new PageImpl<>(List.of(price));

        Mockito.when(priceRepository.findAll(pageable))
                .thenReturn(pricePage);

        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        Mockito.when(productRepository.existsByIdentifier("SKU1"))
                .thenReturn(true);

        Mockito.when(productRepository.findByIdentifier("SKU1"))
                .thenReturn(product);

        List<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Apple", result.get(0).getProduct());
    }

    @Test
    void findAll_productDoesNotExist() {

        Price price = new Price();
        price.setProduct("SKU1");

        PriceDto priceDto = new PriceDto();

        when(priceRepository.findAll())
                .thenReturn(List.of(price));

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        when(productRepository.existsByIdentifier("SKU1"))
                .thenReturn(false);

        List<PriceDto> result = priceService.findAll(null);

        assertEquals(1, result.size());
        assertNull(result.get(0).getProduct());
    }

    @Test
    void save_productNotFound() {
        when(productRepository.existsByIdentifier("SKU1")).thenReturn(false);

        PriceDto response = priceService.save(priceDto);

        assertFalse(response.isSuccess());
        assertEquals("Product not found", response.getMessage());
    }

    @Test
    void save_priceAlreadyExists() {
        when(productRepository.existsByIdentifier("SKU1")).thenReturn(true);
        when(priceRepository.findByIdentifier("SKU1MRP")).thenReturn(price);

        PriceDto response = priceService.save(priceDto);

        assertFalse(response.isSuccess());
        assertEquals("MRP already set for SKU1", response.getMessage());
    }

    @Test
    void save_success() {
        when(productRepository.existsByIdentifier("SKU1")).thenReturn(true);
        when(priceRepository.findByIdentifier("SKU1MRP")).thenReturn(null);
        when(modelMapper.map(priceDto, Price.class)).thenReturn(price);
        when(priceRepository.save(price)).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.save(priceDto);

        assertTrue(response.isSuccess());
        assertEquals("Successfully added the price", response.getMessage());
    }

    @Test
    void findByIdentifier_success() {
        when(priceRepository.findByIdentifier("SKU1MRP"))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        PriceDto result = priceService.findByIdentifier("SKU1MRP");

        assertEquals("SKU1MRP", result.getIdentifier());
    }

    @Test
    void findByIdentifier_notFound() {
        when(priceRepository.findByIdentifier("INVALID"))
                .thenReturn(null);

        when(modelMapper.map(null, PriceDto.class))
                .thenReturn(null);

        PriceDto result = priceService.findByIdentifier("INVALID");

        assertNull(result);
    }

    @Test
    void update_priceNotFound() {
        when(priceRepository.findByIdentifier("SKU1MRP"))
                .thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void update_success() {
        when(priceRepository.findByIdentifier("SKU1MRP"))
                .thenReturn(price);

        PriceDto response = priceService.update(priceDto);

        verify(priceRepository).save(price);

        assertTrue(response.isSuccess());
        assertEquals("Price updated successfully", response.getMessage());
    }

    @Test
    void delete_success() {
        doNothing().when(priceRepository).deleteByIdentifier("SKU1MRP");

        priceService.delete("SKU1MRP");

        verify(priceRepository, times(1))
                .deleteByIdentifier("SKU1MRP");
    }
}