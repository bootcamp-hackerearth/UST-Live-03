package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Price;
import com.ust.pos.models.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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
    void saveTest() {
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
        PriceDto result = priceService.save(dto);
        Assertions.assertTrue(result.isSuccess());
        Price existing = new Price();
        existing.setDeleted(false);
        when(priceRepository.findByIdentifier("P2-MRP")).thenReturn(existing);
        PriceDto duplicateDto = new PriceDto();
        duplicateDto.setProduct("P2");
        duplicateDto.setType("MRP");
        result = priceService.save(duplicateDto);
        Assertions.assertFalse(result.isSuccess());
        Price deleted = new Price();
        deleted.setDeleted(true);
        when(priceRepository.findByIdentifier("P3-MRP")).thenReturn(deleted);
        PriceDto deletedDto = new PriceDto();
        deletedDto.setProduct("P3");
        deletedDto.setType("MRP");
        result = priceService.save(deletedDto);
        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void findByIdentifierAndDeleteTest() {
        Price price = new Price();
        price.setIdentifier("P1-MRP");
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1-MRP");
        when(priceRepository.findByIdentifierAndDeletedFalse("P1-MRP")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);
        PriceDto result = priceService.findByIdentifier("P1-MRP");
        Assertions.assertNotNull(result);
        priceService.delete("P1-MRP");
        Assertions.assertTrue(price.getDeleted());
        verify(priceRepository).save(price);
        when(priceRepository.findByIdentifierAndDeletedFalse("P2-MRP")).thenReturn(null);
        Assertions.assertNull(priceService.findByIdentifier("P2-MRP"));
    }

    @Test
    void updateTest() {
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
        when(modelMapper.map(updated, PriceDto.class)).thenReturn(new PriceDto());
        PriceDto result = priceService.update(dto);
        Assertions.assertTrue(result.isSuccess());
        when(priceRepository.findByIdentifierAndDeletedFalse("X")).thenReturn(null);
        PriceDto notFoundDto = new PriceDto();
        notFoundDto.setIdentifier("X");
        result = priceService.update(notFoundDto);
        Assertions.assertFalse(result.isSuccess());
        Price current = new Price();
        current.setId(1L);
        Price duplicate = new Price();
        duplicate.setId(2L);
        when(priceRepository.findByIdentifierAndDeletedFalse("P2-MRP")).thenReturn(current);
        when(priceRepository.findByIdentifier("P9-MRP")).thenReturn(duplicate);
        PriceDto duplicateDto = new PriceDto();
        duplicateDto.setIdentifier("P2-MRP");
        duplicateDto.setProduct("P9");
        duplicateDto.setType("MRP");
        result = priceService.update(duplicateDto);
        Assertions.assertFalse(result.isSuccess());
        Price same = new Price();
        same.setId(5L);
        when(priceRepository.findByIdentifierAndDeletedFalse("P5-MRP")).thenReturn(same);
        when(priceRepository.findByIdentifier("P5-MRP")).thenReturn(same);
        when(priceRepository.save(same)).thenReturn(same);
        when(modelMapper.map(same, PriceDto.class)).thenReturn(new PriceDto());
        PriceDto sameDto = new PriceDto();
        sameDto.setIdentifier("P5-MRP");
        sameDto.setProduct("P5");
        sameDto.setType("MRP");
        result = priceService.update(sameDto);
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Price price = new Price();
        price.setIdentifier("P1-MRP");
        List<Price> prices = List.of(price);
        Page<Price> page = new PageImpl<>(prices, pageable, 1);
        when(priceRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(prices), any(Type.class))).thenReturn(List.of(new PriceDto()));
        WsDto<PriceDto> result = priceService.findAll(pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Price> specification = mock(Specification.class);
        Page<Price> page =new PageImpl<>(List.of(new Price()), pageable, 1);
        when(priceRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new PriceDto()));
        WsDto<PriceDto> result = priceService.findAll(specification, pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        verify(priceRepository).findAll(specification, pageable);
    }

    @Test
    void deleteTest() {
        Price price = new Price();
        price.setIdentifier("P1-MRP");
        when(priceRepository.findByIdentifierAndDeletedFalse("P1-MRP")).thenReturn(price);
        priceService.delete("P1-MRP");
        Assertions.assertTrue(price.getDeleted());
        verify(priceRepository).save(price);
    }
}