package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {
        CartEntry cartEntry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();
        when(cartEntryRepository.findByIdentifier("CART1_SKU1")).thenReturn(cartEntry);
        when(modelMapper.map(cartEntry, CartEntryDto.class)).thenReturn(dto);
        CartEntryDto response = cartEntryService.findByIdentifier("CART1_SKU1");
        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        when(cartEntryRepository.findByIdentifier("CART1_SKU1")).thenReturn(null);
        CartEntryDto response = cartEntryService.findByIdentifier("CART1_SKU1");
        Assertions.assertNull(response);
    }

    @Test
    void saveCartNotFoundTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        when(cartRepository.findByIdentifier("CART1")).thenReturn(null);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart with identifier - CART1 not found", response.getMessage());
    }

    @Test
    void saveProductNotFoundTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("SKU1");
        when(cartRepository.findByIdentifier("CART1")).thenReturn(new Cart());
        when(productRepository.findByIdentifier("SKU1")).thenReturn(null);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - SKU1 not found", response.getMessage());
    }

    @Test
    void savePriceNotFoundTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("SKU1");
        Product product = new Product();
        product.setId(1L);
        when(cartRepository.findByIdentifier("CART1")).thenReturn(new Cart());
        when(productRepository.findByIdentifier("SKU1")).thenReturn(product);
        when(priceRepository.findByProductId(1L)).thenReturn(null);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price not found for product - SKU1", response.getMessage());
    }

    @Test
    void saveNewCartEntryTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("SKU1");
        dto.setQuantity(2);
        Product product = new Product();
        product.setId(1L);
        Price price = new Price();
        price.setSellingPrice(BigDecimal.valueOf(100));
        price.setMrpPrice(BigDecimal.valueOf(150));
        CartEntry savedEntry = new CartEntry();
        CartEntryDto mappedDto = new CartEntryDto();
        when(cartRepository.findByIdentifier("CART1")).thenReturn(new Cart());
        when(productRepository.findByIdentifier("SKU1")).thenReturn(product);
        when(priceRepository.findByProductId(1L)).thenReturn(price);
        when(cartEntryRepository.findByIdentifier("CART1_SKU1")).thenReturn(null).thenReturn(savedEntry);
        when(modelMapper.map(savedEntry, CartEntryDto.class)).thenReturn(mappedDto);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertNotNull(response);
        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    void saveExistingCartEntryTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("SKU1");
        dto.setQuantity(2);
        Product product = new Product();
        product.setId(1L);
        Price price = new Price();
        price.setSellingPrice(BigDecimal.valueOf(100));
        price.setMrpPrice(BigDecimal.valueOf(150));
        CartEntry existing = new CartEntry();
        existing.setQuantity(3);
        CartEntryDto mappedDto = new CartEntryDto();
        when(cartRepository.findByIdentifier("CART1")).thenReturn(new Cart());
        when(productRepository.findByIdentifier("SKU1")).thenReturn(product);
        when(priceRepository.findByProductId(1L)).thenReturn(price);
        when(cartEntryRepository.findByIdentifier("CART1_SKU1")).thenReturn(existing).thenReturn(existing);
        when(modelMapper.map(existing, CartEntryDto.class)).thenReturn(mappedDto);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(5, existing.getQuantity());
        verify(cartEntryRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("CART1_SKU1");
        when(cartEntryRepository.findByIdentifier("CART1_SKU1")).thenReturn(null);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart Entry with identifier - CART1_SKU1 not found", response.getMessage());
    }

    @Test
    void updateSuccessTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("CART1_SKU1");
        dto.setQuantity(4);
        CartEntry existing = new CartEntry();
        existing.setProductIdentifier("SKU1");
        Product product = new Product();
        product.setId(1L);
        Price price = new Price();
        price.setSellingPrice(BigDecimal.valueOf(100));
        price.setMrpPrice(BigDecimal.valueOf(150));
        CartEntryDto mappedDto = new CartEntryDto();
        when(cartEntryRepository.findByIdentifier("CART1_SKU1")).thenReturn(existing).thenReturn(existing);
        when(productRepository.findByIdentifier("SKU1")).thenReturn(product);
        when(priceRepository.findByProductId(1L)).thenReturn(price);
        when(modelMapper.map(existing, CartEntryDto.class)).thenReturn(mappedDto);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(4, existing.getQuantity());
        verify(cartEntryRepository).save(existing);
    }


    @Test
    void deleteTest() {
        CartEntry cartEntry = new CartEntry();
        when(cartEntryRepository.findByIdentifier("CART1_SKU1")).thenReturn(cartEntry);
        boolean response = cartEntryService.delete("CART1_SKU1");
        Assertions.assertTrue(response);
        verify(cartEntryRepository).deleteByIdentifier("CART1_SKU1");
        verify(cartEntryRepository, never()).save(any());
    }

    @Test
    void deleteNotFoundTest() {
        when(cartEntryRepository.findByIdentifier("CART1_SKU1")).thenReturn(null);
        boolean response = cartEntryService.delete("CART1_SKU1");
        Assertions.assertFalse(response);
        verify(cartEntryRepository, never()).deleteByIdentifier(any());
        verify(cartEntryRepository, never()).save(any());
    }

    @Test
    void deleteByCartIdentifierTest() {
        boolean response = cartEntryService.deleteByCartIdentifier("CART1");
        Assertions.assertTrue(response);
        verify(cartEntryRepository).deleteByCartIdentifier("CART1");
        verify(cartEntryRepository, never()).findAllByCartIdentifier(any());
        verify(cartEntryRepository, never()).save(any());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        CartEntry cartEntry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();
        when(cartEntryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(cartEntry)));
        when(modelMapper.map(cartEntry, CartEntryDto.class)).thenReturn(dto);
        List<CartEntryDto> response = cartEntryService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);
        when(cartEntryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));
        List<CartEntryDto> response = cartEntryService.findAll(pageable);
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void findAllByCartIdentifierTest() {
        CartEntry cartEntry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();
        when(cartEntryRepository.findAllByCartIdentifier("CART1")).thenReturn(List.of(cartEntry));
        when(modelMapper.map(cartEntry, CartEntryDto.class)).thenReturn(dto);
        List<CartEntryDto> response = cartEntryService.findAllByCartIdentifier("CART1");
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllByCartIdentifierEmptyTest() {
        when(cartEntryRepository.findAllByCartIdentifier("CART1")).thenReturn(List.of());
        List<CartEntryDto> response = cartEntryService.findAllByCartIdentifier("CART1");
        Assertions.assertTrue(response.isEmpty());
    }

}