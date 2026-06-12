package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.*;

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
    private CartEntry cartEntry;
    private CartEntryDto cartEntryDto;

    @BeforeEach
    void setup() {
        cartEntry = new CartEntry();
        cartEntry.setIdentifier("cart1-prod1");
        cartEntry.setProductIdentifier("prod1");
        cartEntry.setCartIdentifier("cart1");
        cartEntry.setQuantity(BigDecimal.valueOf(2));
        cartEntryDto = new CartEntryDto();
        cartEntryDto.setIdentifier("cart1-prod1");
        cartEntryDto.setCartIdentifier("cart1");
        cartEntryDto.setProductIdentifier("prod1");
        cartEntryDto.setQuantity(BigDecimal.valueOf(2));
    }

    @Test
    void save_shouldCreateNewEntry() {
        when(cartEntryRepository.findByIdentifier(anyString())).thenReturn(null);
        Price selling = new Price();
        selling.setSumPrice(BigDecimal.valueOf(100));
        Price mrp = new Price();
        mrp.setSumPrice(BigDecimal.valueOf(120));
        when(priceRepository.findByProductAndPriceType("prod1", "sellingPrice")).thenReturn(selling);
        when(priceRepository.findByProductAndPriceType("prod1", "Mrp")).thenReturn(mrp);
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(cartEntryDto);
        CartEntryDto result = cartEntryService.save(cartEntryDto);
        assertNotNull(result);
        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    void save_shouldUpdateExistingEntry() {
        when(cartEntryRepository.findByIdentifier(anyString())).thenReturn(cartEntry);
        Price selling = new Price();
        selling.setSumPrice(BigDecimal.valueOf(100));
        Price mrp = new Price();
        mrp.setSumPrice(BigDecimal.valueOf(120));
        when(priceRepository.findByProductAndPriceType("prod1", "sellingPrice")).thenReturn(selling);
        when(priceRepository.findByProductAndPriceType("prod1", "Mrp")).thenReturn(mrp);
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(cartEntryDto);
        CartEntryDto result = cartEntryService.save(cartEntryDto);
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(4), cartEntry.getQuantity());
        verify(cartEntryRepository).save(cartEntry);
    }

    @Test
    void update_shouldFail_ifNotFound() {
        when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(null);
        CartEntryDto result = cartEntryService.update(cartEntryDto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void update_shouldUpdate_ifExists() {
        when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(cartEntry);
        CartEntryDto result = cartEntryService.update(cartEntryDto);
        verify(modelMapper).map(cartEntryDto, cartEntry);
        verify(cartEntryRepository).save(cartEntry);
        assertNotNull(result);
    }

    @Test
    void delete_shouldCallRepository() {
        cartEntryService.delete("cart1-prod1");
        verify(cartEntryRepository).deleteByIdentifier("cart1-prod1");
    }

    @Test
    void findAll_shouldReturnList() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<CartEntry> page = new PageImpl<>(List.of(cartEntry));
        when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        doReturn(List.of(cartEntryDto))
                .when(modelMapper)
                .map(anyList(), any(Type.class));
        List<CartEntryDto> result = cartEntryService.findAll(pageable);
        assertEquals(1, result.size());
    }

    @Test
    void findByIdentifier_shouldReturnDto() {
        when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(cartEntry);
        when(modelMapper.map(cartEntry, CartEntryDto.class)).thenReturn(cartEntryDto);
        CartEntryDto result = cartEntryService.findByIdentifier("cart1-prod1");
        assertNotNull(result);
    }

    @Test
    void findAllCarts_shouldReturnList() {
        when(cartEntryRepository.findByCartIdentifier("cart1"))
                .thenReturn(List.of(cartEntry));
        doReturn(List.of(cartEntryDto))
                .when(modelMapper)
                .map(anyList(), any(Type.class));
        List<CartEntryDto> result = cartEntryService.findAllCarts("cart1");
        assertEquals(1, result.size());
    }
}