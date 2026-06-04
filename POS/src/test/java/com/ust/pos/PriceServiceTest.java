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

@ExtendWith(MockitoExtension.class)
public class PriceServiceTest {

    @InjectMocks
    PriceServiceImpl priceService;

    @Mock
    PriceRepository priceRepository;
    @Mock
    ModelMapper modelMapper;

    @Test
    void saveTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("P001");
        priceDto.setType("RETAIL");

        String expectedIdentifier = "P001_RETAIL";

        Price price = new Price();

        Mockito.when(priceRepository.findByIdentifier(expectedIdentifier))
                .thenReturn(null);
        Mockito.when(modelMapper.map(priceDto, Price.class))
                .thenReturn(price);
        Mockito.when(priceRepository.save(price))
                .thenReturn(price);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals(expectedIdentifier, response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void findAllWithPageableTest() {
        Price price = new Price();
        price.setIdentifier("P001");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("P001");

        List<Price> prices = List.of(price);
        List<PriceDto> dtos = List.of(priceDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Price> pricePage = new PageImpl<>(prices);

        Mockito.when(priceRepository.findAll(pageable))
                .thenReturn(pricePage);
        Mockito.when(modelMapper.map(
                Mockito.eq(prices),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        List<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("P001", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Price price = new Price();
        price.setIdentifier("P001");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P001");

        List<Price> prices = List.of(price);
        List<PriceDto> dtos = List.of(dto);

        Mockito.when(priceRepository.findAll())
                .thenReturn(prices);
        Mockito.when(modelMapper.map(
                Mockito.eq(prices),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        List<PriceDto> response = priceService.findAll(null);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void updateTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("P001");

        Price price = new Price();
        price.setIdentifier("P001");

        Mockito.when(priceRepository.findByIdentifier(priceDto.getIdentifier())).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("P001");

        Price price = new Price();
        price.setIdentifier("P001");

        Mockito.when(priceRepository.findByIdentifier(priceDto.getIdentifier())).thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("P001");

        Price price = new Price();
        price.setIdentifier("P001");

        Mockito.when(priceRepository.findByIdentifier("P001")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.findByIdentifier("P001");
        Assertions.assertEquals("P001", response.getIdentifier());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(priceRepository).deleteByIdentifier("P001");
        priceService.deleteByIdentifier("P001");
        Mockito.verify(priceRepository).deleteByIdentifier("P001");
    }

}