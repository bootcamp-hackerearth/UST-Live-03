package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
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
        priceDto.setProduct("P1");
        priceDto.setPriceType("T1");

        String identifier = "P1_T1";

        Mockito.when(priceRepository.findByIdentifier(identifier)).thenReturn(null);

        Price price = new Price();
        Mockito.when(modelMapper.map(priceDto, Price.class)).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals(identifier, response.getIdentifier());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveTestFailure() {

        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("P1");
        priceDto.setPriceType("T1");

        String identifier = "P1_T1";

        Price price = new Price();
        price.setIsDeleted(false);

        Mockito.when(priceRepository.findByIdentifier(identifier)).thenReturn(price);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals(identifier, response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailureDeletedRecord() {

        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("P1");
        priceDto.setPriceType("T1");

        Price existingPrice = new Price();
        existingPrice.setIsDeleted(true);

        Mockito.when(priceRepository.findByIdentifier("P1_T1")).thenReturn(existingPrice);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("was deleted"));
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
    void updateTest() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("OLD_ID");
        priceDto.setProduct("P1");
        priceDto.setPriceType("T1");

        Price existingPrice = new Price();
        existingPrice.setId(1L);
        existingPrice.setIdentifier("OLD_ID");

        Mockito.when(priceRepository.findByIdentifier("OLD_ID")).thenReturn(existingPrice);
        Mockito.when(priceRepository.findByIdentifier("P1_T1")).thenReturn(existingPrice);
        Mockito.when(priceRepository.save(existingPrice)).thenReturn(existingPrice);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertEquals("P1_T1", response.getIdentifier());

        Mockito.verify(priceRepository).save(existingPrice);
    }

    @Test
    void updateTestNoDuplicate() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("OLD_ID");
        priceDto.setProduct("P1");
        priceDto.setPriceType("T1");

        Price existingPrice = new Price();
        existingPrice.setId(1L);
        existingPrice.setIdentifier("OLD_ID");

        Mockito.when(priceRepository.findByIdentifier("OLD_ID")).thenReturn(existingPrice);
        Mockito.when(priceRepository.findByIdentifier("P1_T1")).thenReturn(null);
        Mockito.when(priceRepository.save(existingPrice)).thenReturn(existingPrice);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertEquals("P1_T1", response.getIdentifier());

        Mockito.verify(priceRepository).save(existingPrice);
    }

    @Test
    void updateTestFailure() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTestDuplicateFailure() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("OLD_ID");
        priceDto.setProduct("P1");
        priceDto.setPriceType("T1");

        Price existingPrice = new Price();
        existingPrice.setId(1L);
        existingPrice.setIdentifier("OLD_ID");

        Price duplicatePrice = new Price();
        duplicatePrice.setId(2L);

        Mockito.when(priceRepository.findByIdentifier("OLD_ID")).thenReturn(existingPrice);
        Mockito.when(priceRepository.findByIdentifier("P1_T1")).thenReturn(duplicatePrice);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestDuplicateSameId() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("OLD_ID");
        priceDto.setProduct("P1");
        priceDto.setPriceType("T1");

        Price existingPrice = new Price();
        existingPrice.setId(1L);
        existingPrice.setIdentifier("OLD_ID");

        Price duplicatePrice = new Price();
        duplicatePrice.setId(1L);

        Mockito.when(priceRepository.findByIdentifier("OLD_ID")).thenReturn(existingPrice);
        Mockito.when(priceRepository.findByIdentifier("P1_T1")).thenReturn(duplicatePrice);
        Mockito.when(priceRepository.save(existingPrice)).thenReturn(existingPrice);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertEquals("P1_T1", response.getIdentifier());

        Mockito.verify(priceRepository).save(existingPrice);
    }

    @Test
    void deleteTest() {

        Price price = new Price();
        price.setIdentifier("Admin");
        price.setStatus(true);
        price.setIsDeleted(false);

        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);

        PriceDto response = priceService.delete("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Price deleted successfully", response.getMessage());
        Assertions.assertTrue(price.getIsDeleted());
        Assertions.assertFalse(price.getStatus());

        Mockito.verify(priceRepository).save(price);
    }

    @Test
    void deleteTestNotFound() {

        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(null);

        PriceDto response = priceService.delete("Admin");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void findAllTest() {

        Price price = new Price();
        price.setIdentifier("Admin");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        List<Price> prices = List.of(price);
        List<PriceDto> priceDtos = List.of(priceDto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> pricePage = new PageImpl<>(prices, pageable, prices.size());

        Mockito.when(priceRepository.findByIsDeleted(
                Mockito.eq(false),
                Mockito.any(Pageable.class)
        )).thenReturn(pricePage);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(priceDtos);

        PaginatedResponseDto<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals("Admin", response.getItems().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllActiveTest() {

        Price price = new Price();
        price.setIdentifier("Admin");
        price.setStatus(true);

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        List<Price> prices = List.of(price);
        List<PriceDto> priceDtos = List.of(priceDto);

        Mockito.when(priceRepository.findByStatusAndIsDeleted(true, false)).thenReturn(prices);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(priceDtos);

        List<PriceDto> response = priceService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTrueTest() {

        Price price = new Price();
        price.setIdentifier("Admin");
        price.setStatus(false);

        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);

        priceService.changeStatus("Admin", true);

        Assertions.assertTrue(price.getStatus());
        Mockito.verify(priceRepository).save(price);
    }

    @Test
    void changeStatusFalseTest() {

        Price price = new Price();
        price.setIdentifier("Admin");
        price.setStatus(true);

        Mockito.when(priceRepository.findByIdentifier("Admin")).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);

        priceService.changeStatus("Admin", false);

        Assertions.assertFalse(price.getStatus());
        Mockito.verify(priceRepository).save(price);
    }
}