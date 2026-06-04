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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
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
    void saveTestSuccess() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("SALE");
        dto.setIdentifier("P1_SALE");

        Price price = new Price();

        when(priceRepository.findByIdentifier("P1_SALE")).thenReturn(null);
        when(modelMapper.map(dto, Price.class)).thenReturn(price);

        PriceDto result = priceService.save(dto);

        Assertions.assertNotNull(result);
        verify(priceRepository).save(price);
    }

    @Test
    void saveTestFailure() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("SALE");
        dto.setIdentifier("P1_SALE");

        Price price = new Price();

        when(priceRepository.findByIdentifier("P1_SALE")).thenReturn(price);

        PriceDto result = priceService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("SALE");
        dto.setIdentifier("P1_SALE");

        Price price = new Price();

        when(priceRepository.findByIdentifier("P1_SALE")).thenReturn(price);

        PriceDto result = priceService.update(dto);

        Assertions.assertNotNull(result);
        verify(modelMapper).map(dto, price);
        verify(priceRepository).save(price);
    }

    @Test
    void updateTestFailure() {
        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("SALE");
        dto.setIdentifier("P1_SALE");

        when(priceRepository.findByIdentifier("P1_SALE")).thenReturn(null);

        PriceDto result = priceService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        priceService.delete("P1_SALE");
        verify(priceRepository).deleteByIdentifier("P1_SALE");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Price price = new Price();
        price.setIdentifier("P1_SALE");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1_SALE");

        when(priceRepository.findByIdentifier("P1_SALE")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);

        PriceDto result = priceService.findByIdentifier("P1_SALE");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1_SALE", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(priceRepository.findByIdentifier("P1_SALE")).thenReturn(null);

        PriceDto result = priceService.findByIdentifier("P1_SALE");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Price> page = mock(Page.class);

        List<Price> prices = List.of(new Price(), new Price());
        List<PriceDto> dtoList = List.of(new PriceDto(), new PriceDto());

        when(priceRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(prices);
        when(modelMapper.map(eq(prices), any(Type.class))).thenReturn(dtoList);

        List<PriceDto> result = priceService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        verify(priceRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(prices), any(Type.class));
    }
}