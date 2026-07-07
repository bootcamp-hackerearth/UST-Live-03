package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {
        Price price = new Price();
        price.setIdentifier("PROD1_MRP");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PROD1_MRP");

        when(priceRepository.findByIdentifierAndIsDeletedFalse("PROD1_MRP"))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result = priceService.findByIdentifier("PROD1_MRP");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PROD1_MRP", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(priceRepository.findByIdentifierAndIsDeletedFalse("PROD1_MRP"))
                .thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> priceService.findByIdentifier("PROD1_MRP")
        );

        Assertions.assertEquals(
                "Price with identifier 'PROD1_MRP' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveSuccessTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");
        dto.setPriceAmount(BigDecimal.valueOf(100));

        Price price = new Price();

        when(priceRepository.findByIdentifier("PROD1_MRP"))
                .thenReturn(null);
        when(modelMapper.map(dto, Price.class))
                .thenReturn(price);

        PriceDto result = priceService.save(dto);

        Assertions.assertEquals("PROD1_MRP", result.getIdentifier());

        verify(priceRepository).save(price);
    }

    @Test
    void saveAlreadyExistsTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        Price existingPrice = new Price();
        existingPrice.setDeleted(false);

        when(priceRepository.findByIdentifier("PROD1_MRP"))
                .thenReturn(existingPrice);

        PriceDto result = priceService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Price with identifier - PROD1_MRP already exists",
                result.getMessage()
        );

        verify(priceRepository, never()).save(any());
    }

    @Test
    void saveDeletedPriceTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        Price existingPrice = new Price();
        existingPrice.setDeleted(true);

        when(priceRepository.findByIdentifier("PROD1_MRP"))
                .thenReturn(existingPrice);

        PriceDto result = priceService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Price with identifier - PROD1_MRP was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(priceRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("PROD1_MRP");

        when(priceRepository.findByIdentifier("PROD1_MRP"))
                .thenReturn(existingPrice);

        PriceDto result = priceService.update(dto);

        Assertions.assertEquals("PROD1_MRP", result.getIdentifier());

        verify(modelMapper).map(dto, existingPrice);
        verify(priceRepository).save(existingPrice);
    }

    @Test
    void updateFailureTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        when(priceRepository.findByIdentifier("PROD1_MRP"))
                .thenReturn(null);

        PriceDto result = priceService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Price with identifier - PROD1_MRP not found",
                result.getMessage()
        );

        verify(priceRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Price price = new Price();

        when(priceRepository.findByIdentifier("PROD1_MRP"))
                .thenReturn(price);

        priceService.delete("PROD1_MRP");

        verify(priceRepository).findByIdentifier("PROD1_MRP");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Price price1 = new Price();
        Price price2 = new Price();

        List<Price> prices = List.of(price1, price2);

        Page<Price> page = new PageImpl<>(prices, pageable, 2);

        List<PriceDto> dtoList = List.of(
                new PriceDto(),
                new PriceDto()
        );

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        when(priceRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(prices, listType))
                .thenReturn(dtoList);

        WsDto<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void findByProductAndPriceTypeTest() {
        Price price = new Price();
        price.setIdentifier("PROD1_MRP");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PROD1_MRP");

        when(priceRepository.findByProductAndPriceType("PROD1", "MRP"))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result =
                priceService.findByProductAndPriceType("PROD1", "MRP");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PROD1_MRP", result.getIdentifier());
    }
}