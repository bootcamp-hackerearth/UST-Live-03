package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
    void saveTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("cart1");
        dto.setProductIdentifier("prod1");
        dto.setQuantity(BigDecimal.valueOf(2));
        Mockito.when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(null);
        Price selling = new Price();
        selling.setSumPrice(BigDecimal.valueOf(100));
        Price mrp = new Price();
        mrp.setSumPrice(BigDecimal.valueOf(120));
        Mockito.when(priceRepository.findByProductAndPriceType("prod1", "sellingPrice")).thenReturn(selling);
        Mockito.when(priceRepository.findByProductAndPriceType("prod1", "Mrp")).thenReturn(mrp);
        Mockito.when(modelMapper.map(Mockito.any(CartEntry.class), Mockito.eq(CartEntryDto.class))).thenReturn(dto);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(cartEntryRepository).save(Mockito.any());
        Mockito.verify(cartService).recalculate("cart1");
    }

    @Test
    void saveExistingTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("cart1");
        dto.setProductIdentifier("prod1");
        dto.setQuantity(BigDecimal.valueOf(2));
        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.valueOf(2));
        Mockito.when(cartEntryRepository.findByIdentifier("cart1-prod1")).thenReturn(existing);
        Price selling = new Price();
        selling.setSumPrice(BigDecimal.valueOf(100));
        Price mrp = new Price();
        mrp.setSumPrice(BigDecimal.valueOf(120));
        Mockito.when(priceRepository.findByProductAndPriceType("prod1", "sellingPrice")).thenReturn(selling);
        Mockito.when(priceRepository.findByProductAndPriceType("prod1", "Mrp")).thenReturn(mrp);
        Mockito.when(modelMapper.map(Mockito.any(CartEntry.class), Mockito.eq(CartEntryDto.class))).thenReturn(dto);
        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(BigDecimal.valueOf(4), existing.getQuantity());
    }

    @Test
    void updateTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("id1");
        CartEntry existing = new CartEntry();
        Mockito.when(cartEntryRepository.findByIdentifier("id1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(cartEntryRepository.save(existing)).thenReturn(existing);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertNotNull(response);
    }

    @Test
    void updateFailureTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("id1");
        Mockito.when(cartEntryRepository.findByIdentifier("id1")).thenReturn(null);
        CartEntryDto response = cartEntryService.update(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        cartEntryService.delete("id1");
        Mockito.verify(cartEntryRepository).deleteByIdentifier("id1");
    }

    @Test
    void findAllTest() {
        CartEntry entity = new CartEntry();
        CartEntryDto dto = new CartEntryDto();
        List<CartEntry> list = List.of(entity);
        List<CartEntryDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<CartEntry> page = new PageImpl<>(list);
        Mockito.when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        List<CartEntryDto> response = cartEntryService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByIdentifierTest() {
        CartEntry entity = new CartEntry();
        CartEntryDto dto = new CartEntryDto();
        Mockito.when(cartEntryRepository.findByIdentifier("id1")).thenReturn(entity);
        Mockito.when(modelMapper.map(entity, CartEntryDto.class)).thenReturn(dto);
        CartEntryDto response = cartEntryService.findByIdentifier("id1");
        Assertions.assertNotNull(response);
    }

    @Test
    void findAllCartsTest() {
        CartEntry entity = new CartEntry();
        CartEntryDto dto = new CartEntryDto();
        List<CartEntry> list = List.of(entity);
        List<CartEntryDto> dtoList = List.of(dto);
        Mockito.when(cartEntryRepository.findByCartIdentifier("cart1")).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        List<CartEntryDto> response = cartEntryService.findAllCarts("cart1");
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(cartEntryRepository.findByIdentifier("id1"))
                .thenReturn(null);
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> cartEntryService.findByIdentifier("id1")
        );
    }
}