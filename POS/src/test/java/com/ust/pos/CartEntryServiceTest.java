package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Test
    void saveNewEntryFullCoverageTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCart("C1");
        dto.setQuantity(BigDecimal.valueOf(2));
        String identifier = "P1-C1";
        Price price = new Price();
        price.setMrp(BigDecimal.valueOf(100));
        price.setSellingPrice(BigDecimal.valueOf(80));
        CartEntry savedEntry = new CartEntry();
        savedEntry.setCart("C1");
        Mockito.when(cartEntryRepository.findByIdentifier(identifier)).thenReturn(null);
        Mockito.when(priceRepository.findByIdentifier("P1")).thenReturn(price);
        Mockito.when(cartEntryRepository.save(Mockito.any(CartEntry.class))).thenReturn(savedEntry);
        Mockito.when(cartService.recalculate(Mockito.any())).thenReturn(null);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertEquals("P1-C1", response.getIdentifier());
        Assertions.assertEquals(BigDecimal.valueOf(2), response.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(160), response.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(40), response.getDiscount());
        Mockito.verify(cartEntryRepository).save(Mockito.any(CartEntry.class));
        Mockito.verify(cartService).recalculate(Mockito.any());
    }

    @Test
    void saveExistingEntryFullCoverageTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCart("C1");
        dto.setQuantity(BigDecimal.valueOf(2));
        String identifier = "P1-C1";
        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.valueOf(3));
        Price price = new Price();
        price.setMrp(BigDecimal.valueOf(100));
        price.setSellingPrice(BigDecimal.valueOf(80));
        Mockito.when(cartEntryRepository.findByIdentifier(identifier)).thenReturn(existing);
        Mockito.when(priceRepository.findByIdentifier("P1")).thenReturn(price);
        Mockito.when(cartEntryRepository.save(existing)).thenReturn(existing);
        Mockito.when(cartService.recalculate(Mockito.any())).thenReturn(null);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertEquals(BigDecimal.valueOf(5), response.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(400), response.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(100), response.getDiscount());
        Mockito.verify(cartEntryRepository).save(existing);
    }

    @Test
    void findAllEntriesForCartTest() {
        String cart = "C1";
        CartEntry entry = new CartEntry();
        entry.setIdentifier("P1-C1");
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P1-C1");
        List<CartEntry> entityList = List.of(entry);
        List<CartEntryDto> dtoList = List.of(dto);
        Mockito.when(cartEntryRepository.findByCart(cart)).thenReturn(entityList);
        Mockito.when(modelMapper.map(Mockito.eq(entityList), Mockito.any(Type.class))).thenReturn(dtoList);
        List<CartEntryDto> response = cartEntryService.findAllEntriesForCart(cart);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("P1-C1", response.get(0).getIdentifier());
    }

    @Test
    void deleteByIdentifierTest() {
        Mockito.doNothing().when(cartEntryRepository).deleteByIdentifier("P1-C1");
        cartEntryService.deleteByIdentifier("P1-C1");
        Mockito.verify(cartEntryRepository, Mockito.times(1)).deleteByIdentifier("P1-C1");
    }

    @Test
    void deleteAllByCartWithDataTest() {
        String cart = "C1";
        List<CartEntry> list = new ArrayList<>();
        list.add(new CartEntry());
        Mockito.when(cartEntryRepository.findByCart(cart)).thenReturn(list);
        Mockito.doNothing().when(cartEntryRepository).deleteAll(list);
        cartEntryService.deleteAllByCart(cart);
        Mockito.verify(cartEntryRepository).deleteAll(list);
    }

    @Test
    void deleteAllByCartEmptyTest() {
        String cart = "C1";
        Mockito.when(cartEntryRepository.findByCart(cart)).thenReturn(new ArrayList<>());
        cartEntryService.deleteAllByCart(cart);
        Mockito.verify(cartEntryRepository, Mockito.never()).deleteAll(Mockito.any());
    }
}