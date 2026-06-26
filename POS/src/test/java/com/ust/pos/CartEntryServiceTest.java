package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    private CartEntryDto cartEntryDto;
    private CartEntry cartEntry;
    private Price price;

    @BeforeEach
    void setUp() {
        cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("PROD1");
        cartEntryDto.setCart("CART1");
        cartEntryDto.setQuantity(BigDecimal.valueOf(2));

        cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD1-CART1");
        cartEntry.setQuantity(BigDecimal.valueOf(3));

        price = new Price();
        price.setIdentifier("PROD1");
        price.setSellingPrice(BigDecimal.valueOf(80));
        price.setMrp(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Save CartEntry - New Entry Success")
    void save_NewEntry_Success() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(null);
        when(priceRepository.findByIdentifier("PROD1")).thenReturn(price);

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        Assertions.assertEquals(BigDecimal.valueOf(2), result.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(160), result.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(40), result.getDiscount());
        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    @DisplayName("Save CartEntry - Existing Entry Quantity Add Success")
    void save_ExistingEntry_Success() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(cartEntry);
        when(priceRepository.findByIdentifier("PROD1")).thenReturn(price);

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        Assertions.assertEquals(BigDecimal.valueOf(5), result.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(400), result.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(100), result.getDiscount());
        verify(cartEntryRepository).save(cartEntry);
    }

    @Test
    @DisplayName("Find All Entries For Cart - Success")
    void findAllEntriesForCart_Success() {
        List<CartEntry> entries = List.of(cartEntry);
        when(cartEntryRepository.findByCart("CART1")).thenReturn(entries);
        when(modelMapper.map(eq(entries), any(Type.class))).thenReturn(List.of(cartEntryDto));

        List<CartEntryDto> result = cartEntryService.findAllEntriesForCart("CART1");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Delete By Identifier - Success")
    void deleteByIdentifier_Success() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(cartEntry);

        cartEntryService.deleteByIdentifier("PROD1-CART1");

        verify(cartEntryRepository).deleteByIdentifier("PROD1-CART1");
    }

    @Test
    @DisplayName("Delete By Identifier - Failure: Not Found")
    void deleteByIdentifier_NotFound() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                cartEntryService.deleteByIdentifier("PROD1-CART1")
        );

        verify(cartEntryRepository, times(1)).findByIdentifier("PROD1-CART1");
    }

    @Test
    @DisplayName("Delete All By Cart - Success")
    void deleteAllByCart_Success() {
        List<CartEntry> entries = List.of(cartEntry);
        when(cartEntryRepository.findByCart("CART1")).thenReturn(entries);

        cartEntryService.deleteAllByCart("CART1");

        verify(cartEntryRepository).deleteAll(entries);
    }

    @Test
    @DisplayName("Delete All By Cart - Empty Cart")
    void deleteAllByCart_Empty() {
        when(cartEntryRepository.findByCart("CART1")).thenReturn(Collections.emptyList());

        cartEntryService.deleteAllByCart("CART1");

        verify(cartEntryRepository, never()).deleteAll(anyList());
    }
}