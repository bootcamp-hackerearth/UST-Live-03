package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

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
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        String identifier = "PROD1_MRP";

        Mockito.when(priceRepository.findByIdentifier(identifier)).thenReturn(null);
        Price price = new Price();
        Mockito.when(modelMapper.map(dto, Price.class)).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);
        PriceDto response = priceService.save(dto);
        Assertions.assertEquals(identifier, response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveFailure_existingActive() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        String identifier = "PROD1_MRP";

        Price existing = new Price();
        existing.setDeleted(false);
        Mockito.when(priceRepository.findByIdentifier(identifier)).thenReturn(existing);
        PriceDto response = priceService.save(dto);
        Assertions.assertEquals(identifier, response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        String identifier = "PROD1_MRP";
        Price existing = new Price();
        existing.setDeleted(true);

        Mockito.when(priceRepository.findByIdentifier(identifier)).thenReturn(existing);
        PriceDto response = priceService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Price price = new Price();
        price.setIdentifier("Price1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("Price1");

        Mockito.when(priceRepository.findByIdentifier("Price1")).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);
        PriceDto response = priceService.findByIdentifier("Price1");
        Assertions.assertEquals("Price1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("Price1");

        Price existing = new Price();

        Mockito.when(priceRepository.findByIdentifier("Price1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(priceRepository.save(existing)).thenReturn(existing);

        PriceDto response = priceService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(priceRepository).save(existing);
    }

    @Test
    void updateFailure() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("Price1");

        Mockito.when(priceRepository.findByIdentifier("Price1")).thenReturn(null);
        PriceDto response = priceService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Price price = new Price();
        price.setDeleted(false);

        Mockito.when(priceRepository.findByIdentifier("Price1")).thenReturn(price);
        Mockito.when(priceRepository.save(price)).thenReturn(price);
        priceService.delete("Price1");
        Mockito.verify(priceRepository).findByIdentifier("Price1");
        Mockito.verify(priceRepository).save(price);
        Assertions.assertTrue(price.isDeleted());
    }

    @Test
    void findAllTest() {
        Price price = new Price();
        price.setIdentifier("Price1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("Price1");

        List<Price> list = List.of(price);
        Page<Price> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);

        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        WsDto<PriceDto> response = priceService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Price1", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }
}