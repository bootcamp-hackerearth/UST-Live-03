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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    @Test
    void saveSuccessTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE1");

        Price price = new Price();
        price.setIdentifier("PRICE1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Price.class)).thenReturn(price);

        PriceDto result = priceService.save(dto);

        Assertions.assertEquals("PRICE1", result.getIdentifier());
        Mockito.verify(priceRepository).save(price);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Price existing = new Price();
        existing.setIdentifier("PRICE1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(existing);

        PriceDto result = priceService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Price already exists for identifier: PRICE1", result.getMessage());

        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Price existing = new Price();
        existing.setIdentifier("PRICE2");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE2");

        Mockito.when(priceRepository.findByIdentifier("PRICE2")).thenReturn(existing);

        PriceDto result = priceService.update(dto);

        Assertions.assertEquals("PRICE2", result.getIdentifier());

        Mockito.verify(priceRepository).save(existing);
        Mockito.verify(modelMapper).map(dto, existing);
    }

    @Test
    void updateFailureNotFoundTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(priceRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        PriceDto result = priceService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Price not found for identifier: UNKNOWN", result.getMessage());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Price price = new Price();
        price.setIdentifier("PRICE3");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE3");

        Mockito.when(priceRepository.findByIdentifier("PRICE3")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);

        PriceDto result = priceService.findByIdentifier("PRICE3");

        Assertions.assertEquals("PRICE3", result.getIdentifier());
    }

    @Test
    void deleteTest() {
        priceService.delete("PRICE4");
        Mockito.verify(priceRepository).deleteByIdentifier("PRICE4");
    }

    @Test
    void findAllSuccessTest() {
        Price p1 = new Price();
        p1.setIdentifier("P1");

        Price p2 = new Price();
        p2.setIdentifier("P2");

        List<Price> prices = List.of(p1, p2);

        PriceDto d1 = new PriceDto();
        d1.setIdentifier("P1");

        PriceDto d2 = new PriceDto();
        d2.setIdentifier("P2");

        List<PriceDto> priceDtos = List.of(d1, d2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Price> pricePage = new PageImpl<>(prices, pageable, prices.size());

        Mockito.when(priceRepository.findAll(pageable)).thenReturn(pricePage);
        Mockito.when(modelMapper.map(Mockito.eq(prices), Mockito.any(Type.class))).thenReturn(priceDtos);

        List<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("P1", result.get(0).getIdentifier());
        Assertions.assertEquals("P2", result.get(1).getIdentifier());
    }
}