package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginatedResponseDto;
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
    void saveTest() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
        Assertions.assertFalse(brand.getIsDeleted());
    }

    @Test
    void saveTestFailureAlreadyExists() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Brand existingBrand = new Brand();
        existingBrand.setIsDeleted(false);

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(existingBrand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand with identifier - Admin already exists", response.getMessage());
    }

    @Test
    void saveTestFailureDeletedBrand() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Brand existingBrand = new Brand();
        existingBrand.setIsDeleted(true);

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(existingBrand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("was deleted"));
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
    void updateTest() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Admin");

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(existingBrand);
        Mockito.when(brandRepository.save(existingBrand)).thenReturn(existingBrand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertEquals("Admin", response.getIdentifier());

        Mockito.verify(modelMapper).map(brandDto, existingBrand);
        Mockito.verify(brandRepository).save(existingBrand);
    }

    @Test
    void updateTestFailure() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand with identifier - Admin not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(true);
        brand.setIsDeleted(false);

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto response = brandService.delete("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Brand deleted successfully", response.getMessage());
        Assertions.assertTrue(brand.getIsDeleted());
        Assertions.assertFalse(brand.getStatus());
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(null);

        BrandDto response = brandService.delete("Admin");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand with identifier - Admin not found", response.getMessage());
    }

    @Test
    void findAllTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);

        Page<Brand> page = new PageImpl<>(brands);

        Mockito.when(brandRepository.findByIsDeleted(
                Mockito.eq(false),
                Mockito.any(Pageable.class)
        )).thenReturn(page);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(brandDtos);

        PaginatedResponseDto<BrandDto> response = brandService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllActiveTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(true);

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);

        Mockito.when(brandRepository.findByStatusAndIsDeleted(true, false)).thenReturn(brands);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(brandDtos);

        List<BrandDto> response = brandService.findAllActive();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }

    @Test
    void changeStatusTrueTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(false);

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        brandService.changeStatus("Admin", true);

        Assertions.assertTrue(brand.getStatus());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void changeStatusFalseTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        brandService.changeStatus("Admin", false);

        Assertions.assertFalse(brand.getStatus());
        Mockito.verify(brandRepository).save(brand);
    }
}