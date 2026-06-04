package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.modell.Brand;
import com.ust.pos.modell.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl service;

    @Mock
    private BrandRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void save_shouldHandleNewAndExistingBrand() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");
        Brand brand = new Brand();
        brand.setIdentifier("B1");
        when(repository.findByIdentifier("B1")).thenReturn(null);
        when(mapper.map(dto, Brand.class)).thenReturn(brand);
        BrandDto result = service.save(dto);
        verify(repository).save(brand);
        assertTrue(result.isSuccess() || result.getMessage() == null);
        when(repository.findByIdentifier("B1")).thenReturn(brand);
        BrandDto existingResult = service.save(dto);
        assertFalse(existingResult.isSuccess());
        assertEquals("Brand with identifier - B1 already exists",
                existingResult.getMessage());
    }

    @Test
    void update_shouldHandleFoundAndNotFound() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");
        Brand brand = new Brand();
        brand.setIdentifier("B1");
        when(repository.findByIdentifier("B1")).thenReturn(brand);
        BrandDto success = service.update(dto);
        verify(mapper).map(dto, brand);
        verify(repository).save(brand);
        assertTrue(success.isSuccess() || success.getMessage() == null);
        when(repository.findByIdentifier("B2")).thenReturn(null);
        dto.setIdentifier("B2");
        BrandDto failure = service.update(dto);
        assertFalse(failure.isSuccess());
        assertEquals("Brand with identifier - B2 not found",
                failure.getMessage());
    }

    @Test
    void delete_shouldCallRepository() {
        service.deleteByIdentifier("B1");
        verify(repository).deleteByIdentifier("B1");
    }

    @Test
    void findByIdentifier_shouldMapResult() {
        Brand brand = new Brand();
        brand.setIdentifier("B1");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");
        when(repository.findByIdentifier("B1")).thenReturn(brand);
        when(mapper.map(brand, BrandDto.class)).thenReturn(dto);
        BrandDto result = service.findByIdentifier("B1");
        assertEquals("B1", result.getIdentifier());
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Type listType = new TypeToken<List<BrandDto>>() {}.getType();
        Brand brand = new Brand();
        brand.setIdentifier("B1");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");
        Page<Brand> page = new PageImpl<>(List.of(brand), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(listType))).thenReturn(List.of(dto));
        List<BrandDto> result = service.findAll(pageable);
        assertEquals(1, result.size());
        Page<Brand> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(List.of(), listType)).thenReturn(List.of());
        List<BrandDto> emptyResult = service.findAll(pageable);
        assertTrue(emptyResult.isEmpty());
    }
}