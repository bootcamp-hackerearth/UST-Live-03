package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartEntryService cartEntryService;

    @InjectMocks
    private CartServiceImpl cartService;
    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setup() {
        cart = new Cart();
        cart.setIdentifier("cart1");
        cartDto = new CartDto();
        cartDto.setIdentifier("cart1");
    }

    @Test
    void save_shouldFail_ifCartExists() {
        when(cartRepository.findByIdentifier("cart1")).thenReturn(cart);
        CartDto result = cartService.save(cartDto);
        assertFalse(result.isSuccess());
        assertEquals("Cart with identifier - cart1 already exists", result.getMessage());
    }

    @Test
    void save_shouldSave_ifCartNotExists() {
        when(cartRepository.findByIdentifier("cart1")).thenReturn(null);
        when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);
        CartDto result = cartService.save(cartDto);
        assertTrue(result.isSuccess());
        verify(cartRepository).save(cart);
    }

    @Test
    void update_shouldFail_ifCartNotFound() {
        when(cartRepository.findByIdentifier("cart1")).thenReturn(null);
        CartDto result = cartService.update(cartDto);
        assertFalse(result.isSuccess());
        assertEquals("Cart with identifier - cart1 not found", result.getMessage());
    }

    @Test
    void update_shouldUpdate_ifCartExists() {
        when(cartRepository.findByIdentifier("cart1")).thenReturn(cart);
        cartService.update(cartDto);
        verify(modelMapper).map(cartDto, cart);
        verify(cartRepository).save(cart);
    }

    @Test
    void delete_shouldCallRepository() {
        cartService.delete("cart1");
        verify(cartRepository).deleteByIdentifier("cart1");
    }

    @Test
    void findAll_shouldReturnMappedList() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Cart> cartPage = new PageImpl<>(List.of(cart));
        when(cartRepository.findAll(pageable)).thenReturn(cartPage);
        doReturn(List.of(cartDto))
                .when(modelMapper)
                .map(anyList(), any(Type.class));
        List<CartDto> result = cartService.findAll(pageable);
        assertEquals(1, result.size());
    }

    @Test
    void findByIdentifier_shouldReturnCartDto() {
        when(cartRepository.findByIdentifier("cart1")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        CartDto result = cartService.findByIdentifier("cart1");
        assertNotNull(result);
    }

    @Test
    void recalculate_shouldComputeTotalsCorrectly() {
        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(BigDecimal.valueOf(100));
        entry1.setDiscount(BigDecimal.valueOf(10));
        entry1.setOriginalPrice(BigDecimal.valueOf(120));
        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(BigDecimal.valueOf(200));
        entry2.setDiscount(BigDecimal.valueOf(20));
        entry2.setOriginalPrice(BigDecimal.valueOf(240));
        List<CartEntryDto> entries = List.of(entry1, entry2);
        when(cartEntryService.findAllCarts("cart1")).thenReturn(entries);
        when(cartRepository.findByIdentifier("cart1")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        doReturn(entries)
                .when(modelMapper)
                .map(eq(entries), any(Type.class));
        CartDto result = cartService.recalculate("cart1");
        assertEquals(BigDecimal.valueOf(300), cart.getTotalPrice());
        assertEquals(BigDecimal.valueOf(30), cart.getDiscount());
        assertEquals(BigDecimal.valueOf(360), cart.getOriginalPrice());
        verify(cartRepository).save(cart);
        assertNotNull(result);
    }
}