package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
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

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private BrandServiceImpl brandService;

    private BrandDto brandDto;
    private Brand brand;

    @BeforeEach
    void setUp() {
        brandDto = new BrandDto();
        brandDto.setIdentifier("BRD-001");
        brandDto.setIcon(multipartFile);
        brandDto.setDescription("Brand Description");

        brand = new Brand();
        brand.setIdentifier("BRD-001");
        brand.setStatus(true);
        brand.setDeleted(false);
    }

    @Test
    @DisplayName("Save Brand - Success")
    void save_Success() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(null);
        when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        when(fileStorageUtil.saveBrandIcon(multipartFile, "BRD-001")).thenReturn("/path/to/icon.png");
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.save(brandDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Added Successfully"));
        verify(brandRepository).save(brand);
        verify(fileStorageUtil).saveBrandIcon(multipartFile, "BRD-001");
    }

    @Test
    @DisplayName("Save Brand - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        brand.setDeleted(false);
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    @DisplayName("Save Brand - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        brand.setDeleted(true);
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.findByIdentifier("BRD-001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find By Identifier - Failure: Not Found Exception")
    void findByIdentifier_Failure_NotFound() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> brandService.findByIdentifier("BRD-001"));
    }

    @Test
    @DisplayName("Find All Brands - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> brandPage = new PageImpl<>(List.of(brand), pageable, 1);

        when(brandRepository.findByDeletedFalse(pageable)).thenReturn(brandPage);
        when(modelMapper.map(eq(brandPage.getContent()), any(Type.class))).thenReturn(List.of(brandDto));

        WsDto<BrandDto> result = brandService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Brands with Specification - Success")
    void findAll_WithSpecification_Success() {
        Specification<Brand> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> brandPage = new PageImpl<>(List.of(brand), pageable, 1);

        when(brandRepository.findAll(spec, pageable)).thenReturn(brandPage);
        when(modelMapper.map(eq(brandPage.getContent()), any(Type.class))).thenReturn(List.of(brandDto));

        WsDto<BrandDto> result = brandService.findAll(spec, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Active Brands - Success")
    void findAllActive_Success() {
        List<Brand> activeBrands = List.of(brand);
        when(brandRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activeBrands);
        when(modelMapper.map(eq(activeBrands), any(Type.class))).thenReturn(List.of(brandDto));

        List<BrandDto> result = brandService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Update Brand - Success with New Icon")
    void update_Success_WithIcon() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileStorageUtil.saveBrandIcon(multipartFile, "BRD-001")).thenReturn("/new/path/icon.png");
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.update(brandDto);

        Assertions.assertEquals("Brand Description", brand.getDescription());
        Assertions.assertEquals("/new/path/icon.png", brand.getIconPath());
        verify(brandRepository).save(brand);
    }

    @Test
    @DisplayName("Update Brand - Success without Icon")
    void update_Success_WithoutIcon() {
        brandDto.setIcon(null);
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.update(brandDto);

        verify(fileStorageUtil, never()).saveBrandIcon(any(), any());
        verify(brandRepository).save(brand);
    }

    @Test
    @DisplayName("Update Brand - Success with Empty Icon")
    void update_Success_WithEmptyIcon() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);
        when(multipartFile.isEmpty()).thenReturn(true);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.update(brandDto);

        verify(fileStorageUtil, never()).saveBrandIcon(any(), any());
        verify(brandRepository).save(brand);
    }

    @Test
    @DisplayName("Update Brand - Failure: Not Found")
    void update_Failure_NotFound() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(null);

        BrandDto result = brandService.update(brandDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Brand not found"));
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.toggleStatus("BRD-001");

        Assertions.assertFalse(brand.isStatus());
        verify(brandRepository, times(2)).findByIdentifier("BRD-001");
        verify(brandRepository).save(brand);
    }

    @Test
    @DisplayName("Delete Brand - Success")
    void delete_Success() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);

        boolean result = brandService.delete("BRD-001");

        Assertions.assertTrue(result);
        verify(brandRepository).save(brand);
    }

    @Test
    @DisplayName("Delete Brand - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(null);

        boolean result = brandService.delete("BRD-001");

        Assertions.assertFalse(result);
        verify(brandRepository, never()).save(any(Brand.class));
    }
}