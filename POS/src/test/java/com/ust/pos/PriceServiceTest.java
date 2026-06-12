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
    void saveTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("P1");
        priceDto.setPriceType("T1");
        Mockito.when(priceRepository.findByIdentifier(null))
                .thenReturn(null);
        Price price = new Price();
        Mockito.when(modelMapper.map(priceDto, Price.class))
                .thenReturn(price);
        PriceDto response = priceService.save(priceDto);
        Assertions.assertEquals("P1_T1", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("P1");
        priceDto.setPriceType("T1");
        Mockito.when(priceRepository.findByIdentifier(Mockito.any()))
                .thenReturn(new Price());
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
    void findByIdentifierNullTest() {
        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        PriceDto response = priceService.findByIdentifier("Admin");
        Assertions.assertNull(response);
    }

    @Test
    void updateTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        Price existingPrice = new Price();
        existingPrice.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(existingPrice);
        Mockito.when(priceRepository.save(existingPrice))
                .thenReturn(existingPrice);
        PriceDto response = priceService.update(priceDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        PriceDto response = priceService.update(priceDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        priceService.delete("Admin");
        Mockito.verify(priceRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {
        Price price = new Price();
        price.setIdentifier("Admin");
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        List<Price> prices = List.of(price);
        List<PriceDto> priceDtos = List.of(priceDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> pricePage =
                new PageImpl<>(prices, pageable, prices.size());
        Mockito.when(priceRepository.findAll(pageable))
                .thenReturn(pricePage);
        Mockito.when(modelMapper.map(
                Mockito.eq(prices),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(priceDtos);
        WsDto<PriceDto> response = priceService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> pricePage = new PageImpl<>(List.of());
        Mockito.when(priceRepository.findAll(pageable))
                .thenReturn(pricePage);
        Mockito.when(modelMapper.map(
                Mockito.eq(List.of()),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of());
        WsDto<PriceDto> response = priceService.findAll(pageable);
        Assertions.assertTrue(response.getDtoList().isEmpty());
    }
}