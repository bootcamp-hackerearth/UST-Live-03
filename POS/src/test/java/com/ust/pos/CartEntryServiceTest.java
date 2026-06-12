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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    void findByIdentifier_Found() {

        CartEntry cartEntry = new CartEntry();

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");

        when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(cartEntry);

        when(modelMapper.map(cartEntry, CartEntryDto.class))
                .thenReturn(dto);

        CartEntryDto result =
                cartEntryService.findByIdentifier("C1_P1");

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                "C1_P1",
                result.getIdentifier());
    }

    @Test
    void save_NewCartEntry() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry cartEntry = new CartEntry();

        doReturn(BigDecimal.valueOf(10))
                .when(cartEntryService)
                .getSellingPriceAmount("P1");

        doReturn(BigDecimal.valueOf(4))
                .when(cartEntryService)
                .getDiscountPriceAmount(
                        "P1",
                        BigDecimal.valueOf(2));

        doReturn(BigDecimal.valueOf(20))
                .when(cartEntryService)
                .getTotalPrice(
                        "P1",
                        BigDecimal.valueOf(2));

        doNothing()
                .when(cartEntryService)
                .recalculate("C1");

        when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(null);

        when(modelMapper.map(dto, CartEntry.class))
                .thenReturn(cartEntry);

        CartEntryDto result =
                cartEntryService.save(dto);

        Assertions.assertEquals(
                "C1_P1",
                result.getIdentifier());

        Assertions.assertEquals(
                BigDecimal.valueOf(20),
                result.getTotalPrice());

        Assertions.assertEquals(
                BigDecimal.valueOf(4),
                result.getDiscount());

        Assertions.assertEquals(
                BigDecimal.valueOf(10),
                result.getUnitPrice());

        Assertions.assertEquals(
                BigDecimal.valueOf(24),
                result.getTotalOriginalPrice());

        verify(cartEntryRepository)
                .save(cartEntry);

        verify(cartEntryService)
                .recalculate("C1");
    }

    @Test
    void save_ExistingCartEntry() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.valueOf(3));

        doReturn(BigDecimal.valueOf(10))
                .when(cartEntryService)
                .getSellingPriceAmount("P1");

        doReturn(BigDecimal.valueOf(10))
                .when(cartEntryService)
                .getDiscountPriceAmount(
                        "P1",
                        BigDecimal.valueOf(5));

        doReturn(BigDecimal.valueOf(50))
                .when(cartEntryService)
                .getTotalPrice(
                        "P1",
                        BigDecimal.valueOf(5));

        doNothing()
                .when(cartEntryService)
                .recalculate("C1");

        when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(existing);

        CartEntryDto result =
                cartEntryService.save(dto);

        Assertions.assertEquals(
                BigDecimal.valueOf(5),
                result.getQuantity());

        Assertions.assertEquals(
                BigDecimal.valueOf(60),
                result.getTotalOriginalPrice());

        verify(modelMapper)
                .map(dto, existing);

        verify(cartEntryRepository)
                .save(existing);
    }

    @Test
    void deleteTest() {

        doNothing()
                .when(cartEntryService)
                .recalculate("C1");

        cartEntryService.delete("C1", "P1");

        verify(cartEntryRepository)
                .deleteByCartIdAndProduct("C1", "P1");

        verify(cartEntryService)
                .recalculate("C1");
    }

    @Test
    void findAllTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<CartEntry> cartEntries =
                List.of(new CartEntry(), new CartEntry());

        Page<CartEntry> page =
                new PageImpl<>(cartEntries);

        List<CartEntryDto> dtoList =
                List.of(new CartEntryDto(), new CartEntryDto());

        when(cartEntryRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(cartEntries), any(Type.class)))
                .thenReturn(dtoList);

        List<CartEntryDto> result =
                cartEntryService.findAll(pageable);

        Assertions.assertEquals(
                2,
                result.size());
    }

    @Test
    void getSellingPriceAmountTest() {

        PriceDto priceDto = new PriceDto();
        priceDto.setPriceAmount(BigDecimal.valueOf(100));

        when(priceService.findByProductAndPriceType(
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

        when(priceService.findByProductAndPriceType(
                "P1",
                "MRP"))
                .thenReturn(mrp);

        when(priceService.findByProductAndPriceType(
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

        doReturn(BigDecimal.valueOf(50))
                .when(cartEntryService)
                .getSellingPriceAmount("P1");

        BigDecimal result =
                cartEntryService.getTotalPrice(
                        "P1",
                        BigDecimal.valueOf(3));

        Assertions.assertEquals(
                BigDecimal.valueOf(150),
                result);
    }

    @Test
    void recalculate_WithEntries() {

        CartEntry entry1 = new CartEntry();
        entry1.setTotalPrice(BigDecimal.valueOf(100));
        entry1.setDiscount(BigDecimal.valueOf(10));
        entry1.setTotalOriginalPrice(BigDecimal.valueOf(110));

        CartEntry entry2 = new CartEntry();
        entry2.setTotalPrice(BigDecimal.valueOf(200));
        entry2.setDiscount(BigDecimal.valueOf(20));
        entry2.setTotalOriginalPrice(BigDecimal.valueOf(220));

        Cart cart = new Cart();

        when(cartEntryRepository.findAllByCartId("C1"))
                .thenReturn(List.of(entry1, entry2));

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        cartEntryService.recalculate("C1");

        Assertions.assertEquals(
                BigDecimal.valueOf(300),
                cart.getTotalPrice());

        Assertions.assertEquals(
                BigDecimal.valueOf(30),
                cart.getDiscount());

        Assertions.assertEquals(
                BigDecimal.valueOf(330),
                cart.getTotalOriginalPrice());

        verify(cartRepository)
                .save(cart);
    }

    @Test
    void recalculate_EmptyEntries() {

        Cart cart = new Cart();

        when(cartEntryRepository.findAllByCartId("C1"))
                .thenReturn(List.of());

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        cartEntryService.recalculate("C1");

        Assertions.assertEquals(
                BigDecimal.ZERO,
                cart.getTotalPrice());

        Assertions.assertEquals(
                BigDecimal.ZERO,
                cart.getDiscount());

        Assertions.assertEquals(
                BigDecimal.ZERO,
                cart.getTotalOriginalPrice());

        verify(cartRepository)
                .save(cart);
    }

    @Test
    void findByCartIdTest() {

        List<CartEntry> cartEntries =
                List.of(new CartEntry());

        List<CartEntryDto> dtoList =
                List.of(new CartEntryDto());

        when(cartEntryRepository.findAllByCartId("C1"))
                .thenReturn(cartEntries);

        when(modelMapper.map(eq(cartEntries), any(Type.class)))
                .thenReturn(dtoList);

        List<CartEntryDto> result =
                cartEntryService.findByCartId("C1");

        Assertions.assertEquals(
                1,
                result.size());
    }

    @Test
    void deleteAllByCartIdTest() {

        doNothing()
                .when(cartEntryService)
                .recalculate("C1");

        cartEntryService.deleteAllByCartId("C1");

        verify(cartEntryRepository)
                .deleteAllByCartId("C1");

        verify(cartEntryService)
                .recalculate("C1");
    }
}