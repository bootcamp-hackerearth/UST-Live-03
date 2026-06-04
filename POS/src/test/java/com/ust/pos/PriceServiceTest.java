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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
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
        //request data
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(null);
        Price price = new Price();
        Mockito.when(modelMapper.map(priceDto, Price.class)).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);
        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());

    }

    @Test
    void saveTestFailure() {
        //request data
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");
        Price price = new Price();

        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(price);
        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());

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
    void updateTest_Success() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");
        dto.setSellingPrice(200);
        dto.setCostPrice(150);

        Price existing = new Price();
        existing.setIdentifier("P1");

        Price mappedPrice = new Price();

        // Mock repository (existing found)
        Mockito.when(priceRepository.findByIdentifier("P1"))
                .thenReturn(existing);

        // Mock mapping
        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(mappedPrice);

        // Mock save
        Mockito.when(priceRepository.save(mappedPrice))
                .thenReturn(mappedPrice);

        PriceDto response = priceService.update(dto);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertEquals("P1", response.getIdentifier());

        // ✅ Verify difference calculation
        Assertions.assertEquals(50.0, response.getDifference());

        Mockito.verify(priceRepository).save(mappedPrice);
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

        Mockito.doNothing().when(priceRepository)
                .deleteByIdentifier("Admin");

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

        Mockito.when(priceRepository.findAll()).thenReturn(prices);
        Mockito.when(modelMapper.map(
                Mockito.eq(prices),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(priceDtos);

        List<PriceDto> response = priceService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAll_WithPagination_ShouldReturnPriceDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Price> prices = List.of(new Price());
        Page<Price> page = new PageImpl<>(prices);

        List<PriceDto> priceDtos = List.of(new PriceDto());

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        Mockito.when(priceRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(prices, listType))
                .thenReturn(priceDtos);

        List<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(priceRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(prices, listType);
    }
}