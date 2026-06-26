package com.ust.pos;

import com.ust.pos.cartentry.impl.CartEntryServiceImpl;
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
    void saveNewCartEntrySuccess() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("cart1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        PriceDto priceDto = new PriceDto();
        priceDto.setCostPrice(BigDecimal.valueOf(100));
        priceDto.setDifference(BigDecimal.valueOf(20));


        Mockito.when(
                cartEntryRepository.findByIdentifier("cart1_P1")
        ).thenReturn(null);

        Mockito.when(
                priceService.findByIdentifier("P1")
        ).thenReturn(priceDto);

        CartEntryDto response =
                cartEntryService.save(dto);

        Assertions.assertEquals(
                "cart1_P1",
                response.getIdentifier()
        );

        Assertions.assertEquals(
                BigDecimal.valueOf(2),
                response.getQuantity()
        );

        Assertions.assertEquals(
                BigDecimal.valueOf(100),
                response.getUnitPrice()
        );

        Mockito.verify(cartEntryRepository)
                .save(Mockito.any(CartEntry.class));
    }

    @Test
    void saveExistingCartEntrySuccess() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("cart1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.valueOf(3));

        PriceDto priceDto = new PriceDto();
        priceDto.setCostPrice(BigDecimal.valueOf(100));
        priceDto.setDifference(BigDecimal.valueOf(10));

        Mockito.when(
                cartEntryRepository.findByIdentifier("cart1_P1")
        ).thenReturn(existing);

        Mockito.when(
                priceService.findByIdentifier("P1")
        ).thenReturn(priceDto);

        CartEntryDto response =
                cartEntryService.save(dto);

        Assertions.assertEquals(
                BigDecimal.valueOf(5),
                response.getQuantity()
        );

        Mockito.verify(cartEntryRepository)
                .save(existing);
    }

    @Test
    void updateSuccess() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("cart1_P1");
        dto.setQuantity(BigDecimal.valueOf(4));

        CartEntry existing = new CartEntry();
        existing.setIdentifier("cart1_P1");
        existing.setProduct("P1");

        PriceDto priceDto = new PriceDto();
        priceDto.setCostPrice(BigDecimal.valueOf(100));
        priceDto.setDifference(BigDecimal.valueOf(10));

        CartEntryDto mappedDto = new CartEntryDto();

        Mockito.when(
                cartEntryRepository.findByIdentifier("cart1_P1")
        ).thenReturn(existing);

        Mockito.when(
                priceService.findByIdentifier("P1")
        ).thenReturn(priceDto);

        Mockito.when(
                modelMapper.map(existing, CartEntryDto.class)
        ).thenReturn(mappedDto);

        CartEntryDto response =
                cartEntryService.update(dto);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                BigDecimal.valueOf(4),
                existing.getQuantity()
        );

        Mockito.verify(cartEntryRepository)
                .save(existing);
    }

    @Test
    void updateNotFound() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("cart1_P1");

        Mockito.when(
                cartEntryRepository.findByIdentifier("cart1_P1")
        ).thenReturn(null);

        CartEntryDto response =
                cartEntryService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "CartEntry with identifier - cart1_P1 is not found",
                response.getMessage()
        );
    }

    @Test
    void deleteTest() {

        cartEntryService.delete("cart1_P1");

        Mockito.verify(cartEntryRepository)
                .deleteByIdentifier("cart1_P1");
    }

    @Test
    void clearCartTest() {

        cartEntryService.clearCart("cart1");

        Mockito.verify(cartEntryRepository)
                .deleteByCartId("cart1");
    }

    @Test
    void findAllTest() {

        List<CartEntry> entities =
                List.of(new CartEntry(), new CartEntry());

        List<CartEntryDto> dtos =
                List.of(new CartEntryDto(), new CartEntryDto());

        Mockito.when(
                cartEntryRepository.findAll()
        ).thenReturn(entities);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(entities),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findByIdentifierTest() {

        CartEntry entity = new CartEntry();
        entity.setIdentifier("cart1_P1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("cart1_P1");

        Mockito.when(
                cartEntryRepository.findByIdentifier("cart1_P1")
        ).thenReturn(entity);

        Mockito.when(
                modelMapper.map(entity, CartEntryDto.class)
        ).thenReturn(dto);

        CartEntryDto response =
                cartEntryService.findByIdentifier("cart1_P1");

        Assertions.assertEquals(
                "cart1_P1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<CartEntry> entities =
                List.of(new CartEntry());

        Page<CartEntry> page =
                new PageImpl<>(entities);

        List<CartEntryDto> dtos =
                List.of(new CartEntryDto());

        Type listType =
                new TypeToken<List<CartEntryDto>>(){}.getType();

        Mockito.when(
                cartEntryRepository.findAll(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(entities, listType)
        ).thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.size()
        );
    }

    @Test
    void findByCartIdTest() {

        List<CartEntry> entities =
                List.of(new CartEntry(), new CartEntry());

        List<CartEntryDto> dtos =
                List.of(new CartEntryDto(), new CartEntryDto());

        Mockito.when(
                cartEntryRepository.findByCartId("cart1")
        ).thenReturn(entities);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(entities),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        List<CartEntryDto> response =
                cartEntryService.findByCartId("cart1");

        Assertions.assertEquals(
                2,
                response.size()
        );
    }
}