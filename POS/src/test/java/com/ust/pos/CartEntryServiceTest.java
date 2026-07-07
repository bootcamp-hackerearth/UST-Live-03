package com.ust.pos;

import com.ust.pos.cartEntry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.*;
import com.ust.pos.price.service.PriceService;
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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    private Product mockProduct;
    private final String expectedPriceId = "PROD1-Product Name";

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setIdentifier("PROD1");
        mockProduct.setName("Product Name");
    }

    @Test
    void findByIdentifierSuccessTest() {
        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("CART1_PROD1");

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setIdentifier("CART1_PROD1");

        Mockito.when(cartEntryRepository.findByIdentifier("CART1_PROD1")).thenReturn(cartEntry);
        Mockito.when(modelMapper.map(cartEntry, CartEntryDto.class)).thenReturn(cartEntryDto);

        CartEntryDto response = cartEntryService.findByIdentifier("CART1_PROD1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CART1_PROD1", response.getIdentifier());
    }

    @Test
    void saveSuccessNewEntryTest() {
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setCartId("CART1");
        cartEntryDto.setProduct("PROD1");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        PriceDto sellingPriceDto = new PriceDto();
        sellingPriceDto.setPriceAmount(new BigDecimal("100"));

        PriceDto mrpDto = new PriceDto();
        mrpDto.setPriceAmount(new BigDecimal("120"));

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(mockProduct);
        Mockito.when(cartEntryRepository.findByIdentifier("CART1_PROD1")).thenReturn(null);
        Mockito.when(priceService.findByProductAndPriceType(expectedPriceId, "Selling price")).thenReturn(sellingPriceDto);
        Mockito.when(priceService.findByProductAndPriceType(expectedPriceId, "MRP")).thenReturn(mrpDto);

        CartEntry cartEntry = new CartEntry();
        Mockito.when(modelMapper.map(cartEntryDto, CartEntry.class)).thenReturn(cartEntry);

        Cart cart = new Cart();
        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        Mockito.when(cartEntryRepository.findAllByCartId("CART1")).thenReturn(new ArrayList<>());

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertEquals("CART1_PROD1", response.getIdentifier());
        Assertions.assertEquals(new BigDecimal("200"), response.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("40"), response.getDiscount());
        verify(cartEntryRepository).save(cartEntry);
        verify(cartRepository).save(cart);
    }

    @Test
    void saveSuccessExistingEntryTest() {
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setCartId("CART1");
        cartEntryDto.setProduct("PROD1");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        CartEntry existingCartEntry = new CartEntry();
        existingCartEntry.setQuantity(new BigDecimal("3"));

        PriceDto sellingPriceDto = new PriceDto();
        sellingPriceDto.setPriceAmount(new BigDecimal("100"));

        PriceDto mrpDto = new PriceDto();
        mrpDto.setPriceAmount(new BigDecimal("120"));

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(mockProduct);
        Mockito.when(cartEntryRepository.findByIdentifier("CART1_PROD1")).thenReturn(existingCartEntry);
        Mockito.when(priceService.findByProductAndPriceType(expectedPriceId, "Selling price")).thenReturn(sellingPriceDto);
        Mockito.when(priceService.findByProductAndPriceType(expectedPriceId, "MRP")).thenReturn(mrpDto);

        Cart cart = new Cart();
        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertEquals(new BigDecimal("5"), response.getQuantity());
        verify(modelMapper).map(cartEntryDto, existingCartEntry);
        verify(cartEntryRepository).save(existingCartEntry);
    }

    @Test
    void saveFailurePriceNotFoundTest() {
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setCartId("CART1");
        cartEntryDto.setProduct("PROD1");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(mockProduct);
        Mockito.when(cartEntryRepository.findByIdentifier("CART1_PROD1")).thenReturn(null);
        Mockito.when(priceService.findByProductAndPriceType(expectedPriceId, "MRP")).thenThrow(new IllegalArgumentException());

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("The product or price Not found", response.getMessage());
        Mockito.verify(cartEntryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Cart cart = new Cart();
        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);

        cartEntryService.delete("CART1", "PROD1");

        verify(cartEntryRepository).deleteByCartIdAndProduct("CART1", "PROD1");
        verify(cartRepository).save(cart);
    }

    @Test
    void findAllSuccessTest() {
        CartEntry entry = new CartEntry();
        List<CartEntry> entryList = List.of(entry);

        CartEntryDto dto = new CartEntryDto();
        List<CartEntryDto> dtos = List.of(dto);

        Page<CartEntry> page = new PageImpl<>(entryList);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(entryList), Mockito.any(Type.class))).thenReturn(dtos);

        List<CartEntryDto> result = cartEntryService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findByCartIdSuccessTest() {
        CartEntry entry = new CartEntry();
        List<CartEntry> entryList = List.of(entry);

        CartEntryDto dto = new CartEntryDto();
        List<CartEntryDto> dtos = List.of(dto);

        Mockito.when(cartEntryRepository.findAllByCartId("CART1")).thenReturn(entryList);
        Mockito.when(modelMapper.map(Mockito.eq(entryList), Mockito.any(Type.class))).thenReturn(dtos);

        List<CartEntryDto> result = cartEntryService.findByCartId("CART1");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void deleteAllByCartIdSuccessTest() {
        Cart cart = new Cart();
        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);

        cartEntryService.deleteAllByCartId("CART1");

        verify(cartEntryRepository).deleteAllByCartId("CART1");
        verify(cartRepository).save(cart);
    }

    @Test
    void updateQuantitySuccessTest() {
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setCartId("CART1");
        cartEntryDto.setProduct("PROD1");
        cartEntryDto.setQuantity(new BigDecimal("4"));

        PriceDto sellingPriceDto = new PriceDto();
        sellingPriceDto.setPriceAmount(new BigDecimal("50"));

        PriceDto mrpDto = new PriceDto();
        mrpDto.setPriceAmount(new BigDecimal("60"));

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(mockProduct);
        Mockito.when(cartEntryRepository.findByIdentifier("CART1_PROD1")).thenReturn(null);
        Mockito.when(priceService.findByProductAndPriceType(expectedPriceId, "Selling price")).thenReturn(sellingPriceDto);
        Mockito.when(priceService.findByProductAndPriceType(expectedPriceId, "MRP")).thenReturn(mrpDto);

        Cart entryCart = new Cart();
        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(entryCart);

        CartEntry cartEntry = new CartEntry();
        Mockito.when(modelMapper.map(cartEntryDto, CartEntry.class)).thenReturn(cartEntry);

        CartEntryDto response = cartEntryService.updateQuantity(cartEntryDto);

        Assertions.assertEquals("CART1_PROD1", response.getIdentifier());
        Assertions.assertEquals(new BigDecimal("200"), response.getTotalPrice());
        verify(cartEntryRepository).save(cartEntry);
    }
}