package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private CartService cartService;

    @Test
    void saveTest_NewEntry() {
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("PROD001");
        cartEntryDto.setCart("CART001");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        Price price = new Price();
        price.setIdentifier("PROD001");
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setCart("CART001");

        when(cartEntryRepository.findByIdentifier("PROD001-CART001")).thenReturn(null);
        when(priceRepository.findByIdentifier("PROD001")).thenReturn(price);
        Mockito.lenient().when(modelMapper.map(any(), any())).thenReturn(null);
        when(cartEntryRepository.save(any(CartEntry.class))).thenReturn(cartEntry);

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PROD001-CART001", result.getIdentifier());
        Assertions.assertEquals(new BigDecimal("160.00"), result.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("40.00"), result.getDiscount());
        Assertions.assertEquals(new BigDecimal("100.00"), result.getPrice());
        Assertions.assertEquals(new BigDecimal("80.00"), result.getSellingPrice());

        verify(cartEntryRepository, times(1)).save(any(CartEntry.class));
        verify(cartService, times(1)).recalculate(any());
    }

    @Test
    void saveTest_ExistingEntry_QuantityAdded() {
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("PROD001");
        cartEntryDto.setCart("CART001");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        Price price = new Price();
        price.setIdentifier("PROD001");
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));

        CartEntry existingEntry = new CartEntry();
        existingEntry.setIdentifier("PROD001-CART001");
        existingEntry.setCart("CART001");
        existingEntry.setQuantity(new BigDecimal("3"));

        when(cartEntryRepository.findByIdentifier("PROD001-CART001")).thenReturn(existingEntry);
        when(priceRepository.findByIdentifier("PROD001")).thenReturn(price);
        Mockito.lenient().when(modelMapper.map(any(), any())).thenReturn(null);
        when(cartEntryRepository.save(existingEntry)).thenReturn(existingEntry);

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(new BigDecimal("5"), result.getQuantity());
        Assertions.assertEquals(new BigDecimal("400.00"), result.getTotalPrice());

        verify(cartEntryRepository, times(1)).save(existingEntry);
        verify(cartService, times(1)).recalculate(any());
    }

    @Test
    void deleteTest() {
        cartEntryService.delete("PROD001-CART001");
        verify(cartEntryRepository, times(1)).deleteByIdentifier("PROD001-CART001");
    }

    @Test
    void deleteAllByCartTest_WithEntries() {
        CartEntry entry1 = new CartEntry();
        entry1.setIdentifier("PROD001-CART001");

        CartEntry entry2 = new CartEntry();
        entry2.setIdentifier("PROD002-CART001");

        List<CartEntry> entries = List.of(entry1, entry2);

        when(cartEntryRepository.findByCart("CART001")).thenReturn(entries);

        cartEntryService.deleteAllByCart("CART001");

        verify(cartEntryRepository, times(1)).deleteAll(entries);
    }

    @Test
    void deleteAllByCartTest_NoEntries() {
        when(cartEntryRepository.findByCart("CART001")).thenReturn(List.of());
        cartEntryService.deleteAllByCart("CART001");
        verify(cartEntryRepository, never()).deleteAll(any());
    }

    @Test
    void findAllEntriesForCartTest() {
        CartEntry e1 = new CartEntry();
        CartEntry e2 = new CartEntry();
        List<CartEntry> entries = List.of(e1, e2);

        CartEntryDto d1 = new CartEntryDto();
        CartEntryDto d2 = new CartEntryDto();
        List<CartEntryDto> dtoList = List.of(d1, d2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CartEntry> page = new PageImpl<>(entries, pageable, entries.size());

        when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<CartEntryDto> result = cartEntryService.findAllEntriesForCart(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());

        verify(cartEntryRepository).findAll(pageable);
        verify(modelMapper).map(Mockito.eq(entries), Mockito.any(Type.class));
    }
}