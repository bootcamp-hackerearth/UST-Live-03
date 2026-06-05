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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        Brand brand = new Brand();
        Mockito.when(brandRepository.findByIdentifier("BR001")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        BrandDto response = brandService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("BR001", response.getIdentifier());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void saveDuplicateTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        Brand existingBrand = new Brand();
        Mockito.when(brandRepository.findByIdentifier("BR001")).thenReturn(existingBrand);
        BrandDto response = brandService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllWithPageableTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Brand> page = new PageImpl<>(brands);
        Mockito.when(brandRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class))).thenReturn(dtos);
        List<BrandDto> response = brandService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("BR001", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);
        Mockito.when(brandRepository.findAll()).thenReturn(brands);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class))).thenReturn(dtos);
        List<BrandDto> response = brandService.findAll(null);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("BR001", response.get(0).getIdentifier());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        Mockito.when(brandRepository.findByIdentifier("BR001")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);
        BrandDto response = brandService.findByIdentifier("BR001");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("BR001", response.getIdentifier());
    }

    @Test
    void updateSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("BR001");
        Mockito.when(brandRepository.findByIdentifier("BR001")).thenReturn(existingBrand);
        Mockito.when(brandRepository.save(existingBrand)).thenReturn(existingBrand);
        BrandDto response = brandService.update(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(brandRepository).save(existingBrand);
    }

    @Test
    void updateFailureTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        Mockito.when(brandRepository.findByIdentifier("BR001")
        ).thenReturn(null);
        BrandDto response = brandService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void deleteByIdentifierTest() {
        Mockito.doNothing().when(brandRepository).deleteByIdentifier("BR001");
        brandService.deleteByIdentifier("BR001");
        Mockito.verify(brandRepository).deleteByIdentifier("BR001");
    }

    @Test
    void toggleStatusTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(false);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setStatus(true);
        Mockito.when(brandRepository.findByIdentifier("BR001")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);
        BrandDto response = brandService.toggleStatus("BR001", true);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(brand.isStatus());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusBrandNotFoundTest() {
        Mockito.when(brandRepository.findByIdentifier("BR001")).thenReturn(null);
        Mockito.when(modelMapper.map(null, BrandDto.class)).thenReturn(null);
        BrandDto response = brandService.toggleStatus("BR001", true);
        Assertions.assertNull(response);
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }
}