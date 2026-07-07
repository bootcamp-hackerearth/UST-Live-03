package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");
        dto.setCostPrice(BigDecimal.valueOf(200));
        dto.setSellingPrice(BigDecimal.valueOf(100));

        Price price = new Price();

        Mockito.when(
                priceRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(dto, Price.class)
        ).thenReturn(price);

        PriceDto response = priceService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Assertions.assertEquals(
                BigDecimal.valueOf(100),
                response.getDifference()
        );

        Mockito.verify(priceRepository).save(price);
        Assertions.assertFalse(price.getDeleted());
    }

    @Test
    void saveTestFailure() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Mockito.when(
                priceRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(new Price());

        PriceDto response = priceService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Price for product - P1 already exists",
                response.getMessage()
        );

        Mockito.verify(
                priceRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");
        dto.setCostPrice(BigDecimal.valueOf(200));
        dto.setSellingPrice(BigDecimal.valueOf(100));

        Price existingPrice = new Price();

        Mockito.when(
                priceRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(existingPrice);

        PriceDto response = priceService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(modelMapper)
                .map(dto, existingPrice);

        Mockito.verify(priceRepository)
                .save(existingPrice);

        Assertions.assertEquals(
                BigDecimal.valueOf(100),
                existingPrice.getDifference()
        );
    }

    @Test
    void updateTestFailure() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Mockito.when(
                priceRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(null);

        PriceDto response = priceService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Price for product - P1 not defined",
                response.getMessage()
        );

        Mockito.verify(
                priceRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {

        Price price = new Price();
        price.setIdentifier("P1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Mockito.when(
                priceRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(price);

        Mockito.when(
                modelMapper.map(price, PriceDto.class)
        ).thenReturn(dto);

        PriceDto response =
                priceService.findByIdentifier("P1");

        Assertions.assertEquals(
                "P1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllTest() {

        List<Price> prices =
                List.of(new Price(), new Price());

        List<PriceDto> dtoList =
                List.of(new PriceDto(), new PriceDto());

        Mockito.when(
                priceRepository.findByDeletedFalse()
        ).thenReturn(prices);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(prices),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<PriceDto> response =
                priceService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Price> prices =
                List.of(new Price());

        Page<Price> page =
                new PageImpl<>(prices, pageable, 1);

        List<PriceDto> dtoList =
                List.of(new PriceDto());

        Type listType =
                new TypeToken<List<PriceDto>>() {}.getType();

        Mockito.when(
                priceRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(prices, listType)
        ).thenReturn(dtoList);

        WsDto<PriceDto> response =
                priceService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Price price = new Price();
        price.setIdentifier("P1");

        Example<Price> example = Example.of(
                price,
                ExampleMatcher.matching()
                        .withMatcher(
                                "identifier",
                                ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()
                        )
        );

        Page<Price> page =
                new PageImpl<>(List.of(price));

        Mockito.when(
                priceRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(price, PriceDto.class)
        ).thenReturn(new PriceDto());

        Page<PriceDto> response =
                priceService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAllWithoutSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Price price = new Price();

        Example<Price> example = Example.of(new Price());

        Page<Price> page =
                new PageImpl<>(List.of(price));

        Mockito.when(
                priceRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(price, PriceDto.class)
        ).thenReturn(new PriceDto());

        Page<PriceDto> response =
                priceService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void deleteTest() {

        Price price = new Price();
        price.setDeleted(false);

        Mockito.when(
                priceRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(price);

        priceService.delete("P1");

        Assertions.assertTrue(price.getDeleted());

        Mockito.verify(priceRepository)
                .save(price);
    }

    @Test
    void deleteNotFoundTest() {

        Mockito.when(
                priceRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(null);

        priceService.delete("P1");

        Mockito.verify(
                priceRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
}