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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CartEntryServiceTest {

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
    void saveTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART-001");
        dto.setProductId("PROD-001");
        dto.setQuantity(new BigDecimal("2"));

        Mockito.when(
                cartEntryRepository.findByIdentifier("CART-001_PROD-001")
        ).thenReturn(null);

        PriceDto mrp = new PriceDto();
        mrp.setAmount(100L);

        PriceDto sp = new PriceDto();
        sp.setAmount(90L);

        Mockito.when(
                priceService.findByIdentifier("PROD-001_MRP")
        ).thenReturn(mrp);

        Mockito.when(
                priceService.findByIdentifier("PROD-001_Selling_Price")
        ).thenReturn(sp);

        CartEntry saved = new CartEntry();
        saved.setIdentifier("CART-001_PROD-001");

        Mockito.when(
                cartEntryRepository.save(Mockito.any(CartEntry.class))
        ).thenReturn(saved);

        CartEntryDto responseDto = new CartEntryDto();

        Mockito.when(
                modelMapper.map(saved, CartEntryDto.class)
        ).thenReturn(responseDto);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Cart entry saved successfully",
                response.getMessage()
        );
    }

    @Test
    void saveInvalidQuantityTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setQuantity(BigDecimal.ZERO);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Quantity must be greater than 0",
                response.getMessage()
        );
    }

    @Test
    void savePriceNotConfiguredTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART-001");
        dto.setProductId("PROD-001");
        dto.setQuantity(BigDecimal.ONE);

        Mockito.when(
                cartEntryRepository.findByIdentifier("CART-001_PROD-001")
        ).thenReturn(null);

        Mockito.when(
                priceService.findByIdentifier("PROD-001_MRP")
        ).thenReturn(null);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Price not configured for product: PROD-001",
                response.getMessage()
        );
    }

    @Test
    void updateTest() {

        CartEntry entry = new CartEntry();
        entry.setIdentifier("CART-001_PROD-001");
        entry.setCartId("CART-001");
        entry.setMrp(new BigDecimal("100"));
        entry.setSellingPrice(new BigDecimal("90"));

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("CART-001_PROD-001");
        dto.setQuantity(new BigDecimal("5"));

        Mockito.when(
                cartEntryRepository.findByIdentifier("CART-001_PROD-001")
        ).thenReturn(entry);

        Mockito.when(
                cartEntryRepository.save(entry)
        ).thenReturn(entry);

        CartEntryDto mappedDto = new CartEntryDto();

        Mockito.when(
                modelMapper.map(entry, CartEntryDto.class)
        ).thenReturn(mappedDto);

        CartEntryDto response = cartEntryService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Cart entry updated successfully",
                response.getMessage()
        );
    }

    @Test
    void updateNotFoundTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("CART-001_PROD-001");

        Mockito.when(
                cartEntryRepository.findByIdentifier("CART-001_PROD-001")
        ).thenReturn(null);

        CartEntryDto response = cartEntryService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Cart entry not found",
                response.getMessage()
        );
    }

    @Test
    void updateInvalidQuantityTest() {

        CartEntry entry = new CartEntry();

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("CART-001_PROD-001");
        dto.setQuantity(BigDecimal.ZERO);

        Mockito.when(
                cartEntryRepository.findByIdentifier("CART-001_PROD-001")
        ).thenReturn(entry);

        CartEntryDto response = cartEntryService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Invalid quantity",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifierTest() {

        CartEntry entry = new CartEntry();
        entry.setIdentifier("CART-001_PROD-001");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("CART-001_PROD-001");

        Mockito.when(
                cartEntryRepository.findByIdentifier("CART-001_PROD-001")
        ).thenReturn(entry);

        Mockito.when(
                modelMapper.map(entry, CartEntryDto.class)
        ).thenReturn(dto);

        CartEntryDto response =
                cartEntryService.findByIdentifier("CART-001_PROD-001");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "CART-001_PROD-001",
                response.getIdentifier()
        );
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(
                cartEntryRepository.findByIdentifier("CART-001_PROD-001")
        ).thenReturn(null);

        CartEntryDto response =
                cartEntryService.findByIdentifier("CART-001_PROD-001");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Cart entry not found",
                response.getMessage()
        );
    }

    @Test
    void findAllTest() {

        CartEntry entry = new CartEntry();
        entry.setIdentifier("CART-001_PROD-001");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("CART-001_PROD-001");

        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);

        Page<CartEntry> page = new PageImpl<>(entries);

        Mockito.when(
                cartEntryRepository.findAll(PageRequest.of(0, 5))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(entries),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findAll(PageRequest.of(0, 5));

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByCartIdTest() {

        CartEntry entry = new CartEntry();

        CartEntryDto dto = new CartEntryDto();

        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);

        Mockito.when(
                cartEntryRepository.findByCartId("CART-001")
        ).thenReturn(entries);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(entries),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findByCartId("CART-001");

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTest() {

        CartEntry entry = new CartEntry();
        entry.setCartId("CART-001");

        Mockito.when(
                cartEntryRepository.findByIdentifier("ENTRY-001")
        ).thenReturn(entry);

        cartEntryService.delete("ENTRY-001");

        Mockito.verify(cartEntryRepository)
                .deleteByIdentifier("ENTRY-001");

        Mockito.verify(cartService)
                .recalculate("CART-001");
    }

    @Test
    void deleteNotFoundTest() {

        Mockito.when(
                cartEntryRepository.findByIdentifier("ENTRY-001")
        ).thenReturn(null);

        cartEntryService.delete("ENTRY-001");

        Mockito.verify(
                cartEntryRepository,
                Mockito.never()
        ).deleteByIdentifier(Mockito.anyString());
    }
}