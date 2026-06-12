package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
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
        cart.setIdentifier("CART1");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);

        CartDto result = cartService.findByIdentifier("CART1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CART1", result.getIdentifier());

        verify(cartRepository).findByIdentifier("CART1");
        verify(modelMapper).map(cart, CartDto.class);
    }

    @Test
    void saveTestSuccess() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Cart.class))
                .thenReturn(cart);

        CartDto result = cartService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CART1", result.getIdentifier());

        verify(cartRepository).save(cart);
        verify(cartEntryService).recalculate("CART1");
    }

    @Test
    void saveTestFailure() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        CartDto result = cartService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(cartRepository, Mockito.never())
                .save(Mockito.any());
        Mockito.verify(cartEntryService, Mockito.never())
                .recalculate(Mockito.anyString());
    }

    @Test
    void updateTestSuccess() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        CartDto result = cartService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CART1", result.getIdentifier());

        verify(modelMapper).map(dto, cart);
        verify(cartRepository).save(cart);
    }

    @Test
    void updateTestFailure() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(null);

        CartDto result = cartService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(cartRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        cartService.delete("CART1");

        verify(cartRepository).deleteByIdentifier("CART1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Cart> page = Mockito.mock(Page.class);

        Cart cart1 = new Cart();
        cart1.setIdentifier("CART1");

        Cart cart2 = new Cart();
        cart2.setIdentifier("CART2");

        CartDto dto1 = new CartDto();
        dto1.setIdentifier("CART1");

        CartDto dto2 = new CartDto();
        dto2.setIdentifier("CART2");

        List<Cart> carts = List.of(cart1, cart2);

        Mockito.when(cartRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(page.getContent())
                .thenReturn(carts);

        Mockito.when(modelMapper.map(cart1, CartDto.class))
                .thenReturn(dto1);
        Mockito.when(modelMapper.map(cart2, CartDto.class))
                .thenReturn(dto2);

        Mockito.when(cartEntryService.findByCartId("CART1"))
                .thenReturn(List.of());
        Mockito.when(cartEntryService.findByCartId("CART2"))
                .thenReturn(List.of());

        List<CartDto> result = cartService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());

        verify(cartRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(cart1, CartDto.class);
        verify(modelMapper).map(cart2, CartDto.class);
        verify(cartEntryService).findByCartId("CART1");
        verify(cartEntryService).findByCartId("CART2");
    }
}