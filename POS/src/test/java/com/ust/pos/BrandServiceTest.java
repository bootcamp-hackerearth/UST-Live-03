package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.modell.Brand;
import com.ust.pos.modell.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

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
        dto.setIdentifier("B001");
        Brand brand = new Brand();
        when(brandRepository.findByIdentifier("B001")).thenReturn(null);
        when(modelMapper.map(dto, Brand.class)).thenReturn(brand);
        BrandDto result = brandService.save(dto);
        assertTrue(result.isSuccess());
        verify(brandRepository).save(brand);
    }

    @Test
    void save_alreadyExists_deletedTrue() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B001");
        Brand existing = new Brand();
        existing.setDeleted(true);
        when(brandRepository.findByIdentifier("B001")).thenReturn(existing);
        BrandDto result = brandService.save(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted and cannot be created again"));
    }

    @Test
    void save_alreadyExists_deletedFalse() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B001");
        Brand existing = new Brand();
        existing.setDeleted(false);
        when(brandRepository.findByIdentifier("B001")).thenReturn(existing);
        BrandDto result = brandService.save(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void update_success() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B001");
        Brand existing = new Brand();
        when(brandRepository.findByIdentifierAndDeletedFalse("B001")).thenReturn(existing);
        BrandDto result = brandService.update(dto);
        assertNotNull(result);
        verify(brandRepository).save(existing);
    }

    @Test
    void update_notFound() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B001");
        when(brandRepository.findByIdentifierAndDeletedFalse("B001")).thenReturn(null);
        BrandDto result = brandService.update(dto);
        assertFalse(result.isSuccess());
    }

    @Test
    void delete_success() {
        Brand brand = new Brand();
        when(brandRepository.findByIdentifierAndDeletedFalse("B001")).thenReturn(brand);
        brandService.delete("B001");
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatus_success() {
        Brand brand = new Brand();
        brand.setStatus(true);
        BrandDto dto = new BrandDto();
        when(brandRepository.findByIdentifierAndDeletedFalse("B001")).thenReturn(brand);
        when(brandRepository.save(any(Brand.class))).thenAnswer(inv -> inv.getArgument(0));
        when(modelMapper.map(any(Brand.class), eq(BrandDto.class))).thenReturn(dto);
        BrandDto result = brandService.toggleStatus("B001");
        assertNotNull(result);
        assertFalse(brand.getStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatus_nullStatus_shouldSetTrue() {
        Brand brand = new Brand();
        brand.setStatus(null);
        BrandDto dto = new BrandDto();
        dto.setStatus(true);
        when(brandRepository.findByIdentifierAndDeletedFalse("B001")).thenReturn(brand);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        when(modelMapper.map(any(Brand.class), eq(BrandDto.class))).thenReturn(dto);
        BrandDto result = brandService.toggleStatus("B001");
        assertNotNull(result);
        assertTrue(result.getStatus());
        assertTrue(brand.getStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatus_notFound_shouldThrowException() {
        when(brandRepository.findByIdentifierAndDeletedFalse("B001")).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> brandService.toggleStatus("B001"));
    }

    @Test
    void findAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> page = new PageImpl<>(List.of(new Brand()));
        when(brandRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(new BrandDto()));
        var result = brandService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void findByIdentifier_success() {
        Brand brand = new Brand();
        BrandDto dto = new BrandDto();
        when(brandRepository.findByIdentifierAndDeletedFalse("B001")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);
        BrandDto result = brandService.findByIdentifier("B001");
        assertNotNull(result);
    }

    @Test
    void findAllActive_success() {
        Brand brand = new Brand();
        BrandDto dto = new BrandDto();
        when(brandRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(brand));
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);
        List<BrandDto> result = brandService.findAllActive();
        assertEquals(1, result.size());
    }
}