package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Price;
import com.ust.pos.modell.PriceRepository;
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

import java.math.BigDecimal;
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
        priceDto.setProduct("P1");
        priceDto.setType("T1");
        priceDto.setPriceAmount(BigDecimal.valueOf(100.0));
        String identifier = "P1-T1";
        Price mapped = new Price();
        Price saved = new Price();
        saved.setIdentifier(identifier);
        PriceDto responseDto = new PriceDto();
        Mockito.when(priceRepository.findByIdentifier(identifier)).thenReturn(null);
        Mockito.when(modelMapper.map(priceDto, Price.class)).thenReturn(mapped);
        Mockito.when(priceRepository.save(mapped)).thenReturn(saved);
        Mockito.when(modelMapper.map(saved, PriceDto.class)).thenReturn(responseDto);
        PriceDto response = priceService.save(priceDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Price saved successfully", response.getMessage());
    }

    @Test
    void save_failure_alreadyExists() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("P1");
        priceDto.setType("T1");
        String identifier = "P1-T1";
        Mockito.when(priceRepository.findByIdentifier(identifier)).thenReturn(new Price());
        PriceDto response = priceService.save(priceDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price already exists for product and type", response.getMessage()
        );
    }

    @Test
    void findByIdentifierTest() {
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
    void update_success() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("P1-T1");
        priceDto.setProduct("P1");
        priceDto.setType("T1");
        Price existing = new Price();
        existing.setId(1L);
        Price saved = new Price();
        PriceDto mappedBack = new PriceDto();
        Mockito.when(priceRepository.findByIdentifier("P1-T1")).thenReturn(existing);
        Mockito.when(priceRepository.findByIdentifier("P1-T1")).thenReturn(existing);
        Mockito.when(priceRepository.save(existing)).thenReturn(saved);
        Mockito.when(modelMapper.map(saved, PriceDto.class)).thenReturn(mappedBack);
        PriceDto response = priceService.update(priceDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Price updated successfully", response.getMessage());
    }

    @Test
    void update_failure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");
        Mockito.when(priceRepository.findByIdentifier("PRICE1")).thenReturn(null);
        PriceDto response = priceService.update(priceDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void delete_success() {
        Assertions.assertDoesNotThrow(() -> priceService.delete("PRICE1"));
        Mockito.verify(priceRepository).deleteByIdentifier("PRICE1");
    }

    @Test
    void findAllTest() {
        Price price = new Price();
        price.setIdentifier("Admin");
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        List<Price> prices = List.of(price);
        List<PriceDto> priceDtos = List.of(priceDto);
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Page<Price> pricePage = new PageImpl<>(prices, pageable, prices.size());
        Mockito.when(priceRepository.findAll(pageable)).thenReturn(pricePage);
        Mockito.when(modelMapper.map(Mockito.eq(prices), Mockito.any(java.lang.reflect.Type.class))).thenReturn(priceDtos);
        WsDto<PriceDto> response = priceService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPage());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(priceRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(modelMapper, Mockito.times(1)).map(Mockito.eq(prices), Mockito.any(java.lang.reflect.Type.class));
    }
}

