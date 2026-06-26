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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

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
        cart.setIdentifier("CART001");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);
        when(cartEntryService.findByCartId("CART001")).thenReturn(Collections.emptyList());

        CartDto result = cartService.findByIdentifier("CART001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CART001", result.getIdentifier());

        verify(cartEntryService).findByCartId("CART001");
    }

    @Test
    void findByIdentifierFailureTest() {
        when(cartRepository.findByIdentifier("CART001")).thenReturn(null);

        CartDto result = cartService.findByIdentifier("CART001");

        Assertions.assertNull(result);
    }

    @Test
    void saveExistingCustomerCartTest() {
        CartDto dto = new CartDto();
        dto.setCustomer("CUSTOMER1");

        Cart existingCart = new Cart();
        existingCart.setIdentifier("CART001");

        CartDto existingDto = new CartDto();
        existingDto.setIdentifier("CART001");

        when(cartRepository.findTopByCustomerOrderByIdDesc("CUSTOMER1"))
                .thenReturn(existingCart);
        when(modelMapper.map(existingCart, CartDto.class))
                .thenReturn(existingDto);

        CartDto result = cartService.save(dto);

        Assertions.assertEquals("CART001", result.getIdentifier());

        verify(cartRepository, never()).save(any());
    }

    @Test
    void saveSuccessTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        dto.setCustomer("CUSTOMER1");

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto savedDto = new CartDto();
        savedDto.setIdentifier("CART001");

        when(cartRepository.findTopByCustomerOrderByIdDesc("CUSTOMER1"))
                .thenReturn(null);
        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(null);
        when(modelMapper.map(dto, Cart.class))
                .thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(savedDto);

        CartDto result = cartService.save(dto);

        Assertions.assertEquals("CART001", result.getIdentifier());

        verify(cartRepository).save(cart);
        verify(cartEntryService).recalculate("CART001");
    }

    @Test
    void saveDuplicateCartTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        dto.setCustomer("CUSTOMER1");

        Cart existingCart = new Cart();
        existingCart.setDeleted(false);

        when(cartRepository.findTopByCustomerOrderByIdDesc("CUSTOMER1"))
                .thenReturn(null);
        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(existingCart);

        CartDto result = cartService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Cart with identifier - CART001 already exists",
                result.getMessage()
        );

        verify(cartRepository, never()).save(any());
    }

    @Test
    void saveDeletedCartTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        dto.setCustomer("CUSTOMER1");

        Cart existingCart = new Cart();
        existingCart.setDeleted(true);

        when(cartRepository.findTopByCustomerOrderByIdDesc("CUSTOMER1"))
                .thenReturn(null);
        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(existingCart);

        CartDto result = cartService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Cart with identifier - CART001 was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(cartRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        Cart existingCart = new Cart();
        existingCart.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(existingCart);

        CartDto result = cartService.update(dto);

        Assertions.assertEquals("CART001", result.getIdentifier());

        verify(modelMapper).map(dto, existingCart);
        verify(cartRepository).save(existingCart);
    }

    @Test
    void updateFailureTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(null);

        CartDto result = cartService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Cart with identifier - CART001 not found",
                result.getMessage()
        );

        verify(cartRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        cartService.delete("CART001");

        verify(cartRepository).deleteByIdentifier("CART001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Cart cart1 = new Cart();
        cart1.setIdentifier("CART001");

        Cart cart2 = new Cart();
        cart2.setIdentifier("CART002");

        CartDto dto1 = new CartDto();
        dto1.setIdentifier("CART001");

        CartDto dto2 = new CartDto();
        dto2.setIdentifier("CART002");

        Page<Cart> page = new PageImpl<>(
                List.of(cart1, cart2),
                pageable,
                2
        );

        when(cartRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(cart1, CartDto.class)).thenReturn(dto1);
        when(modelMapper.map(cart2, CartDto.class)).thenReturn(dto2);

        when(cartEntryService.findByCartId("CART001"))
                .thenReturn(Collections.emptyList());
        when(cartEntryService.findByCartId("CART002"))
                .thenReturn(Collections.emptyList());

        List<CartDto> result = cartService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        verify(cartEntryService).findByCartId("CART001");
        verify(cartEntryService).findByCartId("CART002");
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Cart> page = new PageImpl<>(
                Collections.emptyList(),
                pageable,
                0
        );

        when(cartRepository.findAll(pageable)).thenReturn(page);

        List<CartDto> result = cartService.findAll(pageable);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findByCustomerSuccessTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        when(cartRepository.findTopByCustomerOrderByIdDesc("CUSTOMER1"))
                .thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);
        when(cartEntryService.findByCartId("CART001"))
                .thenReturn(Collections.emptyList());

        CartDto result = cartService.findByCustomer("CUSTOMER1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CART001", result.getIdentifier());

        verify(cartEntryService).findByCartId("CART001");
    }

    @Test
    void findByCustomerFailureTest() {
        when(cartRepository.findTopByCustomerOrderByIdDesc("CUSTOMER1"))
                .thenReturn(null);

        CartDto result = cartService.findByCustomer("CUSTOMER1");

        Assertions.assertNull(result);
    }
}