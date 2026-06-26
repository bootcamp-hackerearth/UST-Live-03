package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Cart;
import com.ust.pos.modell.CartEntry;
import com.ust.pos.modell.CartEntryRepository;
import com.ust.pos.modell.CartRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    public static final String CART_1 = "CART1";
    public static final String PROD_1_MRP = "PROD1-MRP";
    public static final String PROD_1_SELLING = "PROD1-SELLING";
    public static final String CART_1_PROD_1 = "CART1-PROD1";
    @InjectMocks
    private CartServiceImpl service;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private PriceService priceService;

    @Mock
    private ModelMapper modelMapper;

    private Cart cart;
    private CartEntry entry;

    @BeforeEach
    void setUp() {

        cart = new Cart();
        cart.setIdentifier(CART_1);
        cart.setStatus(true);

        entry = new CartEntry();
        entry.setIdentifier(CART_1_PROD_1);
        entry.setCartIdentifier(CART_1);
        entry.setProductIdentifier("PROD1");
        entry.setQuantity(2);
        entry.setDeleted(false);
    }

    @Test
    void saveSuccess() {

        CartDto dto = new CartDto();
        dto.setIdentifier(CART_1);

        when(cartRepository.findByIdentifier(CART_1))
                .thenReturn(null);

        CartDto result = service.save(dto);

        assertNotNull(result);

        verify(cartRepository).save(any(Cart.class));
    }
    @Test
    void recalculateAndSaveWithOnlyMrp() {

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(PROD_1_SELLING))
                .thenReturn(null);

        when(priceService.findByIdentifier(PROD_1_MRP))
                .thenReturn(mrp);

        service.recalculateAndSave(cart);

        assertEquals(
                BigDecimal.valueOf(240),
                cart.getTotalPrice()
        );

        assertEquals(
                BigDecimal.valueOf(240),
                cart.getOriginalPrice()
        );

        assertEquals(
                BigDecimal.ZERO,
                cart.getDiscount()
        );

        verify(cartRepository).save(cart);
    }

    @Test
    void deleteSuccessWithCartRecalculation() {

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));

        when(cartEntryRepository.findByIdentifierAndDeletedFalse(CART_1_PROD_1))
                .thenReturn(entry);

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(PROD_1_SELLING))
                .thenReturn(selling);

        when(priceService.findByIdentifier(PROD_1_MRP))
                .thenReturn(mrp);

        service.delete(CART_1_PROD_1);

        verify(cartEntryRepository).save(entry);

        verify(cartRepository).save(cart);

        assertEquals(BigDecimal.valueOf(200), cart.getTotalPrice());
        assertEquals(BigDecimal.valueOf(240), cart.getOriginalPrice());
    }

    @Test
    void saveAlreadyExists() {

        CartDto dto = new CartDto();
        dto.setIdentifier(CART_1);

        cart.setDeleted(false);

        when(cartRepository.findByIdentifier(CART_1))
                .thenReturn(cart);

        CartDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Cart already exists",
                result.getMessage()
        );
    }

    @Test
    void saveSoftDeletedCart() {

        CartDto dto = new CartDto();
        dto.setIdentifier(CART_1);

        cart.setDeleted(true);

        when(cartRepository.findByIdentifier(CART_1))
                .thenReturn(cart);

        CartDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Cart with Identifier CART1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void deleteSuccess() {

        when(cartEntryRepository.findByIdentifierAndDeletedFalse(CART_1_PROD_1))
                .thenReturn(entry);

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(null);

        service.delete(CART_1_PROD_1);

        verify(cartEntryRepository).save(entry);
    }

    @Test
    void deleteEntryNotFound() {

        when(cartEntryRepository.findByIdentifierAndDeletedFalse("X"))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.delete("X")
                );

        assertEquals(
                "Cart entry not found",
                exception.getMessage()
        );
    }

    @Test
    void findByIdentifierSuccess() {

        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.valueOf(100));

        CartDto cartDto = new CartDto();

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(anyString()))
                .thenReturn(price);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        when(modelMapper.map(entry, CartEntryDto.class))
                .thenReturn(new CartEntryDto());

        CartDto result = service.findByIdentifier(CART_1);

        assertNotNull(result);
        assertEquals(1, result.getEntryCart().size());
    }

    @Test
    void findByIdentifierNotFound() {

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.findByIdentifier(CART_1)
                );

        assertEquals(
                "Cart not found",
                exception.getMessage()
        );
    }

    @Test
    void findAllSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Cart> page =
                new PageImpl<>(
                        List.of(cart),
                        pageable,
                        1
                );

        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.valueOf(100));

        when(cartRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(anyString()))
                .thenReturn(price);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(new CartDto());

        when(modelMapper.map(entry, CartEntryDto.class))
                .thenReturn(new CartEntryDto());

        WsDto<CartDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void recalculateAndSaveWithSellingAndMrp() {

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(PROD_1_SELLING))
                .thenReturn(selling);

        when(priceService.findByIdentifier(PROD_1_MRP))
                .thenReturn(mrp);

        service.recalculateAndSave(cart);

        assertEquals(
                BigDecimal.valueOf(200),
                cart.getTotalPrice()
        );

        assertEquals(
                BigDecimal.valueOf(240),
                cart.getOriginalPrice()
        );

        verify(cartRepository).save(cart);
    }

    @Test
    void recalculateAndSaveCouponApplied() {

        cart.setCoupon("FLAT10");

        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.valueOf(100));

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(anyString()))
                .thenReturn(price);

        service.recalculateAndSave(cart);

        assertTrue(
                cart.getDiscount()
                        .compareTo(BigDecimal.ZERO) > 0
        );
    }

    @Test
    void recalculateAndSavePriceMissing() {

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(anyString()))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.recalculateAndSave(cart)
                );

        assertEquals(
                "Price not configured",
                exception.getMessage()
        );
    }

    @Test
    void clearCartSuccess() {

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        service.clearCart(CART_1);

        verify(cartEntryRepository).save(entry);
        verify(cartRepository).save(cart);

        assertEquals(
                BigDecimal.ZERO,
                cart.getTotalPrice()
        );
    }

    @Test
    void clearCartCartNotFound() {

        when(cartRepository.findByIdentifierAndDeletedFalse("X"))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.clearCart("X")
                );

        assertEquals(
                "Cart not found",
                exception.getMessage()
        );
    }

    @Test
    void findAllEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Cart> page =
                new PageImpl<>(
                        Collections.emptyList(),
                        pageable,
                        0
                );

        when(cartRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        WsDto<CartDto> result =
                service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }
}