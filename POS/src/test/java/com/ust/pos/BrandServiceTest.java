package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private BrandServiceImpl brandService;

    private Brand brand;
    private BrandDto brandDto;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1L);
        brand.setIdentifier("BRD-001");
        brand.setStatus(true);
        brand.setDeleted(false);

        brandDto = new BrandDto();
        brandDto.setIdentifier("BRD-001");
    }

    @Test
    void testFindByIdentifier() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);

        BrandDto result = brandService.findByIdentifier("BRD-001");

        assertNotNull(result);
        assertEquals("BRD-001", result.getIdentifier());
        verify(brandRepository, times(1)).findByIdentifier("BRD-001");
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> brandService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        brandDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> brandService.save(brandDto));
    }

    @Test
    void testSave_WhenBrandAlreadyExistsAndNotDeleted() {
        brand.setDeleted(false);
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenBrandAlreadyExistsButgetDeleted() {
        brand.setDeleted(true);
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(null);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Brand created successfully", result.getMessage());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void testUpdate_WhenBrandNotFound() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(null);

        BrandDto result = brandService.update(brandDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenBrandgetDeleted() {
        brand.setDeleted(true);
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);

        BrandDto result = brandService.update(brandDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        brand.setDeleted(false);
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        BrandDto result = brandService.update(brandDto);

        assertNotNull(result);
        assertEquals("BRD-001", result.getIdentifier());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void testDelete_WhenBrandNotFound() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(null);

        brandService.delete("BRD-001");

        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testDelete_Success() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        brandService.delete("BRD-001");

        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Brand> list = Collections.singletonList(brand);
        Page<Brand> page = new PageImpl<>(list, pageable, 1);

        when(brandRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<BrandDto> result = brandService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    void testFindIfTrue() {
        List<Brand> list = Arrays.asList(brand);
        when(brandRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(list);

        List<BrandDto> result = brandService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testToggleStatus_WhenBrandNotFound() {
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(null);

        BrandDto result = brandService.toggleStatus("BRD-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        brand.setStatus(true);
        when(brandRepository.findByIdentifier("BRD-001")).thenReturn(brand);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        BrandDto result = brandService.toggleStatus("BRD-001");

        assertNotNull(result);
        assertFalse(result.isStatus());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }
    @Test
    void testFindByIdentifier_WhenNotFound_ThrowsResourceNotFoundException() {
        when(brandRepository.findByIdentifier("UNKNOWN-ID")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            brandService.findByIdentifier("UNKNOWN-ID");
        });

        verify(brandRepository, times(1)).findByIdentifier("UNKNOWN-ID");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Brand> list = Collections.singletonList(brand);
        Page<Brand> page = new PageImpl<>(list, pageable, 1);
        Specification<Brand> spec = mock(Specification.class);

        when(brandRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<BrandDto> result = brandService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());

        verify(brandRepository, times(1)).findAll(spec, pageable);
    }
}