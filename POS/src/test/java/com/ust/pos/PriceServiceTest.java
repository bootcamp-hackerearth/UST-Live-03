package com.ust.pos;

import com.ust.pos.dto.PageDto;
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
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(null);

        Price price = new Price();
        Mockito.when(modelMapper.map(priceDto, Price.class))
                .thenReturn(price);
        Mockito.when(priceRepository.save(price))
                .thenReturn(price);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals("Price1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(new Price());

        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals("Price1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Price price = new Price();
        price.setIdentifier("Price1");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        PriceDto response = priceService.findByIdentifier("Price1");

        Assertions.assertEquals("Price1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("Price1");

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(existingPrice);
        Mockito.when(priceRepository.save(existingPrice))
                .thenReturn(existingPrice);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(priceRepository)
                .deleteByIdentifier("Price1");

        priceService.delete("Price1");

        Mockito.verify(priceRepository)
                .deleteByIdentifier("Price1");
    }
    @Test
    void findAllPaginationTest() {

        Price price = new Price();
        price.setIdentifier("Price1");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Price> pricePage = new PageImpl<>(List.of(price), pageable, 1);

        Mockito.when(priceRepository.findAll(pageable)).thenReturn(pricePage);

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        Mockito.when(modelMapper.map(Mockito.eq(pricePage.getContent()), Mockito.eq(listType))).thenReturn(List.of(priceDto));

        PageDto<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(1, response.getDtoList().size());

        Assertions.assertEquals("Price1", response.getDtoList().get(0).getIdentifier());

        Assertions.assertEquals(1, response.getTotalRecords());

        Assertions.assertEquals(1, response.getTotalPages());

        Assertions.assertEquals(10, response.getSizePerPage());

        Assertions.assertEquals(0, response.getPage());
    }
}