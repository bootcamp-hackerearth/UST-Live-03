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
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private PriceService priceService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;
    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setIdentifier("CART1");
        cart.setCustomerIdentifier("CUST1");
        cart.setCoupon(null);
        cartDto = new CartDto();
        cartDto.setIdentifier("CART1");
        cartDto.setCustomerIdentifier("CUST1");
    }

    @Test
    void save_Success() {
        when(cartRepository.findByIdentifier("CART1")).thenReturn(null);
        CartDto result = cartService.save(cartDto);
        verify(cartRepository).save(any(Cart.class));
        assertEquals("CART1", result.getIdentifier());
    }

    @Test
    void save_AlreadyExists() {
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        CartDto result = cartService.save(cartDto);
        assertFalse(result.isSuccess());
        assertEquals("Cart already exists", result.getMessage());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void findByIdentifier_Success() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("CART1-1");
        entry.setCartIdentifier("CART1");
        entry.setProductIdentifier("PROD1");
        entry.setQuantity(2);
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));
        CartEntryDto entryDto = new CartEntryDto();
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        when(cartEntryRepository.findByCartIdentifier("CART1")).thenReturn(List.of(entry));
        when(priceService.findByIdentifier("PROD1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("PROD1-MRP")).thenReturn(mrp);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(entryDto);
        CartDto result = cartService.findByIdentifier("CART1");
        assertNotNull(result);
        assertEquals(1, result.getEntryCart().size());
        verify(cartRepository, atLeastOnce()).save(any());
    }

    @Test
    void findByIdentifier_NotFound() {
        when(cartRepository.findByIdentifier("CART1")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> cartService.findByIdentifier("CART1"));
        assertEquals("Cart not found", ex.getMessage());
    }

    @Test
    void delete_Success() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("CART1-1");
        entry.setCartIdentifier("CART1");
        entry.setProductIdentifier("PROD1");
        entry.setQuantity(1);
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));
        when(cartEntryRepository.findByIdentifier("CART1-1")).thenReturn(entry);
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        when(cartEntryRepository.findByCartIdentifier("CART1")).thenReturn(List.of(entry));
        when(priceService.findByIdentifier("PROD1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("PROD1-MRP")).thenReturn(mrp);
        cartService.delete("CART1-1");
        verify(cartEntryRepository).delete(entry);
        verify(cartRepository, atLeastOnce()).save(any());
    }

    @Test
    void delete_EntryNotFound() {
        when(cartEntryRepository.findByIdentifier("CART1-1")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> cartService.delete("CART1-1"));
        assertEquals("Cart entry not found", ex.getMessage());
    }

    @Test
    void recalculateAndSave_WithCoupon() {
        cart.setCoupon("FLAT10");
        CartEntry entry = new CartEntry();
        entry.setProductIdentifier("PROD1");
        entry.setQuantity(2);
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));
        when(cartEntryRepository.findByCartIdentifier("CART1")).thenReturn(List.of(entry));
        when(priceService.findByIdentifier("PROD1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("PROD1-MRP")).thenReturn(mrp);
        cartService.recalculateAndSave(cart);
        assertEquals(BigDecimal.valueOf(240), cart.getOriginalPrice()
        );
        verify(cartRepository).save(cart);
    }

    @Test
    void recalculateAndSave_MrpOnly() {
        CartEntry entry = new CartEntry();
        entry.setProductIdentifier("PROD1");
        entry.setQuantity(1);
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(150));
        when(cartEntryRepository.findByCartIdentifier("CART1")).thenReturn(List.of(entry));
        when(priceService.findByIdentifier("PROD1-SELLING")).thenReturn(null);
        when(priceService.findByIdentifier("PROD1-MRP")).thenReturn(mrp);
        cartService.recalculateAndSave(cart);
        assertEquals(BigDecimal.valueOf(150), cart.getTotalPrice());
        verify(cartRepository).save(cart);
    }

    @Test
    void recalculateAndSave_PriceMissing() {
        CartEntry entry = new CartEntry();
        entry.setProductIdentifier("PROD1");
        entry.setQuantity(1);
        when(cartEntryRepository.findByCartIdentifier("CART1")).thenReturn(List.of(entry));
        when(priceService.findByIdentifier("PROD1-SELLING")).thenReturn(null);
        when(priceService.findByIdentifier("PROD1-MRP")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> cartService.recalculateAndSave(cart));
        assertEquals("Price not configured", ex.getMessage());
    }

    @Test
    void clearCart_Success() {
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        cartService.clearCart("CART1");
        verify(cartEntryRepository).deleteByCartIdentifier("CART1");
        verify(cartRepository).save(cart);
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        assertEquals(BigDecimal.ZERO, cart.getOriginalPrice());
        assertEquals(BigDecimal.ZERO, cart.getDiscount());
    }

    @Test
    void clearCart_NotFound() {
        when(cartRepository.findByIdentifier("CART1")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> cartService.clearCart("CART1"));
        assertEquals("Cart not found", ex.getMessage());
    }

    @Test
    void findAll_Success() {
        CartEntry entry = new CartEntry();
        entry.setProductIdentifier("PROD1");
        entry.setQuantity(1);
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cart> page = new PageImpl<>(List.of(cart), pageable, 1);
        when(cartRepository.findAll(pageable)).thenReturn(page);
        when(cartEntryRepository.findByCartIdentifier("CART1")).thenReturn(List.of(entry));
        when(priceService.findByIdentifier("PROD1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("PROD1-MRP")).thenReturn(mrp);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(new CartEntryDto());
        WsDto<CartDto> result = cartService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        verify(cartRepository).findAll(pageable);
    }
}