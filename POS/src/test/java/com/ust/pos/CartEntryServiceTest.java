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
    private PriceService priceService;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest_NewCartEntry() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART1");
        dto.setProduct("PROD1");
        dto.setQuantity(BigDecimal.valueOf(2));
        dto.setDiscount(BigDecimal.ONE);

        CartEntry cartEntry = new CartEntry();

        PriceDto priceDto = new PriceDto();
        priceDto.setSellingPrice(BigDecimal.TEN);

        Mockito.when(
                cartEntryRepository.findByIdentifier("CART1_PROD1")
        ).thenReturn(null);

        Mockito.when(
                priceService.findByIdentifier("PROD1")
        ).thenReturn(priceDto);

        Mockito.when(
                cartEntryRepository.save(Mockito.any(CartEntry.class))
        ).thenReturn(cartEntry);

        CartEntryDto response =
                cartEntryService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "CART1_PROD1",
                response.getIdentifier()
        );

        Assertions.assertEquals(
                new BigDecimal("18"),
                response.getTotalPrice()
        );

        Mockito.verify(cartEntryRepository)
                .save(Mockito.any(CartEntry.class));
    }

    @Test
    void saveTest_ExistingCartEntry() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART1");
        dto.setProduct("PROD1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.valueOf(3));

        PriceDto priceDto = new PriceDto();
        priceDto.setSellingPrice(BigDecimal.TEN);

        Mockito.when(
                cartEntryRepository.findByIdentifier("CART1_PROD1")
        ).thenReturn(existing);

        Mockito.when(
                priceService.findByIdentifier("PROD1")
        ).thenReturn(priceDto);

        CartEntryDto response =
                cartEntryService.save(dto);

        Assertions.assertEquals(
                BigDecimal.valueOf(5),
                response.getQuantity()
        );

        Assertions.assertEquals(
                BigDecimal.valueOf(50),
                response.getTotalPrice()
        );
    }

    @Test
    void updateTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY1");

        CartEntry existing = new CartEntry();
        existing.setIdentifier("ENTRY1");

        CartEntry mapped = new CartEntry();
        mapped.setIdentifier("ENTRY1");

        Mockito.when(
                cartEntryRepository.findByIdentifier("ENTRY1")
        ).thenReturn(existing);

        Mockito.when(
                modelMapper.map(dto, CartEntry.class)
        ).thenReturn(mapped);

        Mockito.when(
                cartEntryRepository.save(mapped)
        ).thenReturn(mapped);

        CartEntryDto response =
                cartEntryService.update(dto);

        Assertions.assertNotNull(response);

        Mockito.verify(cartEntryRepository)
                .save(mapped);
    }

    @Test
    void updateTestFailure() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY1");

        Mockito.when(
                cartEntryRepository.findByIdentifier("ENTRY1")
        ).thenReturn(null);

        CartEntryDto response =
                cartEntryService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(
                cartEntryRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing()
                .when(cartEntryRepository)
                .deleteByIdentifier("ENTRY1");

        cartEntryService.delete("ENTRY1");

        Mockito.verify(cartEntryRepository)
                .deleteByIdentifier("ENTRY1");
    }

    @Test
    void findByIdentifierTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("ENTRY1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY1");

        Mockito.when(
                cartEntryRepository.findByIdentifier("ENTRY1")
        ).thenReturn(cartEntry);

        Mockito.when(
                modelMapper.map(
                        cartEntry,
                        CartEntryDto.class
                )
        ).thenReturn(dto);

        CartEntryDto response =
                cartEntryService.findByIdentifier("ENTRY1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "ENTRY1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("ENTRY1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY1");

        List<CartEntry> entities =
                List.of(cartEntry);

        List<CartEntryDto> dtos =
                List.of(dto);

        Type listType =
                new org.modelmapper.TypeToken<List<CartEntryDto>>() {
                }.getType();

        Mockito.when(cartEntryRepository.findAll())
                .thenReturn(entities);

        Mockito.when(
                modelMapper.map(
                        entities,
                        listType
                )
        ).thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findAll();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "ENTRY1",
                response.get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("ENTRY1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY1");

        Page<CartEntry> page =
                new PageImpl<>(List.of(cartEntry));

        List<CartEntryDto> dtos =
                List.of(dto);

        Type listType =
                new org.modelmapper.TypeToken<List<CartEntryDto>>() {
                }.getType();

        Mockito.when(
                cartEntryRepository.findAll(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        page.getContent(),
                        listType
                )
        ).thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(cartEntryRepository)
                .findAll(pageable);
    }

    @Test
    void findByCartIdTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setCartId("CART1");

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART1");

        List<CartEntry> entities =
                List.of(cartEntry);

        List<CartEntryDto> dtos =
                List.of(dto);

        Type listType =
                new org.modelmapper.TypeToken<List<CartEntryDto>>() {
                }.getType();

        Mockito.when(
                cartEntryRepository.findByCartId("CART1")
        ).thenReturn(entities);

        Mockito.when(
                modelMapper.map(
                        entities,
                        listType
                )
        ).thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findByCartId("CART1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(cartEntryRepository)
                .findByCartId("CART1");
    }
}