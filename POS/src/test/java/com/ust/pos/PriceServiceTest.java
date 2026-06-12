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
    void saveTest_Success() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("Admin");
        dto.setCostPrice(100L);
        dto.setSellingPrice(150L);
        Price entity = new Price();
        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(entity);
        Mockito.when(priceRepository.save(entity))
                .thenReturn(entity);
        PriceDto response = priceService.save(dto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertEquals(50, response.getDifference());
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(priceRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenAlreadyExists() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(new Price());
        PriceDto response = priceService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(priceRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("Admin");
        dto.setCostPrice(200L);
        dto.setSellingPrice(300L);
        Price existing = new Price();
        Price mapped = new Price();
        Mockito.when(priceRepository.findByIdentifier("Admin"))
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
    void updateTest_Failure_WhenNotFound() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifier("Admin"))
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
        entity.setIdentifier("Admin");
        PriceDto dto = new PriceDto();
        dto.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(entity);
        Mockito.when(modelMapper.map(entity, PriceDto.class))
                .thenReturn(dto);
        PriceDto response = priceService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<Price> entities = List.of(new Price());
        List<PriceDto> dtos = List.of(new PriceDto());
        Type listType = new TypeToken<List<PriceDto>>() {}.getType();
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
                .deleteByIdentifier("Admin");
        priceService.delete("Admin");
        Mockito.verify(priceRepository)
                .deleteByIdentifier("Admin");
    }

    @Test
    void findAll_WithPagination_ShouldReturnPriceDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Price price = new Price();
        price.setIdentifier("PRICE1");
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");
        Page<Price> page = new PageImpl<>(List.of(price));
        Mockito.when(priceRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);
        Page<PriceDto> response = priceService.findAll(pageable, null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals("PRICE1",
                response.getContent().get(0).getIdentifier());
        Mockito.verify(priceRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(price, PriceDto.class);
    }

    @Test
    void findAll_WithSearch_ShouldReturnPriceDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Price price = new Price();
        PriceDto dto = new PriceDto();
        Page<Price> page = new PageImpl<>(List.of(price));
        Mockito.when(priceRepository.findByIdentifierContainingIgnoreCase(
                                "ABC", pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);
        Page<PriceDto> response =
                priceService.findAll(pageable, "ABC");
        Assertions.assertEquals(1, response.getContent().size());
        Mockito.verify(priceRepository)
                .findByIdentifierContainingIgnoreCase("ABC", pageable);
    }
}
