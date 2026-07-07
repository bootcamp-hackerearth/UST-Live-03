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
    private PriceService priceService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        PriceDto price = new PriceDto();
        price.setSellingPrice(BigDecimal.valueOf(100));

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(null);

        Mockito.when(priceService.findByIdentifier("P1"))
                .thenReturn(price);

        Mockito.doNothing()
                .when(modelMapper)
                .map(
                        Mockito.same(dto),
                        Mockito.any(CartEntry.class)
                );

        Mockito.when(cartEntryRepository.save(Mockito.any(CartEntry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertEquals("C1_P1", response.getIdentifier());
        Assertions.assertEquals(BigDecimal.valueOf(2), response.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(100), response.getUnitPrice());
        Assertions.assertEquals(BigDecimal.valueOf(200), response.getTotalPrice());

        Mockito.verify(modelMapper)
                .map(
                        Mockito.same(dto),
                        Mockito.any(CartEntry.class)
                );

        Mockito.verify(cartEntryRepository)
                .save(Mockito.any(CartEntry.class));
    }

    @Test
    void saveExistingCartEntryTest() {
        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.valueOf(3));

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        PriceDto price = new PriceDto();
        price.setSellingPrice(BigDecimal.valueOf(100));

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(existing);

        Mockito.when(priceService.findByIdentifier("P1"))
                .thenReturn(price);

        cartEntryService.save(dto);

        Assertions.assertEquals(BigDecimal.valueOf(5), dto.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(100), dto.getUnitPrice());
        Assertions.assertEquals(BigDecimal.valueOf(500), dto.getTotalPrice());

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(cartEntryRepository).save(existing);
    }

    @Test
    void updateTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");

        CartEntry existing = new CartEntry();
        CartEntry mapped = new CartEntry();

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(existing);

        Mockito.when(modelMapper.map(dto, CartEntry.class))
                .thenReturn(mapped);

        CartEntryDto response = cartEntryService.update(dto);

        Assertions.assertEquals("C1_P1", response.getIdentifier());

        Mockito.verify(cartEntryRepository).save(mapped);
    }

    @Test
    void updateTestFailure() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(null);

        CartEntryDto response = cartEntryService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(cartEntryRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(cartEntryRepository)
                .deleteByIdentifier("C1_P1");

        cartEntryService.delete("C1_P1");

        Mockito.verify(cartEntryRepository).deleteByIdentifier("C1_P1");
    }

    @Test
    void deleteByCartIdTest() {
        Mockito.doNothing()
                .when(cartEntryRepository)
                .deleteByCartId("C1");

        cartEntryService.deleteByCartId("C1");

        Mockito.verify(cartEntryRepository).deleteByCartId("C1");
    }

    @Test
    void findAllTest() {
        List<CartEntry> entries = List.of(new CartEntry());
        List<CartEntryDto> dtos = List.of(new CartEntryDto());

        Mockito.when(cartEntryRepository.findAll())
                .thenReturn(entries);

        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class)))
                .thenReturn(dtos);

        List<CartEntryDto> response = cartEntryService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByIdentifierTest() {
        CartEntry entry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(entry);

        Mockito.when(modelMapper.map(entry, CartEntryDto.class))
                .thenReturn(dto);

        Assertions.assertNotNull(cartEntryService.findByIdentifier("C1_P1"));
    }

    @Test
    void findAllPaginationTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<CartEntry> entries = List.of(new CartEntry());
        Page<CartEntry> page = new PageImpl<>(entries);

        List<CartEntryDto> dtos = List.of(new CartEntryDto());

        Mockito.when(cartEntryRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class)))
                .thenReturn(dtos);

        List<CartEntryDto> response = cartEntryService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByCartIdTest() {
        List<CartEntry> entries = List.of(new CartEntry());
        List<CartEntryDto> dtos = List.of(new CartEntryDto());

        Mockito.when(cartEntryRepository.findByCartId("C1"))
                .thenReturn(entries);

        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class)))
                .thenReturn(dtos);

        List<CartEntryDto> response = cartEntryService.findByCartId("C1");

        Assertions.assertEquals(1, response.size());
    }
}