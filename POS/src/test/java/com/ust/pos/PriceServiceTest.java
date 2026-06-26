package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Price;
import com.ust.pos.modell.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    @Test
    void saveSuccessTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setType("MRP");
        dto.setPriceAmount(BigDecimal.valueOf(100));
        Price entity = new Price();
        Price saved = new Price();
        when(priceRepository.findByIdentifier("P1-MRP")).thenReturn(null);
        when(modelMapper.map(dto, Price.class)).thenReturn(entity);
        when(priceRepository.save(entity)).thenReturn(saved);
        when(modelMapper.map(saved, PriceDto.class)).thenReturn(new PriceDto());
        PriceDto response = priceService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Price saved successfully", response.getMessage());
        verify(priceRepository).save(entity);
    }

    @Test
    void saveDuplicateTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setType("MRP");
        when(priceRepository.findByIdentifier("P1-MRP")).thenReturn(new Price());
        PriceDto response = priceService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price already exists for product and type", response.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Price price = new Price();
        price.setIdentifier("P1-MRP");
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1-MRP");
        when(priceRepository.findByIdentifierAndDeletedFalse("P1-MRP")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);
        PriceDto result = priceService.findByIdentifier("P1-MRP");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1-MRP", result.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        when(priceRepository.findByIdentifierAndDeletedFalse("P1-MRP")).thenReturn(null);
        PriceDto result = priceService.findByIdentifier("P1-MRP");
        Assertions.assertNull(result);
    }

    @Test
    void updateSuccessTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1-MRP");
        dto.setProduct("P1");
        dto.setType("SELLING");
        dto.setPriceAmount(BigDecimal.valueOf(90));
        Price existing = new Price();
        existing.setId(1L);
        Price updated = new Price();
        when(priceRepository.findByIdentifierAndDeletedFalse("P1-MRP")).thenReturn(existing);
        when(priceRepository.findByIdentifier("P1-SELLING")).thenReturn(existing);
        when(priceRepository.save(existing)).thenReturn(updated);
        PriceDto mappedResponse = new PriceDto();
        when(modelMapper.map(updated, PriceDto.class)).thenReturn(mappedResponse);
        PriceDto result = priceService.update(dto);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Price updated successfully", result.getMessage());
        verify(priceRepository).save(existing);
    }

    @Test
    void updatePriceNotFoundTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1-MRP");
        when(priceRepository.findByIdentifierAndDeletedFalse("P1-MRP")).thenReturn(null);
        PriceDto result = priceService.update(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Price not found", result.getMessage());
    }

    @Test
    void updateDuplicateIdentifierTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1-MRP");
        dto.setProduct("P2");
        dto.setType("MRP");
        Price existing = new Price();
        existing.setId(1L);
        Price duplicate = new Price();
        duplicate.setId(2L);
        when(priceRepository.findByIdentifierAndDeletedFalse("P1-MRP")).thenReturn(existing);
        when(priceRepository.findByIdentifier("P2-MRP")).thenReturn(duplicate);
        PriceDto result = priceService.update(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Price already exists for this product and type", result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Price price = new Price();
        price.setIdentifier("P1-MRP");
        when(priceRepository.findByIdentifierAndDeletedFalse("P1-MRP")).thenReturn(price);
        priceService.delete("P1-MRP");
        verify(priceRepository).save(price);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Price price = new Price();
        price.setIdentifier("P1-MRP");
        List<Price> prices = List.of(price);
        List<PriceDto> dtoList = List.of(new PriceDto());
        Page<Price> page = new PageImpl<>(prices, pageable, prices.size());
        when(priceRepository.findALlByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(prices), any(Type.class))).thenReturn(dtoList);
        WsDto<PriceDto> result = priceService.findAll(pageable);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void updateSameIdentifierShouldPassTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1-MRP");
        dto.setProduct("P1");
        dto.setType("MRP");
        Price existing = new Price();
        existing.setId(1L);
        when(priceRepository.findByIdentifierAndDeletedFalse("P1-MRP")).thenReturn(existing);
        when(priceRepository.findByIdentifier("P1-MRP")).thenReturn(existing);
        when(priceRepository.save(existing)).thenReturn(existing);
        when(modelMapper.map(existing, PriceDto.class)).thenReturn(new PriceDto());
        PriceDto result = priceService.update(dto);
        Assertions.assertTrue(result.isSuccess());
    }
}