package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Cart;
import com.ust.pos.modell.CartRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setIdentifier("123");
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setOriginalPrice(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        cartDto = new CartDto();
        cartDto.setIdentifier("123");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(cartRepository.findByIdentifier("123")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        CartDto result = cartService.findByIdentifier("123");
        assertNotNull(result);
        verify(cartRepository).findByIdentifier("123");
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(cartRepository.findByIdentifier("123")).thenReturn(null);
        CartDto result = cartService.findByIdentifier("123");
        assertNull(result);
    }

    @Test
    void testSave_Success() {
        when(cartRepository.findByIdentifier("123")).thenReturn(null);
        when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);
        CartDto result = cartService.save(cartDto);
        assertTrue(result.isSuccess());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testSave_AlreadyExists() {
        when(cartRepository.findByIdentifier("123")).thenReturn(cart);
        CartDto result = cartService.save(cartDto);
        assertFalse(result.isSuccess());
        assertEquals("Cart already exists with identifier: 123", result.getMessage());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void testSave_NullFields() {
        Cart newCart = new Cart();
        when(cartRepository.findByIdentifier("123")).thenReturn(null);
        when(modelMapper.map(cartDto, Cart.class)).thenReturn(newCart);
        cartService.save(cartDto);
        assertEquals(BigDecimal.ZERO, newCart.getTotalPrice());
        assertEquals(BigDecimal.ZERO, newCart.getOriginalPrice());
        assertEquals(BigDecimal.ZERO, newCart.getDiscount());
    }

    @Test
    void testUpdate_Success() {
        cartDto.setTotalPrice(BigDecimal.TEN);
        cartDto.setOriginalPrice(BigDecimal.ONE);
        cartDto.setDiscount(BigDecimal.ONE);
        when(cartRepository.findByIdentifier("123")).thenReturn(cart);
        CartDto result = cartService.update(cartDto);
        assertTrue(result.isSuccess());
        verify(cartRepository).save(cart);
    }

    @Test
    void testUpdate_NotFound() {
        when(cartRepository.findByIdentifier("123")).thenReturn(null);
        CartDto result = cartService.update(cartDto);
        assertFalse(result.isSuccess());
        assertEquals("Cart not found with identifier: 123", result.getMessage());
    }

    @Test
    void testDelete_Success() {
        when(cartRepository.findByIdentifier("123")).thenReturn(cart);
        cartService.delete("123");
        verify(cartRepository).delete(cart);
    }

    @Test
    void testDelete_NotFound() {
        when(cartRepository.findByIdentifier("123")).thenReturn(null);
        cartService.delete("123");
        verify(cartRepository, never()).delete(any());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Cart> cartList = List.of(cart);
        Page<Cart> cartPage = new PageImpl<>(cartList, pageable, cartList.size());
        List<CartDto> cartDtoList = List.of(cartDto);
        when(cartRepository.findAll(pageable)).thenReturn(cartPage);
        when(modelMapper.map(eq(cartList), any(java.lang.reflect.Type.class))).thenReturn(cartDtoList);
        WsDto<CartDto> result = cartService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(cartDtoList, result.getDtoList());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
        verify(cartRepository, times(1)).findAll(pageable);
        verify(modelMapper, times(1)).map(eq(cartList), any(java.lang.reflect.Type.class));
    }
}