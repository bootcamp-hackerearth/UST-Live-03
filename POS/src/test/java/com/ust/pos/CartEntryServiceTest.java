package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
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
    private PriceRepository priceRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CartService cartService;

    @Test
    void saveNewCartEntryTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("C1");
        dto.setProductIdentifier("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        Price sellingPrice = new Price();
        sellingPrice.setPriceAmount(BigDecimal.valueOf(80));

        Price mrp = new Price();
        mrp.setPriceAmount(BigDecimal.valueOf(100));

        CartEntryDto responseDto = new CartEntryDto();
        responseDto.setIdentifier("C1-P1");
        responseDto.setCartIdentifier("C1");
        responseDto.setProductIdentifier("P1");
        responseDto.setQuantity(BigDecimal.valueOf(2));

        Mockito.when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        Mockito.when(priceRepository.findByProductIdentifierAndPriceType("P1", "sellingPrice")).thenReturn(sellingPrice);
        Mockito.when(priceRepository.findByProductIdentifierAndPriceType("P1", "Mrp")).thenReturn(mrp);
        Mockito.when(modelMapper.map(Mockito.any(CartEntry.class), Mockito.eq(CartEntryDto.class))).thenReturn(responseDto);

        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("C1-P1", response.getIdentifier());
        Assertions.assertEquals("C1", response.getCartIdentifier());
        Assertions.assertEquals("P1", response.getProductIdentifier());

        Mockito.verify(cartEntryRepository).save(Mockito.any(CartEntry.class));
        Mockito.verify(cartService).recalculate("C1");
    }

    @Test
    void saveExistingCartEntryTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("C1");
        dto.setProductIdentifier("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry existing = new CartEntry();
        existing.setIdentifier("C1-P1");
        existing.setCartIdentifier("C1");
        existing.setProductIdentifier("P1");
        existing.setQuantity(BigDecimal.valueOf(3));

        Price sellingPrice = new Price();
        sellingPrice.setPriceAmount(BigDecimal.valueOf(80));

        Price mrp = new Price();
        mrp.setPriceAmount(BigDecimal.valueOf(100));

        CartEntryDto responseDto = new CartEntryDto();
        responseDto.setIdentifier("C1-P1");
        responseDto.setQuantity(BigDecimal.valueOf(5));

        Mockito.when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(existing);
        Mockito.when(priceRepository.findByProductIdentifierAndPriceType("P1", "sellingPrice")).thenReturn(sellingPrice);
        Mockito.when(priceRepository.findByProductIdentifierAndPriceType("P1", "Mrp")).thenReturn(mrp);
        Mockito.when(modelMapper.map(Mockito.any(CartEntry.class), Mockito.eq(CartEntryDto.class))).thenReturn(responseDto);

        CartEntryDto response = cartEntryService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(BigDecimal.valueOf(5), existing.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(500), existing.getOriginalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(100), existing.getDiscount());
        Assertions.assertEquals(BigDecimal.valueOf(400), existing.getTotalPrice());

        Mockito.verify(cartEntryRepository).save(existing);
        Mockito.verify(cartService).recalculate("C1");
    }

    @Test
    void deleteTest() {
        cartEntryService.delete("C1-P1");
        Mockito.verify(cartEntryRepository).deleteByIdentifier("C1-P1");
    }

    @Test
    void findAllEntriesForCartTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("C1-P1");
        cartEntry.setCartIdentifier("C1");
        cartEntry.setProductIdentifier("P1");

        List<CartEntry> cartEntries = List.of(cartEntry);
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1-P1");
        dto.setCartIdentifier("C1");
        dto.setProductIdentifier("P1");

        List<CartEntryDto> dtos = List.of(dto);
        Mockito.when(cartEntryRepository.findByCartIdentifier("C1")).thenReturn(cartEntries);
        Mockito.when(modelMapper.map(Mockito.eq(cartEntries), Mockito.any(Type.class))).thenReturn(dtos);

        List<CartEntryDto> response = cartEntryService.findAllEntriesForCart("C1");
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("C1-P1", response.get(0).getIdentifier());
        Assertions.assertEquals("C1", response.get(0).getCartIdentifier());
        Mockito.verify(cartEntryRepository).findByCartIdentifier("C1");
    }

    @Test
    void deleteAllByCartIdentifierTest() {
        cartEntryService.deleteAllByCartIdentifier("C1");
        Mockito.verify(cartEntryRepository).deleteAllByCartIdentifier("C1");
    }

}