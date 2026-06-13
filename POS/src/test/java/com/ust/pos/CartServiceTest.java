package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.Assertions;
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
import java.util.List;
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllWithPageableTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        List<Cart> carts = List.of(cart);
        List<CartDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Cart> cartPage = new PageImpl<>(carts);
        Mockito.when(cartRepository.findAll(pageable)).thenReturn(cartPage);
        Mockito.when(modelMapper.map(Mockito.eq(carts), Mockito.any(Type.class))).thenReturn(dtos);
        List<CartDto> response = cartService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("CART001", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        List<Cart> carts = List.of(cart);
        List<CartDto> dtos = List.of(dto);
        Mockito.when(cartRepository.findAll()).thenReturn(carts);
        Mockito.when(modelMapper.map(Mockito.eq(carts), Mockito.any(Type.class))).thenReturn(dtos);
        List<CartDto> response = cartService.findAll(null);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("CART001", response.get(0).getIdentifier());
    }

    @Test
    void findByIdentifierTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);
        CartDto response = cartService.findByIdentifier("CART001");
        Assertions.assertEquals("CART001", response.getIdentifier());
    }

    @Test
    void saveSuccessTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Cart.class)).thenReturn(cart);
        Mockito.when(cartRepository.save(cart)).thenReturn(cart);
        CartDto response = cartService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("CART001", response.getIdentifier());
        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void saveFailureTest_WhenCartExists() {
        Cart existingCart = new Cart();
        existingCart.setIdentifier("CART001");
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(existingCart);
        CartDto response = cartService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
        Mockito.verify(cartRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void recalculateSuccessTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        CartEntry e1 = new CartEntry();
        e1.setOriginalPrice(new BigDecimal("100"));
        e1.setDiscount(new BigDecimal("10"));
        e1.setTotalPrice(new BigDecimal("90"));
        CartEntry e2 = new CartEntry();
        e2.setOriginalPrice(new BigDecimal("200"));
        e2.setDiscount(new BigDecimal("20"));
        e2.setTotalPrice(new BigDecimal("180"));
        CartDto dto = new CartDto();
        dto.setOriginalPrice(new BigDecimal("300"));
        dto.setDiscount(new BigDecimal("30"));
        dto.setTotalPrice(new BigDecimal("270"));
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCartIdentifier("CART001")).thenReturn(List.of(e1, e2));
        Mockito.when(cartRepository.save(cart)).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);
        CartDto response = cartService.recalculate("CART001");
        Assertions.assertEquals(new BigDecimal("300"), response.getOriginalPrice());
        Assertions.assertEquals(new BigDecimal("30"), response.getDiscount());
        Assertions.assertEquals(new BigDecimal("270"), response.getTotalPrice());
        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void recalculateCartNotFoundTest() {
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(null);
        CartDto response = cartService.recalculate("CART001");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart not found", response.getMessage());
        Mockito.verify(cartEntryRepository, Mockito.never()).findByCartIdentifier(Mockito.any());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(cartEntryRepository).deleteByCartIdentifier("CART001");
        Mockito.doNothing().when(cartRepository).deleteByIdentifier("CART001");
        cartService.deleteByIdentifier("CART001");
        Mockito.verify(cartEntryRepository).deleteByCartIdentifier("CART001");
        Mockito.verify(cartRepository).deleteByIdentifier("CART001");
    }

    @Test
    void updateSuccessTest() {
        Cart existingCart = new Cart();
        existingCart.setIdentifier("CART001");
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(existingCart);
        Mockito.when(cartRepository.save(existingCart)).thenReturn(existingCart);
        CartDto response = cartService.update(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(cartRepository).save(existingCart);
    }

    @Test
    void updateFailureTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(null);
        CartDto response = cartService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }
}