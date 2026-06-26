package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    void save_success() {

        BrandDto dto = new BrandDto();
        dto.setBrandName("Nike");
        dto.setDescription("Sports");

        Brand saved = new Brand();
        saved.setBrandName("Nike");
        saved.setDescription("Sports");

        BrandDto mapped = new BrandDto();

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(null);

        when(brandRepository.save(any(Brand.class)))
                .thenReturn(saved);

        when(modelMapper.map(saved, BrandDto.class))
                .thenReturn(mapped);

        BrandDto response = brandService.save(dto);

        assertTrue(response.isSuccess());
        assertEquals("Brand created successfully", response.getMessage());

        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    void save_failure_empty_name() {

        BrandDto dto = new BrandDto();
        dto.setBrandName(" ");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Brand name is required", response.getMessage());

        verifyNoInteractions(brandRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void save_failure_duplicate() {

        BrandDto dto = new BrandDto();
        dto.setBrandName("Nike");

        Brand existing = new Brand();
        existing.setDeleted(false);

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(existing);

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Brand already exists", response.getMessage());

        verify(brandRepository, never()).save(any());
    }

    @Test
    void save_success_deleted_record_exists() {

        BrandDto dto = new BrandDto();
        dto.setBrandName("Nike");

        Brand existing = new Brand();
        existing.setDeleted(true);

        Brand saved = new Brand();

        BrandDto mapped = new BrandDto();

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(existing);

        when(brandRepository.save(any(Brand.class)))
                .thenReturn(saved);

        when(modelMapper.map(saved, BrandDto.class))
                .thenReturn(mapped);

        BrandDto response = brandService.save(dto);

        assertTrue(response.isSuccess());
        assertEquals("Brand created successfully", response.getMessage());
    }

    @Test
    void update_success() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("Nike");
        dto.setBrandName("Nike Updated");
        dto.setDescription("Updated");

        Brand existing = new Brand();
        existing.setIdentifier("Nike");
        existing.setDeleted(false);

        Brand saved = new Brand();

        BrandDto mapped = new BrandDto();

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(existing);

        when(brandRepository.save(existing))
                .thenReturn(saved);

        when(modelMapper.map(saved, BrandDto.class))
                .thenReturn(mapped);

        BrandDto response = brandService.update(dto);

        assertTrue(response.isSuccess());
        assertEquals("Brand updated successfully", response.getMessage());

        verify(brandRepository).save(existing);
    }

    @Test
    void update_failure_not_found() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("Nike");

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(null);

        BrandDto response = brandService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals("Brand not found", response.getMessage());

        verify(brandRepository, never()).save(any());
    }

    @Test
    void update_failure_deleted() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("Nike");

        Brand brand = new Brand();
        brand.setDeleted(true);

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(brand);

        BrandDto response = brandService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals("Brand not found", response.getMessage());

        verify(brandRepository, never()).save(any());
    }

    @Test
    void find_success() {

        Brand brand = new Brand();
        brand.setDeleted(false);

        BrandDto dto = new BrandDto();

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        BrandDto response = brandService.findByIdentifier("Nike");

        assertNotNull(response);
    }

    @Test
    void find_not_found() {

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(null);

        BrandDto response = brandService.findByIdentifier("Nike");

        assertNull(response);
    }

    @Test
    void find_deleted() {

        Brand brand = new Brand();
        brand.setDeleted(true);

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(brand);

        BrandDto response = brandService.findByIdentifier("Nike");

        assertNull(response);
    }

    @Test
    void delete_success() {

        Brand brand = new Brand();
        brand.setDeleted(false);

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(brand);

        when(brandRepository.save(brand))
                .thenReturn(brand);

        brandService.delete("Nike");

        assertTrue(brand.getDeleted());

        verify(brandRepository).save(brand);
    }

    @Test
    void delete_not_found() {

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(null);

        brandService.delete("Nike");

        verify(brandRepository, never()).save(any());
    }

    @Test
    void find_all() {

        Brand brand = new Brand();

        Page<Brand> page = new PageImpl<>(List.of(brand));

        when(brandRepository.findByDeletedFalse(any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(new BrandDto()));

        WsDto<BrandDto> result =
                brandService.findAll(PageRequest.of(0, 5));

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void active_brands() {

        Brand brand = new Brand();
        brand.setStatus(true);
        brand.setDeleted(false);

        when(brandRepository.findByDeletedFalse())
                .thenReturn(List.of(brand));

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(new BrandDto());

        List<BrandDto> result = brandService.findActiveBrands();

        assertEquals(1, result.size());
    }

    @Test
    void active_brands_empty() {

        Brand brand = new Brand();
        brand.setStatus(false);

        when(brandRepository.findByDeletedFalse())
                .thenReturn(List.of(brand));

        List<BrandDto> result = brandService.findActiveBrands();

        assertTrue(result.isEmpty());
    }

    @Test
    void toggle_success() {

        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setStatus(false);
        brand.setDeleted(false);

        BrandDto mapped = new BrandDto();

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(brand);

        when(brandRepository.save(brand))
                .thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(mapped);

        BrandDto response = brandService.toggleStatus("Nike");

        assertTrue(response.isSuccess());
        assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void toggle_not_found() {

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(null);

        BrandDto response = brandService.toggleStatus("Nike");

        assertFalse(response.isSuccess());
        assertEquals("Brand not found", response.getMessage());
    }

    @Test
    void toggle_deleted() {

        Brand brand = new Brand();
        brand.setDeleted(true);

        when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(brand);

        BrandDto response = brandService.toggleStatus("Nike");

        assertFalse(response.isSuccess());
        assertEquals("Brand not found", response.getMessage());
    }
}