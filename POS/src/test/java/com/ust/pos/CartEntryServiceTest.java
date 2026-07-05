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
        cartEntryDto.setProduct("PROD-001");
        cartEntryDto.setCart("CRT-001");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD-001-CRT-001");
        cartEntry.setQuantity(new BigDecimal("3"));

        price = new Price();
        price.setIdentifier("PROD-001");
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));
    }

    @Test
    @DisplayName("Save CartEntry - Success New Entry")
    void save_Success_NewEntry() {
        String expectedIdentifier = "PROD-001-CRT-001";
        when(cartEntryRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);
        when(priceRepository.findByIdentifier("PROD-001")).thenReturn(price);

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedIdentifier, result.getIdentifier());
        Assertions.assertEquals(new BigDecimal("2"), result.getQuantity());
        Assertions.assertEquals(new BigDecimal("160.00"), result.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("40.00"), result.getDiscount());
        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    @DisplayName("Save CartEntry - Success Existing Entry")
    void save_Success_ExistingEntry() {
        String expectedIdentifier = "PROD-001-CRT-001";
        when(cartEntryRepository.findByIdentifier(expectedIdentifier)).thenReturn(cartEntry);
        when(priceRepository.findByIdentifier("PROD-001")).thenReturn(price);

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedIdentifier, result.getIdentifier());
        Assertions.assertEquals(new BigDecimal("5"), result.getQuantity());
        Assertions.assertEquals(new BigDecimal("400.00"), result.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("100.00"), result.getDiscount());
        verify(cartEntryRepository).save(cartEntry);
    }

    @Test
    @DisplayName("Find All Entries For Cart - Success")
    void findAllEntriesForCart_Success() {
        List<CartEntry> entries = List.of(cartEntry);
        List<CartEntryDto> dtos = List.of(cartEntryDto);

        when(cartEntryRepository.findByCart("CRT-001")).thenReturn(entries);
        when(modelMapper.map(eq(entries), any(Type.class))).thenReturn(dtos);

        List<CartEntryDto> result = cartEntryService.findAllEntriesForCart("CRT-001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Delete By Identifier - Success")
    void deleteByIdentifier_Success() {
        when(cartEntryRepository.findByIdentifier("PROD-001-CRT-001")).thenReturn(cartEntry);
        doNothing().when(cartEntryRepository).deleteByIdentifier("PROD-001-CRT-001");

        cartEntryService.deleteByIdentifier("PROD-001-CRT-001");

        verify(cartEntryRepository).deleteByIdentifier("PROD-001-CRT-001");
    }

    @Test
    @DisplayName("Delete By Identifier - Failure: Not Found")
    void deleteByIdentifier_Failure_NotFound() {
        when(cartEntryRepository.findByIdentifier("PROD-001-CRT-001")).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> cartEntryService.deleteByIdentifier("PROD-001-CRT-001"));
        verify(cartEntryRepository, never()).deleteByIdentifier(anyString());
    }

    @Test
    @DisplayName("Delete All By Cart - Success")
    void deleteAllByCart_Success() {
        List<CartEntry> entries = List.of(cartEntry);
        when(cartEntryRepository.findByCart("CRT-001")).thenReturn(entries);
        doNothing().when(cartEntryRepository).deleteAll(entries);

        cartEntryService.deleteAllByCart("CRT-001");

        verify(cartEntryRepository).deleteAll(entries);
    }

    @Test
    @DisplayName("Delete All By Cart - Empty List")
    void deleteAllByCart_EmptyList() {
        when(cartEntryRepository.findByCart("CRT-001")).thenReturn(Collections.emptyList());

        cartEntryService.deleteAllByCart("CRT-001");

        verify(cartEntryRepository, never()).deleteAll(anyList());
    }
}