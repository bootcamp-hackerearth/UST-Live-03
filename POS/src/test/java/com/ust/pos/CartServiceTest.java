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
import java.util.Collections;
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

        Mockito.when(cartRepository.findAll(pageable))
                .thenReturn(cartPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(carts),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

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

        Mockito.when(cartRepository.findAll())
                .thenReturn(carts);

        Mockito.when(modelMapper.map(
                Mockito.eq(carts),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        List<CartDto> response = cartService.findAll(null);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("CART001", response.get(0).getIdentifier());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);

        CartDto response = cartService.findByIdentifier("CART001");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CART001", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(null);

        CartDto response = cartService.findByIdentifier("CART001");

        Assertions.assertNull(response);
    }

    @Test
    void saveTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        Mockito.when(modelMapper.map(Mockito.any(CartDto.class), Mockito.eq(Cart.class)))
                .thenReturn(cart);

        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);

        CartDto response = cartService.save("CART001");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CART001", response.getIdentifier());
        Assertions.assertEquals(BigDecimal.ZERO, response.getTotalPrice());
        Assertions.assertEquals(BigDecimal.ZERO, response.getTotalDiscount());
        Assertions.assertEquals(BigDecimal.ZERO, response.getTotalOriginalPrice());

        Mockito.verify(cartRepository).save(Mockito.any(Cart.class));
    }

    @Test
    void recalculateSuccessTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartEntry entry1 = new CartEntry();
        entry1.setOriginalPrice(new BigDecimal("100"));
        entry1.setDiscount(new BigDecimal("10"));
        entry1.setTotalPrice(new BigDecimal("90"));

        CartEntry entry2 = new CartEntry();
        entry2.setOriginalPrice(new BigDecimal("200"));
        entry2.setDiscount(new BigDecimal("20"));
        entry2.setTotalPrice(new BigDecimal("180"));

        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        dto.setTotalOriginalPrice(new BigDecimal("300"));
        dto.setTotalDiscount(new BigDecimal("30"));
        dto.setTotalPrice(new BigDecimal("270"));

        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCart("CART001"))
                .thenReturn(List.of(entry1, entry2));

        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);

        CartDto response = cartService.recalculate("CART001");

        Assertions.assertEquals(new BigDecimal("300"), response.getTotalOriginalPrice());
        Assertions.assertEquals(new BigDecimal("30"), response.getTotalDiscount());
        Assertions.assertEquals(new BigDecimal("270"), response.getTotalPrice());

        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void recalculateCartNotFoundTest() {
        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(null);

        CartDto response = cartService.recalculate("CART001");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart not found", response.getMessage());

        Mockito.verify(cartEntryRepository, Mockito.never()).findByCart(Mockito.any());
    }

    @Test
    void recalculateWithNoEntriesTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto dto = new CartDto();
        dto.setTotalOriginalPrice(BigDecimal.ZERO);
        dto.setTotalDiscount(BigDecimal.ZERO);
        dto.setTotalPrice(BigDecimal.ZERO);

        Mockito.when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCart("CART001"))
                .thenReturn(Collections.emptyList());

        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);

        CartDto response = cartService.recalculate("CART001");

        Assertions.assertEquals(BigDecimal.ZERO, response.getTotalPrice());
        Assertions.assertEquals(BigDecimal.ZERO, response.getTotalDiscount());
        Assertions.assertEquals(BigDecimal.ZERO, response.getTotalOriginalPrice());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing()
                .when(cartEntryRepository)
                .deleteByCart("CART001");

        Cart deletedCart = new Cart();
        deletedCart.setIdentifier("CART001");

        Mockito.when(cartRepository.deleteByIdentifier("CART001"))
                .thenReturn(deletedCart);

        cartService.delete("CART001");

        Mockito.verify(cartEntryRepository)
                .deleteByCart("CART001");

        Mockito.verify(cartRepository)
                .deleteByIdentifier("CART001");
    }
}