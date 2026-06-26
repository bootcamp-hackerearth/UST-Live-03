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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("PROD1");
        priceDto.setPriceType("MRP");

        Price price = new Price();

        Mockito.when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(null);
        Mockito.when(modelMapper.map(priceDto, Price.class)).thenReturn(price);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals("PROD1_MRP", response.getIdentifier());
        verify(priceRepository).save(price);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("PROD1");
        priceDto.setPriceType("MRP");

        Price existingPrice = new Price();
        existingPrice.setDeleted(false);

        Mockito.when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(existingPrice);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals("PROD1_MRP", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price with identifier - PROD1_MRP already exists", response.getMessage());
        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("PROD1");
        priceDto.setPriceType("MRP");

        Price existingPrice = new Price();
        existingPrice.setDeleted(true);

        Mockito.when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(existingPrice);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals("PROD1_MRP", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Models with identifier - PROD1_MRP was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("PROD1");
        priceDto.setPriceType("MRP");

        Price existingPrice = new Price();

        Mockito.when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(existingPrice);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertEquals("PROD1_MRP", response.getIdentifier());
        verify(modelMapper).map(priceDto, existingPrice);
        verify(priceRepository).save(existingPrice);
    }

    @Test
    void updateFailureTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("PROD1");
        priceDto.setPriceType("MRP");

        Mockito.when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertEquals("PROD1_MRP", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price with identifier - PROD1_MRP not found", response.getMessage());
        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Price price = new Price();
        Mockito.when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);

        priceService.delete("PROD1_MRP");

        verify(priceRepository).findByIdentifier("PROD1_MRP");
    }

    @Test
    void findAllSuccessTest() {
        Price price = new Price();
        List<Price> priceList = List.of(price);

        PriceDto dto = new PriceDto();
        List<PriceDto> priceDtos = List.of(dto);

        Page<Price> page = new PageImpl<>(priceList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(priceRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(priceList), Mockito.any(Type.class))).thenReturn(priceDtos);

        WsDto<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Price price = new Price();
        PriceDto priceDto = new PriceDto();

        Mockito.when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.findByIdentifier("PROD1_MRP");

        Assertions.assertNotNull(response);
    }

    @Test
    void findByProductAndPriceTypeSuccessTest() {
        Price price = new Price();
        PriceDto priceDto = new PriceDto();

        Mockito.when(priceRepository.findByProductAndPriceType("PROD1", "MRP")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.findByProductAndPriceType("PROD1", "MRP");

        Assertions.assertNotNull(response);
        verify(priceRepository).findByProductAndPriceType("PROD1", "MRP");
    }
}