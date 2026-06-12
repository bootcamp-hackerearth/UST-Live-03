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
import org.modelmapper.TypeToken;
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

        PriceDto priceDto = new PriceDto();
        priceDto.setSellingPrice(BigDecimal.valueOf(100));

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(null);

        Mockito.when(priceService.findByIdentifier("P1"))
                .thenReturn(priceDto);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertEquals("C1_P1", response.getIdentifier());
        Assertions.assertEquals(
                BigDecimal.valueOf(200),
                response.getTotalPrice()
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

        PriceDto priceDto = new PriceDto();
        priceDto.setSellingPrice(BigDecimal.valueOf(100));

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(existing);

        Mockito.when(priceService.findByIdentifier("P1"))
                .thenReturn(priceDto);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertEquals(
                BigDecimal.valueOf(5),
                response.getQuantity()
        );

        Assertions.assertEquals(
                BigDecimal.valueOf(500),
                response.getTotalPrice()
        );
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

        Mockito.verify(cartEntryRepository)
                .save(mapped);
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

        Mockito.verify(cartEntryRepository)
                .deleteByIdentifier("C1_P1");
    }

    @Test
    void findAllTest() {
        CartEntry entry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);

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
        entry.setIdentifier("C1_P1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(entry);

        Mockito.when(modelMapper.map(entry, CartEntryDto.class))
                .thenReturn(dto);

        CartEntryDto response =
                cartEntryService.findByIdentifier("C1_P1");

        Assertions.assertEquals(
                "C1_P1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationShouldReturnCartEntryDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<CartEntry> entries =
                List.of(new CartEntry());

        Page<CartEntry> page =
                new PageImpl<>(entries);

        List<CartEntryDto> dtos =
                List.of(new CartEntryDto());

        Type listType =
                new TypeToken<List<CartEntryDto>>() {
                }.getType();

        Mockito.when(cartEntryRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entries, listType))
                .thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(cartEntryRepository)
                .findAll(pageable);

        Mockito.verify(modelMapper)
                .map(entries, listType);
    }

    @Test
    void findByCartIdTest() {
        CartEntry entry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);

        Type listType =
                new TypeToken<List<CartEntryDto>>() {
                }.getType();

        Mockito.when(cartEntryRepository.findByCartId("C1"))
                .thenReturn(entries);

        Mockito.when(modelMapper.map(entries, listType))
                .thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findByCartId("C1");

        Assertions.assertEquals(1, response.size());

        Mockito.verify(cartEntryRepository)
                .findByCartId("C1");
    }
}