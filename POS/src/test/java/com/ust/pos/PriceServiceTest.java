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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        price.setIdentifier("PRODUCT1SELLING");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRODUCT1SELLING");

        when(priceRepository.findByIdentifier("PRODUCT1SELLING"))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result =
                priceService.findByIdentifier("PRODUCT1SELLING");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "PRODUCT1SELLING",
                result.getIdentifier());
    }

    @Test
    void save_NewPrice() {

        PriceDto dto = new PriceDto();
        dto.setProduct("PRODUCT1");
        dto.setPriceType("SELLING");

        Price price = new Price();

        when(priceRepository.findByIdentifier("PRODUCT1SELLING"))
                .thenReturn(null);

        when(modelMapper.map(dto, Price.class))
                .thenReturn(price);

        when(priceRepository.save(price))
                .thenReturn(price);

        PriceDto result = priceService.save(dto);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                "PRODUCT1SELLING",
                result.getIdentifier());

        verify(priceRepository).save(price);
    }

    @Test
    void save_PriceAlreadyExists() {

        Price existing = new Price();

        PriceDto dto = new PriceDto();
        dto.setProduct("PRODUCT1");
        dto.setPriceType("SELLING");

        when(priceRepository.findByIdentifier("PRODUCT1SELLING"))
                .thenReturn(existing);

        PriceDto result = priceService.save(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertNotNull(result.getMessage());

        Assertions.assertEquals(
                "PRODUCT1SELLING",
                result.getIdentifier());

        verify(priceRepository, never())
                .save(any());
    }

    @Test
    void update_PriceExists() {

        Price existing = new Price();

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRODUCT1SELLING");

        when(priceRepository.findByIdentifier("PRODUCT1SELLING"))
                .thenReturn(existing);

        when(priceRepository.save(existing))
                .thenReturn(existing);

        PriceDto result = priceService.update(dto);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                "PRODUCT1SELLING",
                result.getIdentifier());

        verify(modelMapper)
                .map(dto, existing);

        verify(priceRepository)
                .save(existing);
    }

    @Test
    void update_PriceNotFound() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRODUCT1SELLING");

        when(priceRepository.findByIdentifier("PRODUCT1SELLING"))
                .thenReturn(null);

        PriceDto result = priceService.update(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertNotNull(result.getMessage());

        verify(priceRepository, never())
                .save(any());
    }

    @Test
    void deleteTest() {

        doNothing().when(priceRepository)
                .deleteByIdentifier("PRODUCT1SELLING");

        priceService.delete("PRODUCT1SELLING");

        verify(priceRepository)
                .deleteByIdentifier("PRODUCT1SELLING");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Price> priceList =
                List.of(new Price(), new Price());

        Page<Price> page = new PageImpl<>(
                priceList,
                pageable,
                2
        );

        List<PriceDto> dtoList =
                List.of(new PriceDto(), new PriceDto());

        when(priceRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(priceList), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<PriceDto> result =
                priceService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                2,
                result.getContent().size());

        Assertions.assertEquals(
                dtoList,
                result.getContent());

        Assertions.assertEquals(
                0,
                result.getPage());

        Assertions.assertEquals(
                10,
                result.getSizePerPage());

        Assertions.assertEquals(
                1,
                result.getTotalPages());

        Assertions.assertEquals(
                2,
                result.getTotalRecords());

        verify(priceRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(eq(priceList), any(Type.class));
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Price> emptyList = List.of();

        Page<Price> page =
                new PageImpl<>(emptyList);

        when(priceRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(emptyList), any(Type.class)))
                .thenReturn(List.of());

        WsDto<PriceDto> result =
                priceService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertTrue(
                result.getContent().isEmpty());

        Assertions.assertEquals(
                0,
                result.getTotalRecords());

        verify(priceRepository)
                .findAll(pageable);
    }

    @Test
    void findByProductAndPriceType_Found() {

        Price price = new Price();
        price.setProduct("PRODUCT1");
        price.setPriceType("SELLING");

        PriceDto dto = new PriceDto();
        dto.setProduct("PRODUCT1");
        dto.setPriceType("SELLING");

        when(priceRepository.findByProductAndPriceType(
                "PRODUCT1",
                "SELLING"))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result =
                priceService.findByProductAndPriceType(
                        "PRODUCT1",
                        "SELLING");

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                "PRODUCT1",
                result.getProduct());

        Assertions.assertEquals(
                "SELLING",
                result.getPriceType());
    }
}