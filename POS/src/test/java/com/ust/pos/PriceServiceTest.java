package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("T1");
        Mockito.when(priceRepository.findByIdentifier("P1_T1")).thenReturn(null);
        Price price = new Price();
        Mockito.when(modelMapper.map(dto, Price.class)).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);
        PriceDto response = priceService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("P1_T1", response.getIdentifier());
    }

    @Test
    void saveTestAlreadyExists() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("T1");
        Price existing = new Price();
        existing.setDeleted(false);
        Mockito.when(priceRepository.findByIdentifier("P1_T1")).thenReturn(existing);
        PriceDto response = priceService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("T1");
        Price existing = new Price();
        existing.setDeleted(true);
        Mockito.when(priceRepository.findByIdentifier("P1_T1")).thenReturn(existing);
        PriceDto response = priceService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Price price = new Price();
        PriceDto dto = new PriceDto();
        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);
        PriceDto response = priceService.findByIdentifier("P1");
        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> priceService.findByIdentifier("P1")
        );
    }

    @Test
    void updateTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");
        Price existing = new Price();
        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(priceRepository.save(existing)).thenReturn(existing);
        PriceDto response = priceService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");
        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(null);
        PriceDto response = priceService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Price price = new Price();
        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);
        priceService.delete("P1");
        Mockito.verify(priceRepository).save(price);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(priceRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(null);
        priceService.delete("P1");
        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        Price price = new Price();
        PriceDto dto = new PriceDto();
        List<Price> list = List.of(price);
        List<PriceDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Price> page = new PageImpl<>(list);
        Mockito.when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<PriceDto> response = priceService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Price> page = new PageImpl<>(List.of());
        Mockito.when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        WsDto<PriceDto> response = priceService.findAll(pageable);
        Assertions.assertTrue(response.getDtoList().isEmpty());
    }
    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> page = new PageImpl<>(List.of(new Price()), pageable, 1);
        Mockito.when(priceRepository.findAll(
                        Mockito.<org.springframework.data.jpa.domain.Specification<Price>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        List<PriceDto> dtoList = List.of(new PriceDto());
        Mockito.when(modelMapper.map(
                        Mockito.eq(page.getContent()),
                        Mockito.any(Type.class)))
                .thenReturn(dtoList);
        WsDto<PriceDto> result =
                priceService.findAll(
                        Mockito.mock(org.springframework.data.jpa.domain.Specification.class),
                        pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }
}