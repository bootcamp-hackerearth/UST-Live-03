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
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(priceRepository).save(price);
    }

    @Test
    void saveFailureTest() {

        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("PROD1");
        priceDto.setPriceType("MRP");

        Mockito.when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(new Price());

        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals("PROD1_MRP", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
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
        Assertions.assertNotNull(response.getMessage());
    }


    @Test
    void deleteSuccessTest() {

        priceService.delete("PROD1_MRP");

        verify(priceRepository).deleteByIdentifier("PROD1_MRP");
    }


    @Test
    void findByIdentifierSuccessTest() {

        Price price = new Price();
        price.setIdentifier("PROD1_MRP");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PROD1_MRP");

        Mockito.when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.findByIdentifier("PROD1_MRP");

        Assertions.assertEquals("PROD1_MRP", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(priceRepository.findByIdentifier("PROD1_MRP")).thenReturn(null);

        PriceDto response = priceService.findByIdentifier("PROD1_MRP");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSuccessTest() {

        Price p1 = new Price();
        p1.setIdentifier("PROD1_MRP");

        Price p2 = new Price();
        p2.setIdentifier("PROD2_SP");

        List<Price> prices = List.of(p1, p2);

        PriceDto d1 = new PriceDto();
        d1.setIdentifier("PROD1_MRP");

        PriceDto d2 = new PriceDto();
        d2.setIdentifier("PROD2_SP");

        List<PriceDto> priceDtos = List.of(d1, d2);

        Page<Price> page = new PageImpl<>(prices);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(priceRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(prices), Mockito.any(Type.class))).thenReturn(priceDtos);

        List<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
    }
}