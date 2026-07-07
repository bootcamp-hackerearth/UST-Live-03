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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND001");

        Mockito.when(brandRepository.findByIdentifierAndIsDeletedFalse("BRAND001"))
                .thenReturn(brand);

        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        BrandDto result = brandService.findByIdentifier("BRAND001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("BRAND001", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        Mockito.when(brandRepository.findByIdentifierAndIsDeletedFalse("BRAND001"))
                .thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> brandService.findByIdentifier("BRAND001")
        );

        Assertions.assertEquals(
                "Brand with identifier 'BRAND001' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Brand brand = new Brand();

        when(brandRepository.findByIdentifier("BR001")).thenReturn(null);
        when(modelMapper.map(dto, Brand.class)).thenReturn(brand);

        BrandDto result = brandService.save(dto);

        Assertions.assertEquals("BR001", result.getIdentifier());

        verify(brandRepository).save(brand);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Brand existing = new Brand();
        existing.setDeleted(false);

        when(brandRepository.findByIdentifier("BR001")).thenReturn(existing);

        BrandDto result = brandService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Brand with identifier - BR001 already exists",
                result.getMessage()
        );

        verify(brandRepository, never()).save(any());
    }

    @Test
    void saveFailureDeletedBrandTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Brand existing = new Brand();
        existing.setDeleted(true);

        when(brandRepository.findByIdentifier("BR001")).thenReturn(existing);

        BrandDto result = brandService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Brand with identifier -BR001 was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(brandRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Brand existing = new Brand();
        existing.setIdentifier("BR001");

        when(brandRepository.findByIdentifier("BR001")).thenReturn(existing);

        BrandDto result = brandService.update(dto);

        Assertions.assertEquals("BR001", result.getIdentifier());

        verify(modelMapper).map(dto, existing);
        verify(brandRepository).save(existing);
    }

    @Test
    void updateFailureTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        when(brandRepository.findByIdentifier("BR001")).thenReturn(null);

        BrandDto result = brandService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Brand with identifier - BR001 not found",
                result.getMessage()
        );

        verify(brandRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        when(brandRepository.findByIdentifier("BR001")).thenReturn(brand);

        brandService.delete("BR001");

        verify(brandRepository).findByIdentifier("BR001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Brand brand1 = new Brand();
        Brand brand2 = new Brand();

        List<Brand> brands = List.of(brand1, brand2);

        Page<Brand> page = new PageImpl<>(brands, pageable, 2);

        List<BrandDto> dtoList = List.of(new BrandDto(), new BrandDto());

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        when(brandRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(brands, listType)).thenReturn(dtoList);

        WsDto<BrandDto> result = brandService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());

        verify(brandRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void toggleStatusWhenBrandExistsAndStatusTrueTest() {
        Brand brand = new Brand();
        brand.setStatus(true);

        when(brandRepository.findByIdentifier("BR001")).thenReturn(brand);

        brandService.toggleStatus("BR001");

        Assertions.assertFalse(brand.isStatus());

        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusWhenBrandExistsAndStatusFalseTest() {
        Brand brand = new Brand();
        brand.setStatus(false);

        when(brandRepository.findByIdentifier("BR001")).thenReturn(brand);

        brandService.toggleStatus("BR001");

        Assertions.assertTrue(brand.isStatus());

        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusWhenBrandNotFoundTest() {
        when(brandRepository.findByIdentifier("BR001")).thenReturn(null);

        brandService.toggleStatus("BR001");

        verify(brandRepository, never()).save(any());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Specification<Brand> specification = Mockito.mock(Specification.class);

        List<Brand> brands = List.of(new Brand(), new Brand());
        Page<Brand> page = new PageImpl<>(brands, pageable, 2);

        List<BrandDto> dtoList = List.of(new BrandDto(), new BrandDto());

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        Mockito.when(brandRepository.findAll(specification, pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(brands, listType))
                .thenReturn(dtoList);

        WsDto<BrandDto> result = brandService.findAll(specification, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }
}