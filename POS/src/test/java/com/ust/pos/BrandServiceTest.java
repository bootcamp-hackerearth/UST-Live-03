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
import org.modelmapper.TypeToken;
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
    void saveTest_Success() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(brandDto, Brand.class))
                .thenReturn(brand);
        Mockito.when(brandRepository.save(brand))
                .thenReturn(brand);
        BrandDto response = brandService.save(brandDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void saveTest_Failure_WhenBrandExists() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(existingBrand);
        BrandDto response = brandService.save(brandDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        brandDto.setDescription("Updated Description");
        brandDto.setStatus(true);
        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(existingBrand);
        Mockito.when(brandRepository.save(existingBrand))
                .thenReturn(existingBrand);
        BrandDto response = brandService.update(brandDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Updated Description", existingBrand.getDescription());
        Assertions.assertTrue(existingBrand.isStatus());
        Mockito.verify(brandRepository).save(existingBrand);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        BrandDto response = brandService.update(brandDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(brandDto);
        BrandDto response = brandService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<Brand> brands = List.of(new Brand());
        List<BrandDto> brandDtos = List.of(new BrandDto());
        Type listType = new TypeToken<List<BrandDto>>() {}.getType();
        Mockito.when(brandRepository.findAll())
                .thenReturn(brands);
        Mockito.when(modelMapper.map(brands, listType))
                .thenReturn(brandDtos);
        List<BrandDto> response = brandService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(brandRepository)
                .deleteByIdentifier("Admin");
        brandService.delete("Admin");
        Mockito.verify(brandRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAll_WithPagination_ShouldReturnBrandDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Brand> brands = List.of(new Brand());
        Page<Brand> brandPage = new PageImpl<>(brands);
        List<BrandDto> brandDtos = List.of(new BrandDto());
        Type listType = new TypeToken<List<BrandDto>>() {}.getType();
        Mockito.when(brandRepository.findAll(pageable))
                .thenReturn(brandPage);
        Mockito.when(modelMapper.map(brands, listType))
                .thenReturn(brandDtos);
        List<BrandDto> response = brandService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(brandRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(brands, listType);
    }
}