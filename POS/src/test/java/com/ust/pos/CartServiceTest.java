package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl service;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccess() {
        Cart cart = new Cart();
        CartDto dto = new CartDto();

        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);

        assertNotNull(service.findByIdentifier("C1"));
    }

    @Test
    void findByIdentifierNull() {
        when(cartRepository.findByIdentifier("C1")).thenReturn(null);

        assertNull(service.findByIdentifier("C1"));
    }

    @Test
    void getAllCartEntriesByCartIdTest() {
        List<CartEntry> list = List.of(new CartEntry());

        when(cartEntryRepository.findByCartId("C1")).thenReturn(list);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CartEntryDto()));

        List<CartEntryDto> result = service.getAllCartEntriesByCartId("C1");

        assertEquals(1, result.size());
    }

    @Test
    void findTotalPriceSingleTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setTotalPrice(new BigDecimal("100"));

        BigDecimal result = service.findTotalPrice(List.of(dto));

        assertEquals(new BigDecimal("100"), result);
    }

    @Test
    void findTotalPriceMultipleTest() {
        CartEntryDto d1 = new CartEntryDto();
        d1.setTotalPrice(new BigDecimal("100"));

        CartEntryDto d2 = new CartEntryDto();
        d2.setTotalPrice(new BigDecimal("50"));

        BigDecimal result = service.findTotalPrice(List.of(d1, d2));

        assertEquals(new BigDecimal("150"), result);
    }

    @Test
    void getDiscountSingleTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setDiscount(new BigDecimal("20"));

        BigDecimal result = service.getDiscount(List.of(dto));

        assertEquals(new BigDecimal("20"), result);
    }

    @Test
    void getDiscountMultipleTest() {
        CartEntryDto d1 = new CartEntryDto();
        d1.setDiscount(new BigDecimal("20"));

        CartEntryDto d2 = new CartEntryDto();
        d2.setDiscount(new BigDecimal("10"));

        BigDecimal result = service.getDiscount(List.of(d1, d2));

        assertEquals(new BigDecimal("30"), result);
    }

    @Test
    void saveSuccessTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        CartEntryDto entryDto = new CartEntryDto();
        entryDto.setTotalPrice(new BigDecimal("100"));
        entryDto.setDiscount(new BigDecimal("10"));

        when(cartRepository.findByIdentifier("C1")).thenReturn(null);
        when(cartEntryRepository.findByCartId("C1"))
                .thenReturn(List.of(new CartEntry()));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(entryDto));
        when(modelMapper.map(any(), eq(Cart.class)))
                .thenReturn(new Cart());

        CartDto result = service.save(cartDto);

        assertEquals(new BigDecimal("100"), result.getTotalPrice());
        assertEquals(new BigDecimal("10"), result.getDiscount());
        verify(cartRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        Cart existing = new Cart();
        existing.setDeleted(false);

        when(cartRepository.findByIdentifier("C1")).thenReturn(existing);

        CartDto result = service.save(cartDto);

        assertTrue(result.getMessage().contains("already exists"));
        verify(cartRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        Cart existing = new Cart();
        existing.setDeleted(true);

        when(cartRepository.findByIdentifier("C1")).thenReturn(existing);

        CartDto result = service.save(cartDto);

        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void deleteTest() {
        service.delete("C1");

        verify(cartRepository).deleteByIdentifier("C1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Cart> carts = List.of(new Cart());
        Page<Cart> page = new PageImpl<>(carts);

        when(cartRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CartDto()));

        WsDto<CartDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());

        verify(cartRepository).findAll(pageable);
    }
}