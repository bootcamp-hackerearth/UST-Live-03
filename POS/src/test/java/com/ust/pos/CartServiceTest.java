package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.*;
import com.ust.pos.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

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
    void findTotalPriceTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setTotalPrice(new BigDecimal("100"));

        BigDecimal result = service.findTotalPrice(List.of(dto));

        assertEquals(new BigDecimal("100"), result);
    }

    @Test
    void getDiscountTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setDiscount(new BigDecimal("20"));

        BigDecimal result = service.getDiscount(List.of(dto));

        assertEquals(new BigDecimal("20"), result);
    }

    @Test
    void saveTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        CartEntryDto entryDto = new CartEntryDto();
        entryDto.setTotalPrice(new BigDecimal("100"));
        entryDto.setDiscount(new BigDecimal("10"));

        when(cartEntryRepository.findByCartId("C1"))
                .thenReturn(List.of(new CartEntry()));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(entryDto));
        when(modelMapper.map(any(), eq(Cart.class)))
                .thenReturn(new Cart());

        CartDto result = service.save(cartDto);

        assertNotNull(result);
        verify(cartRepository).save(any());
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

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        verify(cartRepository).findAll(pageable);
    }
}