package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartService cartService;

    @Test
    void saveTest_NewEntry() {

        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCart("C1");
        dto.setQuantity(new BigDecimal("2"));

        Price price = new Price();
        price.setSellingprice(new BigDecimal("100"));
        price.setMrpprice(new BigDecimal("120"));

        Mockito.when(cartEntryRepository.findByIdentifier("P1-C1"))
                .thenReturn(null);

        Mockito.when(priceRepository.findByIdentifier("P1"))
                .thenReturn(price);

        Mockito.doNothing()
                .when(modelMapper)
                .map(Mockito.any(CartEntryDto.class),
                        Mockito.any(CartEntry.class));

        Mockito.when(cartService.recalculate(Mockito.any()))
                .thenReturn(new CartDto());

        Mockito.when(cartEntryRepository.save(Mockito.any(CartEntry.class)))
                .thenReturn(new CartEntry());

        CartEntryDto response = cartEntryService.save(dto);

        assertNotNull(response);
        assertEquals(new BigDecimal("2"), response.getQuantity());
        assertEquals(new BigDecimal("200"), response.getTotalPrice());
        assertEquals(new BigDecimal("40"), response.getDiscount());
    }
    @Test
    void saveTest_ExistingEntry() {

        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCart("C1");
        dto.setQuantity(new BigDecimal("2"));

        CartEntry existing = new CartEntry();
        existing.setQuantity(new BigDecimal("3"));
        existing.setCart("C1");

        Price price = new Price();
        price.setSellingprice(new BigDecimal("100"));
        price.setMrpprice(new BigDecimal("120"));

        Mockito.when(cartEntryRepository.findByIdentifier("P1-C1"))
                .thenReturn(existing);

        Mockito.when(priceRepository.findByIdentifier("P1"))
                .thenReturn(price);

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, existing);

        Mockito.when(cartEntryRepository.save(existing))
                .thenReturn(existing);

        CartEntryDto response = cartEntryService.save(dto);

        assertNotNull(response);
        assertEquals(new BigDecimal("5"), response.getQuantity());
        assertEquals(new BigDecimal("500"), response.getTotalPrice());
        assertEquals(new BigDecimal("100"), response.getDiscount());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(cartEntryRepository)
                .deleteByIdentifier("P1-C1");

        cartEntryService.delete("P1-C1");

        Mockito.verify(cartEntryRepository)
                .deleteByIdentifier("P1-C1");
    }

    @Test
    void findAllEntriesForCartTest() {
        List<CartEntry> entityList = Arrays.asList(new CartEntry());
        List<CartEntryDto> dtoList = Arrays.asList(new CartEntryDto());

        Mockito.when(cartEntryRepository.findByCart("C1"))
                .thenReturn(entityList);

        Mockito.when(modelMapper.map(
                Mockito.eq(entityList),
                Mockito.any(java.lang.reflect.Type.class))
        ).thenReturn(dtoList);

        List<CartEntryDto> response =
                cartEntryService.findAllEntriesForCart("C1");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void deleteAllByCart_WhenEntriesExist() {
        List<CartEntry> entries = Arrays.asList(new CartEntry());

        Mockito.when(cartEntryRepository.findByCart("C1"))
                .thenReturn(entries);

        cartEntryService.deleteAllByCart("C1");

        Mockito.verify(cartEntryRepository).deleteAll(entries);
    }

    @Test
    void deleteAllByCart_WhenEmpty() {
        Mockito.when(cartEntryRepository.findByCart("C1"))
                .thenReturn(Collections.emptyList());

        cartEntryService.deleteAllByCart("C1");

        Mockito.verify(cartEntryRepository, Mockito.never())
                .deleteAll(Mockito.any());
    }
}