package com.ust.pos;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.brand.service.impl.BrandServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

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

        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(null);
        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);
        Assertions.assertEquals("B101", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure_existingActive() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B101");

        Brand existing = new Brand();
        existing.setDeleted(false);

        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(existing);

        BrandDto response = brandService.save(brandDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestFailure_softDeleted() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("B101");

        Brand existing = new Brand();
        existing.setDeleted(true);

        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(existing);
        BrandDto response = brandService.save(brandDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
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
        Brand brand = new Brand();
        brand.setIdentifier("B101");

        Mockito.when(brandRepository.findByIdentifier("B101")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        brandService.delete("B101");

        Mockito.verify(brandRepository).findByIdentifier("B101");
        Mockito.verify(brandRepository).save(brand);
        Assertions.assertTrue(brand.isDeleted());
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
        Mockito.when(brandRepository.findByDeletedFalse(pageable)).thenReturn(brandPage);
        Mockito.when(modelMapper.map(Mockito.eq(brandList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(brandDto));

        WsDto<BrandDto> response = brandService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("B101", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void toggleStatusSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(false);

        BrandDto mappedDto = new BrandDto();
        mappedDto.setIdentifier("Admin");
        mappedDto.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(mappedDto);
        BrandDto response = brandService.toggleStatus("Admin", true);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(null);
        BrandDto response = brandService.toggleStatus("Admin", true);
        Assertions.assertNull(response);
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveBrandsTest() {
        Brand brand = new Brand();
        brand.setIdentifier("RACK_01");
        brand.setStatus(true);

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("RACK_01");
        brandDto.setStatus(true);

        List<Brand> brands = List.of(brand);
        Mockito.when(brandRepository.findByStatusTrue()).thenReturn(brands);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(brandDto));

        List<BrandDto> response = brandService.findActiveBrands();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("RACK_01", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }
}