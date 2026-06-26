package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.PriceRepository;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Mock
    private PriceService priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;


    @Test
    void savePriceNotFoundTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("CART001");
        dto.setProduct("PROD001");
        dto.setQuantity(BigDecimal.ONE);

        when(cartEntryRepository.findByIdentifier("CART001_PROD001"))
                .thenReturn(null);

        when(priceRepository.findByIdentifierAndDeletedFalse("PROD001"))
                .thenReturn(null);

        CartEntryDto response = cartEntryService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(
                response.getMessage().contains("Price details not found"));
    }


    @Test
    void updateTestFailure() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("ENTRY1");

        when(
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

        when(
                cartEntryRepository.findByIdentifier("ENTRY1")
        ).thenReturn(cartEntry);

        when(
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

        when(cartEntryRepository.findAll())
                .thenReturn(entities);

        when(
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

        when(
                cartEntryRepository.findAll(pageable)
        ).thenReturn(page);

        when(
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

        when(
                cartEntryRepository.findByCartId("CART1")
        ).thenReturn(entities);

        when(
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