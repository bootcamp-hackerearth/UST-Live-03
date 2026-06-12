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
    void findByIdentifierTest() {
        Cart cart = new Cart();
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);

        CartDto response = cartService.findByIdentifier("CART1");

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
    void saveFailureTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(new Cart());

        CartDto response = cartService.save(cartDto);

        Assertions.assertEquals("CART1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateSuccessTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Cart existingCart = new Cart();

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(existingCart);

        CartDto response = cartService.update(cartDto);

        Assertions.assertEquals("CART1", response.getIdentifier());
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
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        cartService.delete("CART1");

        verify(cartRepository).deleteByIdentifier("CART1");
    }

    @Test
    void findAllTest() {
        Cart cart = new Cart();
        List<Cart> cartList = List.of(cart);

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Cart> cartPage = new PageImpl<>(cartList);

        CartEntryDto entryDto = new CartEntryDto();
        List<CartEntryDto> entryDtoList = List.of(entryDto);

        Mockito.when(cartRepository.findAll(pageable)).thenReturn(cartPage);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        Mockito.when(cartEntryService.findByCartId("CART1")).thenReturn(entryDtoList);

        List<CartDto> response = cartService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(entryDtoList, response.get(0).getCartEntryDtoList());
    }
}