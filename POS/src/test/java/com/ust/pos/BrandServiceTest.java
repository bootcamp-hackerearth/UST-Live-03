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
    void saveTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("B1"))
                .thenReturn(null);

        Brand brand = new Brand();

        Mockito.when(modelMapper.map(dto, Brand.class))
                .thenReturn(brand);

        BrandDto response = brandService.save(dto);

        Assertions.assertEquals("B1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void saveTestFailure() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("B1"))
                .thenReturn(new Brand());

        BrandDto response = brandService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(brandRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        BrandDto dto = new BrandDto();

        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("B1"))
                .thenReturn(brand);

        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        Assertions.assertNotNull(brandService.findByIdentifier("B1"));
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("B1"))
                .thenReturn(null);

        BrandDto response = brandService.findByIdentifier("B1");

        Assertions.assertNull(response);
    }

    @Test
    void updateTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("B1"))
                .thenReturn(brand);

        BrandDto response = brandService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(modelMapper).map(dto, brand);
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void updateTestFailure() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("B1"))
                .thenReturn(null);

        BrandDto response = brandService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(brandRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("B1"))
                .thenReturn(brand);

        brandService.delete("B1");

        Assertions.assertTrue(brand.isDeleted());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void findAllTest() {
        List<Brand> brands = List.of(new Brand());
        List<BrandDto> dtos = List.of(new BrandDto());

        Type type = new TypeToken<List<BrandDto>>() {
        }.getType();

        Mockito.when(brandRepository.findByDeletedFalse())
                .thenReturn(brands);

        Mockito.when(modelMapper.map(brands, type))
                .thenReturn(dtos);

        Assertions.assertEquals(1, brandService.findAll().size());
    }

    @Test
    void toggleStatusTest() {
        Brand brand = new Brand();
        brand.setStatus(false);

        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("B1"))
                .thenReturn(brand);

        brandService.toggleStatus("B1");

        Assertions.assertTrue(brand.isStatus());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusNotFoundTest() {
        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("B1"))
                .thenReturn(null);

        brandService.toggleStatus("B1");

        Mockito.verify(brandRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Brand brand = new Brand();
        Page<Brand> page = new PageImpl<>(List.of(brand));

        Mockito.when(brandRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(new BrandDto());

        Page<BrandDto> response =
                brandService.findAll(pageable, null);

        Assertions.assertEquals(1, response.getContent().size());
        Mockito.verify(brandRepository).findByDeletedFalse(pageable);
    }
}