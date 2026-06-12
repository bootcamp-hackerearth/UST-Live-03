package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl service;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private CartService cartService;

    @Mock
    private PriceService priceService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_success() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProductId("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry entry = new CartEntry();
        entry.setCartId("C1");
        entry.setProductId("P1");
        entry.setQuantity(BigDecimal.valueOf(1));

        PriceDto mrp = new PriceDto();
        mrp.setValue(BigDecimal.valueOf(100));

        PriceDto sp = new PriceDto();
        sp.setValue(BigDecimal.valueOf(80));

        CartEntry saved = new CartEntry();
        saved.setIdentifier("C1_P1");

        CartEntryDto responseDto = new CartEntryDto();
        responseDto.setSuccess(true);
        responseDto.setMessage("Cart entry saved successfully");

        when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(entry);

        when(priceService.findByIdentifier("P1_MRP"))
                .thenReturn(mrp);

        when(priceService.findByIdentifier("P1_Selling_Price"))
                .thenReturn(sp);

        when(cartEntryRepository.save(any(CartEntry.class)))
                .thenReturn(saved);

        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(responseDto);

        CartEntryDto result = service.save(dto);

        assertTrue(result.isSuccess());
        assertEquals("Cart entry saved successfully", result.getMessage());

        verify(cartService).recalculate("C1");
    }

    @Test
    void save_failure_quantity_zero() {

        CartEntryDto dto = new CartEntryDto();
        dto.setQuantity(BigDecimal.ZERO);

        CartEntryDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Quantity must be greater than 0", result.getMessage());
    }

    @Test
    void save_failure_price_missing() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProductId("P1");
        dto.setQuantity(BigDecimal.valueOf(1));

        when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(null);

        when(priceService.findByIdentifier("P1_MRP"))
                .thenReturn(null);

        CartEntryDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Price not configured for product: P1", result.getMessage());
    }

    @Test
    void update_success() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");
        dto.setQuantity(BigDecimal.valueOf(3));

        CartEntry entry = new CartEntry();
        entry.setCartId("C1");
        entry.setMrp(BigDecimal.valueOf(100));
        entry.setSellingPrice(BigDecimal.valueOf(80));

        CartEntry saved = new CartEntry();

        CartEntryDto responseDto = new CartEntryDto();
        responseDto.setSuccess(true);
        responseDto.setMessage("Cart entry updated successfully");

        when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(entry);

        when(cartEntryRepository.save(any(CartEntry.class)))
                .thenReturn(saved);

        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(responseDto);

        CartEntryDto result = service.update(dto);

        assertTrue(result.isSuccess());
        assertEquals("Cart entry updated successfully", result.getMessage());

        verify(cartService).recalculate("C1");
    }

    @Test
    void update_failure_not_found() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");
        dto.setQuantity(BigDecimal.valueOf(1));

        when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(null);

        CartEntryDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertEquals("Cart entry not found", result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        CartEntry entry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(entry);

        when(modelMapper.map(entry, CartEntryDto.class))
                .thenReturn(dto);

        CartEntryDto result = service.findByIdentifier("C1_P1");

        assertTrue(result.isSuccess());
    }

    @Test
    void findByIdentifier_failure() {

        when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(null);

        CartEntryDto result = service.findByIdentifier("C1_P1");

        assertFalse(result.isSuccess());
        assertEquals("Cart entry not found", result.getMessage());
    }

    @Test
    void findAll_success() {

        CartEntry entry = new CartEntry();
        Page<CartEntry> page = new PageImpl<>(List.of(entry));
        Pageable pageable = PageRequest.of(0, 5);

        when(cartEntryRepository.findAll(pageable))
                .thenReturn(page);

        Type type = new TypeToken<List<CartEntryDto>>() {}.getType();

        when(modelMapper.map(anyList(), eq(type)))
                .thenReturn(List.of(new CartEntryDto()));

        List<CartEntryDto> result = service.findAll(pageable);

        assertEquals(1, result.size());
    }

    @Test
    void findByCartId_success() {

        CartEntry entry = new CartEntry();

        Type type = new TypeToken<List<CartEntryDto>>() {}.getType();

        when(cartEntryRepository.findByCartId("C1"))
                .thenReturn(List.of(entry));

        when(modelMapper.map(anyList(), eq(type)))
                .thenReturn(List.of(new CartEntryDto()));

        List<CartEntryDto> result = service.findByCartId("C1");

        assertEquals(1, result.size());
    }

    @Test
    void delete_success() {

        CartEntry entry = new CartEntry();
        entry.setCartId("C1");

        when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(entry);

        service.delete("C1_P1");

        verify(cartEntryRepository).deleteByIdentifier("C1_P1");
        verify(cartService).recalculate("C1");
    }
}