package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartEntry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
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

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartEntryService cartEntryService;

    @Test
    void findByIdentifierSuccessTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);

        CartDto response = cartService.findByIdentifier("CART1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CART1", response.getIdentifier());
    }

    @Test
    void saveSuccessTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(null);
        Mockito.when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);

        CartDto response = cartService.save(cartDto);

        Assertions.assertEquals("CART1", response.getIdentifier());
        verify(cartRepository).save(cart);
        verify(cartEntryService).recalculate("CART1");
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Cart existingCart = new Cart();

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(existingCart);

        CartDto response = cartService.save(cartDto);

        Assertions.assertEquals("CART1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart with identifier - CART1 already exists", response.getMessage());
        Mockito.verify(cartRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(cartEntryService, Mockito.never()).recalculate(Mockito.anyString());
    }

    @Test
    void updateSuccessTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Cart existingCart = new Cart();
        existingCart.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(existingCart);

        CartDto response = cartService.update(cartDto);

        Assertions.assertEquals("CART1", response.getIdentifier());
        verify(modelMapper).map(cartDto, existingCart);
        verify(cartRepository).save(existingCart);
    }

    @Test
    void updateFailureTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(null);

        CartDto response = cartService.update(cartDto);

        Assertions.assertEquals("CART1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart with identifier - CART1 not found", response.getMessage());
        Mockito.verify(cartRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        cartService.delete("CART1");

        verify(cartRepository).deleteByIdentifier("CART1");
    }

    @Test
    void findAllSuccessTest() {
        Cart c1 = new Cart();
        c1.setIdentifier("CART1");
        List<Cart> cartList = List.of(c1);

        CartDto d1 = new CartDto();
        d1.setIdentifier("CART1");

        Page<Cart> cartPage = new PageImpl<>(cartList);
        Pageable pageable = PageRequest.of(0, 10);

        CartEntryDto entryDto = new CartEntryDto();
        List<CartEntryDto> entryDtoList = List.of(entryDto);

        Mockito.when(cartRepository.findAll(pageable)).thenReturn(cartPage);
        Mockito.when(modelMapper.map(c1, CartDto.class)).thenReturn(d1);
        Mockito.when(cartEntryService.findByCartId("CART1")).thenReturn(entryDtoList);

        List<CartDto> result = cartService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("CART1", result.get(0).getIdentifier());
        Assertions.assertEquals(entryDtoList, result.get(0).getCartEntryDtoList());
    }
}