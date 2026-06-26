package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl service;

    @Mock
    private BrandRepository brandRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Brand brand = new Brand();
        brand.setIdentifier("B1");

        Page<Brand> page = new PageImpl<>(List.of(brand), pageable, 1);

        when(brandRepository.findByIsDeletedFalse(pageable)).thenReturn(page);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        WsDto<BrandDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        BrandDto dto = service.findByIdentifier("B1");

        assertEquals("B1", dto.getIdentifier());
    }

    @Test
    void saveSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        BrandDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(brandRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Brand existing = new Brand();
        existing.setDeleted(false);

        when(brandRepository.findByIdentifier("B1")).thenReturn(existing);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        BrandDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(brandRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Brand existing = new Brand();
        existing.setDeleted(true);

        when(brandRepository.findByIdentifier("B1")).thenReturn(existing);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        BrandDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Brand existing = new Brand();
        existing.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1")).thenReturn(existing);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        BrandDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(brandRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        BrandDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(brandRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B1");
        brand.setDeleted(false);

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        service.delete("B1");

        assertTrue(brand.isDeleted());
    }

    @Test
    void findActiveBrandsTest() {
        when(brandRepository.findByStatus(true)).thenReturn(List.of(new Brand(), new Brand()));

        service = new BrandServiceImpl(brandRepository, modelMapper);

        List<Brand> result = service.findActiveBrands();

        assertEquals(2, result.size());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Brand brand = new Brand();
        brand.setStatus(true);

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        service.toggleStatus("B1");

        assertFalse(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Brand brand = new Brand();
        brand.setStatus(false);

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        service.toggleStatus("B1");

        assertTrue(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        service = new BrandServiceImpl(brandRepository, modelMapper);

        service.toggleStatus("B1");

        verify(brandRepository, never()).save(any());
    }
}