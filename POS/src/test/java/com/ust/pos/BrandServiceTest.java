package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Brand;
import com.ust.pos.modell.BrandRepository;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    public static final String BR_001 = "BR001";
    @InjectMocks
    private BrandServiceImpl service;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier(BR_001);

        Brand brand = new Brand();
        brand.setIdentifier(BR_001);

        when(brandRepository.findByIdentifier(BR_001)).thenReturn(null);
        when(modelMapper.map(dto, Brand.class)).thenReturn(brand);

        BrandDto result = service.save(dto);

        assertNotNull(result);
        verify(modelMapper).map(dto, Brand.class);
        verify(brandRepository).save(brand);
    }

    @Test
    void saveAlreadyExistsTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier(BR_001);

        Brand existing = new Brand();
        existing.setDeleted(false);

        when(brandRepository.findByIdentifier(BR_001)).thenReturn(existing);

        BrandDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Brand with identifier - BR001 already exists", result.getMessage());

        verify(brandRepository, never()).save(any());
    }

    @Test
    void saveSoftDeletedBrandTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier(BR_001);

        Brand existing = new Brand();
        existing.setDeleted(true);

        when(brandRepository.findByIdentifier(BR_001)).thenReturn(existing);

        BrandDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Brand with Identifier BR001 already exists (Soft-Deleted)", result.getMessage());

        verify(brandRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier(BR_001);

        Brand existing = new Brand();
        existing.setIdentifier(BR_001);
        existing.setCreatedBy("user");
        existing.setCreatedOn(LocalDateTime.now());

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001)).thenReturn(existing);

        BrandDto result = service.update(dto);

        assertNotNull(result);

        verify(modelMapper).map(dto, existing);
        verify(brandRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR002");

        when(brandRepository.findByIdentifierAndDeletedFalse("BR002")).thenReturn(null);

        BrandDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertEquals("Brand with identifier - BR002 not found", result.getMessage());

        verify(brandRepository, never()).save(any());
    }

    @Test
    void deleteExistingBrandTest() {
        Brand brand = new Brand();
        brand.setIdentifier(BR_001);

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001)).thenReturn(brand);

        service.delete(BR_001);

        verify(brandRepository).save(brand);
    }

    @Test
    void deleteNonExistingBrandTest() {
        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001)).thenReturn(null);

        service.delete(BR_001);

        verify(brandRepository, never()).save(any());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        BrandDto dto = new BrandDto();

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001)).thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto result = service.findByIdentifier(BR_001);

        assertNotNull(result);

        verify(brandRepository).findByIdentifierAndDeletedFalse(BR_001);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Brand brand = new Brand();
        BrandDto dto = new BrandDto();

        Page<Brand> page = new PageImpl<>(List.of(brand), pageable, 1);

        when(brandRepository.findAllByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(dto));

        WsDto<BrandDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Brand> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(brandRepository.findAllByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(any(), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<BrandDto> result = service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }

    @Test
    void findAllActiveTest() {
        Brand brand = new Brand();
        BrandDto dto = new BrandDto();

        when(brandRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(brand));

        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        List<BrandDto> result = service.findAllActive();

        assertEquals(1, result.size());

        verify(modelMapper).map(brand, BrandDto.class);
    }

    @Test
    void findAllActiveEmptyTest() {
        when(brandRepository.findByStatusTrueAndDeletedFalse()).thenReturn(Collections.emptyList());

        List<BrandDto> result = service.findAllActive();

        assertTrue(result.isEmpty());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Brand brand = new Brand();
        brand.setStatus(true);

        BrandDto dto = new BrandDto();

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001)).thenReturn(brand);

        when(brandRepository.save(brand)).thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto result = service.toggleStatus(BR_001);

        assertNotNull(result);
        assertFalse(brand.getStatus());

        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusNullTest() {
        Brand brand = new Brand();
        brand.setStatus(null);

        BrandDto dto = new BrandDto();

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001)).thenReturn(brand);

        when(brandRepository.save(brand)).thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto result = service.toggleStatus(BR_001);

        assertNotNull(result);
        assertTrue(brand.getStatus());

        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusBrandNotFoundTest() {
        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001)).thenReturn(null);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.toggleStatus(BR_001)
                );

        assertEquals(
                "brand not found with identifier: BR001",
                exception.getMessage()
        );
    }
}