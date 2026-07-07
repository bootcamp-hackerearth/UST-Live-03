package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    void findByIdentifierSuccessTest() {

        Price price = new Price();
        price.setIdentifier("P1.RETAIL");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1.RETAIL");

        Mockito.when(priceRepository.findByIdentifier("P1.RETAIL"))
                .thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto response = priceService.findByIdentifier("P1.RETAIL");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("P1.RETAIL", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        when(priceRepository.findByIdentifier("P1.RETAIL"))
                .thenReturn(null);

        assertThrows(
                ResourseNotFoundException.class,
                () -> priceService.findByIdentifier("P1.RETAIL")
        );
    }

    @Test
    void saveTestSuccess() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("RETAIL");

        Price price = new Price();

        Mockito.when(priceRepository.findByIdentifier(anyString()))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(price);
        PriceDto response = priceService.save(dto);

        Assertions.assertEquals("P1.RETAIL", response.getIdentifier());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(priceRepository).save(any(Price.class));
    }

    @Test
    void saveTestFailure_deletedPrice() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("RETAIL");

        Price existing = new Price();
        existing.setDeleted(true);

        when(priceRepository.findByIdentifier(anyString()))
                .thenReturn(existing);

        PriceDto response = priceService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(response.getMessage()
                .contains("already exists but was deleted"));
    }

    @Test
    void saveTestFailure_duplicate() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("RETAIL");

        Price existing = new Price();

        Mockito.when(priceRepository.findByIdentifier(anyString()))
                .thenReturn(existing);
        PriceDto response = priceService.save(dto);

        Assertions.assertEquals("P1.RETAIL", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("RETAIL");

        Price existing = new Price();

        Mockito.when(priceRepository.findByIdentifier(anyString()))
                .thenReturn(existing);
        PriceDto response = priceService.update(dto);
        Assertions.assertEquals("P1.RETAIL", response.getIdentifier());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(priceRepository).save(existing);
    }

    @Test
    void updateTestFailure_notFound() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P1");
        dto.setPriceType("RETAIL");

        Mockito.when(priceRepository.findByIdentifier(anyString()))
                .thenReturn(null);
        PriceDto response = priceService.update(dto);

        Assertions.assertEquals("P1.RETAIL", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        Price price = new Price();

        when(priceRepository.findByIdentifier("P1.RETAIL"))
                .thenReturn(price);

        priceService.delete("P1.RETAIL");

        assertTrue(price.isDeleted());

        verify(priceRepository)
                .findByIdentifier("P1.RETAIL");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();
        price.setIdentifier("P1.RETAIL");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1.RETAIL");

        Page<Price> page =
                new PageImpl<>(List.of(price), pageable, 1);

        when(priceRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        var result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("P1.RETAIL",
                result.getContent().getFirst().getIdentifier());

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalRecords());

        verify(priceRepository)
                .findByIsDeletedFalse(pageable);

        verify(modelMapper)
                .map(price, PriceDto.class);
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Price> specification = mock(Specification.class);

        Price price = new Price();
        price.setIdentifier("P1.RETAIL");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("P1.RETAIL");

        List<Price> priceList = List.of(price);

        Page<Price> page = new PageImpl<>(priceList, pageable, 1);

        when(priceRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(priceList), any(Type.class)))
                .thenReturn(List.of(priceDto));

        WsDto<PriceDto> result =
                priceService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("P1.RETAIL",
                result.getContent().get(0).getIdentifier());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(priceRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(priceList), any(Type.class));
    }

    @Test
    void findAllWithSpecificationEmptyResultTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Price> specification = mock(Specification.class);

        Page<Price> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(priceRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(List.of()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<PriceDto> result =
                priceService.findAll(specification, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(priceRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(List.of()), any(Type.class));
    }
}