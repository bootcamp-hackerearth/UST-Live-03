package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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

    @Test
    @DisplayName("Save CartEntry - New Record Entry Creation")
    void saveTest_NewCartEntry() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("PROD1");
        dto.setCart("CART1");
        dto.setQuantity(new BigDecimal("2"));

        Price price = new Price();
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));

        String expectedIdentifier = "PROD1-CART1";

        Mockito.when(cartEntryRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);
        Mockito.when(priceRepository.findByIdentifier("PROD1")).thenReturn(price);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedIdentifier, response.getIdentifier());
        Assertions.assertEquals(new BigDecimal("2"), response.getQuantity());
        Assertions.assertEquals(new BigDecimal("160.00"), response.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("40.00"), response.getDiscount());
        Assertions.assertEquals(new BigDecimal("100.00"), response.getPrice());
        Assertions.assertEquals(new BigDecimal("80.00"), response.getSellingPrice());

        Mockito.verify(modelMapper).map(eq(dto), any(CartEntry.class));
        Mockito.verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    @DisplayName("Save CartEntry - Update Existing Record Quantity")
    void saveTest_ExistingCartEntry() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("PROD1");
        dto.setCart("CART1");
        dto.setQuantity(new BigDecimal("3"));

        CartEntry existingCartEntry = new CartEntry();
        existingCartEntry.setQuantity(new BigDecimal("2"));

        Price price = new Price();
        price.setMrp(new BigDecimal("50.00"));
        price.setSellingPrice(new BigDecimal("40.00"));

        String expectedIdentifier = "PROD1-CART1";

        Mockito.when(cartEntryRepository.findByIdentifier(expectedIdentifier)).thenReturn(existingCartEntry);
        Mockito.when(priceRepository.findByIdentifier("PROD1")).thenReturn(price);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(new BigDecimal("5"), response.getQuantity());
        Assertions.assertEquals(new BigDecimal("200.00"), response.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("50.00"), response.getDiscount());

        Mockito.verify(modelMapper).map(dto, existingCartEntry);
        Mockito.verify(cartEntryRepository).save(existingCartEntry);
    }

    @Test
    @DisplayName("Find All Entries For Cart - Success")
    void findAllEntriesForCartTest() {
        String cartId = "CART1";
        List<CartEntry> records = List.of(new CartEntry());
        List<CartEntryDto> expectedDtos = List.of(new CartEntryDto());

        Mockito.when(cartEntryRepository.findByCart(cartId)).thenReturn(records);
        Mockito.when(modelMapper.map(eq(records), any(Type.class))).thenReturn(expectedDtos);

        List<CartEntryDto> response = cartEntryService.findAllEntriesForCart(cartId);

        Assertions.assertEquals(expectedDtos, response);
    }

    @Test
    @DisplayName("Delete By Identifier - Success")
    void deleteByIdentifierTest_Success() {
        String identifier = "PROD1-CART1";
        CartEntry record = new CartEntry();

        Mockito.when(cartEntryRepository.findByIdentifier(identifier)).thenReturn(record);

        cartEntryService.deleteByIdentifier(identifier);

        Mockito.verify(cartEntryRepository).deleteByIdentifier(identifier);
    }

    @Test
    @DisplayName("Delete By Identifier - Failure: Not Found Exception")
    void deleteByIdentifierTest_NotFound() {
        String identifier = "MISSING-CART";

        Mockito.when(cartEntryRepository.findByIdentifier(identifier)).thenReturn(null);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            cartEntryService.deleteByIdentifier(identifier);
        });

        Assertions.assertEquals("CartEntry not found", exception.getMessage());
        Mockito.verify(cartEntryRepository, Mockito.never()).deleteByIdentifier(any());
    }

    @Test
    @DisplayName("Delete All By Cart - Success with Records")
    void deleteAllByCartTest_WithRecords() {
        String cartId = "CART1";
        List<CartEntry> records = List.of(new CartEntry(), new CartEntry());

        Mockito.when(cartEntryRepository.findByCart(cartId)).thenReturn(records);

        cartEntryService.deleteAllByCart(cartId);

        Mockito.verify(cartEntryRepository).deleteAll(records);
    }

    @Test
    @DisplayName("Delete All By Cart - Success with No Records")
    void deleteAllByCartTest_Empty() {
        String cartId = "CART_EMPTY";

        Mockito.when(cartEntryRepository.findByCart(cartId)).thenReturn(Collections.emptyList());

        cartEntryService.deleteAllByCart(cartId);

        Mockito.verify(cartEntryRepository, Mockito.never()).deleteAll(any());
    }
}