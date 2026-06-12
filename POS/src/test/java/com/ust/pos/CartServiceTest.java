package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    void findByIdentifier_Found() {

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);

        CartDto result =
                cartService.findByIdentifier("CART001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "CART001",
                result.getIdentifier());
    }

    @Test
    void save_NewCart() {

        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(null);

        when(modelMapper.map(dto, Cart.class))
                .thenReturn(cart);

        when(cartRepository.save(cart))
                .thenReturn(cart);

        CartDto result =
                cartService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "CART001",
                result.getIdentifier());

        verify(cartRepository)
                .save(cart);

        verify(cartEntryService)
                .recalculate("CART001");
    }

    @Test
    void save_CartAlreadyExists() {

        Cart existing = new Cart();

        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(existing);

        CartDto result =
                cartService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(cartRepository, never())
                .save(any());

        verify(cartEntryService, never())
                .recalculate(any());
    }

    @Test
    void update_CartExists() {

        Cart existing = new Cart();

        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(existing);

        when(cartRepository.save(existing))
                .thenReturn(existing);

        CartDto result =
                cartService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "CART001",
                result.getIdentifier());

        verify(modelMapper)
                .map(dto, existing);

        verify(cartRepository)
                .save(existing);
    }

    @Test
    void update_CartNotFound() {

        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(null);

        CartDto result =
                cartService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(cartRepository, never())
                .save(any());
    }

    @Test
    void deleteTest() {

        doNothing().when(cartRepository)
                .deleteByIdentifier("CART001");

        cartService.delete("CART001");

        verify(cartRepository)
                .deleteByIdentifier("CART001");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Cart cart1 = new Cart();
        cart1.setIdentifier("CART001");

        Cart cart2 = new Cart();
        cart2.setIdentifier("CART002");

        List<Cart> carts = List.of(cart1, cart2);

        Page<Cart> page =
                new PageImpl<>(
                        carts,
                        pageable,
                        2
                );

        CartDto dto1 = new CartDto();
        dto1.setIdentifier("CART001");

        CartDto dto2 = new CartDto();
        dto2.setIdentifier("CART002");

        when(cartRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(cart1, CartDto.class))
                .thenReturn(dto1);

        when(modelMapper.map(cart2, CartDto.class))
                .thenReturn(dto2);

        when(cartEntryService.findByCartId("CART001"))
                .thenReturn(List.of());

        when(cartEntryService.findByCartId("CART002"))
                .thenReturn(List.of());

        WsDto<CartDto> result =
                cartService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                2,
                result.getContent().size());

        Assertions.assertEquals(
                0,
                result.getPage());

        Assertions.assertEquals(
                10,
                result.getSizePerPage());

        Assertions.assertEquals(
                1,
                result.getTotalPages());

        Assertions.assertEquals(
                2,
                result.getTotalRecords());

        verify(cartEntryService)
                .findByCartId("CART001");

        verify(cartEntryService)
                .findByCartId("CART002");
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Cart> page =
                new PageImpl<>(List.of());

        when(cartRepository.findAll(pageable))
                .thenReturn(page);

        WsDto<CartDto> result =
                cartService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertTrue(
                result.getContent().isEmpty());

        Assertions.assertEquals(
                0,
                result.getTotalRecords());

        verify(cartRepository)
                .findAll(pageable);

        verify(cartEntryService, never())
                .findByCartId(any());
    }
}