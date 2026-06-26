package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    private CartEntry cartEntry;
    private CartEntryDto cartEntryDto;
    private Price price;

    @BeforeEach
    void setUp() {
        cartEntry = new CartEntry();
        cartEntry.setIdentifier("P1-C1");
        cartEntry.setQuantity(BigDecimal.valueOf(2));

        cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("P1");
        cartEntryDto.setCart("C1");
        cartEntryDto.setQuantity(BigDecimal.ONE);

        price = new Price();
        price.setSellingPrice(BigDecimal.valueOf(100));
        price.setMrp(BigDecimal.valueOf(120));
    }

    @Test
    void testSave_NewCartEntry() {
        when(cartEntryRepository.findByIdentifier("P1-C1")).thenReturn(null);
        when(priceRepository.findByIdentifier("P1")).thenReturn(price);

        doNothing().when(modelMapper).map(eq(cartEntryDto), any(CartEntry.class));

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        assertNotNull(result);
        assertEquals(BigDecimal.ONE, result.getQuantity());
        assertEquals(BigDecimal.valueOf(100), result.getSellingPrice());
        assertEquals(BigDecimal.valueOf(100), result.getTotalPrice());
        assertEquals(BigDecimal.valueOf(20), result.getDiscount());

        verify(cartEntryRepository).save(any(CartEntry.class));
    }


    @Test
    void testSave_ExistingCartEntry() {
        when(cartEntryRepository.findByIdentifier("P1-C1")).thenReturn(cartEntry);
        when(priceRepository.findByIdentifier("P1")).thenReturn(price);

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        // existing qty = 2 + new qty = 1 => 3
        assertEquals(BigDecimal.valueOf(3), result.getQuantity());
        assertEquals(BigDecimal.valueOf(300), result.getTotalPrice()); // 100 * 3
        assertEquals(BigDecimal.valueOf(60), result.getDiscount());   // 20 * 3

        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    void testDeleteAllByCart_WithData() {
        List<CartEntry> list = Collections.singletonList(cartEntry);

        when(cartEntryRepository.findByCart("C1")).thenReturn(list);

        cartEntryService.deleteAllByCart("C1");

        verify(cartEntryRepository).deleteAll(list);
    }

    @Test
    void testDeleteAllByCart_Empty() {
        when(cartEntryRepository.findByCart("C1")).thenReturn(Collections.emptyList());

        cartEntryService.deleteAllByCart("C1");

        verify(cartEntryRepository, never()).deleteAll(any());
    }

    // ✅ DELETE BY IDENTIFIER - SUCCESS
    @Test
    void testDeleteByIdentifier_Success() {
        when(cartEntryRepository.findByIdentifier("P1-C1")).thenReturn(cartEntry);

        cartEntryService.deleteByIdentifier("P1-C1");

        verify(cartEntryRepository).deleteByIdentifier("P1-C1");
    }

    @Test
    void testDeleteByIdentifier_NotFound() {
        when(cartEntryRepository.findByIdentifier("P1-C1")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                cartEntryService.deleteByIdentifier("P1-C1")
        );
    }

    @Test
    void testFindAllCarts() {
        List<CartEntry> list = Collections.singletonList(cartEntry);
        List<CartEntryDto> dtoList = Collections.singletonList(cartEntryDto);

        when(cartEntryRepository.findByCart("C1")).thenReturn(list);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(dtoList);

        List<CartEntryDto> result = cartEntryService.findAllCarts("C1");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindActiveStatus() {
        cartEntry.setStatus(true);
        CartEntry inactive = new CartEntry();
        inactive.setStatus(false);

        List<CartEntry> list = List.of(cartEntry, inactive);

        when(cartEntryRepository.findAll()).thenReturn(list);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(cartEntryDto));

        List<CartEntryDto> result = cartEntryService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}