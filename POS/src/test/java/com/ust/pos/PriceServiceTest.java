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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifier_Found() {

        Price price = new Price();
        price.setIdentifier("P1R1");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1R1");

        when(priceRepository.findByIdentifier("P1R1"))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result = priceService.findByIdentifier("P1R1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1R1", result.getIdentifier());
    }

    @Test
    void save_NewPrice() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("R1");

        Price price = new Price();

        when(priceRepository.findByIdentifier("P1R1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Price.class))
                .thenReturn(price);

        when(priceRepository.save(price))
                .thenReturn(price);

        PriceDto result = priceService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1R1", result.getIdentifier());
        verify(priceRepository).save(price);
    }

    @Test
    void save_PriceAlreadyExists() {

        Price existing = new Price();

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("R1");

        when(priceRepository.findByIdentifier("P1R1"))
                .thenReturn(existing);

        PriceDto result = priceService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void update_PriceExists() {

        Price existing = new Price();

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1R1");

        when(priceRepository.findByIdentifier("P1R1"))
                .thenReturn(existing);

        when(priceRepository.save(existing))
                .thenReturn(existing);

        PriceDto result = priceService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1R1", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(priceRepository).save(existing);
    }

    @Test
    void update_PriceNotFound() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1R1");

        when(priceRepository.findByIdentifier("P1R1"))
                .thenReturn(null);

        PriceDto result = priceService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        Price price = new Price();

        when(priceRepository.findByIdentifier("P1R1"))
                .thenReturn(price);

        priceService.delete("P1R1");

        verify(priceRepository).findByIdentifier("P1R1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Price price1 = new Price();
        Price price2 = new Price();

        Page<Price> page = new PageImpl<>(
                List.of(price1, price2),
                pageable,
                2
        );

        List<PriceDto> dtoList = List.of(new PriceDto(), new PriceDto());

        when(priceRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());

        verify(priceRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Price> page = new PageImpl<>(List.of(), pageable, 0);

        when(priceRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());

        verify(priceRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findByProductAndPriceType_Found() {

        Price price = new Price();

        PriceDto dto = new PriceDto();

        when(priceRepository.findByProductAndPriceType("P1", "R1"))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result =
                priceService.findByProductAndPriceType("P1", "R1");

        Assertions.assertNotNull(result);
    }

    @Test
    void findByProductAndPriceType_NotFound() {

        when(priceRepository.findByProductAndPriceType("P1", "R1"))
                .thenReturn(null);

        PriceDto result =
                priceService.findByProductAndPriceType("P1", "R1");

        Assertions.assertNull(result);
    }
}