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
        dto.setCostPrice(100L);
        dto.setSellingPrice(150L);

        Price entity = new Price();

        Mockito.when(priceRepository.findByIdentifier("P1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(entity);
        Mockito.when(priceRepository.save(entity))
                .thenReturn(entity);

        PriceDto response = priceService.save(dto);

        Assertions.assertEquals("P1", response.getIdentifier());
        Assertions.assertEquals(50, response.getDifference());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(priceRepository).save(entity);
    }

    @Test
    void saveTestFailureWhenAlreadyExists() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Mockito.when(priceRepository.findByIdentifier("P1"))
                .thenReturn(new Price());

        PriceDto response = priceService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(priceRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");
        dto.setCostPrice(200L);
        dto.setSellingPrice(300L);

        Price existing = new Price();
        Price mapped = new Price();

        Mockito.when(priceRepository.findByIdentifier("P1"))
                .thenReturn(existing);
        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(mapped);
        Mockito.when(priceRepository.save(mapped))
                .thenReturn(mapped);

        PriceDto response = priceService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(100, response.getDifference());

        Mockito.verify(priceRepository).save(mapped);
    }

    @Test
    void updateTestFailureWhenNotFound() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Mockito.when(priceRepository.findByIdentifier("P1"))
                .thenReturn(null);

        PriceDto response = priceService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(priceRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Price entity = new Price();
        entity.setIdentifier("P1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Mockito.when(priceRepository.findByIdentifier("P1"))
                .thenReturn(entity);
        Mockito.when(modelMapper.map(entity, PriceDto.class))
                .thenReturn(dto);

        PriceDto response = priceService.findByIdentifier("P1");

        Assertions.assertEquals("P1", response.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<Price> entities = List.of(new Price());
        List<PriceDto> dtos = List.of(new PriceDto());

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        Mockito.when(priceRepository.findAll())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<PriceDto> response = priceService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(priceRepository)
                .deleteByIdentifier("P1");

        priceService.delete("P1");

        Mockito.verify(priceRepository)
                .deleteByIdentifier("P1");
    }

    @Test
    void findAllWithPaginationShouldReturnPriceDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Price> prices = List.of(new Price());
        Page<Price> page = new PageImpl<>(prices);

        List<PriceDto> priceDtos = List.of(new PriceDto());

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        Mockito.when(priceRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(prices, listType))
                .thenReturn(priceDtos);

        List<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(priceRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(prices, listType);
    }
}
