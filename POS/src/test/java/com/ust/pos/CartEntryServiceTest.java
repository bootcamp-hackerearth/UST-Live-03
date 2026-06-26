package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Test
    void saveNewEntryTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("PROD1");
        dto.setCart("CART1");
        dto.setQuantity(BigDecimal.valueOf(2));
        Price price = new Price();
        price.setMrp(BigDecimal.valueOf(120));
        price.setSellingPrice(BigDecimal.valueOf(100));

        Mockito.when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(null);
        Mockito.when(priceRepository.findByIdentifier("PROD1")).thenReturn(price);

        Mockito.doAnswer(invocation -> {
            CartEntryDto source = invocation.getArgument(0);
            CartEntry target = invocation.getArgument(1);

            target.setIdentifier(source.getIdentifier());
            target.setCart(source.getCart());
            target.setProduct(source.getProduct());
            target.setQuantity(source.getQuantity());
            target.setPrice(source.getPrice());
            target.setSellingPrice(source.getSellingPrice());
            target.setDiscount(source.getDiscount());
            target.setTotalPrice(source.getTotalPrice());
            return null;
        }).when(modelMapper).map(ArgumentMatchers.any(CartEntryDto.class), ArgumentMatchers.any(CartEntry.class));

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertEquals(BigDecimal.valueOf(200), response.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(40), response.getDiscount());
        Assertions.assertEquals(BigDecimal.valueOf(2), response.getQuantity());

        Mockito.verify(cartEntryRepository).save(Mockito.any(CartEntry.class));
        Mockito.verify(cartService).recalculate("CART1");
    }

    @Test
    void saveExistingEntryTest() {
        CartEntry existing = new CartEntry();
        existing.setIdentifier("PROD1-CART1");
        existing.setCart("CART1");
        existing.setQuantity(BigDecimal.valueOf(3));
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("PROD1");
        dto.setCart("CART1");
        dto.setQuantity(BigDecimal.valueOf(2));
        Price price = new Price();
        price.setMrp(BigDecimal.valueOf(120));
        price.setSellingPrice(BigDecimal.valueOf(100));

        Mockito.when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(existing);
        Mockito.when(priceRepository.findByIdentifier("PROD1")).thenReturn(price);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertEquals(BigDecimal.valueOf(5), response.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(500), response.getTotalPrice());

        Mockito.verify(cartEntryRepository).save(Mockito.any(CartEntry.class));
        Mockito.verify(cartService).recalculate("CART1");
    }

    @Test
    void saveRemoveEntryWhenQuantityZeroTest() {
        CartEntry existing = new CartEntry();
        existing.setIdentifier("PROD1-CART1");
        existing.setCart("CART1");
        existing.setQuantity(BigDecimal.valueOf(2));
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("PROD1");
        dto.setCart("CART1");
        dto.setQuantity(BigDecimal.valueOf(-2));
        Price price = new Price();

        Mockito.when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(existing);
        Mockito.when(priceRepository.findByIdentifier("PROD1")).thenReturn(price);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertEquals(BigDecimal.ZERO, response.getQuantity());

        Mockito.verify(cartEntryRepository).delete(existing);
        Mockito.verify(cartService).recalculate("CART1");
    }

    @Test
    void saveRemoveEntryWhenQuantityNegativeTest() {
        CartEntry existing = new CartEntry();
        existing.setIdentifier("PROD1-CART1");
        existing.setCart("CART1");
        existing.setQuantity(BigDecimal.valueOf(2));
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("PROD1");
        dto.setCart("CART1");
        dto.setQuantity(BigDecimal.valueOf(-5));
        Price price = new Price();

        Mockito.when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(existing);
        Mockito.when(priceRepository.findByIdentifier("PROD1")).thenReturn(price);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertEquals(BigDecimal.ZERO, response.getQuantity());

        Mockito.verify(cartEntryRepository).delete(existing);
        Mockito.verify(cartService).recalculate("CART1");
    }

    @Test
    void findAllEntriesForCartTest() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("ENTRY1");
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY1");

        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("identifier"));
        Page<CartEntry> page = new PageImpl<>(entries, pageable, entries.size());

        Mockito.when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<CartEntryDto> result = cartEntryService.findAllEntriesForCart(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(5, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void deleteTest() {
        cartEntryService.delete("ENTRY1");
        Mockito.verify(cartEntryRepository).deleteByIdentifier("ENTRY1");
    }

    @Test
    void deleteAllByCartTest() {
        CartEntry entry = new CartEntry();

        List<CartEntry> entries = List.of(entry);

        Mockito.when(cartEntryRepository.findByCart("CART1")).thenReturn(entries);

        cartEntryService.deleteAllByCart("CART1");

        Mockito.verify(cartEntryRepository).deleteAll(entries);
    }

    @Test
    void deleteAllByCartEmptyListTest() {
        Mockito.when(cartEntryRepository.findByCart("CART1")).thenReturn(List.of());
        cartEntryService.deleteAllByCart("CART1");
        Mockito.verify(cartEntryRepository, Mockito.never()).deleteAll(Mockito.anyList());
    }
}