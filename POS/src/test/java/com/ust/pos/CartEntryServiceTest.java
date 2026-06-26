package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
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

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private CartService cartService;

    @Mock
    private PriceService priceService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART001");
        dto.setProductId("PROD001");
        dto.setQuantity(BigDecimal.valueOf(2));
        PriceDto mrpDto = new PriceDto();
        mrpDto.setAmount(100L);
        PriceDto spDto = new PriceDto();
        spDto.setAmount(90L);
        CartEntry savedEntry = new CartEntry();
        savedEntry.setIdentifier("CART001_PROD001");
        CartEntryDto responseDto = new CartEntryDto();
        responseDto.setIdentifier("CART001_PROD001");
        Mockito.when(cartEntryRepository.findByIdentifier("CART001_PROD001")).thenReturn(null);
        Mockito.when(priceService.findByIdentifier("PROD001_MRP")).thenReturn(mrpDto);
        Mockito.when(priceService.findByIdentifier("PROD001_Selling_Price")).thenReturn(spDto);
        Mockito.when(cartEntryRepository.save(Mockito.any(CartEntry.class))).thenReturn(savedEntry);
        Mockito.when(modelMapper.map(savedEntry, CartEntryDto.class)).thenReturn(responseDto);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Cart entry saved successfully", response.getMessage());
        Mockito.verify(cartService).save("CART001");
        Mockito.verify(cartService).recalculate("CART001");
    }

    @Test
    void saveInvalidQuantityTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setQuantity(BigDecimal.ZERO);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Quantity must be greater than 0", response.getMessage());
    }

    @Test
    void savePriceNotConfiguredTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART001");
        dto.setProductId("PROD001");
        dto.setQuantity(BigDecimal.ONE);
        Mockito.when(cartEntryRepository.findByIdentifier("CART001_PROD001")).thenReturn(null);
        Mockito.when(priceService.findByIdentifier("PROD001_MRP")).thenReturn(null);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price not configured for product: PROD001", response.getMessage());
    }

    @Test
    void updateSuccessTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("ENTRY001");
        entry.setCartId("CART001");
        entry.setQuantity(BigDecimal.ONE);
        entry.setMrp(BigDecimal.valueOf(100));
        entry.setSellingPrice(BigDecimal.valueOf(90));
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY001");
        dto.setQuantity(BigDecimal.valueOf(3));
        CartEntryDto mappedDto = new CartEntryDto();
        Mockito.when(cartEntryRepository.findByIdentifier("ENTRY001")).thenReturn(entry);
        Mockito.when(cartEntryRepository.save(entry)).thenReturn(entry);
        Mockito.when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(mappedDto);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Cart entry updated successfully", response.getMessage());
        Mockito.verify(cartService).recalculate("CART001");
    }

    @Test
    void updateEntryNotFoundTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY001");
        Mockito.when(cartEntryRepository.findByIdentifier("ENTRY001")).thenReturn(null);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart entry not found", response.getMessage());
    }

    @Test
    void updateInvalidQuantityTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("ENTRY001");
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY001");
        dto.setQuantity(BigDecimal.ZERO);
        Mockito.when(cartEntryRepository.findByIdentifier("ENTRY001")).thenReturn(entry);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Invalid quantity", response.getMessage());
    }

    @Test
    void findByIdentifierSuccessTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("ENTRY001");
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY001");
        Mockito.when(cartEntryRepository.findByIdentifier("ENTRY001")).thenReturn(entry);
        Mockito.when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(dto);
        CartEntryDto response = cartEntryService.findByIdentifier("ENTRY001");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("ENTRY001", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(cartEntryRepository.findByIdentifier("ENTRY001")).thenReturn(null);
        CartEntryDto response = cartEntryService.findByIdentifier("ENTRY001");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart entry not found", response.getMessage());
    }

    @Test
    void findAllTest() {
        CartEntry entry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();
        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<CartEntry> page = new PageImpl<>(entries);
        Mockito.when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(dtos);
        List<CartEntryDto> response = cartEntryService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByCartIdTest() {
        CartEntry entry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();
        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);
        Mockito.when(cartEntryRepository.findByCartId("CART001")).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(dtos);
        List<CartEntryDto> response = cartEntryService.findByCartId("CART001");
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteExistingEntryTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("ENTRY001");
        entry.setCartId("CART001");
        Mockito.when(cartEntryRepository.findByIdentifier("ENTRY001")).thenReturn(entry);
        cartEntryService.delete("ENTRY001");
        Mockito.verify(cartEntryRepository).deleteByIdentifier("ENTRY001");
        Mockito.verify(cartService).recalculate("CART001");
    }

    @Test
    void deleteNonExistingEntryTest() {
        Mockito.when(cartEntryRepository.findByIdentifier("ENTRY001")).thenReturn(null);
        cartEntryService.delete("ENTRY001");
        Mockito.verify(cartEntryRepository, Mockito.never()).deleteByIdentifier(Mockito.anyString());
        Mockito.verify(cartService, Mockito.never()).recalculate(Mockito.anyString());
    }
}