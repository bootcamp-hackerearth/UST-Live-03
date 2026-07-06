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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
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
    void findByIdentifierTest() {
        Price price = new Price();
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRC01");

        Mockito.when(priceRepository.findByIdentifier("PRC01")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.findByIdentifier("PRC01");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("PRC01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestNull() {
        Mockito.when(priceRepository.findByIdentifier("PRC01")).thenReturn(null);

        PriceDto response = priceService.findByIdentifier("PRC01");

        Assertions.assertNull(response);
    }

    @Test
    void saveTestSuccess() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRC01");

        Mockito.when(priceRepository.findByIdentifier("PRC01")).thenReturn(null);
        Price price = new Price();
        Mockito.when(modelMapper.map(priceDto, Price.class)).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("PRC01", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRC01");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("PRC01");
        existingPrice.setDeleted(false);

        Mockito.when(priceRepository.findByIdentifier("PRC01")).thenReturn(existingPrice);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price with identifier - PRC01 already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRC01");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("PRC01");
        existingPrice.setDeleted(true);

        Mockito.when(priceRepository.findByIdentifier("PRC01")).thenReturn(existingPrice);

        PriceDto response = priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price with identifier PRC01 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRC01");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("PRC01");

        Mockito.when(priceRepository.findByIdentifier("PRC01")).thenReturn(existingPrice);
        Mockito.when(priceRepository.save(existingPrice)).thenReturn(existingPrice);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("PRC01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRC01");

        Mockito.when(priceRepository.findByIdentifier("PRC01")).thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price with identifier - PRC01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Price price = new Price();

        Mockito.when(priceRepository.findByIdentifier("PRC01")).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);

        boolean response = priceService.delete("PRC01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(priceRepository.findByIdentifier("PRC01")).thenReturn(null);

        boolean response = priceService.delete("PRC01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Price price = new Price();
        List<Price> priceList = List.of(price);
        Page<Price> pricePage = new PageImpl<>(priceList, pageable, priceList.size());

        PriceDto priceDto = new PriceDto();
        List<PriceDto> priceDtos = List.of(priceDto);

        Mockito.when(priceRepository.findByDeletedFalse(pageable)).thenReturn(pricePage);
        Mockito.when(modelMapper.map(Mockito.eq(priceList), Mockito.any(Type.class))).thenReturn(priceDtos);

        WsDto<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findIfTrueTest() {
        Price price = new Price();
        List<Price> priceList = List.of(price);
        PriceDto priceDto = new PriceDto();
        List<PriceDto> priceDtos = List.of(priceDto);

        Mockito.when(priceRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(priceList);
        Mockito.when(modelMapper.map(Mockito.eq(priceList), Mockito.any(Type.class))).thenReturn(priceDtos);

        List<PriceDto> response = priceService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Price price = new Price();
        price.setStatus(false);
        PriceDto priceDto = new PriceDto();
        priceDto.setStatus(true);

        Mockito.when(priceRepository.findByIdentifier("PRC01")).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.toggleStatus("PRC01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findByIdentifierAndDeletedFalseTest() {
        Price price = new Price();
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PRC01");

        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("PRC01")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto response = priceService.findByIdentifierAndDeletedFalse("PRC01");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("PRC01", response.getIdentifier());
    }

    @Test
    void findByIdentifierAndDeletedFalseTestNull() {
        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("PRC01")).thenReturn(null);

        PriceDto response = priceService.findByIdentifierAndDeletedFalse("PRC01");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Price> specification = Mockito.mock(Specification.class);
        Price price = new Price();
        List<Price> priceList = List.of(price);
        Page<Price> page = new PageImpl<>(priceList, pageable, priceList.size());

        PriceDto priceDto = new PriceDto();
        List<PriceDto> priceDtos = List.of(priceDto);

        Mockito.when(priceRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(priceList), Mockito.any(Type.class))).thenReturn(priceDtos);

        WsDto<PriceDto> response = priceService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}