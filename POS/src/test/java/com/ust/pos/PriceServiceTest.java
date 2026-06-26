package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.data.domain.*;

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
        dto.setProduct("P001");
        dto.setType("RETAIL");
        String identifier = "P001_RETAIL";
        Price price = new Price();
        Mockito.when(priceRepository.findByIdentifier(identifier)).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Price.class)).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);
        PriceDto response = priceService.save(dto);
        Assertions.assertEquals(identifier, response.getIdentifier());
        Mockito.verify(priceRepository).save(price);
    }

    @Test
    void saveDuplicatePriceTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P001");
        dto.setType("RETAIL");
        Price existing = new Price();
        Mockito.when(priceRepository.findByIdentifier("P001_RETAIL")).thenReturn(existing);
        PriceDto response = priceService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedPriceTest() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P001");
        dto.setType("RETAIL");
        Price existing = new Price();
        existing.setDeleted(true);
        Mockito.when(priceRepository.findByIdentifier("P001_RETAIL")).thenReturn(existing);
        PriceDto response = priceService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Price price = new Price();
        price.setIdentifier("P001");
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P001");
        Mockito.when(priceRepository.findByIdentifier("P001")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);
        PriceDto response = priceService.findByIdentifier("P001");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("P001", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(priceRepository.findByIdentifier("P001")).thenReturn(null);
        PriceDto response = priceService.findByIdentifier("P001");
        Assertions.assertNull(response);
    }

    @Test
    void updateTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P001");
        Price existingPrice = new Price();
        Mockito.when(priceRepository.findByIdentifier("P001")).thenReturn(existingPrice);
        Mockito.when(priceRepository.save(existingPrice)).thenReturn(existingPrice);
        PriceDto response = priceService.update(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(priceRepository).save(existingPrice);
    }

    @Test
    void updateNotFoundTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P001");
        Mockito.when(priceRepository.findByIdentifier("P001")).thenReturn(null);
        PriceDto response = priceService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteByIdentifierTest() {
        Price price = new Price();
        price.setIdentifier("P001");
        Mockito.when(priceRepository.findByIdentifier("P001")).thenReturn(price);
        priceService.deleteByIdentifier("P001");
        Assertions.assertTrue(price.isDeleted());
        Mockito.verify(priceRepository).save(price);
    }

    @Test
    void deleteByIdentifierNotFoundTest() {
        Mockito.when(priceRepository.findByIdentifier("P001")).thenReturn(null);
        Assertions.assertThrows(RuntimeException.class, () -> priceService.deleteByIdentifier("P001"));
    }

    @Test
    void findAllWithPageableTest() {
        Price price = new Price();
        price.setIdentifier("P001");
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P001");
        List<Price> prices = List.of(price);
        List<PriceDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Price> page = new PageImpl<>(prices);
        Mockito.when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(prices), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<PriceDto> response = priceService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("P001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Price price = new Price();
        price.setIdentifier("P001");
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P001");
        List<Price> prices = List.of(price);
        List<PriceDto> dtos = List.of(dto);
        Mockito.when(priceRepository.findAll()).thenReturn(prices);
        Mockito.when(modelMapper.map(Mockito.eq(prices), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<PriceDto> response = priceService.findAll(null);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("P001", response.getDtoList().get(0).getIdentifier());
    }
}