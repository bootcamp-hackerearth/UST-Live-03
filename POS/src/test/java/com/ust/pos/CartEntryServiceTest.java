package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
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
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Test
    void findByIdentifierTest() {
        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD001-CART001");

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setIdentifier("PROD001-CART001");

        Mockito.when(cartEntryRepository.findByIdentifier("PROD001-CART001")).thenReturn(cartEntry);
        Mockito.when(modelMapper.map(cartEntry, CartEntryDto.class)).thenReturn(cartEntryDto);

        CartEntryDto response = cartEntryService.findByIdentifier("PROD001-CART001");

        Assertions.assertEquals("PROD001-CART001", response.getIdentifier());
    }

    @Test
    void saveTestNewEntry() {
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("PROD001");
        cartEntryDto.setCart("CART001");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        Price price = new Price();
        price.setSellingPrice(new BigDecimal("100.00"));
        price.setMrp(new BigDecimal("120.00"));

        Mockito.when(cartEntryRepository.findByIdentifier("PROD001-CART001")).thenReturn(null);
        Mockito.when(priceRepository.findByIdentifier("PROD001")).thenReturn(price);

        CartEntry cartEntry = new CartEntry();
        Mockito.when(cartEntryRepository.save(Mockito.any(CartEntry.class))).thenReturn(cartEntry);

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertEquals("PROD001-CART001", response.getIdentifier());
        Assertions.assertEquals(new BigDecimal("200.00"), response.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("40.00"), response.getDiscount());
    }

    @Test
    void saveTestExistingEntry() {
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("PROD001");
        cartEntryDto.setCart("CART001");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        CartEntry existingEntry = new CartEntry();
        existingEntry.setIdentifier("PROD001-CART001");
        existingEntry.setQuantity(new BigDecimal("3"));

        Price price = new Price();
        price.setSellingPrice(new BigDecimal("100.00"));
        price.setMrp(new BigDecimal("120.00"));

        Mockito.when(cartEntryRepository.findByIdentifier("PROD001-CART001")).thenReturn(existingEntry);
        Mockito.when(priceRepository.findByIdentifier("PROD001")).thenReturn(price);
        Mockito.when(cartEntryRepository.save(Mockito.any(CartEntry.class))).thenReturn(existingEntry);

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertEquals(new BigDecimal("5"), response.getQuantity());
        Assertions.assertEquals(new BigDecimal("500.00"), response.getTotalPrice());
    }

    @Test
    void findAllCartsTest() {
        CartEntry cartEntry = new CartEntry();
        cartEntry.setCart("CART001");

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setCart("CART001");

        List<CartEntry> cartEntries = List.of(cartEntry);
        List<CartEntryDto> cartEntryDtos = List.of(cartEntryDto);

        Mockito.when(cartEntryRepository.findByCart("CART001")).thenReturn(cartEntries);
        Mockito.when(modelMapper.map(
                Mockito.eq(cartEntries),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(cartEntryDtos);

        List<CartEntryDto> response = cartEntryService.findAllCarts("CART001");

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllTest() {
        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD001-CART001");

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setIdentifier("PROD001-CART001");

        List<CartEntry> cartEntries = List.of(cartEntry);
        List<CartEntryDto> cartEntryDtos = List.of(cartEntryDto);

        Page<CartEntry> cartEntryPage = new PageImpl<>(cartEntries,
                PageRequest.of(0, 2), cartEntries.size());

        Pageable pageable = PageRequest.of(0, 50,
                Sort.by(new ArrayList<>()));

        Mockito.when(cartEntryRepository.findAll(pageable)).thenReturn(cartEntryPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(cartEntries),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(cartEntryDtos);

        List<CartEntryDto> response = cartEntryService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTestSuccess() {
        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD001-CART001");

        Mockito.when(cartEntryRepository.findByIdentifier("PROD001-CART001")).thenReturn(cartEntry);
        Mockito.doNothing().when(cartEntryRepository).deleteByIdentifier("PROD001-CART001");

        boolean response = cartEntryService.delete("PROD001", "CART001");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(cartEntryRepository.findByIdentifier("PROD001-CART001")).thenReturn(null);

        boolean response = cartEntryService.delete("PROD001", "CART001");

        Assertions.assertFalse(response);
    }

    @Test
    void deleteAllByCartTestSuccess() {
        CartEntry cartEntry = new CartEntry();
        List<CartEntry> entries = List.of(cartEntry);

        Mockito.when(cartEntryRepository.findByCart("CART001")).thenReturn(entries);
        Mockito.doNothing().when(cartEntryRepository).deleteAll(entries);

        boolean response = cartEntryService.deleteAllByCart("CART001");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteAllByCartTestFailure() {
        Mockito.when(cartEntryRepository.findByCart("CART001")).thenReturn(new ArrayList<>());

        boolean response = cartEntryService.deleteAllByCart("CART001");

        Assertions.assertFalse(response);
    }
}