package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Cart;
import com.ust.pos.modell.CartEntry;
import com.ust.pos.modell.CartEntryRepository;
import com.ust.pos.modell.CartRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    public static final String INVALID = "INVALID";
    public static final String CART_1 = "CART1";
    public static final String PROD_1_SELLING = "PROD1-SELLING";
    public static final String PROD_1 = "CART1-PROD1";
    @InjectMocks
    private CartServiceImpl service;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private PriceService priceService;

    @Mock
    private ModelMapper modelMapper;

    private Cart cart;
    private CartEntry entry;

    @BeforeEach
    void setup() {

        cart = new Cart();
        cart.setIdentifier(CART_1);
        cart.setStatus(true);

        entry = new CartEntry();
        entry.setIdentifier(PROD_1);
        entry.setCartIdentifier(CART_1);
        entry.setProductIdentifier("PROD1");
        entry.setQuantity(2);
        entry.setDeleted(false);
    }

    @Test
    void saveTest() {

        CartDto dto = new CartDto();
        dto.setIdentifier(CART_1);

        when(cartRepository.findByIdentifier(CART_1))
                .thenReturn(null);

        CartDto result = service.save(dto);

        verify(cartRepository).save(any(Cart.class));

        assertNotNull(result);

        cart.setDeleted(false);

        when(cartRepository.findByIdentifier(CART_1))
                .thenReturn(cart);

        result = service.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Cart already exists", result.getMessage());

        cart.setDeleted(true);

        result = service.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Cart with Identifier CART1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void deleteTest() {

        when(cartEntryRepository.findByIdentifierAndDeletedFalse("X"))
                .thenReturn(null);

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> service.delete("X")
                );

        assertEquals(
                "Cart entry not found",
                ex.getMessage()
        );

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));

        when(cartEntryRepository.findByIdentifierAndDeletedFalse(PROD_1))
                .thenReturn(entry);

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(PROD_1_SELLING))
                .thenReturn(selling);

        when(priceService.findByIdentifier("PROD1-MRP"))
                .thenReturn(mrp);

        service.delete(PROD_1);

        verify(cartEntryRepository).save(entry);
        verify(cartRepository).save(cart);
    }

    @Test
    void findByIdentifierTest() {

        when(cartRepository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        ResourceNotFoundException ex =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier(INVALID)
                );

        assertEquals("Cart not found", ex.getMessage());

        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.valueOf(100));

        CartDto dto = new CartDto();

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(anyString()))
                .thenReturn(price);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);

        when(modelMapper.map(entry, CartEntryDto.class))
                .thenReturn(new CartEntryDto());

        CartDto result = service.findByIdentifier(CART_1);

        assertEquals(1, result.getEntryCart().size());
    }

    @Test
    void recalculateAndSaveTest() {

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(anyString()))
                .thenReturn(null);

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> service.recalculateAndSave(cart)
                );

        assertEquals(
                "Price not configured",
                ex.getMessage()
        );

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));

        when(priceService.findByIdentifier(PROD_1_SELLING))
                .thenReturn(null);

        when(priceService.findByIdentifier("PROD1-MRP"))
                .thenReturn(mrp);

        service.recalculateAndSave(cart);

        assertEquals(
                BigDecimal.valueOf(240),
                cart.getTotalPrice()
        );

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        when(priceService.findByIdentifier(PROD_1_SELLING))
                .thenReturn(selling);

        service.recalculateAndSave(cart);

        assertEquals(
                BigDecimal.valueOf(200),
                cart.getTotalPrice()
        );

        cart.setCoupon("FLAT10");

        service.recalculateAndSave(cart);

        assertTrue(
                cart.getDiscount().compareTo(BigDecimal.ZERO) > 0
        );
    }

    @Test
    void clearCartTest() {

        when(cartRepository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> service.clearCart(INVALID)
                );

        assertEquals(
                "Cart not found",
                ex.getMessage()
        );

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        service.clearCart(CART_1);

        verify(cartEntryRepository).save(entry);
        verify(cartRepository).save(cart);

        assertEquals(
                BigDecimal.ZERO,
                cart.getTotalPrice()
        );
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Cart> page =
                new PageImpl<>(
                        List.of(cart),
                        pageable,
                        1
                );

        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.valueOf(100));

        when(cartRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(entry));

        when(priceService.findByIdentifier(anyString()))
                .thenReturn(price);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(new CartDto());

        when(modelMapper.map(entry, CartEntryDto.class))
                .thenReturn(new CartEntryDto());

        WsDto<CartDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void specificationFindAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Cart> page =
                new PageImpl<>(
                        List.of(cart),
                        pageable,
                        1
                );

        when(cartRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CartDto()));

        Specification<Cart> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<CartDto> result =
                service.findAll(specification, pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());

        verify(cartRepository)
                .findAll(any(Specification.class), eq(pageable));
    }
}