package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
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
    private CartServiceImpl cartService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceServiceImpl priceService;

    @Test
    void findByIdentifierTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("P001_CART1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");

        Mockito.when(
                        cartEntryRepository.findByIdentifier("P001_CART1"))
                .thenReturn(cartEntry);

        Mockito.when(
                        modelMapper.map(cartEntry, CartEntryDto.class))
                .thenReturn(dto);

        CartEntryDto response =
                cartEntryService.findByIdentifier("P001_CART1");

        Assertions.assertEquals(
                "P001_CART1",
                response.getIdentifier());
    }

    @Test
    void findAllWithPageableTest() {

        CartEntry entry = new CartEntry();
        entry.setIdentifier("P001_CART1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");

        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<CartEntry> page = new PageImpl<>(entries);

        Mockito.when(cartEntryRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(entries),
                        Mockito.any(Type.class)))
                .thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "P001_CART1",
                response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {

        CartEntry entry = new CartEntry();
        entry.setIdentifier("P001_CART1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");

        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);

        Mockito.when(cartEntryRepository.findAll())
                .thenReturn(entries);

        Mockito.when(modelMapper.map(
                        Mockito.eq(entries),
                        Mockito.any(Type.class)))
                .thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findAll(null);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void saveTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P001");
        dto.setCart("CART1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setQuantity(BigDecimal.ZERO);

        PriceDto sellingPrice = new PriceDto();
        sellingPrice.setValue(BigDecimal.valueOf(80));

        PriceDto mrp = new PriceDto();
        mrp.setValue(BigDecimal.valueOf(100));

        CartEntry savedEntry = new CartEntry();
        savedEntry.setIdentifier("P001_CART1");

        CartEntryDto responseDto = new CartEntryDto();
        responseDto.setIdentifier("P001_CART1");

        Mockito.when(
                        cartEntryRepository.findByIdentifier("P001_CART1"))
                .thenReturn(cartEntry);

        Mockito.when(
                        priceService.findByIdentifier("P001Selling"))
                .thenReturn(sellingPrice);

        Mockito.when(
                        priceService.findByIdentifier("P001Mrp"))
                .thenReturn(mrp);

        Mockito.when(
                        cartEntryRepository.save(Mockito.any(CartEntry.class)))
                .thenReturn(savedEntry);

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Mockito.when(
                        cartService.findByIdentifier("CART1"))
                .thenReturn(cartDto);

        Mockito.when(
                        modelMapper.map(savedEntry, CartEntryDto.class))
                .thenReturn(responseDto);

        CartEntryDto response =
                cartEntryService.save(dto);

        Assertions.assertEquals(
                "P001_CART1",
                response.getIdentifier());

        Mockito.verify(cartService)
                .recalculate("CART1");
    }

    @Test
    void savePriceNotConfiguredTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P001");
        dto.setCart("CART1");
        dto.setQuantity(BigDecimal.ONE);

        CartEntry cartEntry = new CartEntry();
        cartEntry.setQuantity(BigDecimal.ZERO);

        Mockito.when(
                        cartEntryRepository.findByIdentifier("P001_CART1"))
                .thenReturn(cartEntry);

        Mockito.when(
                        priceService.findByIdentifier("P001Selling"))
                .thenReturn(null);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> cartEntryService.save(dto));
    }

    @Test
    void updateSuccessTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");
        dto.setCart("CART1");
        dto.setQuantity(BigDecimal.valueOf(5));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("P001_CART1");
        cartEntry.setProduct("P001");
        cartEntry.setUnitPrice(BigDecimal.valueOf(80));

        PriceDto mrpDto = new PriceDto();
        mrpDto.setValue(BigDecimal.valueOf(100));

        CartEntry savedEntry = new CartEntry();

        CartEntryDto responseDto = new CartEntryDto();
        responseDto.setIdentifier("P001_CART1");

        Mockito.when(
                        cartEntryRepository.findByIdentifier("P001_CART1"))
                .thenReturn(cartEntry);

        Mockito.when(
                        priceService.findByIdentifier("P001Mrp"))
                .thenReturn(mrpDto);

        Mockito.when(
                        cartEntryRepository.save(cartEntry))
                .thenReturn(savedEntry);

        Mockito.when(
                        modelMapper.map(savedEntry, CartEntryDto.class))
                .thenReturn(responseDto);

        CartEntryDto response =
                cartEntryService.update(dto);

        Assertions.assertEquals(
                "P001_CART1",
                response.getIdentifier());

        Mockito.verify(cartService)
                .recalculate("CART1");
    }

    @Test
    void updateFailureTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");

        Mockito.when(
                        cartEntryRepository.findByIdentifier("P001_CART1"))
                .thenReturn(null);

        CartEntryDto response =
                cartEntryService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Entry not found",
                response.getMessage());
    }

    @Test
    void deleteTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setCart("CART1");

        CartEntryDto dto = new CartEntryDto();
        dto.setCart("CART1");

        Mockito.when(
                        cartEntryRepository.findByIdentifier("P001_CART1"))
                .thenReturn(cartEntry);

        Mockito.when(
                        modelMapper.map(cartEntry, CartEntryDto.class))
                .thenReturn(dto);

        Mockito.when(
                        cartService.recalculate("CART1"))
                .thenReturn(new CartDto());

        cartEntryService.delete("P001_CART1");

        Mockito.verify(cartEntryRepository)
                .deleteByIdentifier("P001_CART1");

        Mockito.verify(cartService)
                .recalculate("CART1");
    }

    @Test
    void findByCartTest() {

        CartEntry entry = new CartEntry();
        entry.setIdentifier("P001_CART1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("P001_CART1");

        List<CartEntry> entries = List.of(entry);
        List<CartEntryDto> dtos = List.of(dto);

        Mockito.when(
                        cartEntryRepository.findByCart("CART1"))
                .thenReturn(entries);

        Mockito.when(
                        modelMapper.map(
                                Mockito.eq(entries),
                                Mockito.any(Type.class)))
                .thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findByCart("CART1");

        Assertions.assertEquals(1, response.size());
    }
}