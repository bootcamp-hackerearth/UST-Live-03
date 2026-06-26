package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BrandServiceImpl brandService;

    private Brand brand;
    private BrandDto brandDto;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1L);
        brand.setIdentifier("BR001");
        brand.setDescription("Test Brand");
        brand.setStatus(true);

        brandDto = new BrandDto();
        brandDto.setIdentifier("BR001");
        brandDto.setDescription("Test Brand");
        brandDto.setStatus(true);
    }

    @Test
    void save_ShouldReturnFailure_WhenBrandAlreadyExists() {

        when(brandRepository.findByIdentifierAndIsDeleteFalse("BR001"))
                .thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Brand with identifier - BR001 already exists",
                result.getMessage());

        verify(brandRepository, never()).save(any());
    }

    @Test
    void save_ShouldSaveBrand_WhenBrandDoesNotExist() {

        when(brandRepository.findByIdentifierAndIsDeleteFalse("BR001"))
                .thenReturn(null);

        when(modelMapper.map(brandDto, Brand.class))
                .thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertNotNull(result);

        verify(modelMapper).map(brandDto, Brand.class);
        verify(brandRepository).save(brand);
    }

    @Test
    void update_ShouldReturnFailure_WhenBrandNotFound() {

        when(brandRepository.findByIdentifierAndIsDeleteFalse("BR001"))
                .thenReturn(null);

        BrandDto result = brandService.update(brandDto);

        assertFalse(result.isSuccess());
        assertEquals("Brand not found", result.getMessage());

        verify(brandRepository, never()).save(any());
    }

    @Test
    void update_ShouldUpdateBrand_WhenBrandExists() {

        when(brandRepository.findByIdentifierAndIsDeleteFalse("BR001"))
                .thenReturn(brand);

        BrandDto result = brandService.update(brandDto);

        assertNotNull(result);

        verify(brandRepository).save(brand);

        assertEquals(
                brandDto.getDescription(),
                brand.getDescription());

        assertEquals(
                brandDto.getStatus(),
                brand.getStatus());
    }

    @Test
    void delete_ShouldSoftDelete_WhenBrandExists() {

        when(brandRepository.findByIdentifierAndIsDeleteFalse("BR001"))
                .thenReturn(brand);

        brandService.delete("BR001");

        assertTrue(brand.isDelete());

        verify(brandRepository).save(brand);
    }

    @Test
    void delete_ShouldDoNothing_WhenBrandNotFound() {

        when(brandRepository.findByIdentifierAndIsDeleteFalse("BR001"))
                .thenReturn(null);

        brandService.delete("BR001");

        verify(brandRepository, never()).save(any());
    }

    @Test
    void findAll_WithPageable_ShouldReturnMappedDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Brand> page =
                new PageImpl<>(List.of(brand));

        List<BrandDto> expected =
                List.of(brandDto);

        when(brandRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()),
                any(Type.class)))
                .thenReturn(expected);

        List<BrandDto> result =
                brandService.findAll(pageable);

        assertEquals(1, result.size());

        verify(brandRepository)
                .findByIsDeleteFalse(pageable);
    }

    @Test
    void findByIdentifier_ShouldReturnMappedDto() {

        when(brandRepository.findByIdentifierAndIsDeleteFalse("BR001"))
                .thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(brandDto);

        BrandDto result =
                brandService.findByIdentifier("BR001");

        assertNotNull(result);
        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void updateStatusOnly_ShouldUpdateStatus() {

        when(brandRepository.findByIdentifierAndIsDeleteFalse("BR001"))
                .thenReturn(brand);

        brandService.updateStatusOnly("BR001", false);

        assertFalse(brand.getStatus());

        verify(brandRepository).save(brand);
    }

    @Test
    void findAll_WithSearch_ShouldReturnMappedPage() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Brand> brandPage =
                new PageImpl<>(List.of(brand));

        when(brandRepository
                .findByIdentifierContainingIgnoreCaseAndIsDeleteFalse(
                        "BR",
                        pageable))
                .thenReturn(brandPage);

        when(modelMapper.map(any(Brand.class), eq(BrandDto.class)))
                .thenReturn(brandDto);

        Page<BrandDto> result =
                brandService.findAll(pageable, "BR");

        assertEquals(1, result.getTotalElements());

        verify(brandRepository)
                .findByIdentifierContainingIgnoreCaseAndIsDeleteFalse(
                        "BR",
                        pageable);
    }

    @Test
    void findAll_WithoutSearch_ShouldReturnMappedPage() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Brand> brandPage =
                new PageImpl<>(List.of(brand));

        when(brandRepository.findByIsDeleteFalse(pageable))
                .thenReturn(brandPage);

        when(modelMapper.map(any(Brand.class), eq(BrandDto.class)))
                .thenReturn(brandDto);

        Page<BrandDto> result =
                brandService.findAll(pageable, "");

        assertEquals(1, result.getTotalElements());

        verify(brandRepository)
                .findByIsDeleteFalse(pageable);
    }

    @Test
    void findAll_ShouldReturnMappedList() {

        List<Brand> brands = List.of(brand);
        List<BrandDto> expected = List.of(brandDto);

        when(brandRepository.findByIsDeleteFalse())
                .thenReturn(brands);

        when(modelMapper.map(
                eq(brands),
                any(Type.class)))
                .thenReturn(expected);

        List<BrandDto> result =
                brandService.findAll();

        assertEquals(1, result.size());

        verify(brandRepository)
                .findByIsDeleteFalse();
    }
}