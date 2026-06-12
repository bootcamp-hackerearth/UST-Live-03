package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.*;
import com.ust.pos.modell.*;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl service;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Mock
    private CartService cartService;

    private CartEntryDto dto;
    private CartEntry entry;

    @BeforeEach
    void setUp() {
        dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("PROD1");
        dto.setQuantity(2);
        entry = new CartEntry();
        entry.setIdentifier("CART1-PROD1");
        entry.setProductIdentifier("PROD1");
        entry.setCartIdentifier("CART1");
        entry.setQuantity(1);
    }

    @Test
    void save_shouldCreateNewEntry() {
        when(cartEntryRepository.findByIdentifier("CART1-PROD1"))
                .thenReturn(null);
        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier(anyString()))
                .thenReturn(price);
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(new CartEntryDto());
        CartEntryDto result = service.save(dto);
        verify(cartEntryRepository).save(any(CartEntry.class));
        assertNotNull(result);
    }

    @Test
    void save_shouldUpdateExistingEntry() {
        when(cartEntryRepository.findByIdentifier("CART1-PROD1"))
                .thenReturn(entry);
        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier(anyString()))
                .thenReturn(price);
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(new CartEntryDto());
        service.save(dto);
        verify(cartEntryRepository).save(any(CartEntry.class));
        assertEquals(3, entry.getQuantity()); // 1 + 2
    }

    @Test
    void save_shouldThrowIfProductMissing() {
        dto.setProductIdentifier(null);
        assertThrows(RuntimeException.class,
                () -> service.save(dto));
    }

    @Test
    void save_shouldThrowIfPriceMissing() {
        when(priceService.findByIdentifier(anyString()))
                .thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> service.save(dto));
    }

    @Test
    void save_shouldCalculateDiscount() {
        when(cartEntryRepository.findByIdentifier("CART1-PROD1"))
                .thenReturn(null);
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(80));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier("PROD1-SELLING"))
                .thenReturn(selling);
        when(priceService.findByIdentifier("PROD1-MRP"))
                .thenReturn(mrp);
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(new CartEntryDto());
        service.save(dto);
        ArgumentCaptor<CartEntry> captor =
                ArgumentCaptor.forClass(CartEntry.class);
        verify(cartEntryRepository).save(captor.capture());
        CartEntry saved = captor.getValue();
        assertEquals(BigDecimal.valueOf(80), saved.getUnitPrice());
        assertEquals(BigDecimal.valueOf(20), saved.getDiscount());
    }

    @Test
    void findByIdentifier_shouldReturnDto() {
        when(cartEntryRepository.findByIdentifier("ID"))
                .thenReturn(entry);
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(new CartEntryDto());
        CartEntryDto result = service.findByIdentifier("ID");
        assertNotNull(result);
    }

    @Test
    void findAll_shouldReturnPagedResult() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<CartEntry> page = new PageImpl<>(List.of(entry));
        when(cartEntryRepository.findAll(pageable))
                .thenReturn(page);
        List<CartEntryDto> dtoList = List.of(new CartEntryDto());
        Type listType = new TypeToken<List<CartEntryDto>>() {}.getType();
        when(modelMapper.map(page.getContent(), listType))
                .thenReturn(dtoList);
        WsDto<CartEntryDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }
}
