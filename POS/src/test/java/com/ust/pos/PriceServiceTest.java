package com.ust.pos;

import com.ust.pos.dto.PriceDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    // ================= SAVE =================

    @Test
    void saveTest_Success() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE001");
        dto.setCostPrice(BigDecimal.valueOf(100));
        dto.setSellingPrice(BigDecimal.valueOf(150));

        Price price = new Price();

        Mockito.when(priceRepository.findByIdentifier("PRICE001"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(price);

        Mockito.when(priceRepository.save(price))
                .thenReturn(price);

        PriceDto response = priceService.save(dto);

        Assertions.assertEquals(
                BigDecimal.valueOf(50),
                response.getDifference());

        Assertions.assertEquals(
                "PRICE001",
                response.getIdentifier());

        Mockito.verify(priceRepository)
                .save(price);
    }

    @Test
    void saveTest_Failure_WhenAlreadyExists() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE001");

        Mockito.when(priceRepository.findByIdentifier("PRICE001"))
                .thenReturn(new Price());

        PriceDto response = priceService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Price with identifier - PRICE001 already exists",
                response.getMessage());

        Mockito.verify(priceRepository,
                        Mockito.never())
                .save(Mockito.any());
    }

    // ================= UPDATE =================

    @Test
    void updateTest_Success() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE001");
        dto.setCostPrice(BigDecimal.valueOf(100));
        dto.setSellingPrice(BigDecimal.valueOf(250));

        Price existing = new Price();
        Price mapped = new Price();

        Mockito.when(priceRepository.findByIdentifier("PRICE001"))
                .thenReturn(existing);

        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(mapped);

        Mockito.when(priceRepository.save(mapped))
                .thenReturn(mapped);

        PriceDto response = priceService.update(dto);

        Assertions.assertEquals(
                BigDecimal.valueOf(150),
                response.getDifference());

        Mockito.verify(priceRepository)
                .save(mapped);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE001");

        Mockito.when(priceRepository.findByIdentifier("PRICE001"))
                .thenReturn(null);

        PriceDto response = priceService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Price with identifier - PRICE001 is not found",
                response.getMessage());

        Mockito.verify(priceRepository,
                        Mockito.never())
                .save(Mockito.any());
    }

    // ================= DELETE =================

    @Test
    void deleteTest() {

        Mockito.doNothing()
                .when(priceRepository)
                .deleteByIdentifier("PRICE001");

        priceService.delete("PRICE001");

        Mockito.verify(priceRepository)
                .deleteByIdentifier("PRICE001");
    }

    // ================= FIND BY IDENTIFIER =================

    @Test
    void findByIdentifierTest_Success() {

        Price price = new Price();
        price.setIdentifier("PRICE001");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE001");

        Mockito.when(priceRepository.findByIdentifier("PRICE001"))
                .thenReturn(price);

        Mockito.when(modelMapper.map(
                        price,
                        PriceDto.class))
                .thenReturn(dto);

        PriceDto response =
                priceService.findByIdentifier("PRICE001");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                "PRICE001",
                response.getIdentifier());
    }

    @Test
    void findByIdentifierTest_NotFound() {

        Mockito.when(priceRepository.findByIdentifier("PRICE001"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(
                        null,
                        PriceDto.class))
                .thenReturn(null);

        PriceDto response =
                priceService.findByIdentifier("PRICE001");

        Assertions.assertNull(response);
    }

    // ================= FIND ALL =================

    @Test
    void findAllTest() {

        List<Price> prices =
                List.of(new Price());

        List<PriceDto> priceDtos =
                List.of(new PriceDto());

        Type listType =
                new TypeToken<List<PriceDto>>() {
                }.getType();

        Mockito.when(priceRepository.findAll())
                .thenReturn(prices);

        Mockito.when(modelMapper.map(
                        prices,
                        listType))
                .thenReturn(priceDtos);

        List<PriceDto> response =
                priceService.findAll();

        Assertions.assertEquals(
                1,
                response.size());
    }

    // ================= PAGINATION =================

    @Test
    void findAll_WithPagination_NoSearch() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Price price = new Price();

        Page<Price> page =
                new PageImpl<>(List.of(price));

        PriceDto dto = new PriceDto();

        Mockito.when(priceRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.any(Price.class),
                        Mockito.eq(PriceDto.class)))
                .thenReturn(dto);

        Page<PriceDto> response =
                priceService.findAll(pageable, null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size());

        Mockito.verify(priceRepository)
                .findAll(pageable);
    }

    @Test
    void findAll_WithPagination_AndSearch() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Price price = new Price();

        Page<Price> page =
                new PageImpl<>(List.of(price));

        PriceDto dto = new PriceDto();

        Mockito.when(
                        priceRepository
                                .findByIdentifierContainingIgnoreCase(
                                        pageable,
                                        "PRICE"))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.any(Price.class),
                        Mockito.eq(PriceDto.class)))
                .thenReturn(dto);

        Page<PriceDto> response =
                priceService.findAll(pageable,
                        "PRICE");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getContent().size());

        Mockito.verify(priceRepository)
                .findByIdentifierContainingIgnoreCase(
                        pageable,
                        "PRICE");
    }
}