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
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    @Test
    void saveTestSuccess() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Price price = new Price();

        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(priceDto, Price.class))
                .thenReturn(price);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertEquals(priceDto.isSuccess(), response.isSuccess());

        Mockito.verify(priceRepository, Mockito.times(1))
                .save(price);
    }

    @Test
    void findByIdentifierTest() {
        Price price = new Price();
        price.setIdentifier("Admin");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        PriceDto response = priceService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTestSuccess() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Price price = new Price();
        price.setIdentifier("Admin");

        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(price);
        Mockito.when(priceRepository.save(price))
                .thenReturn(price);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertEquals("Product Updated Successfully", response.getMessage());
    }

    @Test
    void updateTestFailure_NotFound() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price not found!", response.getMessage());
    }

    @Test
    void updateTestFailure_DuplicateIdentifier() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("NEW");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("OLD");

        Mockito.when(priceRepository.findByIdentifier("NEW"))
                .thenReturn(existingPrice);
        Mockito.when(priceRepository.existsByIdentifier("NEW"))
                .thenReturn(true);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("This price already exist!", response.getMessage());
        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdTestSuccess() {
        Price price = new Price();
        price.setIdentifier("Admin");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Mockito.when(priceRepository.findById(1L))
                .thenReturn(Optional.of(price));
        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        PriceDto response = priceService.findById(1L);

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdTestNotFound() {
        Mockito.when(priceRepository.findById(99L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class,
                () -> priceService.findById(99L));
    }

    @Test
    void findAllTest() {
        Price price = new Price();
        price.setIdentifier("Admin");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Page<Price> page = new PageImpl<>(List.of(price));

        Mockito.when(priceRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(priceDto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        List<PriceDto> response = priceService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(priceRepository)
                .deleteByIdentifier("Admin");

        priceService.delete("Admin");

        Mockito.verify(priceRepository, Mockito.times(1))
                .deleteByIdentifier("Admin");
    }

    @Test
    void changePriceStatusTest() {
        Price price = new Price();
        price.setIdentifier("price1");
        price.setStatus(false);

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("price1");
        priceDto.setStatus(true);

        Mockito.when(priceRepository.findByIdentifier("price1")).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.changePriceStatus("price1", true);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isStatus());
        Assertions.assertEquals("price1", response.getIdentifier());

        Mockito.verify(priceRepository, Mockito.times(1)).save(price);
    }

    @Test
    void changePriceStatus_NotFound() {
        Mockito.when(priceRepository.findByIdentifier("price1")).thenReturn(null);

        PriceDto response = priceService.changePriceStatus("price1", true);

        Assertions.assertNull(response);
    }

    @Test
    void saveTestFailure_DuplicateIdentifier() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRICE1");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("PRICE1");

        Mockito.when(priceRepository.findByIdentifier("PRICE1"))
                .thenReturn(existingPrice);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Price with identifier - PRICE1 already exists",
                response.getMessage()
        );

        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }
}