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
    void saveTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B1");

        Mockito.when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        Brand brand = new Brand();

        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("B1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void saveTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B1");

        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("B1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B1");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B1");

        Mockito.when(brandRepository.findByIdentifier("B1")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto response = brandService.findByIdentifier("B1");

        Assertions.assertEquals("B1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(brandRepository.findByIdentifier("B1"))
                .thenReturn(null);

        BrandDto response = brandService.findByIdentifier("B1");

        Assertions.assertNull(response);
    }

    @Test
    void updateTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B1");

        Brand existingBrand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("B1")).thenReturn(existingBrand);
        Mockito.when(brandRepository.save(existingBrand)).thenReturn(existingBrand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper).map(brandDto, existingBrand);
        Mockito.verify(brandRepository).save(existingBrand);
    }

    @Test
    void updateTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B1");

        Mockito.when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(brandRepository).deleteByIdentifier("B1");

        brandService.delete("B1");

        Mockito.verify(brandRepository).deleteByIdentifier("B1");
    }

    @Test
    void findAllTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B1");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B1");

        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);

        Mockito.when(brandRepository.findAll()).thenReturn(brands);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class)))
                .thenReturn(brandDtos);

        List<BrandDto> response = brandService.findAll();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("B1", response.get(0).getIdentifier());
    }

    @Test
    void toggleStatusTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B1");
        brand.setStatus(false);

        Mockito.when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        brandService.toggleStatus("B1");

        Assertions.assertTrue(brand.isStatus());

        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusNotFoundTest() {
        Mockito.when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        brandService.toggleStatus("B1");

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllWithPaginationShouldReturnBrandDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Brand> brands = List.of(new Brand());
        Page<Brand> brandPage = new PageImpl<>(brands);

        List<BrandDto> brandDtos = List.of(new BrandDto());

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

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