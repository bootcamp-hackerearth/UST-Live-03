package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.*;
import com.ust.pos.modell.*;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

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
        cart.setIdentifier("CART-1");
        entry = new CartEntry();
        entry.setIdentifier("CART-1-ITEM-1");
        entry.setProductIdentifier("PROD-1");
        entry.setQuantity(2);
    }

    @Test
    void save_shouldCreateCart() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART-1");
        when(cartRepository.findByIdentifier("CART-1")).thenReturn(null);
        CartDto result = cartService.save(dto);
        verify(cartRepository).save(any(Cart.class));
        assertEquals("CART-1", result.getIdentifier());
    }

    @Test
    void save_shouldFailIfCartExists() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART-1");
        when(cartRepository.findByIdentifier("CART-1")).thenReturn(cart);
        CartDto result = cartService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Cart already exists", result.getMessage());
    }

    @Test
    void delete_shouldRemoveEntry() {
        when(cartEntryRepository.findByIdentifier("CART-1-ITEM-1"))
                .thenReturn(entry);
        when(cartRepository.findByIdentifier("CART"))
                .thenReturn(cart);
        when(cartEntryRepository.findByCartIdentifier("CART-1"))
                .thenReturn(List.of());
        cartService.delete("CART-1-ITEM-1");
        verify(cartEntryRepository).delete(entry);
    }

    @Test
    void delete_shouldThrowIfEntryNotFound() {
        when(cartEntryRepository.findByIdentifier("X")).thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> cartService.delete("X"));
    }

    @Test
    void findByIdentifier_shouldReturnCart() {
        when(cartRepository.findByIdentifier("CART-1")).thenReturn(cart);
        when(cartEntryRepository.findByCartIdentifier("CART-1"))
                .thenReturn(List.of(entry));
        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier(anyString()))
                .thenReturn(price);
        when(modelMapper.map(any(Cart.class), eq(CartDto.class)))
                .thenReturn(new CartDto());
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(new CartEntryDto());
        CartDto result = cartService.findByIdentifier("CART-1");
        assertNotNull(result);
    }

    @Test
    void findByIdentifier_shouldThrowIfNotFound() {
        when(cartRepository.findByIdentifier("X")).thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> cartService.findByIdentifier("X"));
    }

    @Test
    void findAll_shouldReturnPagedResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cart> page = new PageImpl<>(List.of(cart));
        when(cartRepository.findAll(pageable)).thenReturn(page);
        when(cartEntryRepository.findByCartIdentifier("CART-1"))
                .thenReturn(List.of(entry));
        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier(anyString()))
                .thenReturn(price);
        when(modelMapper.map(any(Cart.class), eq(CartDto.class)))
                .thenReturn(new CartDto());
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(new CartEntryDto());
        WsDto<CartDto> result = cartService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void recalculate_shouldCalculatePrices() {
        when(cartEntryRepository.findByCartIdentifier("CART-1"))
                .thenReturn(List.of(entry));
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));
        when(priceService.findByIdentifier("PROD-1-SELLING"))
                .thenReturn(selling);
        when(priceService.findByIdentifier("PROD-1-MRP"))
                .thenReturn(mrp);
        cartService.recalculateAndSave(cart);
        assertEquals(BigDecimal.valueOf(200), cart.getTotalPrice());
        assertEquals(BigDecimal.valueOf(240), cart.getOriginalPrice());
    }

    @Test
    void recalculate_shouldApplyCoupon() {
        cart.setCoupon("FLAT10");
        when(cartEntryRepository.findByCartIdentifier("CART-1"))
                .thenReturn(List.of(entry));
        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier(anyString()))
                .thenReturn(price);
        cartService.recalculateAndSave(cart);
        assertTrue(cart.getDiscount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void recalculate_shouldThrowIfNoPrice() {
        when(cartEntryRepository.findByCartIdentifier("CART-1"))
                .thenReturn(List.of(entry));
        when(priceService.findByIdentifier(anyString()))
                .thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> cartService.recalculateAndSave(cart));
    }

    @Test
    void clearCart_shouldResetCart() {
        when(cartRepository.findByIdentifier("CART-1")).thenReturn(cart);
        cartService.clearCart("CART-1");
        verify(cartEntryRepository).deleteByCartIdentifier("CART-1");
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        assertEquals(BigDecimal.ZERO, cart.getOriginalPrice());
    }

    @Test
    void clearCart_shouldThrowIfCartNotFound() {
        when(cartRepository.findByIdentifier("X")).thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> cartService.clearCart("X"));
    }
}