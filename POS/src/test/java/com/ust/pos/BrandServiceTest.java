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
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("S1");
        Mockito.when(brandRepository.findByIdentifier("S1")).thenReturn(null);
        Brand brand = new Brand();
        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        BrandDto response = brandService.save(brandDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("S1");
        Brand existingBrand = new Brand();
        Mockito.when(brandRepository.findByIdentifier("S1")).thenReturn(existingBrand);
        BrandDto response = brandService.save(brandDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("S1");
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("S1");
        Mockito.when(brandRepository.findByIdentifier("S1")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);
        BrandDto response = brandService.findByIdentifier("S1");
        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("S1");
        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("S1");
        Mockito.when(brandRepository.findByIdentifier("S1"))
                .thenReturn(existingBrand);
        Mockito.when(brandRepository.save(existingBrand))
                .thenReturn(existingBrand);
        BrandDto response = brandService.update(brandDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("S1");
        Mockito.when(brandRepository.findByIdentifier("S1"))
                .thenReturn(null);
        BrandDto response = brandService.update(brandDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(brandRepository)
                .deleteByIdentifier("S1");
        brandService.delete("S1");
        Mockito.verify(brandRepository).deleteByIdentifier("S1");
    }

    @Test
    void findAllTest() {
        Brand brand = new Brand();
        brand.setIdentifier("S1");
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("S1");
        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> brandPage = new PageImpl<>(brands);
        Mockito.when(brandRepository.findAll(pageable)).thenReturn(brandPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(brands),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(brandDtos);
        List<BrandDto> response = brandService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void updateStatusTest() {
        Brand brand = new Brand();
        brand.setIdentifier("S1");
        Mockito.when(brandRepository.findByIdentifier("S1"))
                .thenReturn(brand);
        Mockito.when(brandRepository.save(brand))
                .thenReturn(brand);
        brandService.updateStatus("S1", true);
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void findAllActiveTest() {
        Brand brand = new Brand();
        brand.setIdentifier("S1");
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("S1");
        List<Brand> shelves = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);
        Mockito.when(brandRepository.findByStatus(true)).thenReturn(shelves);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(brandDtos);
        List<BrandDto> response = brandService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}