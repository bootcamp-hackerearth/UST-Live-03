package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

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

    @Test
    void findByIdentifierTest() {
        CartEntry cartEntry = new CartEntry();
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setIdentifier("PROD01-CART01");

        Mockito.when(cartEntryRepository.findByIdentifier("PROD01-CART01")).thenReturn(cartEntry);
        Mockito.when(modelMapper.map(cartEntry, CartEntryDto.class)).thenReturn(cartEntryDto);

        CartEntryDto response = cartEntryService.findByIdentifier("PROD01-CART01");

        Assertions.assertEquals("PROD01-CART01", response.getIdentifier());
    }

    @Test
    void saveTestNewEntry() {
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("PROD01");
        cartEntryDto.setCart("CART01");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        Price price = new Price();
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));

        Mockito.when(cartEntryRepository.findByIdentifier("PROD01-CART01")).thenReturn(null);
        Mockito.when(priceRepository.findByIdentifier("PROD01")).thenReturn(price);

        CartEntry cartEntry = new CartEntry();

        Mockito.doAnswer(invocation -> {
            return null;
        }).when(modelMapper).map(Mockito.eq(cartEntryDto), Mockito.any(CartEntry.class));

        Mockito.when(cartEntryRepository.save(Mockito.any(CartEntry.class))).thenReturn(cartEntry);

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertEquals("PROD01-CART01", response.getIdentifier());
        Assertions.assertEquals(new BigDecimal("2"), response.getQuantity());
        Assertions.assertEquals(new BigDecimal("160.00"), response.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("40.00"), response.getDiscount());
        Assertions.assertEquals(new BigDecimal("100.00"), response.getPrice());
        Assertions.assertEquals(new BigDecimal("80.00"), response.getSellingPrice());
    }

    @Test
    void saveTestExistingEntry() {
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("PROD01");
        cartEntryDto.setCart("CART01");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        CartEntry existingCartEntry = new CartEntry();
        existingCartEntry.setQuantity(new BigDecimal("3"));

        Price price = new Price();
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));

        Mockito.when(cartEntryRepository.findByIdentifier("PROD01-CART01")).thenReturn(existingCartEntry);
        Mockito.when(priceRepository.findByIdentifier("PROD01")).thenReturn(price);
        Mockito.doNothing().when(modelMapper).map(cartEntryDto, existingCartEntry);
        Mockito.when(cartEntryRepository.save(existingCartEntry)).thenReturn(existingCartEntry);

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertEquals("PROD01-CART01", response.getIdentifier());
        Assertions.assertEquals(new BigDecimal("5"), response.getQuantity());
        Assertions.assertEquals(new BigDecimal("400.00"), response.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("100.00"), response.getDiscount());
    }

    @Test
    void findAllCartsTest() {
        CartEntry cartEntry = new CartEntry();
        List<CartEntry> cartEntryList = List.of(cartEntry);
        CartEntryDto cartEntryDto = new CartEntryDto();
        List<CartEntryDto> cartEntryDtos = List.of(cartEntryDto);

        Mockito.when(cartEntryRepository.findByCart("CART01")).thenReturn(cartEntryList);
        Mockito.when(modelMapper.map(Mockito.eq(cartEntryList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(cartEntryDtos);

        List<CartEntryDto> response = cartEntryService.findAllCarts("CART01");

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        CartEntry cartEntry = new CartEntry();
        List<CartEntry> cartEntries = List.of(cartEntry);
        Page<CartEntry> cartEntryPage = new PageImpl<>(cartEntries, pageable, cartEntries.size());

        CartEntryDto cartEntryDto = new CartEntryDto();
        List<CartEntryDto> cartEntryDtos = List.of(cartEntryDto);

        Mockito.when(cartEntryRepository.findAll(pageable)).thenReturn(cartEntryPage);
        Mockito.when(modelMapper.map(Mockito.eq(cartEntries), Mockito.any(java.lang.reflect.Type.class))).thenReturn(cartEntryDtos);

        List<CartEntryDto> response = cartEntryService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTestSuccess() {
        CartEntry cartEntry = new CartEntry();
        Mockito.when(cartEntryRepository.findByIdentifier("PROD01-CART01")).thenReturn(cartEntry);
        Mockito.doNothing().when(cartEntryRepository).deleteByIdentifier("PROD01-CART01");

        boolean response = cartEntryService.delete("PROD01", "CART01");

        Assertions.assertTrue(response);
        Mockito.verify(cartEntryRepository, Mockito.times(1)).deleteByIdentifier("PROD01-CART01");
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(cartEntryRepository.findByIdentifier("PROD01-CART01")).thenReturn(null);

        boolean response = cartEntryService.delete("PROD01", "CART01");

        Assertions.assertFalse(response);
        Mockito.verify(cartEntryRepository, Mockito.never()).deleteByIdentifier(Mockito.anyString());
    }

    @Test
    void deleteAllByCartSuccess() {
        CartEntry cartEntry1 = new CartEntry();
        List<CartEntry> entries = List.of(cartEntry1);

        Mockito.when(cartEntryRepository.findByCart("CART01")).thenReturn(entries);
        Mockito.doNothing().when(cartEntryRepository).deleteAll(entries);

        boolean response = cartEntryService.deleteAllByCart("CART01");

        Assertions.assertTrue(response);
        Mockito.verify(cartEntryRepository, Mockito.times(1)).deleteAll(entries);
    }

    @Test
    void deleteAllByCartFailure() {
        Mockito.when(cartEntryRepository.findByCart("CART01")).thenReturn(Collections.emptyList());

        boolean response = cartEntryService.deleteAllByCart("CART01");

        Assertions.assertFalse(response);
        Mockito.verify(cartEntryRepository, Mockito.never()).deleteAll(Mockito.anyList());
    }
}