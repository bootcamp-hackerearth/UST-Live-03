package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR01");

        Brand brand = new Brand();
        brand.setIdentifier("BR01");

        when(brandRepository.findByIdentifier("BR01")).thenReturn(null);
        when(modelMapper.map(dto, Brand.class)).thenReturn(brand);

        BrandDto response = brandService.save(dto);

        assertEquals("BR01", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        verify(brandRepository).save(brand);
    }

    @Test
    void saveFailureTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR01");

        Brand brand = new Brand();
        brand.setIdentifier("BR01");

        when(brandRepository.findByIdentifier("BR01")).thenReturn(brand);

        BrandDto response = brandService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(brandRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR01");

        Brand brand = new Brand();
        brand.setIdentifier("BR01");

        when(brandRepository.findByIdentifier("BR01")).thenReturn(brand);

        BrandDto response = brandService.update(dto);

        assertEquals("BR01", response.getIdentifier());
        verify(modelMapper).map(dto, brand);
        verify(brandRepository).save(brand);
    }

    @Test
    void updateFailureTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR01");

        when(brandRepository.findByIdentifier("BR01")).thenReturn(null);

        BrandDto response = brandService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(brandRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        brandService.delete("BR01");
        verify(brandRepository).deleteByIdentifier("BR01");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR01");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR01");

        when(brandRepository.findByIdentifier("BR01")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto response = brandService.findByIdentifier("BR01");

        assertNotNull(response);
        assertEquals("BR01", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(brandRepository.findByIdentifier("BR01")).thenReturn(null);

        BrandDto response = brandService.findByIdentifier("BR01");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Brand> brands = List.of(new Brand(), new Brand());
        Page<Brand> page = new PageImpl<>(brands);

        List<BrandDto> dtoList = List.of(new BrandDto(), new BrandDto());

        when(brandRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(brands),
                any(Type.class)
        )).thenReturn(dtoList);

        List<BrandDto> result = brandService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(brandRepository).findAll(pageable);
    }

    @Test
    void toggleStatusSuccessTest() {
        Brand brand = new Brand();
        brand.setStatus(true);

        when(brandRepository.findByIdentifier("BR01")).thenReturn(brand);

        brandService.toggleStatus("BR01");

        Assertions.assertFalse(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusFailureTest() {
        when(brandRepository.findByIdentifier("BR01")).thenReturn(null);

        brandService.toggleStatus("BR01");

        verify(brandRepository, never()).save(any());
    }
}