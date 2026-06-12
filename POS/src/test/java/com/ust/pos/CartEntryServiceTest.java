package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
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
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    private CartEntry cartEntry;
    private CartEntryDto cartEntryDto;
    private Price mrpPrice;
    private Price sellingPrice;
    private Cart cart;

    @BeforeEach
    void setUp() {

        cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("PROD1");
        cartEntryDto.setCartId("CART1");
        cartEntryDto.setQuantity(BigDecimal.valueOf(2));

        cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD1_CART1");
        cartEntry.setCartId("CART1");
        cartEntry.setQuantity(BigDecimal.valueOf(2));

        mrpPrice = new Price();
        mrpPrice.setPriceAmount(BigDecimal.valueOf(150));

        sellingPrice = new Price();
        sellingPrice.setPriceAmount(BigDecimal.valueOf(100));

        cart = new Cart();
        cart.setIdentifier("CART1");
    }

    @Test
    void testFindByIdentifier_Found() {

        when(cartEntryRepository.findByIdentifier("PROD1_CART1"))
                .thenReturn(cartEntry);

        when(modelMapper.map(cartEntry, CartEntryDto.class))
                .thenReturn(cartEntryDto);

        CartEntryDto result =
                cartEntryService.findByIdentifier("PROD1_CART1");

        assertNotNull(result);

        verify(cartEntryRepository)
                .findByIdentifier("PROD1_CART1");
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(cartEntryRepository.findByIdentifier("PROD1_CART1"))
                .thenReturn(null);

        CartEntryDto result =
                cartEntryService.findByIdentifier("PROD1_CART1");

        assertNull(result);
    }

    @Test
    void testGetSellingPrice() {

        when(priceRepository.findByProductAndPriceType(
                "PROD1",
                "Selling Price"))
                .thenReturn(sellingPrice);

        BigDecimal result =
                cartEntryService.getSellingPrice("PROD1");

        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    void testGetDiscount() {

        when(priceRepository.findByProductAndPriceType(
                "PROD1",
                "Selling Price"))
                .thenReturn(sellingPrice);

        when(priceRepository.findByProductAndPriceType(
                "PROD1",
                "MRP"))
                .thenReturn(mrpPrice);

        BigDecimal result =
                cartEntryService.getDiscount(cartEntryDto);

        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    void testSave_NewCartEntry() {

        when(priceRepository.findByProductAndPriceType(
                "PROD1",
                "MRP"))
                .thenReturn(mrpPrice);

        when(priceRepository.findByProductAndPriceType(
                "PROD1",
                "Selling Price"))
                .thenReturn(sellingPrice);

        when(cartEntryRepository.findByIdentifier("PROD1_CART1"))
                .thenReturn(null);

        when(modelMapper.map(cartEntryDto, CartEntry.class))
                .thenReturn(cartEntry);

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(Collections.emptyList());

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        CartEntryDto result =
                cartEntryService.save(cartEntryDto);

        assertNotNull(result);

        verify(cartEntryRepository)
                .save(any(CartEntry.class));
    }

    @Test
    void testSave_ExistingCartEntry() {

        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.valueOf(3));

        when(priceRepository.findByProductAndPriceType(
                "PROD1",
                "MRP"))
                .thenReturn(mrpPrice);

        when(priceRepository.findByProductAndPriceType(
                "PROD1",
                "Selling Price"))
                .thenReturn(sellingPrice);

        when(cartEntryRepository.findByIdentifier("PROD1_CART1"))
                .thenReturn(existing);

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(Collections.emptyList());

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        CartEntryDto result =
                cartEntryService.save(cartEntryDto);

        assertEquals(
                BigDecimal.valueOf(5),
                result.getQuantity()
        );

        verify(modelMapper)
                .map(result, existing);

        verify(cartEntryRepository)
                .save(existing);
    }

    @Test
    void testRecalculate_WithEntries() {

        CartEntry e1 = new CartEntry();
        e1.setTotalPrice(BigDecimal.valueOf(100));
        e1.setDiscount(BigDecimal.valueOf(10));
        e1.setOriginalPrice(BigDecimal.valueOf(110));

        CartEntry e2 = new CartEntry();
        e2.setTotalPrice(BigDecimal.valueOf(200));
        e2.setDiscount(BigDecimal.valueOf(20));
        e2.setOriginalPrice(BigDecimal.valueOf(220));

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(List.of(e1, e2));

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        cartEntryService.recalculate("CART1");

        assertEquals(
                BigDecimal.valueOf(300),
                cart.getTotalPrice()
        );

        assertEquals(
                BigDecimal.valueOf(30),
                cart.getDiscount()
        );

        verify(cartRepository)
                .save(cart);
    }

    @Test
    void testRecalculate_EmptyEntries() {

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(Collections.emptyList());

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        cartEntryService.recalculate("CART1");

        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        assertEquals(BigDecimal.ZERO, cart.getDiscount());

        verify(cartRepository)
                .save(cart);
    }

    @Test
    void testFindByCartId() {

        List<CartEntry> entries =
                List.of(cartEntry);

        List<CartEntryDto> dtos =
                List.of(cartEntryDto);

        Type listType =
                new TypeToken<List<CartEntryDto>>(){}.getType();

        when(cartEntryRepository.findByCartId("CART1"))
                .thenReturn(entries);

        when(modelMapper.map(entries, listType))
                .thenReturn(dtos);

        List<CartEntryDto> result =
                cartEntryService.findByCartId("CART1");

        assertEquals(1, result.size());
    }

    @Test
    void testDelete() {

        when(cartEntryRepository.findByIdentifier("PROD1_CART1"))
                .thenReturn(cartEntry);

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(Collections.emptyList());

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        cartEntryService.delete("PROD1_CART1");

        verify(cartEntryRepository)
                .deleteByIdentifier("PROD1_CART1");
    }

    @Test
    void testDeleteAllByCartId() {

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(Collections.emptyList());

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        cartEntryService.deleteAllByCartId("CART1");

        verify(cartEntryRepository)
                .deleteAllByCartId("CART1");
    }

    @Test
    void testFindAll() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<CartEntry> page =
                new PageImpl<>(List.of(cartEntry));

        Type listType =
                new TypeToken<List<CartEntryDto>>(){}.getType();

        when(cartEntryRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(cartEntryDto));

        List<CartEntryDto> result =
                cartEntryService.findAll(pageable);

        assertEquals(1, result.size());
    }
}