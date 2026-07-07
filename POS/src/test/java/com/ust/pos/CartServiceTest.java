package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Cart;
import com.ust.pos.models.CartEntry;
import com.ust.pos.models.CartEntryRepository;
import com.ust.pos.models.CartRepository;
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
        cart.setCustomerIdentifier("CUS1");
        cartDto = new CartDto();
        cartDto.setIdentifier("CART1");
        cartDto.setCustomerIdentifier("CUS1");
    }

    @Test
    void saveTest() {
        when(cartRepository.findByIdentifier("CART1")).thenReturn(null);
        CartDto result = cartService.save(cartDto);
        assertEquals("CART1", result.getIdentifier());
        verify(cartRepository).save(any(Cart.class));
        when(cartRepository.findByIdentifier("DUP")).thenReturn(new Cart());
        CartDto duplicate = new CartDto();
        duplicate.setIdentifier("DUP");
        result = cartService.save(duplicate);
        assertFalse(result.isSuccess());
        assertEquals("Cart already exists", result.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        CartEntry entry = new CartEntry();
        entry.setProductIdentifier("PROD1");
        entry.setQuantity(2);
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        when(cartEntryRepository.findByCartIdentifier("CART1")).thenReturn(List.of(entry));
        when(priceService.findByIdentifier("PROD1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("PROD1-MRP")).thenReturn(mrp);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(new CartEntryDto());
        CartDto result = cartService.findByIdentifier("CART1");
        assertNotNull(result);
        assertEquals(1, result.getEntryCart().size());
        verify(cartRepository, atLeastOnce()).save(any());
        when(cartRepository.findByIdentifier("UNKNOWN")).thenReturn(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cartService.findByIdentifier("UNKNOWN"));
        assertEquals("Cart not found", ex.getMessage()
        );
    }

    @Test
    void deleteTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("CART1-1");
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
        when(cartEntryRepository.findByIdentifier("UNKNOWN")).thenReturn(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cartService.delete("UNKNOWN"));
        assertEquals("Cart entry not found", ex.getMessage());
        CartEntry entry2 = new CartEntry();
        entry2.setIdentifier("CART2-1");
        when(cartEntryRepository.findByIdentifier("CART2-1")).thenReturn(entry2);
        when(cartRepository.findByIdentifier("CART2")).thenReturn(null);
        cartService.delete("CART2-1");
        verify(cartEntryRepository).delete(entry2);
    }

    @Test
    void recalculateAndSaveTest() {
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
        assertEquals(BigDecimal.valueOf(240), cart.getOriginalPrice());
        verify(cartRepository, atLeastOnce()).save(cart);
        Cart cart2 = new Cart();
        cart2.setIdentifier("CART2");
        CartEntry entry2 = new CartEntry();
        entry2.setProductIdentifier("PROD2");
        entry2.setQuantity(1);
        PriceDto mrpOnly = new PriceDto();
        mrpOnly.setPriceAmount(BigDecimal.valueOf(150));
        when(cartEntryRepository.findByCartIdentifier("CART2")).thenReturn(List.of(entry2));
        when(priceService.findByIdentifier("PROD2-SELLING")).thenReturn(null);
        when(priceService.findByIdentifier("PROD2-MRP")).thenReturn(mrpOnly);
        cartService.recalculateAndSave(cart2);
        assertEquals(BigDecimal.valueOf(150), cart2.getTotalPrice());
        Cart cart3 = new Cart();
        cart3.setIdentifier("CART3");
        when(cartEntryRepository.findByCartIdentifier("CART3")).thenReturn(List.of());
        cartService.recalculateAndSave(cart3);
        assertEquals(BigDecimal.ZERO, cart3.getTotalPrice());
        Cart cart4 = new Cart();
        cart4.setIdentifier("CART4");
        CartEntry entry4 = new CartEntry();
        entry4.setProductIdentifier("PROD4");
        entry4.setQuantity(1);
        when(cartEntryRepository.findByCartIdentifier("CART4")).thenReturn(List.of(entry4));
        when(priceService.findByIdentifier("PROD4-SELLING")).thenReturn(null);
        when(priceService.findByIdentifier("PROD4-MRP")).thenReturn(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cartService.recalculateAndSave(cart4));
        assertEquals("Price not configured", ex.getMessage());
    }

    @Test
    void clearCartAndFindAllTest() {
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        cartService.clearCart("CART1");
        verify(cartEntryRepository).deleteByCartIdentifier("CART1");
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        when(cartRepository.findByIdentifier("UNKNOWN")).thenReturn(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cartService.clearCart("UNKNOWN"));
        assertEquals("Cart not found", ex.getMessage());
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
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        Page<Cart> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(cartRepository.findAll(pageable)).thenReturn(emptyPage);
        result = cartService.findAll(pageable);
        assertEquals(0, result.getTotalRecords());
        assertTrue(result.getDtoList().isEmpty());
    }
}