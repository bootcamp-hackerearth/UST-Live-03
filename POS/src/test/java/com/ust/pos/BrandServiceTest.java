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
import org.springframework.data.domain.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    @InjectMocks
    private BrandServiceImpl brandService;

    @Test
    void saveSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND1");

        Brand brand = new Brand();
        brand.setIdentifier("BRAND1");

        Mockito.when(brandRepository.findByIdentifier("BRAND1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Brand.class)).thenReturn(brand);

        BrandDto result = brandService.save(dto);

        Assertions.assertEquals("BRAND1", result.getIdentifier());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("BRAND1");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND1");

        Mockito.when(brandRepository.findByIdentifier("BRAND1")).thenReturn(existingBrand);

        BrandDto result = brandService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Brand with identifier - BRAND1 already exists", result.getMessage());

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("BRAND2");
        existingBrand.setStatus(true);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND2");
        dto.setDescription("Updated desc");
        dto.setStatus(false);

        Mockito.when(brandRepository.findByIdentifier("BRAND2")).thenReturn(existingBrand);

        BrandDto result = brandService.update(dto);

        Assertions.assertEquals("BRAND2", result.getIdentifier());

        Mockito.verify(brandRepository).save(existingBrand);

        Assertions.assertFalse(existingBrand.getStatus());
    }

    @Test
    void updateFailureBrandNotFoundTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(brandRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        BrandDto result = brandService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Brand not found", result.getMessage());
    }

    @Test
    void deleteTest() {
        brandService.delete("BRAND3");
        Mockito.verify(brandRepository).deleteByIdentifier("BRAND3");
    }

    @Test
    void findAllTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);

        Pageable pageable = PageRequest.of(0, 50, Sort.by("identifier"));

        Page<Brand> brandPage = new PageImpl<>(brands, pageable, brands.size());

        Mockito.when(brandRepository.findAll(pageable)).thenReturn(brandPage);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(java.lang.reflect.Type.class))).thenReturn(brandDtos);

        List<BrandDto> response = brandService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
        Assertions.assertEquals(0, brandPage.getPageable().getPageNumber());
        Assertions.assertEquals(50, brandPage.getPageable().getPageSize());
        Assertions.assertEquals(1, brandPage.getTotalElements());

        Mockito.verify(brandRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(Mockito.eq(brands), Mockito.any(java.lang.reflect.Type.class));
    }

    @Test
    void findByIdentifierSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND4");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND4");

        Mockito.when(brandRepository.findByIdentifier("BRAND4")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto result = brandService.findByIdentifier("BRAND4");

        Assertions.assertEquals("BRAND4", result.getIdentifier());
    }

    @Test
    void toggleStatusTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND1");
        brand.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("BRAND1")).thenReturn(brand);

        brandService.toggleStatus("BRAND1");

        Assertions.assertFalse(brand.getStatus());

        Mockito.verify(brandRepository).save(brand);
    }
}