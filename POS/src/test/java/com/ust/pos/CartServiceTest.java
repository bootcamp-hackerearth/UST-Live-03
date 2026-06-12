package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setUp() {

        cart = new Cart();
        cart.setIdentifier("CART-001");

        cartDto = new CartDto();
        cartDto.setIdentifier("CART-001");
    }

    @Test
    void testFindByIdentifier_Found() {

        when(cartRepository.findByIdentifier("CART-001"))
                .thenReturn(cart);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        CartDto result =
                cartService.findByIdentifier("CART-001");

        assertNotNull(result);
        assertEquals("CART-001", result.getIdentifier());

        verify(cartRepository).findByIdentifier("CART-001");
        verify(modelMapper).map(cart, CartDto.class);
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(cartRepository.findByIdentifier("CART-001"))
                .thenReturn(null);

        CartDto result =
                cartService.findByIdentifier("CART-001");

        assertNull(result);

        verify(cartRepository).findByIdentifier("CART-001");
        verify(modelMapper, never())
                .map(any(), eq(CartDto.class));
    }

    @Test
    void testSave_CartAlreadyExists() {

        when(cartRepository.findByIdentifier("CART-001"))
                .thenReturn(cart);

        CartDto result =
                cartService.save(cartDto);

        assertNotNull(result);
        assertEquals(
                "cart withCART-001exists",
                result.getMessage()
        );

        verify(cartRepository, never())
                .save(any());
    }

    @Test
    void testSave_NewCart() {

        when(cartRepository.findByIdentifier("CART-001"))
                .thenReturn(null);

        when(modelMapper.map(cartDto, Cart.class))
                .thenReturn(cart);

        CartDto result =
                cartService.save(cartDto);

        assertNotNull(result);

        verify(cartRepository)
                .save(cart);

        verify(modelMapper)
                .map(cartDto, Cart.class);
    }

    @Test
    void testDelete() {

        cartService.delete("CART-001");

        verify(cartEntryRepository)
                .deleteAllByCartId("CART-001");

        verify(cartRepository)
                .deleteByIdentifier("CART-001");
    }

    @Test
    void testFindAll_WithData() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Cart> cartList =
                Collections.singletonList(cart);

        List<CartDto> dtoList =
                Collections.singletonList(cartDto);

        Page<Cart> cartPage =
                new PageImpl<>(cartList, pageable, 1);

        Type listType =
                new TypeToken<List<CartDto>>() {
                }.getType();

        when(cartRepository.findAll(pageable))
                .thenReturn(cartPage);

        when(modelMapper.map(cartPage.getContent(), listType))
                .thenReturn(dtoList);

        List<CartDto> result =
                cartService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(cartRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(cartPage.getContent(), listType);
    }

    @Test
    void testFindAll_EmptyList() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Cart> emptyPage =
                new PageImpl<>(
                        Collections.emptyList(),
                        pageable,
                        0
                );

        Type listType =
                new TypeToken<List<CartDto>>() {
                }.getType();

        when(cartRepository.findAll(pageable))
                .thenReturn(emptyPage);

        when(modelMapper.map(emptyPage.getContent(), listType))
                .thenReturn(Collections.emptyList());

        List<CartDto> result =
                cartService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(cartRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(emptyPage.getContent(), listType);
    }

    @Test
    void testFindAllWs_WithData() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Cart> cartList =
                Collections.singletonList(cart);

        List<CartDto> dtoList =
                Collections.singletonList(cartDto);

        Page<Cart> cartPage =
                new PageImpl<>(cartList, pageable, 1);

        Type listType =
                new TypeToken<List<CartDto>>() {
                }.getType();

        when(cartRepository.findAll(pageable))
                .thenReturn(cartPage);

        when(modelMapper.map(cartPage.getContent(), listType))
                .thenReturn(dtoList);

        WsDto<CartDto> result =
                cartService.findAllws(pageable);

        assertNotNull(result);

        assertEquals(1,
                result.getDtoList().size());

        assertEquals(1,
                result.getTotalRecords());

        assertEquals(1,
                result.getTotalPages());

        assertEquals(10,
                result.getSizePerPage());

        assertEquals(0,
                result.getPage());

        verify(cartRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(cartPage.getContent(), listType);
    }

    @Test
    void testFindAllWs_EmptyList() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Cart> emptyPage =
                new PageImpl<>(
                        Collections.emptyList(),
                        pageable,
                        0
                );

        Type listType =
                new TypeToken<List<CartDto>>() {
                }.getType();

        when(cartRepository.findAll(pageable))
                .thenReturn(emptyPage);

        when(modelMapper.map(emptyPage.getContent(), listType))
                .thenReturn(Collections.emptyList());

        WsDto<CartDto> result =
                cartService.findAllws(pageable);

        assertNotNull(result);

        assertTrue(result.getDtoList().isEmpty());

        assertEquals(0,
                result.getTotalRecords());

        verify(cartRepository)
                .findAll(pageable);
    }

}
