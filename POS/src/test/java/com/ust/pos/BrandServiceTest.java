package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.modell.Brand;
import com.ust.pos.modell.BrandRepository;
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
        dto.setIdentifier("Apple");
        Mockito.when(brandRepository.findByIdentifier("Apple")).thenReturn(null);
        Brand entity = new Brand();
        Mockito.when(modelMapper.map(dto, Brand.class)).thenReturn(entity);
        Mockito.when(brandRepository.save(entity)).thenReturn(entity);
        BrandDto response = brandService.save(dto);
        Assertions.assertEquals("Apple", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("Apple");
        Brand existing = new Brand();
        Mockito.when(brandRepository.findByIdentifier("Apple")).thenReturn(existing);
        BrandDto response = brandService.save(dto);
        Assertions.assertEquals("Apple", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("Apple");
        Brand entity = new Brand();
        entity.setIdentifier("Apple");
        Mockito.when(brandRepository.findByIdentifier("Apple")).thenReturn(entity);
        Mockito.when(brandRepository.save(entity)).thenReturn(entity);
        BrandDto response = brandService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("Apple");
        Mockito.when(brandRepository.findByIdentifier("Apple")).thenReturn(null);
        BrandDto response = brandService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Brand entity = new Brand();
        entity.setIdentifier("Apple");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("Apple");
        Mockito.when(brandRepository.findByIdentifier("Apple")).thenReturn(entity);
        Mockito.when(modelMapper.map(entity, BrandDto.class)).thenReturn(dto);
        BrandDto response = brandService.findByIdentifier("Apple");
        Assertions.assertEquals("Apple", response.getIdentifier());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(brandRepository).deleteByIdentifier("Apple");
        brandService.deleteByIdentifier("Apple");
        Mockito.verify(brandRepository).deleteByIdentifier("Apple");
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
        Mockito.when(brandRepository.findAll(pageable)).thenReturn(brandPage);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(java.lang.reflect.Type.class))).thenReturn(brandDtos);
        List<BrandDto> response = brandService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }
}
