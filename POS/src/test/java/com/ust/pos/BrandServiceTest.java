package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BrandServiceImpl brandService;

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Brand> brands = List.of(new Brand());
        Page<Brand> page = new PageImpl<>(brands);
        List<BrandDto> dtoList = List.of(new BrandDto());

        when(brandRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(brands), any(Type.class))).thenReturn(dtoList);

        List<BrandDto> result = brandService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(brandRepository).findAll(pageable);
    }

    @Test
    void testFindByIdentifier() {
        String identifier = "BR1";

        Brand brand = new Brand();
        BrandDto dto = new BrandDto();

        when(brandRepository.findByIdentifier(identifier)).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto result = brandService.findByIdentifier(identifier);

        assertNotNull(result);
    }

    @Test
    void testSaveSuccess() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR1");

        when(brandRepository.findByIdentifier("BR1")).thenReturn(null);
        when(modelMapper.map(dto, Brand.class)).thenReturn(new Brand());

        BrandDto result = brandService.save(dto);

        assertNotNull(result);
        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    void testSaveDuplicate() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR1");

        when(brandRepository.findByIdentifier("BR1")).thenReturn(new Brand());

        BrandDto result = brandService.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));

        verify(brandRepository, never()).save(any());
    }

    @Test
    void testDelete() {
        brandService.delete("BR1");

        verify(brandRepository).deleteByIdentifier("BR1");
    }

    @Test
    void testFindActiveBrands() {
        List<Brand> activeBrands = Arrays.asList(new Brand(), new Brand());

        when(brandRepository.findByStatus(true)).thenReturn(activeBrands);

        List<Brand> result = brandService.findActiveBrands();

        assertEquals(2, result.size());
        verify(brandRepository).findByStatus(true);
    }

    @Test
    void testUpdateSuccess() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR1");

        Brand existing = new Brand();

        when(brandRepository.findByIdentifier("BR1")).thenReturn(existing);

        BrandDto result = brandService.update(dto);

        assertNotNull(result);

        verify(modelMapper).map(dto, existing);
        verify(brandRepository).save(existing);
    }

    @Test
    void testUpdateNotFound() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR1");

        when(brandRepository.findByIdentifier("BR1")).thenReturn(null);

        BrandDto result = brandService.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));

        verify(brandRepository, never()).save(any());
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Brand brand = new Brand();
        brand.setStatus(true);

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        brandService.toggleStatus("B1");

        assertFalse(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Brand brand = new Brand();
        brand.setStatus(false);

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        brandService.toggleStatus("B1");

        assertTrue(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusNotFoundTest() {

        when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        brandService.toggleStatus("B1");

        verify(brandRepository, never()).save(any());
    }
}