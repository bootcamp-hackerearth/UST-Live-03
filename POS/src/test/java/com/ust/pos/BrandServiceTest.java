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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

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
        Mockito.when(brandRepository.save(Mockito.any(Brand.class))).thenReturn(brand);

        BrandDto result = brandService.save(dto);

        Assertions.assertTrue(result.isSuccess());
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
    void saveFailureDeletedIdentifierTest() {
        Brand deletedBrand = new Brand();
        deletedBrand.setIdentifier("BRAND1");
        deletedBrand.setDeleted(true);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND1");

        Mockito.when(brandRepository.findByIdentifier("BRAND1")).thenReturn(deletedBrand);

        BrandDto result = brandService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Brand identifier - BRAND1 not available", result.getMessage());

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("BRAND2");
        existingBrand.setStatus(true);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND2");
        dto.setDescription("Updated Description");
        dto.setStatus(false);

        Mockito.when(brandRepository.findByIdentifier("BRAND2")).thenReturn(existingBrand);

        BrandDto result = brandService.update(dto);

        Assertions.assertEquals("BRAND2", result.getIdentifier());
        Assertions.assertFalse(existingBrand.getStatus());

        Mockito.verify(brandRepository).save(existingBrand);
    }

    @Test
    void updateFailureBrandNotFoundTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(brandRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        BrandDto result = brandService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Brand not found", result.getMessage());

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND3");

        Mockito.when(brandRepository.findByIdentifier("BRAND3")).thenReturn(brand);

        brandService.delete("BRAND3");

        Assertions.assertTrue(brand.isDeleted());

        Mockito.verify(brandRepository).findByIdentifier("BRAND3");
    }

    @Test
    void findAllTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND1");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND1");

        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 4, Sort.by("identifier"));
        Page<Brand> page = new PageImpl<>(brands, pageable, brands.size());

        Mockito.when(brandRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<BrandDto> result = brandService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals("BRAND1", result.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, result.getTotalRecords());

        Mockito.verify(brandRepository).findByIsDeletedFalse(pageable);
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

    @Test
    void findAllActiveTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND1");
        brand.setStatus(true);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND1");

        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);

        Mockito.when(brandRepository.findByStatusTrueAndIsDeletedFalse()).thenReturn(brands);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class))).thenReturn(dtos);

        List<BrandDto> result = brandService.findAllActive();

        Assertions.assertEquals(1, result.size());

        Mockito.verify(brandRepository).findByStatusTrueAndIsDeletedFalse();
    }

    @Test
    void toggleStatusFromFalseToTrueTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND2");
        brand.setStatus(false);

        Mockito.when(brandRepository.findByIdentifier("BRAND2")).thenReturn(brand);

        brandService.toggleStatus("BRAND2");

        Assertions.assertTrue(brand.getStatus());

        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void constructorTest() {
        BrandServiceImpl service = new BrandServiceImpl(brandRepository, modelMapper);
        Assertions.assertNotNull(service);
    }
}