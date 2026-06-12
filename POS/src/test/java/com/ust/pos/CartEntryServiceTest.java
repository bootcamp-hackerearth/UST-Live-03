package com.ust.pos;

import com.ust.pos.cartEntry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;

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
    private CartRepository cartRepository;

    @Test
    void findByIdentifierTest() {
        CartEntry cartEntry = new CartEntry();
        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setIdentifier("CART1_PROD1");

        Mockito.when(cartEntryRepository.findByIdentifier("CART1_PROD1")).thenReturn(cartEntry);
        Mockito.when(modelMapper.map(cartEntry, CartEntryDto.class)).thenReturn(cartEntryDto);

        CartEntryDto response = cartEntryService.findByIdentifier("CART1_PROD1");

        Assertions.assertEquals("CART1_PROD1", response.getIdentifier());
    }

    @Test
    void saveNewEntryTest() {
        CartEntryDto inputDto = new CartEntryDto();
        inputDto.setCartId("CART1");
        inputDto.setProduct("PROD1");
        inputDto.setQuantity(new BigDecimal("2"));

        PriceDto mrpPrice = new PriceDto();
        mrpPrice.setPriceAmount(new BigDecimal("100"));

        PriceDto sellingPrice = new PriceDto();
        sellingPrice.setPriceAmount(new BigDecimal("80"));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setTotalPrice(new BigDecimal("160"));
        cartEntry.setDiscount(new BigDecimal("40"));
        cartEntry.setTotalOriginalPrice(new BigDecimal("200"));

        Cart cart = new Cart();

        Mockito.when(cartEntryRepository.findByIdentifier("CART1_PROD1")).thenReturn(null);
        Mockito.when(priceService.findByProductAndPriceType("PROD1", "MRP")).thenReturn(mrpPrice);
        Mockito.when(priceService.findByProductAndPriceType("PROD1", "Selling price")).thenReturn(sellingPrice);
        Mockito.when(modelMapper.map(inputDto, CartEntry.class)).thenReturn(cartEntry);

        Mockito.when(cartEntryRepository.findAllByCartId("CART1")).thenReturn(Collections.singletonList(cartEntry));
        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);

        CartEntryDto response = cartEntryService.save(inputDto);

        Assertions.assertEquals("CART1_PROD1", response.getIdentifier());
        verify(cartEntryRepository).save(cartEntry);
        verify(cartRepository).save(cart);
    }

    @Test
    void deleteTest() {
        Cart cart = new Cart();
        Mockito.when(cartEntryRepository.findAllByCartId("CART1")).thenReturn(Collections.emptyList());
        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);

        cartEntryService.delete("CART1", "PROD1");

        verify(cartEntryRepository).deleteByCartIdAndProduct("CART1", "PROD1");
        verify(cartRepository).save(cart);
    }

    @Test
    void findAllTest() {
        CartEntry entry = new CartEntry();
        List<CartEntry> entries = List.of(entry);

        CartEntryDto dto = new CartEntryDto();
        List<CartEntryDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CartEntry> page = new PageImpl<>(entries);

        Mockito.when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(dtos);

        List<CartEntryDto> response = cartEntryService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByCartIdTest() {
        CartEntry entry = new CartEntry();
        List<CartEntry> entries = List.of(entry);

        CartEntryDto dto = new CartEntryDto();
        List<CartEntryDto> dtos = List.of(dto);

        Mockito.when(cartEntryRepository.findAllByCartId("CART1")).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(dtos);

        List<CartEntryDto> response = cartEntryService.findByCartId("CART1");

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteAllByCartIdTest() {
        Cart cart = new Cart();
        Mockito.when(cartEntryRepository.findAllByCartId("CART1")).thenReturn(Collections.emptyList());
        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);

        cartEntryService.deleteAllByCartId("CART1");

        verify(cartEntryRepository).deleteAllByCartId("CART1");
        verify(cartRepository).save(cart);
    }
}