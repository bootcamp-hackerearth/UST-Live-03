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
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    @Test
    void saveTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(null);
        Price price = new Price();
        Mockito.when(modelMapper.map(priceDto, Price.class)).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);
        PriceDto response = priceService.save(priceDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        Price existingPrice = new Price();
        existingPrice.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(existingPrice);
        PriceDto response = priceService.save(priceDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Price price = new Price();
        price.setIdentifier("Admin");
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);
        PriceDto response = priceService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        Price existingPrice = new Price();
        existingPrice.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(existingPrice);
        Mockito.when(priceRepository.save(existingPrice)).thenReturn(existingPrice);
        PriceDto response = priceService.update(priceDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(null);
        PriceDto response = priceService.update(priceDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(priceRepository).deleteByIdentifier("Admin");
        boolean response = priceService.delete("Admin");
        Assertions.assertEquals(true, response);
    }

    @Test
    void findAllTest() {
        Price price = new Price();
        price.setIdentifier("Admin");
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        List<Price> prices = List.of(price);
        List<PriceDto> priceDtos = List.of(priceDto);
        Page<Price> pricePage = new PageImpl<>(prices,
                PageRequest.of(0, 2), prices.size());
        Pageable pageable = PageRequest.of(0,
                50, Sort.by(new ArrayList<>()));
        Mockito.when(priceRepository.findAll(pageable)).thenReturn(pricePage);
        Mockito.when(modelMapper.map(
                Mockito.eq(prices),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(priceDtos);
        WsDto<PriceDto> response = priceService.findAll(pageable);
        Assertions.assertEquals(priceDtos, response.getDtoList());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findByStatusTest() {
        Price price = new Price();
        price.setIdentifier("Admin");
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        List<Price> prices = List.of(price);
        List<PriceDto> priceDtos = List.of(priceDto);
        Mockito.when(priceRepository.findByStatusIsTrue()).thenReturn(prices);
        Mockito.when(modelMapper.map(
                Mockito.eq(prices),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(priceDtos);
        List<PriceDto> response = priceService.findIfTrue();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {
        Price price = new Price();
        price.setStatus(false);
        PriceDto priceDto = new PriceDto();
        priceDto.setStatus(true);
        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);
        PriceDto response = priceService.toggleStatus("Admin");
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        Price price = new Price();
        price.setStatus(true);
        PriceDto priceDto = new PriceDto();
        priceDto.setStatus(false);
        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);
        PriceDto response = priceService.toggleStatus("Admin");
        Assertions.assertFalse(response.isStatus());
    }
}