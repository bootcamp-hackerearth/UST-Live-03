package com.ust.pos;

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
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Test
    void findByIdentifierTest() {
        CartEntry entity = new CartEntry();
        entity.setIdentifier("P001_CART1");
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");
        Mockito.when(cartEntryRepository.findByIdentifier("P001_CART1")).thenReturn(entity);
        Mockito.when(modelMapper.map(entity, CartEntryDto.class)).thenReturn(dto);
        CartEntryDto response = cartEntryService.findByIdentifier("P001_CART1");
        Assertions.assertEquals("P001_CART1", response.getIdentifier());
    }

    @Test
    void findAllWithPageableTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("P001_CART1");
        List<CartEntry> entries = List.of(entry);
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");
        List<CartEntryDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<CartEntry> page = new PageImpl<>(entries);
        Mockito.when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(dtos);
        List<CartEntryDto> response = cartEntryService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithoutPageableTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("P001_CART1");
        List<CartEntry> entries = List.of(entry);
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");
        List<CartEntryDto> dtos = List.of(dto);
        Mockito.when(cartEntryRepository.findAll()).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.eq(entries),Mockito.any(Type.class))).thenReturn(dtos);
        List<CartEntryDto> response = cartEntryService.findAll(null);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void saveTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProductIdentifier("P001");
        dto.setCartIdentifier("CART1");
        dto.setQuantity(BigDecimal.valueOf(2));
        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.ZERO);
        Mockito.when(cartEntryRepository.findByIdentifier("P001_CART1")).thenReturn(existing);
        PriceDto selling = new PriceDto();
        selling.setAmount(80L);
        PriceDto mrp = new PriceDto();
        mrp.setAmount(100L);
        Mockito.when(priceService.findByIdentifier("P001_SELLING_PRICE")).thenReturn(selling);
        Mockito.when(priceService.findByIdentifier("P001_MRP")).thenReturn(mrp);
        Mockito.when(cartEntryRepository.save(Mockito.any())).
                thenAnswer(invocation -> invocation.getArgument(0));
        CartEntryDto mappedDto = new CartEntryDto();
        mappedDto.setIdentifier("P001_CART1");
        Mockito.when(modelMapper.map(Mockito.any(CartEntry.class), Mockito.eq(CartEntryDto.class)))
                .thenReturn(mappedDto);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertEquals("P001_CART1", response.getIdentifier());
    }

    @Test
    void updateSuccessTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");
        CartEntry entity = new CartEntry();
        entity.setIdentifier("P001_CART1");
        Mockito.when(cartEntryRepository.findByIdentifier("P001_CART1")).thenReturn(entity);
        Mockito.when(cartEntryRepository.save(entity)).thenReturn(entity);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertEquals("P001_CART1", response.getIdentifier());
    }

    @Test
    void updateFailureTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");
        Mockito.when(cartEntryRepository.findByIdentifier("P001_CART1")).thenReturn(null);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void deleteTest() {
        cartEntryService.deleteByIdentifier("P001_CART1");
        Mockito.verify(cartEntryRepository).deleteByIdentifier("P001_CART1");
    }

    @Test
    void findByCartTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("P001_CART1");
        List<CartEntry> entries = List.of(entry);
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");
        List<CartEntryDto> dtos = List.of(dto);
        Mockito.when(cartEntryRepository.findByCartIdentifier("CART1")).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(dtos);
        List<CartEntryDto> response = cartEntryService.findByCart("CART1");
        Assertions.assertEquals(1, response.size());
    }
}