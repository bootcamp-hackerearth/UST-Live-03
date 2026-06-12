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
    void save_success() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");
        priceDto.setProductIdentifier("PROD1");

        Mockito.when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(null);
        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(null);
        Mockito.when(modelMapper.map(priceDto, Price.class)).thenReturn(new Price());

        PriceDto response = priceService.save(priceDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Price saved successfully", response.getMessage());
    }

    @Test
    void save_failure_productIdentifierAlreadyExists() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");
        priceDto.setProductIdentifier("PROD1");

        Mockito.when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(new Price());

        PriceDto response = priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("ProductIdentifier already exists: PROD1", response.getMessage());
    }

    @Test
    void save_failure_priceIdentifierAlreadyExists() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");
        priceDto.setProductIdentifier("PROD1");

        Mockito.when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(null);
        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(new Price());

        PriceDto response = priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price identifier already exists  PRICE1", response.getMessage());
    }


    @Test
    void findByIdentifier_success() {
        Price price = new Price();
        price.setIdentifier("PRICE1");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.findByIdentifier("PRICE1");

        Assertions.assertEquals("PRICE1", response.getIdentifier());
    }

    @Test
    void findByProductIdentifier_success() {
        Price price = new Price();
        price.setProductIdentifier("PROD1");

        PriceDto priceDto = new PriceDto();
        priceDto.setProductIdentifier("PROD1");

        Mockito.when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.findByProductIdentifier("PROD1");

        Assertions.assertEquals("PROD1", response.getProductIdentifier());
    }

    @Test
    void update_success_productIdentifierUnchanged() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");
        priceDto.setProductIdentifier("PROD1");

        Price existing = new Price();
        existing.setIdentifier("PRICE1");
        existing.setProductIdentifier("PROD1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(existing);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(priceRepository).save(existing);
    }

    @Test
    void update_success_productIdentifierChangedNoConflict() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");
        priceDto.setProductIdentifier("PROD2");

        Price existing = new Price();
        existing.setIdentifier("PRICE1");
        existing.setProductIdentifier("PROD1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(existing);
        Mockito.when(priceRepository.findByProductIdentifier("PROD2")).thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(priceRepository).save(existing);
    }

    @Test
    void update_failure_notFound() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price not found : PRICE1", response.getMessage());
    }

    @Test
    void update_failure_productIdentifierConflict() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");
        priceDto.setProductIdentifier("PROD2");

        Price existing = new Price();
        existing.setIdentifier("PRICE1");
        existing.setProductIdentifier("PROD1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(existing);
        Mockito.when(priceRepository.findByProductIdentifier("PROD2")).thenReturn(new Price());

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("ProductIdentifier already exists: PROD2", response.getMessage());
    }


    @Test
    void delete_success() {
        Assertions.assertDoesNotThrow(() -> priceService.delete("PRICE1"));

        Mockito.verify(priceRepository).deleteByIdentifier("PRICE1");
    }

    @Test
    void findAll_success() {
        Price price = new Price();
        price.setIdentifier("PRICE1");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");

        List<Price> prices = List.of(price);
        List<PriceDto> priceDtos = List.of(priceDto);

        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Page<Price> pricePage = new PageImpl<>(prices, pageable, prices.size());

        Mockito.when(priceRepository.findAll(pageable)).thenReturn(pricePage);
        Mockito.when(modelMapper.map(Mockito.eq(prices), Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(priceDtos);

        WsDto<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findIfTrue_success() {
        List<Price> prices = List.of(new Price());
        List<PriceDto> priceDtos = List.of(new PriceDto());

        Mockito.when(priceRepository.findByStatusIsTrue()).thenReturn(prices);
        Mockito.when(modelMapper.map(Mockito.eq(prices), Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(priceDtos);

        Assertions.assertEquals(1, priceService.findIfTrue().size());
    }

    @Test
    void toggle_trueToFalse() {
        Price price = new Price();
        price.setStatus(true);

        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(new PriceDto());

        priceService.toggleStatus("PRICE1");

        Assertions.assertFalse(price.isStatus());
    }

    @Test
    void toggle_falseToTrue() {
        Price price = new Price();
        price.setStatus(false);

        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(new PriceDto());

        priceService.toggleStatus("PRICE1");

        Assertions.assertTrue(price.isStatus());
    }
}