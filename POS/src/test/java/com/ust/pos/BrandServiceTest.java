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

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);
        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Nike", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveFailureTest() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Nike", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateSuccessTest() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertEquals("Nike", response.getIdentifier());
    }

    @Test
    void updateFailureTest() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteSuccessTest() {

        brandService.delete("Nike");

        verify(brandRepository).deleteByIdentifier("Nike");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto response = brandService.findByIdentifier("Nike");

        Assertions.assertEquals("Nike", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);

        BrandDto response = brandService.findByIdentifier("Nike");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSuccessTest() {

        Brand b1 = new Brand();
        b1.setIdentifier("Nike");

        Brand b2 = new Brand();
        b2.setIdentifier("Adidas");

        List<Brand> brands = List.of(b1, b2);

        BrandDto d1 = new BrandDto();
        d1.setIdentifier("Nike");

        BrandDto d2 = new BrandDto();
        d2.setIdentifier("Adidas");

        List<BrandDto> brandDtos = List.of(d1, d2);

        Page<Brand> page = new PageImpl<>(brands);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(brandRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class))).thenReturn(brandDtos);

        List<BrandDto> result = brandService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void findAllActiveSuccessTest() {

        Brand b1 = new Brand();
        b1.setIdentifier("Nike");
        b1.setStatus(true);

        Brand b2 = new Brand();
        b2.setIdentifier("Adidas");
        b2.setStatus(true);

        List<Brand> activeBrands = List.of(b1, b2);

        BrandDto d1 = new BrandDto();
        d1.setIdentifier("Nike");

        BrandDto d2 = new BrandDto();
        d2.setIdentifier("Adidas");

        List<BrandDto> brandDtos = List.of(d1, d2);

        Mockito.when(brandRepository.findByStatus(true)).thenReturn(activeBrands);
        Mockito.when(modelMapper.map(Mockito.eq(activeBrands), Mockito.any(Type.class))).thenReturn(brandDtos);

        List<BrandDto> result = brandService.findAllActive();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void toggleStatusSuccessTest() {

        Brand brand = new Brand();
        brand.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);

        brandService.toggleStatus("Nike");

        Assertions.assertFalse(brand.isStatus());
    }

    @Test
    void toggleStatusBrandNotFoundTest() {

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);

        brandService.toggleStatus("Nike");

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }
}