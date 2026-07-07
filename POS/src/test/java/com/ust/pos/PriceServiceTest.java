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
import org.springframework.data.jpa.domain.Specification;

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
    void saveTest_Success() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("Admin");
        dto.setCostPrice(BigDecimal.valueOf(100));
        dto.setSellingPrice(BigDecimal.valueOf(150));

        Price entity = new Price();
        entity.setIdentifier("Admin");
        entity.setCostPrice(BigDecimal.valueOf(100));
        entity.setSellingPrice(BigDecimal.valueOf(150));

        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(entity);

        Mockito.when(priceRepository.save(entity))
                .thenReturn(entity);

        PriceDto response = priceService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());

        Assertions.assertEquals(
                0,
                BigDecimal.valueOf(150)
                        .compareTo(response.getSellingPrice())
        );

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(priceRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenAlreadyExists() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("Admin"))
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
        dto.setCostPrice(BigDecimal.valueOf(200));
        dto.setSellingPrice(BigDecimal.valueOf(300));

        Price existing = new Price();
        existing.setIdentifier("Admin");

        Price mapped = new Price();
        mapped.setIdentifier("Admin");
        mapped.setCostPrice(BigDecimal.valueOf(200));
        mapped.setSellingPrice(BigDecimal.valueOf(300));

        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(existing);

        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(mapped);

        Mockito.when(priceRepository.save(mapped))
                .thenReturn(mapped);

        PriceDto response = priceService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                0,
                BigDecimal.valueOf(200)
                        .compareTo(response.getCostPrice())
        );

        Assertions.assertEquals(
                0,
                BigDecimal.valueOf(300)
                        .compareTo(response.getSellingPrice())
        );

        Mockito.verify(priceRepository).save(mapped);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("Admin"))
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
        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("Admin"))
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

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        Mockito.when(priceRepository.findByDeletedFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<PriceDto> response = priceService.findAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(priceRepository)
                .findByDeletedFalse();

        Mockito.verify(modelMapper)
                .map(entities, listType);
    }

    @Test
    void deleteTest() {
        Price price = new Price();
        price.setIdentifier("PRICE1");
        price.setDeleted(false);

        Mockito.when(
                        priceRepository.findByIdentifierAndDeletedFalse("PRICE1"))
                .thenReturn(price);

        priceService.delete("PRICE1");

        Assertions.assertTrue(price.isDeleted());

        Mockito.verify(priceRepository)
                .findByIdentifierAndDeletedFalse("PRICE1");

        Mockito.verify(priceRepository)
                .save(price);
    }

    @Test
    void findAll_WithPagination_ShouldReturnPriceDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();
        price.setIdentifier("PRICE1");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");

        Page<Price> page =
                new PageImpl<>(List.of(price));

        Mockito.when(
                        priceRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(
                        modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        Page<PriceDto> response =
                priceService.findAll(null, pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Assertions.assertEquals(
                "PRICE1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(priceRepository)
                .findByDeletedFalse(pageable);

        Mockito.verify(modelMapper)
                .map(price, PriceDto.class);
    }

    @Test
    void findAll_WithSearch_ShouldReturnPriceDtos() {

        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();
        price.setIdentifier("PRICE1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE1");

        Page<Price> page =
                new PageImpl<>(List.of(price));

        Mockito.when(
                priceRepository.findAll(
                        Mockito.<Specification<Price>>any(),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        price,
                        PriceDto.class
                )
        ).thenReturn(dto);

        // Act
        Page<PriceDto> response =
                priceService.findAll(
                        "ABC",
                        pageable
                );

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Assertions.assertEquals(
                "PRICE1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(priceRepository)
                .findAll(
                        Mockito.<Specification<Price>>any(),
                        Mockito.eq(pageable)
                );

        Mockito.verify(priceRepository, Mockito.never())
                .findByDeletedFalse(Mockito.any(Pageable.class));

        Mockito.verify(modelMapper)
                .map(
                        price,
                        PriceDto.class
                );
    }
}
