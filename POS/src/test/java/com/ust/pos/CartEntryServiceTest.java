package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    @Spy
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
    void findByIdentifierTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("CART_PROD");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("CART_PROD");

        when(cartEntryRepository.findByIdentifier("CART_PROD")).thenReturn(cartEntry);

        when(modelMapper.map(cartEntry, CartEntryDto.class)).thenReturn(dto);

        CartEntryDto result = cartEntryService.findByIdentifier("CART_PROD");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CART_PROD", result.getIdentifier());

        verify(cartEntryRepository).findByIdentifier("CART_PROD");
    }

    @Test
    void findByIdentifierNotFoundTest() {

        when(cartEntryRepository.findByIdentifier("ID")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartEntryService.findByIdentifier("ID"));
    }

    @Test
    void saveNewCartEntryTest() {

        Cart cart = new Cart();

        Product product = new Product();
        product.setId(1L);
        product.setIdentifier("PROD1");

        Price price = new Price();
        price.setMrpPrice(BigDecimal.valueOf(120));
        price.setSellingPrice(BigDecimal.valueOf(100));

        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("PROD1");
        dto.setQuantity(2);

        CartEntry saved = new CartEntry();
        saved.setIdentifier("CART1_PROD1");

        CartEntryDto response = new CartEntryDto();
        response.setIdentifier("CART1_PROD1");

        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);

        when(productRepository.findByIdentifier("PROD1")).thenReturn(product);

        when(priceRepository.findByProductId(1L)).thenReturn(price);

        when(cartEntryRepository.findByIdentifier("CART1_PROD1")).thenReturn(null).thenReturn(saved);

        when(modelMapper.map(saved, CartEntryDto.class)).thenReturn(response);

        CartEntryDto result = cartEntryService.save(dto);

        Assertions.assertEquals("CART1_PROD1", result.getIdentifier());

        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    void saveExistingCartEntryTest() {

        Cart cart = new Cart();

        Product product = new Product();
        product.setId(1L);

        Price price = new Price();
        price.setMrpPrice(BigDecimal.valueOf(120));
        price.setSellingPrice(BigDecimal.valueOf(100));

        CartEntry existing = new CartEntry();
        existing.setIdentifier("CART1_PROD1");
        existing.setQuantity(3);

        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("PROD1");
        dto.setQuantity(2);

        CartEntryDto response = new CartEntryDto();

        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);

        when(productRepository.findByIdentifier("PROD1")).thenReturn(product);

        when(priceRepository.findByProductId(1L)).thenReturn(price);

        when(cartEntryRepository.findByIdentifier("CART1_PROD1")).thenReturn(existing);

        when(modelMapper.map(existing, CartEntryDto.class)).thenReturn(response);

        cartEntryService.save(dto);

        Assertions.assertEquals(5, existing.getQuantity());

        Assertions.assertEquals(BigDecimal.valueOf(500), existing.getTotalPrice());

        Assertions.assertEquals(BigDecimal.valueOf(100), existing.getDiscount());

        verify(cartEntryRepository).save(existing);
    }

    @Test
    void saveCartNotFoundTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("PROD1");

        when(cartRepository.findByIdentifier("CART1")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartEntryService.save(dto));
    }

    @Test
    void saveProductNotFoundTest() {

        Cart cart = new Cart();

        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("PROD1");

        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);

        when(productRepository.findByIdentifier("PROD1")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartEntryService.save(dto));
    }

    @Test
    void savePriceNotFoundTest() {

        Cart cart = new Cart();

        Product product = new Product();
        product.setId(1L);

        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("PROD1");

        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);

        when(productRepository.findByIdentifier("PROD1")).thenReturn(product);

        when(priceRepository.findByProductId(1L)).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartEntryService.save(dto));
    }

    @Test
    void updateTest() {

        CartEntry entry = new CartEntry();
        entry.setIdentifier("ID");
        entry.setProductIdentifier("PROD1");

        Product product = new Product();
        product.setId(1L);

        Price price = new Price();
        price.setMrpPrice(BigDecimal.valueOf(120));
        price.setSellingPrice(BigDecimal.valueOf(100));

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ID");
        dto.setQuantity(4);

        CartEntryDto response = new CartEntryDto();

        when(cartEntryRepository.findByIdentifier("ID")).thenReturn(entry);

        when(productRepository.findByIdentifier("PROD1")).thenReturn(product);

        when(priceRepository.findByProductId(1L)).thenReturn(price);

        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(response);

        CartEntryDto result = cartEntryService.update(dto);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(4, entry.getQuantity());

        Assertions.assertEquals(BigDecimal.valueOf(400), entry.getTotalPrice());

        verify(cartEntryRepository).save(entry);
    }

    @Test
    void updateCartEntryNotFoundTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ID");

        when(cartEntryRepository.findByIdentifier("ID")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartEntryService.update(dto));
    }

    @Test
    void updatePriceNotFoundTest() {

        CartEntry entry = new CartEntry();
        entry.setIdentifier("ID");
        entry.setProductIdentifier("PROD1");

        Product product = new Product();
        product.setId(1L);

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ID");

        when(cartEntryRepository.findByIdentifier("ID")).thenReturn(entry);

        when(productRepository.findByIdentifier("PROD1")).thenReturn(product);

        when(priceRepository.findByProductId(1L)).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartEntryService.update(dto));
    }

    @Test
    void deleteTest() {

        CartEntry entry = new CartEntry();

        when(cartEntryRepository.findByIdentifier("ID")).thenReturn(entry);

        boolean result = cartEntryService.delete("ID");

        Assertions.assertTrue(result);

        verify(cartEntryRepository).deleteByIdentifier("ID");
    }

    @Test
    void deleteNotFoundTest() {

        when(cartEntryRepository.findByIdentifier("ID")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartEntryService.delete("ID"));
    }

    @Test
    void deleteByCartIdentifierTest() {

        boolean result = cartEntryService.deleteByCartIdentifier("CART1");

        Assertions.assertTrue(result);

        verify(cartEntryRepository).deleteByCartIdentifier("CART1");
    }

    @Test
    void findAllTest() {

        CartEntry entry = new CartEntry();

        Pageable pageable = PageRequest.of(0, 10);

        Page<CartEntry> page = new PageImpl<>(List.of(entry));

        CartEntryDto dto = new CartEntryDto();

        when(cartEntryRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(dto);

        List<CartEntryDto> result = cartEntryService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findAllEmptyPageTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<CartEntry> page = new PageImpl<>(List.of());

        when(cartEntryRepository.findAll(pageable)).thenReturn(page);

        List<CartEntryDto> result = cartEntryService.findAll(pageable);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findAllByCartIdentifierTest() {

        CartEntry entry = new CartEntry();

        CartEntryDto dto = new CartEntryDto();

        when(cartEntryRepository.findAllByCartIdentifier("CART1")).thenReturn(List.of(entry));

        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(dto);

        List<CartEntryDto> result = cartEntryService.findAllByCartIdentifier("CART1");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findAllByCartIdentifierEmptyTest() {

        when(cartEntryRepository.findAllByCartIdentifier("CART1")).thenReturn(List.of());

        List<CartEntryDto> result = cartEntryService.findAllByCartIdentifier("CART1");

        Assertions.assertTrue(result.isEmpty());
    }

}