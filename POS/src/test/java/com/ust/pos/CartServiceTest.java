package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
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
    private CartEntryService cartEntryService;

    @Mock
    private ModelMapper modelMapper;


    @Test
    void saveTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");
        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        Mockito.when(modelMapper.map(cartDto, Cart.class))
                .thenReturn(cart);
        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);
        CartDto response = cartService.save(cartDto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("CART1", response.getIdentifier());
        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void recalculateCart_WhenCartExists() {
        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        cart.setDiscount(BigDecimal.TEN);
        CartEntryDto e1 = new CartEntryDto();
        e1.setTotalPrice(new BigDecimal("100"));
        CartEntryDto e2 = new CartEntryDto();
        e2.setTotalPrice(new BigDecimal("50"));
        CartDto cartDto = new CartDto();
        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);
        Mockito.when(cartEntryService.findByCartId("CART1"))
                .thenReturn(List.of(e1, e2));
        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);
        CartDto response = cartService.recalulateCart("CART1");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                new BigDecimal("140"),
                cart.getTotalPrice()
        );
        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void recalculateCart_WhenCartDoesNotExist() {
        CartEntryDto entry = new CartEntryDto();
        entry.setTotalPrice(new BigDecimal("100"));
        CartDto cartDto = new CartDto();
        Mockito.when(cartRepository.findByIdentifier("CART2"))
                .thenReturn(null);
        Mockito.when(cartEntryService.findByCartId("CART2"))
                .thenReturn(List.of(entry));
        Mockito.when(modelMapper.map(Mockito.any(Cart.class), Mockito.eq(CartDto.class)))
                .thenReturn(cartDto);
        CartDto response = cartService.recalulateCart("CART2");
        Assertions.assertNotNull(response);
        Mockito.verify(cartRepository).save(Mockito.any(Cart.class));
    }

    @Test
    void updateTest_Success() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");
        Cart existing = new Cart();
        existing.setIdentifier("CART1");
        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(existing);
        Mockito.when(modelMapper.map(dto, Cart.class))
                .thenReturn(existing);
        Mockito.when(cartRepository.save(existing))
                .thenReturn(existing);
        CartDto response = cartService.update(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(modelMapper).map(dto, Cart.class);
        Mockito.verify(cartRepository).save(existing);
    }

    @Test
    void updateTest_Failure() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");
        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(null);
        CartDto response = cartService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(cartRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(cartRepository)
                .deleteByIdentifier("CART1");
        cartService.delete("CART1");
        Mockito.verify(cartRepository).deleteByIdentifier("CART1");
    }

    @Test
    void deleteAllTest() {
        Mockito.doNothing()
                .when(cartRepository)
                .deleteAll();
        cartService.deleteAll();
        Mockito.verify(cartRepository).deleteAll();
    }


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
        CartDto response = cartService.findByIdentifier("CART1");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("CART1", response.getIdentifier());
    }

    @Test
    void findByIdentifier_NotFound() {
        Mockito.when(cartRepository.findByIdentifier("UNKNOWN"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(null, CartDto.class))
                .thenReturn(null);
        CartDto response = cartService.findByIdentifier("UNKNOWN");
        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {
        List<Cart> carts = List.of(new Cart());
        List<CartDto> dtos = List.of(new CartDto());
        Type listType = new TypeToken<List<CartDto>>() {}.getType();
        Mockito.when(cartRepository.findAll())
                .thenReturn(carts);
        Mockito.when(modelMapper.map(carts, listType))
                .thenReturn(dtos);
        List<CartDto> response = cartService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithPaginationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");
        Page<Cart> page = new PageImpl<>(List.of(cart));
        Mockito.when(cartRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.any(Cart.class), Mockito.eq(CartDto.class)))
                .thenReturn(dto);
        Page<CartDto> response =
                cartService.findAll(pageable, null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals("CART1",
                response.getContent().get(0).getIdentifier());
        Mockito.verify(cartRepository).findAll(pageable);
    }

    @Test
    void findAllWithSearchTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Cart cart = new Cart();
        Page<Cart> page = new PageImpl<>(List.of(cart));
        Mockito.when(cartRepository.findByIdentifierContainingIgnoreCase("CART", pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.any(Cart.class), Mockito.eq(CartDto.class)))
                .thenReturn(new CartDto());
        Page<CartDto> response =
                cartService.findAll(pageable, "CART");
        Assertions.assertEquals(1, response.getContent().size());
        Mockito.verify(cartRepository)
                .findByIdentifierContainingIgnoreCase("CART", pageable);
    }

    @Test
    void updateCustomerTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        CartDto dto = new CartDto();
        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);
        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);
        CartDto response =
                cartService.updateCustomer("CART1", "CUST1");
        Assertions.assertNotNull(response);
        Mockito.verify(cartRepository).save(cart);
    }
}