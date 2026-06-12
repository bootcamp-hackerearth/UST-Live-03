package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;
    @Mock
    private CartEntryRepository cartEntryRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PriceRepository priceRepository;
    @Mock
    private CartService cartService;

    @Test
    void findByIdentifierTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("cart1-prod1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("cart1-prod1");

        Mockito.when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(entry);
        Mockito.when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(dto);
        CartEntryDto response = cartEntryService.findByIdentifier("cart1-prod1");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("cart1-prod1", response.getIdentifier());
    }

    @Test
    void saveNewEntryTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("cart1");
        dto.setProductIdentifier("prod1");
        dto.setQuantity(new BigDecimal("2"));

        Price selling = new Price();
        selling.setAmount(new BigDecimal("100"));

        Price mrp = new Price();
        mrp.setAmount(new BigDecimal("120"));

        Mockito.when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(null);
        Mockito.when(priceRepository.findByProductAndPriceType("prod1", "SELLING PRICE")).thenReturn(selling);
        Mockito.when(priceRepository.findByProductAndPriceType("prod1", "MRP")).thenReturn(mrp);

        Mockito.when(cartService.findByIdentifier("cart1")).thenReturn(null);
        Mockito.when(cartEntryRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));
        Mockito.when(modelMapper.map(Mockito.any(CartEntry.class), Mockito.eq(CartEntryDto.class))).thenReturn(dto);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(cartService).save(Mockito.any(CartDto.class));
        Mockito.verify(cartService).reCalculate("cart1");
    }

    @Test
    void saveExistingEntryTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("cart1");
        dto.setProductIdentifier("prod1");
        dto.setQuantity(new BigDecimal("2"));

        CartEntry existing = new CartEntry();
        existing.setIdentifier("cart1-prod1");
        existing.setQuantity(new BigDecimal("3"));
        existing.setCartIdentifier("cart1");

        Price selling = new Price();
        selling.setAmount(new BigDecimal("100"));
        Price mrp = new Price();
        mrp.setAmount(new BigDecimal("120"));

        Mockito.when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(existing);
        Mockito.when(priceRepository.findByProductAndPriceType("prod1", "SELLING PRICE"))
                .thenReturn(selling);
        Mockito.when(priceRepository.findByProductAndPriceType("prod1", "MRP"))
                .thenReturn(mrp);
        Mockito.when(cartService.findByIdentifier("cart1"))
                .thenReturn(new CartDto());
        Mockito.when(modelMapper.map(Mockito.any(CartEntry.class), Mockito.eq(CartEntryDto.class)))
                .thenReturn(dto);
        cartEntryService.save(dto);
        Assertions.assertEquals(new BigDecimal("5"), existing.getQuantity());
        Mockito.verify(cartEntryRepository).save(existing);
        Mockito.verify(cartService).reCalculate("cart1");
    }

    @Test
    void updateTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("cart1-prod1");

        CartEntry existing = new CartEntry();
        Mockito.when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(cartEntryRepository.save(existing)).thenReturn(existing);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(cartEntryRepository).save(existing);
    }

    @Test
    void updateFailureTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("cart1-prod1");
        Mockito.when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(null);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("cart1-prod1");
        entry.setCartIdentifier("cart1");

        Mockito.when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(entry);
        Mockito.doNothing().when(cartEntryRepository).deleteByIdentifier("cart1-prod1");
        cartEntryService.delete("cart1-prod1");
        Mockito.verify(cartEntryRepository).deleteByIdentifier("cart1-prod1");
        Mockito.verify(cartService).reCalculate("cart1");
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(null);
        cartEntryService.delete("cart1-prod1");
        Mockito.verify(cartEntryRepository, Mockito.never()).deleteByIdentifier(Mockito.any());
        Mockito.verify(cartService, Mockito.never()).reCalculate(Mockito.any());
    }

    @Test
    void findAllTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("cart1-prod1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("cart1-prod1");

        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CartEntry> page = new PageImpl<>(entries, pageable, entries.size());

        Mockito.when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<CartEntryDto> response = cartEntryService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByCartIdentifierTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("cart1-prod1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("cart1-prod1");

        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);

        Mockito.when(cartEntryRepository.findByCartIdentifier("cart1")).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<CartEntryDto> response = cartEntryService.findByCartIdentifier("cart1");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("cart1-prod1", response.get(0).getIdentifier());
    }
}