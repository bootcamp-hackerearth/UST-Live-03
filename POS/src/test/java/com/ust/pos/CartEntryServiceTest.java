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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Test
    void findByIdentifierTest() {
        CartEntry cartEntry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        when(cartEntryRepository.findByIdentifier("CART1_PROD1"))
                .thenReturn(cartEntry);

        when(modelMapper.map(cartEntry, CartEntryDto.class))
                .thenReturn(dto);

        CartEntryDto result = cartEntryService.findByIdentifier("CART1_PROD1");

        Assertions.assertNotNull(result);
    }

    @Test
    void saveNewCartEntryTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART1");
        dto.setProduct("PROD1");
        dto.setQuantity(BigDecimal.valueOf(2));

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(150));

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        CartEntry cartEntry = new CartEntry();

        Cart cart = new Cart();

        when(cartEntryRepository.findByIdentifier("CART1_PROD1"))
                .thenReturn(null);

        when(priceService.findByProductAndPriceType("PROD1", "MRP"))
                .thenReturn(mrp);

        when(priceService.findByProductAndPriceType("PROD1", "Selling price"))
                .thenReturn(selling);

        when(modelMapper.map(dto, CartEntry.class))
                .thenReturn(cartEntry);

        when(cartEntryRepository.save(cartEntry))
                .thenReturn(cartEntry);

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(Collections.emptyList());

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        CartEntryDto result = cartEntryService.save(dto);

        Assertions.assertEquals("CART1_PROD1", result.getIdentifier());
        Assertions.assertEquals(BigDecimal.valueOf(100), result.getUnitPrice());
        Assertions.assertEquals(BigDecimal.valueOf(200), result.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(100), result.getDiscount());

        verify(cartEntryRepository).save(cartEntry);
    }

    @Test
    void saveExistingCartEntryTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART1");
        dto.setProduct("PROD1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.valueOf(3));

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(150));

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        Cart cart = new Cart();

        when(cartEntryRepository.findByIdentifier("CART1_PROD1"))
                .thenReturn(existing);

        when(priceService.findByProductAndPriceType("PROD1", "MRP"))
                .thenReturn(mrp);

        when(priceService.findByProductAndPriceType("PROD1", "Selling price"))
                .thenReturn(selling);

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(Collections.emptyList());

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        CartEntryDto result = cartEntryService.save(dto);

        Assertions.assertEquals(BigDecimal.valueOf(5), result.getQuantity());

        verify(modelMapper).map(dto, existing);
        verify(cartEntryRepository).save(existing);
    }

    @Test
    void deleteTest() {
        Cart cart = new Cart();

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(Collections.emptyList());

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        cartEntryService.delete("CART1", "PROD1");

        verify(cartEntryRepository).deleteByCartIdAndProduct("CART1", "PROD1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<CartEntry> entries = List.of(
                new CartEntry(),
                new CartEntry()
        );

        Page<CartEntry> page = new PageImpl<>(entries, pageable, 2);

        List<CartEntryDto> dtoList = List.of(
                new CartEntryDto(),
                new CartEntryDto()
        );

        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();

        when(cartEntryRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(entries, listType))
                .thenReturn(dtoList);

        List<CartEntryDto> result = cartEntryService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void getSellingPriceAmountTest() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        when(priceService.findByProductAndPriceType("PROD1", "Selling price"))
                .thenReturn(selling);

        BigDecimal result = cartEntryService.getSellingPriceAmount("PROD1");

        Assertions.assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    void getDiscountPriceAmountTest() {
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(150));

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        when(priceService.findByProductAndPriceType("PROD1", "MRP"))
                .thenReturn(mrp);

        when(priceService.findByProductAndPriceType("PROD1", "Selling price"))
                .thenReturn(selling);

        BigDecimal result = cartEntryService.getDiscountPriceAmount(
                "PROD1",
                BigDecimal.valueOf(2)
        );

        Assertions.assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    void getTotalPriceTest() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        when(priceService.findByProductAndPriceType("PROD1", "Selling price"))
                .thenReturn(selling);

        BigDecimal result = cartEntryService.getTotalPrice(
                "PROD1",
                BigDecimal.valueOf(3)
        );

        Assertions.assertEquals(BigDecimal.valueOf(300), result);
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

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(List.of(entry1, entry2));

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        cartEntryService.recalculate("CART1");

        Assertions.assertEquals(BigDecimal.valueOf(300), cart.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(30), cart.getDiscount());
        Assertions.assertEquals(BigDecimal.valueOf(330), cart.getTotalOriginalPrice());

        verify(cartRepository).save(cart);
    }

    @Test
    void recalculateWithEmptyEntriesTest() {
        Cart cart = new Cart();

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(Collections.emptyList());

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        cartEntryService.recalculate("CART1");

        Assertions.assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        Assertions.assertEquals(BigDecimal.ZERO, cart.getDiscount());
        Assertions.assertEquals(BigDecimal.ZERO, cart.getTotalOriginalPrice());

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

        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(entries);

        when(modelMapper.map(entries, listType))
                .thenReturn(dtoList);

        List<CartEntryDto> result = cartEntryService.findByCartId("CART1");

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void deleteAllByCartIdTest() {
        Cart cart = new Cart();

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(Collections.emptyList());

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        cartEntryService.deleteAllByCartId("CART1");

        verify(cartEntryRepository).deleteAllByCartId("CART1");
    }
}