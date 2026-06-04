package com.ust.pos;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.brand.service.impl.BrandServiceImpl;
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
        brandDto.setIdentifier("B101");

        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(null);
        Brand brand = new Brand();

        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        BrandDto response = brandService.save(brandDto);
        Assertions.assertEquals("B101", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B101");

        Brand brand = new Brand();
        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(brand);
        BrandDto response = brandService.save(brandDto);
        Assertions.assertEquals("B101", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B101");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B101");

        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);
        BrandDto response = brandService.findByIdentifier("B101");
        Assertions.assertEquals("B101", response.getIdentifier());
    }

    @Test
    void updateTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B101");

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("B101");

        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(existingBrand);
        Mockito.when(brandRepository.save(existingBrand)).thenReturn(existingBrand);
        BrandDto response = brandService.update(brandDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B101");

        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(null);
        BrandDto response = brandService.update(brandDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        brandService.delete("B101");
        Mockito.verify(brandRepository).deleteByIdentifier("B101");
    }

    @Test
    void findAllWithPageableTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B101");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B101");

        List<Brand> brandList = List.of(brand);
        Page<Brand> brandPage = new PageImpl<>(brandList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(brandRepository.findAll(pageable)).thenReturn(brandPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(brandList),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(brandDto));

        List<BrandDto> response = brandService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("B101", response.get(0).getIdentifier());
    }

    @Test
    void toggleStatusSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B101");
        brand.setStatus(false);
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B101");
        brandDto.setStatus(true);
        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);
        BrandDto response = brandService.toggleStatus("B101", true);
        Assertions.assertEquals("B101", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B101");

        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(null);
        BrandDto response = brandService.toggleStatus("B101", true);
        Assertions.assertNull(response);
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveBrandsTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B101");
        brand.setStatus(true);

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B101");
        brandDto.setStatus(true);

        List<Brand> activeBrands = List.of(brand);
        List<BrandDto> activeBrandDtos = List.of(brandDto);

        Mockito.when(brandRepository.findByStatusTrue()).thenReturn(activeBrands);
        Mockito.when(modelMapper.map(
                Mockito.eq(activeBrands),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(activeBrandDtos);

        List<BrandDto> response = brandService.findActiveBrands();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("B101", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }
}