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

    // SAVE

    @Test
    void save_Success() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");
        dto.setCostPrice(BigDecimal.valueOf(100));
        dto.setSellingPrice(BigDecimal.valueOf(150));
        dto.setDiscountPrice(BigDecimal.valueOf(10));

        Price entity = new Price();

        Mockito.when(priceRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(entity);

        Mockito.when(priceRepository.save(entity))
                .thenReturn(entity);

        PriceDto result = priceService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Assertions.assertEquals(
                BigDecimal.valueOf(140),
                result.getSellingPrice()
        );

        Assertions.assertEquals(
                BigDecimal.valueOf(40),
                result.getDifference()
        );

        Mockito.verify(priceRepository).save(entity);
    }

    @Test
    void save_WhenExists_ShouldFail() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Mockito.when(priceRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(new Price());

        PriceDto result = priceService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    // UPDATE

    @Test
    void update_Success() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");
        dto.setCostPrice(BigDecimal.valueOf(100));
        dto.setSellingPrice(BigDecimal.valueOf(150));
        dto.setDiscountPrice(BigDecimal.valueOf(10));

        Price existing = new Price();
        existing.setIdentifier("P1");

        Mockito.when(priceRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(existing);

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(priceRepository.save(existing)).thenReturn(existing);

        PriceDto result = priceService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Assertions.assertEquals(
                BigDecimal.valueOf(50),
                result.getDifference()
        );

        Mockito.verify(priceRepository).save(existing);
    }

    @Test
    void update_WhenNotFound_ShouldFail() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Mockito.when(priceRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(null);

        PriceDto result = priceService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    // DELETE (SOFT DELETE)

    @Test
    void delete_Success() {

        Price entity = new Price();
        entity.setIdentifier("P1");

        Mockito.when(priceRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(entity);

        Mockito.when(priceRepository.save(entity)).thenReturn(entity);

        priceService.delete("P1");

        Assertions.assertTrue(entity.isDelete());

        Mockito.verify(priceRepository).save(entity);
    }

    @Test
    void delete_WhenNotFound_ShouldDoNothing() {

        Mockito.when(priceRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(null);

        priceService.delete("P1");

        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    // FIND ALL

    @Test
    void findAll_Success() {

        List<Price> entities = List.of(new Price());
        List<PriceDto> dtos = List.of(new PriceDto());

        Type type = new TypeToken<List<PriceDto>>() {
        }.getType();

        Mockito.when(priceRepository.findByIsDeleteFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, type))
                .thenReturn(dtos);

        List<PriceDto> result = priceService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // FIND BY IDENTIFIER

    @Test
    void findByIdentifier_Success() {

        Price entity = new Price();
        entity.setIdentifier("P1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Mockito.when(priceRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(entity);

        Mockito.when(modelMapper.map(entity, PriceDto.class))
                .thenReturn(dto);

        PriceDto result = priceService.findByIdentifier("P1");

        Assertions.assertEquals("P1", result.getIdentifier());
    }

    // PAGINATION

    @Test
    void findAll_WithPagination_NoSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Price entity = new Price();
        entity.setIdentifier("P1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Page<Price> page = new PageImpl<>(List.of(entity));

        Mockito.when(priceRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, PriceDto.class))
                .thenReturn(dto);

        Page<PriceDto> result = priceService.findAll(pageable, null);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAll_WithSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Price entity = new Price();
        entity.setIdentifier("P1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Page<Price> page = new PageImpl<>(List.of(entity));

        Mockito.when(priceRepository
                        .findByIdentifierContainingIgnoreCaseAndIsDeleteFalse("P", pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, PriceDto.class))
                .thenReturn(dto);

        Page<PriceDto> result = priceService.findAll(pageable, "P");

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAll_WithBlankSearch_ShouldFallback() {

        Pageable pageable = PageRequest.of(0, 10);

        Price entity = new Price();
        entity.setIdentifier("P1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");

        Page<Price> page = new PageImpl<>(List.of(entity));

        Mockito.when(priceRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, PriceDto.class))
                .thenReturn(dto);

        Page<PriceDto> result = priceService.findAll(pageable, " ");

        Assertions.assertEquals(1, result.getContent().size());
    }
}