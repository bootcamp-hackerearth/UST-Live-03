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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

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

    @Mock
    private CartRepository cartRepository;

    @Test
    void findByIdentifier_Found() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("C1_P1");

        CartEntryDto dto = new CartEntryDto();

        when(cartEntryRepository.findByIdentifier("C1_P1")).thenReturn(entry);
        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(dto);

        CartEntryDto result = cartEntryService.findByIdentifier("C1_P1");

        Assertions.assertNotNull(result);
    }

    @Test
    void save_NewEntry() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.ONE);

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.TEN);

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(new BigDecimal("15"));

        when(priceService.findByProductAndPriceType("P1", "SELLING")).thenReturn(selling);
        when(priceService.findByProductAndPriceType("P1", "MRP")).thenReturn(mrp);

        when(cartEntryRepository.findByIdentifier("C1_P1")).thenReturn(null);

        CartEntry entry = new CartEntry();
        entry.setTotalPrice(BigDecimal.TEN);
        entry.setDiscount(BigDecimal.ONE);
        entry.setTotalOriginalPrice(new BigDecimal("11"));

        when(modelMapper.map(dto, CartEntry.class)).thenReturn(entry);
        when(cartEntryRepository.save(entry)).thenReturn(entry);

        when(cartEntryRepository.findAllByCartId("C1")).thenReturn(List.of(entry));

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);

        CartEntryDto result = cartEntryService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("C1_P1", result.getIdentifier());
        verify(cartEntryRepository).save(entry);
    }

    @Test
    void save_ExistingEntry() {
        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.ONE);
        existing.setTotalPrice(BigDecimal.TEN);
        existing.setDiscount(BigDecimal.ONE);
        existing.setTotalOriginalPrice(new BigDecimal("11"));

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.ONE);

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.TEN);

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(new BigDecimal("15"));

        when(priceService.findByProductAndPriceType("P1", "SELLING")).thenReturn(selling);
        when(priceService.findByProductAndPriceType("P1", "MRP")).thenReturn(mrp);

        when(cartEntryRepository.findByIdentifier("C1_P1")).thenReturn(existing);
        when(cartEntryRepository.findAllByCartId("C1")).thenReturn(List.of(existing));

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);

        CartEntryDto result = cartEntryService.save(dto);

        Assertions.assertNotNull(result);
        verify(cartEntryRepository).save(existing);
    }

    @Test
    void delete_Test() {
        doNothing().when(cartEntryRepository).deleteByCartIdAndProduct("C1", "P1");

        when(cartEntryRepository.findAllByCartId("C1")).thenReturn(List.of());

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);

        cartEntryService.delete("C1", "P1");

        verify(cartEntryRepository).deleteByCartIdAndProduct("C1", "P1");
    }

    @Test
    void findAll_Test() {
        Pageable pageable = PageRequest.of(0, 10);

        CartEntry entry1 = new CartEntry();
        CartEntry entry2 = new CartEntry();

        Page<CartEntry> page = new PageImpl<>(List.of(entry1, entry2), pageable, 2);

        List<CartEntryDto> dtoList = List.of(new CartEntryDto(), new CartEntryDto());

        when(cartEntryRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtoList);

        List<CartEntryDto> result = cartEntryService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void getSellingPriceAmount_Test() {
        PriceDto price = new PriceDto();
        price.setPriceAmount(BigDecimal.TEN);

        when(priceService.findByProductAndPriceType("P1", "SELLING")).thenReturn(price);

        BigDecimal result = cartEntryService.getSellingPriceAmount("P1");

        Assertions.assertEquals(BigDecimal.TEN, result);
    }

    @Test
    void getDiscountPriceAmount_Test() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.TEN);

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(new BigDecimal("15"));

        when(priceService.findByProductAndPriceType("P1", "SELLING")).thenReturn(selling);
        when(priceService.findByProductAndPriceType("P1", "MRP")).thenReturn(mrp);

        BigDecimal result = cartEntryService.getDiscountPriceAmount("P1", BigDecimal.ONE);

        Assertions.assertEquals(new BigDecimal("5"), result);
    }

    @Test
    void getTotalPrice_Test() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.TEN);

        when(priceService.findByProductAndPriceType("P1", "SELLING")).thenReturn(selling);

        BigDecimal result = cartEntryService.getTotalPrice("P1", BigDecimal.valueOf(2));

        Assertions.assertEquals(new BigDecimal("20"), result);
    }

    @Test
    void recalculate_Test() {
        CartEntry entry = new CartEntry();
        entry.setTotalPrice(BigDecimal.TEN);
        entry.setDiscount(BigDecimal.ONE);
        entry.setTotalOriginalPrice(new BigDecimal("11"));

        when(cartEntryRepository.findAllByCartId("C1")).thenReturn(List.of(entry));

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);

        cartEntryService.recalculate("C1");

        Assertions.assertEquals(BigDecimal.TEN, cart.getTotalPrice());
        Assertions.assertEquals(BigDecimal.ONE, cart.getDiscount());
        Assertions.assertEquals(new BigDecimal("11"), cart.getTotalOriginalPrice());

        verify(cartRepository).save(cart);
    }

    @Test
    void findByCartId_Test() {
        List<CartEntry> entries = List.of(new CartEntry());
        List<CartEntryDto> dtoList = List.of(new CartEntryDto());

        when(cartEntryRepository.findAllByCartId("C1")).thenReturn(entries);
        when(modelMapper.map(eq(entries), any(Type.class))).thenReturn(dtoList);

        List<CartEntryDto> result = cartEntryService.findByCartId("C1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void deleteAllByCartId_Test() {
        doNothing().when(cartEntryRepository).deleteAllByCartId("C1");

        when(cartEntryRepository.findAllByCartId("C1")).thenReturn(List.of());

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);

        cartEntryService.deleteAllByCartId("C1");

        verify(cartEntryRepository).deleteAllByCartId("C1");
    }

    @Test
    void updateQuantity_Test() {
        CartEntry existing = new CartEntry();
        existing.setTotalPrice(BigDecimal.TEN);
        existing.setDiscount(BigDecimal.ONE);
        existing.setTotalOriginalPrice(new BigDecimal("11"));

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.ONE);

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.TEN);

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(new BigDecimal("15"));

        when(priceService.findByProductAndPriceType("P1", "SELLING")).thenReturn(selling);
        when(priceService.findByProductAndPriceType("P1", "MRP")).thenReturn(mrp);

        when(cartEntryRepository.findByIdentifier("C1_P1")).thenReturn(existing);
        when(cartEntryRepository.findAllByCartId("C1")).thenReturn(List.of(existing));

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);

        CartEntryDto result = cartEntryService.updateQuantity(dto);

        Assertions.assertNotNull(result);
        verify(cartEntryRepository).save(existing);
    }

    @Test
    void updateQuantity_NewEntry() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.ONE);

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.TEN);

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(new BigDecimal("15"));

        when(priceService.findByProductAndPriceType("P1", "SELLING")).thenReturn(selling);
        when(priceService.findByProductAndPriceType("P1", "MRP")).thenReturn(mrp);

        when(cartEntryRepository.findByIdentifier("C1_P1")).thenReturn(null);

        CartEntry entry = new CartEntry();
        entry.setTotalPrice(BigDecimal.TEN);
        entry.setDiscount(BigDecimal.ONE);
        entry.setTotalOriginalPrice(new BigDecimal("11"));

        when(modelMapper.map(dto, CartEntry.class)).thenReturn(entry);
        when(cartEntryRepository.findAllByCartId("C1")).thenReturn(List.of(entry));

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);

        CartEntryDto result = cartEntryService.updateQuantity(dto);

        Assertions.assertNotNull(result);
        verify(cartEntryRepository).save(entry);
    }
}