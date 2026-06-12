package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        when(brandRepository.findByIdentifier("BR001")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto result = brandService.findByIdentifier("BR001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void saveSuccessTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        when(brandRepository.findByIdentifier("BR001")).thenReturn(null);
        when(modelMapper.map(dto, Brand.class)).thenReturn(brand);
        when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto result = brandService.save(dto);

        Assertions.assertEquals("BR001", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
        verify(brandRepository).save(brand);
    }

    @Test
    void saveFailureTest() {

        Brand existing = new Brand();
        existing.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        when(brandRepository.findByIdentifier("BR001")).thenReturn(existing);

        BrandDto result = brandService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(brandRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {

        Brand existing = new Brand();
        existing.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        when(brandRepository.findByIdentifier("BR001")).thenReturn(existing);
        when(brandRepository.save(existing)).thenReturn(existing);

        BrandDto result = brandService.update(dto);

        Assertions.assertEquals("BR001", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(brandRepository).save(existing);
    }

    @Test
    void updateFailureTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        when(brandRepository.findByIdentifier("BR001")).thenReturn(null);

        BrandDto result = brandService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(brandRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        doNothing().when(brandRepository).deleteByIdentifier("BR001");

        brandService.delete("BR001");

        verify(brandRepository).deleteByIdentifier("BR001");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Brand> brands = List.of(new Brand(), new Brand());
        Page<Brand> page = new PageImpl<>(brands, pageable, 2);

        List<BrandDto> dtoList = List.of(new BrandDto(), new BrandDto());

        when(brandRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(brands), any(Type.class))).thenReturn(dtoList);

        WsDto<BrandDto> result = brandService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());
    }

    @Test
    void toggleStatusTest() {

        Brand brand = new Brand();
        brand.setStatus(true);

        when(brandRepository.findByIdentifier("BR001")).thenReturn(brand);

        brandService.toggleStatus("BR001");

        Assertions.assertFalse(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusNotFoundTest() {

        when(brandRepository.findByIdentifier("BR001")).thenReturn(null);

        brandService.toggleStatus("BR001");

        verify(brandRepository, never()).save(any());
    }
}