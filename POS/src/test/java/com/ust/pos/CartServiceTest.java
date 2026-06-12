package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.PaginatedResponseDto;
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
    void saveTest() {

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        Cart savedCart = new Cart();
        savedCart.setIdentifier("CART001");

        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(null)
                .thenReturn(savedCart)
                .thenReturn(savedCart);

        Mockito.when(modelMapper.map(savedCart, CartDto.class))
                .thenReturn(cartDto);

        CartDto response = cartService.save(cartDto);

        Assertions.assertEquals("CART001",
                response.getIdentifier());

        Mockito.verify(cartRepository, Mockito.atLeastOnce())
                .save(Mockito.any(Cart.class));
    }

    @Test
    void saveExistingCartTest() {

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        Cart existingCart = new Cart();
        existingCart.setIdentifier("CART001");

        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(existingCart)
                .thenReturn(existingCart)
                .thenReturn(existingCart)
                .thenReturn(existingCart);

        Mockito.when(cartEntryRepository.findByCartId("CART001"))
                .thenReturn(List.of());

        Mockito.when(modelMapper.map(existingCart, CartDto.class))
                .thenReturn(cartDto);

        CartDto response = cartService.save(cartDto);

        Assertions.assertEquals("CART001", response.getIdentifier());

        Mockito.verify(cartRepository, Mockito.atLeastOnce())
                .save(existingCart);
    }

    @Test
    void deleteTest() {

        Mockito.doNothing()
                .when(cartRepository)
                .deleteByIdentifier("CART001");

        cartService.delete("CART001");

        Mockito.verify(cartRepository)
                .deleteByIdentifier("CART001");
    }

    @Test
    void findByIdentifierTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        CartDto response = cartService.findByIdentifier("CART001");

        Assertions.assertEquals("CART001",
                response.getIdentifier());
    }

    @Test
    void findAllTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        List<Cart> carts = List.of(cart);
        List<CartDto> cartDtos = List.of(cartDto);

        Page<Cart> cartPage = new PageImpl<>(carts);

        Mockito.when(cartRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(cartPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(carts),
                Mockito.any(Type.class)
        )).thenReturn(cartDtos);

        PaginatedResponseDto<CartDto> response =
                cartService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
    }

    @Test
    void recalculateTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartEntry entry1 = new CartEntry();
        entry1.setTotalPrice(new BigDecimal("100"));
        entry1.setDiscount(new BigDecimal("10"));

        CartEntry entry2 = new CartEntry();
        entry2.setTotalPrice(new BigDecimal("200"));
        entry2.setDiscount(new BigDecimal("20"));

        List<CartEntry> entries = List.of(entry1, entry2);

        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCartId("CART001"))
                .thenReturn(entries);

        cartService.recalculate("CART001");

        Assertions.assertEquals(
                new BigDecimal("300"),
                cart.getTotalPrice()
        );

        Assertions.assertEquals(
                new BigDecimal("30"),
                cart.getDiscount()
        );

        Mockito.verify(cartRepository)
                .save(cart);
    }

    @Test
    void recalculateEmptyEntriesTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCartId("CART001"))
                .thenReturn(List.of());

        cartService.recalculate("CART001");

        Assertions.assertEquals(
                BigDecimal.ZERO,
                cart.getTotalPrice()
        );

        Assertions.assertEquals(
                BigDecimal.ZERO,
                cart.getDiscount()
        );

        Mockito.verify(cartRepository)
                .save(cart);
    }
}