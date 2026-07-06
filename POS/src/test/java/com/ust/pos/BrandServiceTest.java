package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.data.jpa.domain.Specification;

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
    void saveSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("NIKE");
        Brand brand = new Brand();
        Mockito.when(brandRepository.findByIdentifier("NIKE")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        BrandDto response = brandService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Brand existing = new Brand();
        existing.setDeleted(false);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("NIKE");
        Mockito.when(brandRepository.findByIdentifier("NIKE")).thenReturn(existing);
        BrandDto response = brandService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists")
        );
    }

    @Test
    void saveFailureSoftDeletedTest() {
        Brand existing = new Brand();
        existing.setDeleted(true);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("NIKE");
        Mockito.when(brandRepository.findByIdentifier("NIKE")).thenReturn(existing);
        BrandDto response = brandService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("NIKE");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("NIKE");
        Mockito.when(brandRepository.findByIdentifier("NIKE")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);
        BrandDto response = brandService.findByIdentifier("NIKE");
        Assertions.assertEquals("NIKE", response.getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);
        Mockito.when(brandRepository.findAll()).thenReturn(brands);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<BrandDto> response = brandService.findAll(null);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("BR001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithPageableTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Brand> page = new PageImpl<>(brands);
        Mockito.when(brandRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<BrandDto> response = brandService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("BR001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void updateSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("NIKE");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("NIKE");
        Mockito.when(brandRepository.findByIdentifier("NIKE")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        BrandDto response = brandService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void updateFailureTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("NIKE");
        Mockito.when(brandRepository.findByIdentifier("NIKE")).thenReturn(null);
        BrandDto response = brandService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void deleteByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("NIKE");
        brand.setDeleted(false);
        Mockito.when(brandRepository.findByIdentifier("NIKE")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        brandService.deleteByIdentifier("NIKE");
        Assertions.assertTrue(brand.isDeleted());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void deleteByIdentifierBrandNotFoundTest() {
        Mockito.when(brandRepository.findByIdentifier("NIKE")).thenReturn(null);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> brandService.deleteByIdentifier("NIKE"));
        Assertions.assertEquals(
                "Brand with identifier - NIKE not found",
                exception.getMessage()
        );
    }

    @Test
    void toggleStatusSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("NIKE");
        brand.setStatus(false);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("NIKE");
        dto.setStatus(true);
        Mockito.when(brandRepository.findByIdentifier("NIKE")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);
        BrandDto response = brandService.toggleStatus("NIKE", true);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(brand.isStatus());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusBrandNotFoundTest() {
        Mockito.when(brandRepository.findByIdentifier("NIKE")).thenReturn(null);
        Mockito.when(modelMapper.map(null, BrandDto.class)).thenReturn(null);
        BrandDto response = brandService.toggleStatus("NIKE", true);
        Assertions.assertNull(response);
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllWithSpecificationTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        List<Brand> brandList = List.of(brand);
        List<BrandDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> page = new PageImpl<>(brandList, pageable, 1);
        Specification<Brand> specification = Mockito.mock(Specification.class);
        Mockito.when(brandRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(brandList), Mockito.any(Type.class))).thenReturn(dtoList);
        PaginationResponseDto<BrandDto> response = brandService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("BR001", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(brandRepository).findAll(specification, pageable);
    }

    @Test
    void findAllWithSpecificationEmptyResultTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Brand> specification = Mockito.mock(Specification.class);
        Page<Brand> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        Mockito.when(brandRepository.findAll(specification, pageable)).thenReturn(emptyPage);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        PaginationResponseDto<BrandDto> response = brandService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0, response.getTotalRecords());
        Assertions.assertEquals(0, response.getTotalPages());
        Mockito.verify(brandRepository).findAll(specification, pageable);
    }
}