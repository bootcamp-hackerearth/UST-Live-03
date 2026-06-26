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
        priceDto.setSuccess(true);

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

        Mockito.verify(priceRepository).save(price);
    }

    @Test
    void saveTestFailure() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(new Price());

        PriceDto response = priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(priceRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void saveSoftDeletedFailureTest() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        Price existingPrice = new Price();
        existingPrice.setDeleted(true);

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(existingPrice);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(
                response.getMessage().contains("soft deleted")
        );
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

        PriceDto response =
                priceService.findByIdentifier("Price1");

        Assertions.assertEquals(
                "Price1",
                response.getIdentifier()
        );
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(null);

        PriceDto response =
                priceService.findByIdentifier("Price1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Price not found",
                response.getMessage()
        );
    }

    @Test
    void updateTest() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");
        priceDto.setSuccess(true);

        Price existingPrice = new Price();
        existingPrice.setIdentifier("Price1");

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(existingPrice);

        Mockito.when(priceRepository.save(existingPrice))
                .thenReturn(existingPrice);

        PriceDto response =
                priceService.update(priceDto);

        Mockito.verify(modelMapper)
                .map(priceDto, existingPrice);

        Mockito.verify(priceRepository)
                .save(existingPrice);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(null);

        PriceDto response =
                priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        Price price = new Price();
        price.setIdentifier("Price1");
        price.setDeleted(false);

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(price);

        boolean result =
                priceService.delete("Price1");

        Assertions.assertTrue(result);
        Assertions.assertTrue(price.getDeleted());

        Mockito.verify(priceRepository)
                .save(price);
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(null);

        boolean result =
                priceService.delete("Price1");

        Assertions.assertFalse(result);

        Mockito.verify(priceRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {

        Price price = new Price();
        price.setIdentifier("Price1");
        price.setStatus(true);

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(price);

        Mockito.when(priceRepository.save(price))
                .thenReturn(price);

        priceService.toggleStatus("Price1");

        Assertions.assertFalse(price.getStatus());

        Mockito.verify(priceRepository)
                .save(price);
    }

    @Test
    void toggleStatusTestFailure() {

        Mockito.when(priceRepository.findByIdentifier("Price1"))
                .thenReturn(null);

        priceService.toggleStatus("Price1");

        Mockito.verify(priceRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Price price = new Price();
        price.setIdentifier("Price1");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Price> pricePage =
                new PageImpl<>(List.of(price), pageable, 1);

        Mockito.when(priceRepository.findByDeletedFalse(pageable))
                .thenReturn(pricePage);

        Type listType =
                new TypeToken<List<PriceDto>>() {
                }.getType();

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(pricePage.getContent()),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(priceDto));

        PageDto<PriceDto> response =
                priceService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Price1",
                response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findActivePricesTest() {

        Price price = new Price();
        price.setIdentifier("Price1");
        price.setStatus(true);

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Price1");

        List<Price> prices = List.of(price);

        Type listType =
                new TypeToken<List<PriceDto>>() {
                }.getType();

        Mockito.when(priceRepository.findByStatusTrue())
                .thenReturn(prices);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(prices),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(priceDto));

        List<PriceDto> response =
                priceService.findActivePrices();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "Price1",
                response.get(0).getIdentifier()
        );
    }
}