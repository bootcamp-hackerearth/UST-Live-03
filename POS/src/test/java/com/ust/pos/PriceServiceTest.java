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
    void save_success() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(priceDto, Price.class))
                .thenReturn(new Price());

        PriceDto response = priceService.save(priceDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_failure_alreadyExists() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1"))
                .thenReturn(new Price());

        PriceDto response = priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());
    }


    @Test
    void findByIdentifierTest() {
        Price price = new Price();
        price.setIdentifier("PRICE1");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1"))
                .thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        PriceDto response = priceService.findByIdentifier("PRICE1");

        Assertions.assertEquals("PRICE1", response.getIdentifier());
    }


    @Test
    void update_success() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1"))
                .thenReturn(new Price());

        PriceDto response = priceService.update(priceDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void update_failure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1"))
                .thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
    }


    @Test
    void delete_success() {
        Assertions.assertDoesNotThrow(() ->
                priceService.delete("PRICE1"));

        Mockito.verify(priceRepository)
                .deleteByIdentifier("PRICE1");
    }


    @Test
    void findAllTest() {
        Price price = new Price();
        price.setIdentifier("Admin");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        List<Price> prices = List.of(price);
        List<PriceDto> priceDtos = List.of(priceDto);

        Page<Price> pricePage = new PageImpl<>(prices, PageRequest.of(0, 2), prices.size());

        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));

        Mockito.when(priceRepository.findAll(pageable)).thenReturn(pricePage);
        Mockito.when(modelMapper.map(Mockito.eq(prices), Mockito.any(java.lang.reflect.Type.class))).thenReturn(priceDtos);

        List<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void findByStatusTrueTest() {
        List<Price> prices = List.of(new Price());
        List<PriceDto> priceDtos = List.of(new PriceDto());

        Mockito.when(priceRepository.findByStatusIsTrue())
                .thenReturn(prices);
        Mockito.when(modelMapper.map(
                Mockito.eq(prices),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(priceDtos);

        List<PriceDto> response = priceService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void toggle_activeToInactive() {
        Price price = new Price();
        price.setStatus(true);

        Mockito.when(priceRepository.findByIdentifier("PRICE1"))
                .thenReturn(price);

        priceService.toggleStatus("PRICE1");

        Assertions.assertFalse(price.isStatus());
    }

    @Test
    void toggle_inactiveToActive() {
        Price price = new Price();
        price.setStatus(false);

        Mockito.when(priceRepository.findByIdentifier("PRICE1"))
                .thenReturn(price);

        priceService.toggleStatus("PRICE1");

        Assertions.assertTrue(price.isStatus());
    }
}
