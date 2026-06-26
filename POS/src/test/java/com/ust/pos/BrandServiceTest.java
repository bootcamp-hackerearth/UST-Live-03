package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    private Brand brand;
    private BrandDto brandDto;

    @BeforeEach
    void setUp() {
        brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        brandDto.setSuccess(true);
        brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(true);
        brand.setDeleted(false);
    }

    @Test
    void testSave_Success() {
        when(brandRepository.findByIdentifier("Admin")).thenReturn(null);
        when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        assertNotNull(response);
        assertEquals("Admin", response.getIdentifier());
        assertTrue(response.isSuccess());
        assertNull(response.getMessage());

        verify(brandRepository, times(1)).findByIdentifier("Admin");
        verify(modelMapper, times(1)).map(brandDto, Brand.class);
        verify(brandRepository, times(1)).save(brand);
    }

    @Test
    void testSave_Failure_AlreadyExists_Active() {
        brand.setDeleted(false);
        when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Brand with identifier - Admin already exists", response.getMessage());

        verify(brandRepository, times(1)).findByIdentifier("Admin");
        verify(brandRepository, never()).save(any());
    }

    @Test
    void testSave_Failure_AlreadyExists_SoftDeleted() {
        brand.setDeleted(true);
        when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Brand identifier - Admin not available", response.getMessage());

        verify(brandRepository, times(1)).findByIdentifier("Admin");
        verify(brandRepository, never()).save(any());
    }

    @Test
    void testUpdate_Success() {
        when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto response = brandService.update(brandDto);

        assertNotNull(response);
        assertEquals("Admin", response.getIdentifier());
        assertTrue(response.isSuccess());

        verify(brandRepository, times(1)).findByIdentifier("Admin");
        verify(modelMapper, times(1)).map(brandDto, brand);
        verify(brandRepository, times(1)).save(brand);
    }

    @Test
    void testUpdate_Failure_NotFound() {
        when(brandRepository.findByIdentifier("Admin")).thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("brand with identifier - Admin not found", response.getMessage());

        verify(brandRepository, times(1)).findByIdentifier("Admin");
        verify(brandRepository, never()).save(any());
    }

    @Test
    void testFindByIdentifier_Success() {
        when(brandRepository.findByIdentifierAndDeletedFalse("Admin")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto response = brandService.findByIdentifier("Admin");

        assertNotNull(response);
        assertEquals("Admin", response.getIdentifier());
        verify(brandRepository, times(1)).findByIdentifierAndDeletedFalse("Admin");
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(brandRepository.findByIdentifierAndDeletedFalse("Admin")).thenReturn(null);
        when(modelMapper.map(null, BrandDto.class)).thenReturn(null);

        BrandDto response = brandService.findByIdentifier("Admin");

        assertNull(response);
        verify(brandRepository, times(1)).findByIdentifierAndDeletedFalse("Admin");
    }

    @Test
    void testFindAll_WithData() {
        List<Brand> brandList = List.of(brand);
        List<BrandDto> brandDtoList = List.of(brandDto);
        Pageable pageable = PageRequest.of(0, 50);
        Page<Brand> brandPage = new PageImpl<>(brandList, pageable, brandList.size());
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        when(brandRepository.findAllByDeletedFalse(pageable)).thenReturn(brandPage);
        when(modelMapper.map(brandPage.getContent(), listType)).thenReturn(brandDtoList);

        WsDto<BrandDto> result = brandService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        verify(brandRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void testFindAll_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        when(brandRepository.findAllByDeletedFalse(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(emptyPage.getContent(), listType)).thenReturn(Collections.emptyList());

        WsDto<BrandDto> result = brandService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
        verify(brandRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void testFindActiveBrands() {
        when(brandRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(brand));

        List<Brand> result = brandService.findActiveBrands();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(brandRepository, times(1)).findByStatusTrueAndDeletedFalse();
    }

    @Test
    void testDelete_Success() {
        String identifier = "Admin";
        when(brandRepository.findByIdentifierAndDeletedFalse(identifier)).thenReturn(brand);

        assertDoesNotThrow(() -> brandService.delete(identifier));

        verify(brandRepository, times(1)).findByIdentifierAndDeletedFalse(identifier);
        assertTrue(brand.getDeleted());
    }

    @Test
    void testToggleStatus_Success() {
        brand.setStatus(true);
        when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.toggleStatus("Admin");

        assertNotNull(result);
        assertFalse(brand.isStatus());
        verify(brandRepository, times(1)).save(brand);
    }

    @Test
    void testToggleStatus_BrandNotFound() {
        when(brandRepository.findByIdentifier("Admin")).thenReturn(null);

        BrandDto result = brandService.toggleStatus("Admin");

        assertNull(result);
        verify(brandRepository, times(1)).findByIdentifier("Admin");
        verify(brandRepository, never()).save(any());
    }

    @Test
    void testToggleStatus_FromFalseToTrue() {
        brand.setStatus(false);
        when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.toggleStatus("Admin");

        assertNotNull(result);
        assertTrue(brand.isStatus());
        verify(brandRepository, times(1)).save(brand);
    }
}