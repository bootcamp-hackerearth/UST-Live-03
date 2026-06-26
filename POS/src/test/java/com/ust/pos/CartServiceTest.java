package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import com.ust.pos.model.Coupon;
import com.ust.pos.model.CouponRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private ModelMapper modelMapper;

    // SAVE

    @Test
    void save_ShouldReturnCartDto() {

        CartDto dto = new CartDto();
        dto.setIdentifier("C1");

        Cart cart = new Cart();
        cart.setIdentifier("C1");

        Mockito.when(modelMapper.map(dto, Cart.class))
                .thenReturn(cart);

        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);

        CartDto result = cartService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("C1", result.getIdentifier());
    }

    // RECALCULATE CART

    @Test
    void recalculateCart_WithEntriesAndCoupon_PERCENT() {

        String cartId = "C1";

        Cart cart = new Cart();
        cart.setIdentifier(cartId);
        cart.setCouponCode("DISC10");

        CartEntryDto entry = new CartEntryDto();
        entry.setQuantity(new BigDecimal("2"));
        entry.setUnitPrice(new BigDecimal("100"));

        Mockito.when(cartRepository.findByIdentifier(cartId))
                .thenReturn(cart);

        Mockito.when(cartEntryService.findByCartId(cartId))
                .thenReturn(List.of(entry));

        Coupon coupon = new Coupon();
        coupon.setCode("DISC10");
        coupon.setActive(true);
        coupon.setType("PERCENT");
        coupon.setValue(new BigDecimal("10"));

        Mockito.when(couponRepository.findByCode("DISC10"))
                .thenReturn(Optional.of(coupon));

        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(new CartDto());

        CartDto result = cartService.recalulateCart(cartId);

        Assertions.assertNotNull(result);

        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void recalculateCart_WhenCartNotExists_ShouldCreateNewCart() {

        String cartId = "C1";

        Mockito.when(cartRepository.findByIdentifier(cartId))
                .thenReturn(null);

        Mockito.when(cartEntryService.findByCartId(cartId))
                .thenReturn(List.of());

        Mockito.when(cartRepository.save(Mockito.any(Cart.class)))
                .thenAnswer(i -> i.getArgument(0));

        Mockito.when(modelMapper.map(Mockito.any(), Mockito.eq(CartDto.class)))
                .thenReturn(new CartDto());

        CartDto result = cartService.recalulateCart(cartId);

        Assertions.assertNotNull(result);
    }

    // APPLY COUPON

    @Test
    void applyCoupon_CartNotFound() {

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);

        CartDto result = cartService.applyCoupon("C1", "DISC10");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Cart not found", result.getMessage());
    }

    @Test
    void applyCoupon_InvalidCoupon() {

        Cart cart = new Cart();
        cart.setIdentifier("C1");

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        Mockito.when(couponRepository.findByCode("DISC10"))
                .thenReturn(Optional.empty());

        CartDto result = cartService.applyCoupon("C1", "DISC10");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Invalid coupon", result.getMessage());
    }

    @Test
    void applyCoupon_Success() {

        Cart cart = new Cart();
        cart.setIdentifier("C1");

        Coupon coupon = new Coupon();
        coupon.setCode("DISC10");
        coupon.setActive(true);
        coupon.setType("FLAT");
        coupon.setValue(new BigDecimal("50"));

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        Mockito.when(couponRepository.findByCode("DISC10"))
                .thenReturn(Optional.of(coupon));

        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);

        Mockito.when(cartEntryService.findByCartId("C1"))
                .thenReturn(List.of());

        Mockito.when(modelMapper.map(Mockito.any(), Mockito.eq(CartDto.class)))
                .thenReturn(new CartDto());

        CartDto result = cartService.applyCoupon("C1", "DISC10");

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Coupon applied successfully", result.getMessage());
    }

    // REMOVE COUPON

    @Test
    void removeCoupon_WhenCartNotFound() {

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);

        CartDto result = cartService.removeCoupon("C1");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Cart not found", result.getMessage());
    }

    // FIND BY ID

    @Test
    void findByIdentifier_WhenCartNotFound() {

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);

        CartDto result = cartService.findByIdentifier("C1");

        Assertions.assertEquals("C1", result.getIdentifier());
        Assertions.assertEquals(BigDecimal.ZERO, result.getTotalPrice());
    }

    @Test
    void findByIdentifier_WhenCartExists() {

        Cart cart = new Cart();
        cart.setIdentifier("C1");

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(new CartDto());

        Mockito.when(cartEntryService.findByCartId("C1"))
                .thenReturn(List.of());

        CartDto result = cartService.findByIdentifier("C1");

        Assertions.assertNotNull(result);
    }

    // DELETE

    @Test
    void delete_ShouldCallRepository() {

        cartService.delete("C1");

        Mockito.verify(cartRepository).deleteByIdentifier("C1");
    }

    //  UPDATE

    @Test
    void update_WhenCartNotFound() {

        CartDto dto = new CartDto();
        dto.setIdentifier("C1");

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);

        CartDto result = cartService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void update_WhenCartExists() {

        CartDto dto = new CartDto();
        dto.setIdentifier("C1");
        dto.setTotalPrice(new BigDecimal("100"));
        dto.setDiscount(new BigDecimal("10"));

        Cart cart = new Cart();
        cart.setIdentifier("C1");

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);

        CartDto result = cartService.update(dto);

        Assertions.assertNotNull(result);
    }

    // FIND ALL

    @Test
    void findAll_ShouldReturnList() {

        Cart cart = new Cart();
        CartDto dto = new CartDto();

        Type type = new TypeToken<List<CartDto>>() {
        }.getType();

        Mockito.when(cartRepository.findAll())
                .thenReturn(List.of(cart));

        Mockito.when(modelMapper.map(List.of(cart), type))
                .thenReturn(List.of(dto));

        List<CartDto> result = cartService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // PAGINATION

    @Test
    void findAll_Pageable() {

        Pageable pageable = PageRequest.of(0, 10);

        Cart cart = new Cart();
        Page<Cart> page = new PageImpl<>(List.of(cart));

        Mockito.when(cartRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(List.of(cart),
                        new TypeToken<List<CartDto>>() {
                        }.getType()))
                .thenReturn(List.of(new CartDto()));

        List<CartDto> result = cartService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
    }
}