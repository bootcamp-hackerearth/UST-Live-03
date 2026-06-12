package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
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
import java.util.Optional;

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
        priceDto.setProduct("SKU1");

        List<Price> priceList = List.of(price);
        List<PriceDto> dtoList = List.of(priceDto);

        Mockito.when(priceRepository.findAll())
                .thenReturn(priceList);

        Mockito.when(modelMapper.map(
                        eq(priceList),
                        any(java.lang.reflect.Type.class)))
                .thenReturn(dtoList);

        PaginationResponseDto<PriceDto> result =
                priceService.findAll(null);

        Assertions.assertEquals(1, result.getDtoList().size());

        Assertions.assertEquals(
                "SKU1",
                result.getDtoList().get(0).getProduct()
        );
    }

    @Test
    void findAllWithPageableTest() {

        Price price = new Price();
        price.setProduct("SKU1");

        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("SKU1");

        Pageable pageable = PageRequest.of(0, 5);
        Page<Price> pricePage = new PageImpl<>(List.of(price));

        List<PriceDto> dtoList = List.of(priceDto);

        Mockito.when(priceRepository.findAll(pageable))
                .thenReturn(pricePage);

        Mockito.when(modelMapper.map(
                        eq(pricePage.getContent()),
                        any(java.lang.reflect.Type.class)))
                .thenReturn(dtoList);

        PaginationResponseDto<PriceDto> result =
                priceService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());

        Assertions.assertEquals(
                "SKU1",
                result.getDtoList().get(0).getProduct()
        );
    }

    @Test
    void findAll_productDoesNotExist() {

        Price price = new Price();
        price.setProduct("SKU1");

        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("SKU1");

        List<Price> priceList = List.of(price);
        List<PriceDto> dtoList = List.of(priceDto);

        when(priceRepository.findAll())
                .thenReturn(priceList);

        when(modelMapper.map(
                eq(priceList),
                any(java.lang.reflect.Type.class)))
                .thenReturn(dtoList);

        PaginationResponseDto<PriceDto> result =
                priceService.findAll(null);

        assertEquals(1, result.getDtoList().size());

        assertEquals(
                "SKU1",
                result.getDtoList().get(0).getProduct()
        );
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
    void findById_success() {
        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));
        when(modelMapper.map(any(), eq(PriceDto.class)))
                .thenReturn(priceDto);


        PriceDto result = priceService.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findById_notFound_throwsException() {

        when(priceRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> priceService.findById(99L)
        );

        assertEquals("Price not found", exception.getMessage());
    }

    @Test
    void update_priceNotFound() {
        when(priceRepository.findById(1L)).thenReturn(Optional.empty());

        PriceDto response = priceService.update(priceDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void update_success() {
        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));

        doNothing().when(modelMapper).map(priceDto, price);

        when(priceRepository.save(price)).thenReturn(price);

        PriceDto response = priceService.update(priceDto);

        verify(priceRepository).save(price);

        assertTrue(response.isSuccess());
        assertEquals("Successfully updated price", response.getMessage());
    }

    @Test
    void delete_success() {

        doNothing().when(priceRepository)
                .deleteByIdentifier("SKU1MRP");

        priceService.delete("SKU1MRP");

        verify(priceRepository, times(1))
                .deleteByIdentifier("SKU1MRP");
    }
}