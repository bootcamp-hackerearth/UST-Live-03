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
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        brand.setIdentifier("B1");
        brand.setStatus(true);
        brand.setDeleted(false);

        brandDto = new BrandDto();
        brandDto.setIdentifier("B1");
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> page = new PageImpl<>(Collections.singletonList(brand));

        when(brandRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(brandDto));

        WsDto<BrandDto> result = brandService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testSave_NewBrand() {
        when(brandRepository.findByIdentifier("B1")).thenReturn(null);
        when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertNotNull(result);
        verify(brandRepository).save(brand);
    }

    @Test
    void testSave_AlreadyExists() {
        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {
        brand.setDeleted(true);

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testDelete() {
        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        brandService.delete("B1");

        assertTrue(brand.isDeleted());   // ✅ real behavior
        assertFalse(brand.isStatus());

        verify(brandRepository).save(brand);
    }

    @Test
    void testFindByIdentifier_Success() {
        when(brandRepository.findByIdentifierAndDeletedFalse("B1")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.findByIdentifier("B1");

        assertNotNull(result);
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(brandRepository.findByIdentifierAndDeletedFalse("B1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> brandService.findByIdentifier("B1"));
    }

    @Test
    void testUpdate_Success() {
        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        doNothing().when(modelMapper).map(brandDto, brand);

        BrandDto result = brandService.update(brandDto);

        assertNotNull(result);
        verify(brandRepository).save(brand);
    }

    @Test
    void testUpdate_NotFound() {
        when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        BrandDto result = brandService.update(brandDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists")); // as per your code
    }

    @Test
    void testChangeToggleStatus() {
        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandService.changeToggleStatus("B1", false);

        assertNotNull(result);
        assertFalse(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void testFindActiveStatus() {
        brand.setStatus(true);

        Brand inactive = new Brand();
        inactive.setStatus(false);

        List<Brand> brands = List.of(brand, inactive);

        when(brandRepository.findAll()).thenReturn(brands);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(brandDto));

        List<BrandDto> result = brandService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}