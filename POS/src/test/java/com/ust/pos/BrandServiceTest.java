package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.util.FileStorageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private FileStorageUtil fileStorageUtil;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BrandServiceImpl brandService;

    private BrandDto brandDto;
    private Brand brand;

    @BeforeEach
    void setUp() {
        brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");
        brandDto.setDescription("Athletic Gear");

        brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setDescription("Athletic Gear");
        brand.setStatus(true);
    }

    @Test
    @DisplayName("Save Brand - Success with Icon")
    void save_Success() {
        MultipartFile mockFile = mock(MultipartFile.class);
        brandDto.setIcon(mockFile);

        when(brandRepository.findByIdentifier("Nike")).thenReturn(null);
        when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        when(fileStorageUtil.saveBrandIcon(mockFile, "Nike")).thenReturn("path/icon.png");
        when(modelMapper.map(any(Brand.class), eq(BrandDto.class))).thenReturn(brandDto);

        BrandDto result = brandService.save(brandDto);

        Assertions.assertNotNull(result);
        verify(brandRepository).save(brand);
        verify(fileStorageUtil).saveBrandIcon(mockFile, "Nike");
    }

    @Test
    @DisplayName("Save Brand - Failure: Already Exists")
    void save_Failure_Duplicate() {
        when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        Assertions.assertFalse(result.isSuccess());
        verify(brandRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update Brand - Success with New Icon")
    void update_Success_WithIcon() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        brandDto.setIcon(mockFile);

        when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        when(fileStorageUtil.saveBrandIcon(mockFile, "Nike")).thenReturn("new/path.png");
        when(modelMapper.map(any(Brand.class), eq(BrandDto.class))).thenReturn(brandDto);

        BrandDto result = brandService.update(brandDto);

        Assertions.assertNotNull(result);
        verify(brandRepository).save(brand);
    }

    @Test
    @DisplayName("Update Brand - Success with Empty Icon Multipart")
    void update_Success_EmptyIconFile() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true);
        brandDto.setIcon(mockFile);

        when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        when(modelMapper.map(any(Brand.class), eq(BrandDto.class))).thenReturn(brandDto);

        brandService.update(brandDto);

        verify(fileStorageUtil, never()).saveBrandIcon(any(), anyString());
    }

    @Test
    @DisplayName("Update Brand - Failure: Not Found")
    void update_Failure_NotFound() {
        when(brandRepository.findByIdentifier("Nike")).thenReturn(null);
        BrandDto result = brandService.update(brandDto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Brand not found", result.getMessage());
    }

    @Test
    @DisplayName("Find All - Paginated")
    void findAll_Paginated() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Brand> brandList = Collections.singletonList(brand);
        Page<Brand> brandPage = new PageImpl<>(brandList);

        when(brandRepository.findAll(pageable)).thenReturn(brandPage);
        when(modelMapper.map(eq(brandList), any(Type.class))).thenReturn(Collections.singletonList(brandDto));

        List<BrandDto> result = brandService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find All Active")
    void findAllActive_Success() {
        List<Brand> activeBrands = Collections.singletonList(brand);
        when(brandRepository.findAllByStatus(true)).thenReturn(activeBrands);
        when(modelMapper.map(eq(activeBrands), any(Type.class))).thenReturn(Collections.singletonList(brandDto));

        List<BrandDto> result = brandService.findAllActive();

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Find By Identifier")
    void findByIdentifier_Success() {
        when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.findByIdentifier("Nike");
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Toggle Status - Logic Flip")
    void toggleStatus_Success() {
        brand.setStatus(true);

        when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        when(modelMapper.map(any(), eq(BrandDto.class))).thenReturn(brandDto);

        brandService.toggleStatus("Nike");

        Assertions.assertFalse(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    @DisplayName("Delete Brand")
    void delete_Success() {
        boolean result = brandService.delete("Nike");
        Assertions.assertTrue(result);
        verify(brandRepository).deleteByIdentifier("Nike");
    }
}