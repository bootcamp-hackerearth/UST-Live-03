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

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {

        Brand brand = new Brand();
        brand.setIdentifier("APPLE");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");

        Mockito.when(brandRepository.findByIdentifier("APPLE")).thenReturn(brand);

        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto response = brandService.findByIdentifier("APPLE");

        Assertions.assertEquals("APPLE", response.getIdentifier());
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Brand brand = new Brand();
        brand.setIdentifier("APPLE");
        brand.setStatus(true);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");

        Mockito.when(brandRepository.findByIdentifier("APPLE")).thenReturn(brand);

        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto response = brandService.toggleStatus("APPLE");

        Assertions.assertEquals("APPLE", response.getIdentifier());

        Assertions.assertFalse(brand.isStatus());

        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Brand brand = new Brand();
        brand.setIdentifier("APPLE");
        brand.setStatus(false);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");

        Mockito.when(brandRepository.findByIdentifier("APPLE")).thenReturn(brand);

        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto response = brandService.toggleStatus("APPLE");

        Assertions.assertEquals("APPLE", response.getIdentifier());

        Assertions.assertTrue(brand.isStatus());

        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void saveTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier(" APPLE ");

        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("APPLE")).thenReturn(null);

        Mockito.when(modelMapper.map(dto, Brand.class)).thenReturn(brand);

        BrandDto response = brandService.save(dto);

        Assertions.assertEquals("APPLE", response.getIdentifier());

        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void saveDuplicateTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");

        Brand existing = new Brand();

        Mockito.when(brandRepository.findByIdentifier("APPLE")).thenReturn(existing);

        BrandDto response = brandService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Brand with identifier - APPLE already exists", response.getMessage());
    }

    @Test
    void updateTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");

        Brand existing = new Brand();
        existing.setIdentifier("APPLE");

        Mockito.when(brandRepository.findByIdentifier("APPLE")).thenReturn(existing);

        Mockito.doAnswer(invocation -> {

            BrandDto source = invocation.getArgument(0);
            Brand target = invocation.getArgument(1);

            target.setIdentifier(source.getIdentifier());

            return null;

        }).when(modelMapper).map(Mockito.any(BrandDto.class), Mockito.any(Brand.class));

        BrandDto response = brandService.update(dto);

        Assertions.assertEquals("APPLE", response.getIdentifier());

        Mockito.verify(brandRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");

        Mockito.when(brandRepository.findByIdentifier("APPLE")).thenReturn(null);

        BrandDto response = brandService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Brand with identifier - APPLE not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(brandRepository).deleteByIdentifier("APPLE");

        boolean result = brandService.delete("APPLE");

        Assertions.assertTrue(result);

        Mockito.verify(brandRepository).deleteByIdentifier("APPLE");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Brand brand = new Brand();
        brand.setIdentifier("APPLE");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");

        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);

        Page<Brand> brandPage = new PageImpl<>(brands);

        Mockito.when(brandRepository.findAll(pageable)).thenReturn(brandPage);

        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class))).thenReturn(dtos);

        List<BrandDto> response = brandService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("APPLE", response.get(0).getIdentifier());
    }

    @Test
    void findIfTrueTest() {

        Brand brand = new Brand();
        brand.setIdentifier("APPLE");
        brand.setStatus(true);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");

        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);

        Mockito.when(brandRepository.findByStatusIsTrue()).thenReturn(brands);

        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class))).thenReturn(dtos);

        List<BrandDto> response = brandService.findIfTrue();

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("APPLE", response.get(0).getIdentifier());
    }
}