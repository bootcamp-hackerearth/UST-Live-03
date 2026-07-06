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
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;
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
    void saveTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(null);
        Brand brand = new Brand();
        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        BrandDto response = brandService.save(brandDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(existingBrand);
        BrandDto response = brandService.save(brandDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);
        BrandDto response = brandService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findAllWithKeywordTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);
        Page<Brand> brandPage = new PageImpl<>(brands, PageRequest.of(0, 2), brands.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Specification<Brand> spec = Mockito.mock(Specification.class);
        Mockito.when(brandRepository.findAll(spec, pageable)).thenReturn(brandPage);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(java.lang.reflect.Type.class))).thenReturn(brandDtos);
        WsDto<BrandDto> response = brandService.findAll(spec, pageable, "Admin");
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getKeyword());
    }

    @Test
    void updateTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(existingBrand);
        Mockito.when(brandRepository.save(existingBrand))
                .thenReturn(existingBrand);
        BrandDto response = brandService.update(brandDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        BrandDto response = brandService.update(brandDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        boolean response = brandService.delete("Admin");
        Assertions.assertEquals(true, response);
    }

    @Test
    void findAllTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);
        Page<Brand> brandPage = new PageImpl<>(brands, PageRequest.of(0, 2), brands.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(brandRepository.findByDeletedFalse(pageable)).thenReturn(brandPage);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(java.lang.reflect.Type.class))).thenReturn(brandDtos);
        WsDto<BrandDto> response = brandService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findByStatusTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);
        Mockito.when(brandRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(brands);
        Mockito.when(modelMapper.map(
                Mockito.eq(brands),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(brandDtos);
        List<BrandDto> response = brandService.findIfTrue();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {
        Brand brand = new Brand();
        brand.setStatus(false);
        BrandDto brandDto = new BrandDto();
        brandDto.setStatus(true);
        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);
        BrandDto response = brandService.toggleStatus("Admin");
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        Brand brand = new Brand();
        brand.setStatus(true);
        BrandDto brandDto = new BrandDto();
        brandDto.setStatus(false);
        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);
        BrandDto response = brandService.toggleStatus("Admin");
        Assertions.assertFalse(response.isStatus());
    }
}