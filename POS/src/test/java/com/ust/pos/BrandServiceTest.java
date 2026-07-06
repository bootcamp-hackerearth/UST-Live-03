package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
    void findByIdentifierTestFailure() {
        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            brandService.findByIdentifier("Admin");
        });
    }

    @Test
    void toggleStatusTest() {
        Brand brand = new Brand();
        brand.setStatus(false);
        BrandDto brandDto = new BrandDto();
        brandDto.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto response = brandService.toggleStatus("Admin");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void saveTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("  Admin  ");

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(null);
        Brand brand = new Brand();
        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Admin");
        existingBrand.setDeleted(false);

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(existingBrand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand with identifier - Admin already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Admin");
        existingBrand.setDeleted(true);

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(existingBrand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand with identifier Admin was previously deleted. Please contact backend team to restore.", response.getMessage());
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

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        boolean response = brandService.delete("Admin");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(null);

        boolean response = brandService.delete("Admin");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Brand brand = new Brand();
        List<Brand> brands = List.of(brand);
        Page<Brand> brandPage = new PageImpl<>(brands, pageable, brands.size());

        BrandDto brandDto = new BrandDto();
        List<BrandDto> brandDtos = List.of(brandDto);

        Mockito.when(brandRepository.findByDeletedFalse(pageable)).thenReturn(brandPage);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(java.lang.reflect.Type.class))).thenReturn(brandDtos);

        WsDto<BrandDto> response = brandService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findIfTrueTest() {
        Brand brand = new Brand();
        List<Brand> brands = List.of(brand);
        BrandDto brandDto = new BrandDto();
        List<BrandDto> brandDtos = List.of(brandDto);

        Mockito.when(brandRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(brands);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(java.lang.reflect.Type.class))).thenReturn(brandDtos);

        List<BrandDto> response = brandService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Brand> specification = Mockito.mock(Specification.class);
        Brand brand = new Brand();
        List<Brand> brands = List.of(brand);
        Page<Brand> brandPage = new PageImpl<>(brands, pageable, brands.size());

        BrandDto brandDto = new BrandDto();
        List<BrandDto> brandDtos = List.of(brandDto);

        Mockito.when(brandRepository.findAll(specification, pageable)).thenReturn(brandPage);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(java.lang.reflect.Type.class))).thenReturn(brandDtos);

        WsDto<BrandDto> response = brandService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}