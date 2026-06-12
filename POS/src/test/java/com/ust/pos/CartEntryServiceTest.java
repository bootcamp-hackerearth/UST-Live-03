package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Spy
    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Mock
    private CartRepository cartRepository;

    @Test
    void findByIdentifierTest() {
        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("CART1_P1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("CART1_P1");

        Mockito.when(cartEntryRepository.findByIdentifier("CART1_P1"))
                .thenReturn(cartEntry);
        Mockito.when(modelMapper.map(cartEntry, CartEntryDto.class))
                .thenReturn(dto);

        CartEntryDto result = cartEntryService.findByIdentifier("CART1_P1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CART1_P1", result.getIdentifier());
    }

    @Test
    void saveNewCartEntryTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry cartEntry = new CartEntry();

        Mockito.when(cartEntryRepository.findByIdentifier("CART1_P1"))
                .thenReturn(null);

        Mockito.doReturn(BigDecimal.valueOf(100))
                .when(cartEntryService)
                .getSellingPriceAmount("P1");

        Mockito.doReturn(BigDecimal.valueOf(20))
                .when(cartEntryService)
                .getDiscountPriceAmount("P1", BigDecimal.valueOf(2));

        Mockito.doReturn(BigDecimal.valueOf(200))
                .when(cartEntryService)
                .getTotalPrice("P1", BigDecimal.valueOf(2));

        Mockito.when(modelMapper.map(dto, CartEntry.class))
                .thenReturn(cartEntry);

        Mockito.doNothing().when(cartEntryService)
                .recalculate("CART1");

        CartEntryDto result = cartEntryService.save(dto);

        Assertions.assertNotNull(result);

        verify(cartEntryRepository).save(cartEntry);
    }

    @Test
    void saveExistingCartEntryTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.ONE);

        Mockito.when(cartEntryRepository.findByIdentifier("CART1_P1"))
                .thenReturn(existing);

        Mockito.doReturn(BigDecimal.valueOf(100))
                .when(cartEntryService)
                .getSellingPriceAmount("P1");

        Mockito.doReturn(BigDecimal.valueOf(30))
                .when(cartEntryService)
                .getDiscountPriceAmount("P1", BigDecimal.valueOf(3));

        Mockito.doReturn(BigDecimal.valueOf(300))
                .when(cartEntryService)
                .getTotalPrice("P1", BigDecimal.valueOf(3));

        Mockito.doNothing().when(cartEntryService)
                .recalculate("CART1");

        CartEntryDto result = cartEntryService.save(dto);

        Assertions.assertNotNull(result);

        verify(modelMapper).map(dto, existing);
        verify(cartEntryRepository).save(existing);
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(cartEntryService)
                .recalculate("CART1");

        cartEntryService.delete("CART1", "P1");

        verify(cartEntryRepository)
                .deleteByCartIdAndProduct("CART1", "P1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = Mockito.mock(Pageable.class);
        Page<CartEntry> page = Mockito.mock(Page.class);

        List<CartEntry> entries = List.of(
                new CartEntry(),
                new CartEntry()
        );

        List<CartEntryDto> dtoList = List.of(
                new CartEntryDto(),
                new CartEntryDto()
        );

        Mockito.when(cartEntryRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(page.getContent())
                .thenReturn(entries);

        Mockito.when(modelMapper.map(
                Mockito.eq(entries),
                Mockito.any(Type.class)
        )).thenReturn(dtoList);

        List<CartEntryDto> result = cartEntryService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void getSellingPriceAmountTest() {

        PriceDto priceDto = new PriceDto();
        priceDto.setPriceAmount(BigDecimal.valueOf(100));

        Mockito.when(
                        priceService.findByProductAndPriceType(
                                "P1",
                                "Selling price"))
                .thenReturn(priceDto);

        BigDecimal result =
                cartEntryService.getSellingPriceAmount("P1");

        Assertions.assertEquals(
                BigDecimal.valueOf(100),
                result);
    }

    @Test
    void getDiscountPriceAmountTest() {

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(120));

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        Mockito.when(
                        priceService.findByProductAndPriceType(
                                "P1",
                                "MRP"))
                .thenReturn(mrp);

        Mockito.when(
                        priceService.findByProductAndPriceType(
                                "P1",
                                "Selling price"))
                .thenReturn(selling);

        BigDecimal result =
                cartEntryService.getDiscountPriceAmount(
                        "P1",
                        BigDecimal.valueOf(2));

        Assertions.assertEquals(
                BigDecimal.valueOf(40),
                result);
    }

    @Test
    void getTotalPriceTest() {

        Mockito.doReturn(BigDecimal.valueOf(100))
                .when(cartEntryService)
                .getSellingPriceAmount("P1");

        BigDecimal result =
                cartEntryService.getTotalPrice(
                        "P1",
                        BigDecimal.valueOf(2));

        Assertions.assertEquals(
                BigDecimal.valueOf(200),
                result);
    }

    @Test
    void recalculateTest() {

        CartEntry entry1 = new CartEntry();
        entry1.setTotalPrice(BigDecimal.valueOf(100));
        entry1.setDiscount(BigDecimal.valueOf(10));
        entry1.setTotalOriginalPrice(BigDecimal.valueOf(110));

        CartEntry entry2 = new CartEntry();
        entry2.setTotalPrice(BigDecimal.valueOf(200));
        entry2.setDiscount(BigDecimal.valueOf(20));
        entry2.setTotalOriginalPrice(BigDecimal.valueOf(220));

        Cart cart = new Cart();

        Mockito.when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(List.of(entry1, entry2));

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        cartEntryService.recalculate("CART1");

        Assertions.assertEquals(
                BigDecimal.valueOf(300),
                cart.getTotalPrice());

        Assertions.assertEquals(
                BigDecimal.valueOf(30),
                cart.getDiscount());

        Assertions.assertEquals(
                BigDecimal.valueOf(330),
                cart.getTotalOriginalPrice());

        verify(cartRepository).save(cart);
    }

    @Test
    void findByCartIdTest() {

        List<CartEntry> entries = List.of(
                new CartEntry(),
                new CartEntry()
        );

        List<CartEntryDto> dtoList = List.of(
                new CartEntryDto(),
                new CartEntryDto()
        );

        Mockito.when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(entries);

        Mockito.when(modelMapper.map(
                Mockito.eq(entries),
                Mockito.any(Type.class)
        )).thenReturn(dtoList);

        List<CartEntryDto> result =
                cartEntryService.findByCartId("CART1");

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void deleteAllByCartIdTest() {

        Mockito.doNothing().when(cartEntryService)
                .recalculate("CART1");

        cartEntryService.deleteAllByCartId("CART1");

        verify(cartEntryRepository)
                .deleteAllByCartId("CART1");
    }
}