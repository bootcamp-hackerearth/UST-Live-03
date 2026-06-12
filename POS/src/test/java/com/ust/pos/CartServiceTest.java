package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl service;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void testFindByIdentifier_Found() {

        Cart cart = new Cart();
        cart.setIdentifier("cart1");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("cart1");

        when(cartRepository.findByIdentifier("cart1"))
                .thenReturn(cart);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        CartDto result = service.findByIdentifier("cart1");

        assertNotNull(result);
        assertEquals("cart1", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(cartRepository.findByIdentifier("cart1"))
                .thenReturn(null);

        CartDto result = service.findByIdentifier("cart1");

        assertNull(result);
    }

    @Test
    void testSave_NewCart() {

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("cart1");

        Cart cart = new Cart();

        when(cartRepository.findByIdentifier("cart1"))
                .thenReturn(null);

        when(modelMapper.map(cartDto, Cart.class))
                .thenReturn(cart);

        CartDto result = service.save(cartDto);

        assertNotNull(result);

        verify(cartRepository)
                .save(cart);
    }

    @Test
    void testSave_ExistingCart() {

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("cart1");

        Cart existing = new Cart();

        when(cartRepository.findByIdentifier("cart1"))
                .thenReturn(existing);

        CartDto result = service.save(cartDto);

        assertNotNull(result);
        assertEquals(
                "cart withcart1exists",
                result.getMessage()
        );

        verify(cartRepository, never())
                .save(any());
    }

    @Test
    void testDelete() {

        service.delete("cart1");

        verify(cartEntryRepository)
                .deleteAllByCartId("cart1");

        verify(cartRepository)
                .deleteByIdentifier("cart1");
    }

    @Test
    void testFindAll() {

        Cart cart = new Cart();
        cart.setIdentifier("cart1");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("cart1");

        Page<Cart> page =
                new PageImpl<>(
                        List.of(cart),
                        PageRequest.of(0, 10),
                        1
                );

        when(cartRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        WsDto<CartDto> result =
                service.findAll(PageRequest.of(0, 10));

        assertNotNull(result);

        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalRecords());

        verify(cartRepository)
                .findAll(any(Pageable.class));
    }
}