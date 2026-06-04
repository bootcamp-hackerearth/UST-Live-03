package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        dto.setStatus(true);
        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setBrandName("Nike");
        when(brandRepository.findByIdentifier("Nike")).thenReturn(null);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        when(modelMapper.map(any(Brand.class), eq(BrandDto.class))).thenReturn(new BrandDto());
        BrandDto response = brandService.save(dto);
        Assertions.assertNotNull(response);
    }

    @Test
    void update_success() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("Nike");
        dto.setBrandName("Nike Updated");
        Brand existing = new Brand();
        existing.setIdentifier("Nike");
        when(brandRepository.findByIdentifier("Nike")).thenReturn(existing);
        when(brandRepository.save(any(Brand.class))).thenReturn(existing);
        when(modelMapper.map(any(), any())).thenAnswer(invocation -> {
            Object src = invocation.getArgument(0);
            if (src instanceof Brand) {
                BrandDto res = new BrandDto();
                res.setSuccess(true);
                res.setMessage("Brand updated successfully");
                return res;
            }
            return existing;
        });
        BrandDto response = brandService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Brand updated successfully", response.getMessage());
    }

    @Test
    void update_not_found() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("Nike");
        when(brandRepository.findByIdentifier("Nike")).thenReturn(null);
        BrandDto response = brandService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand not found", response.getMessage());
    }

    @Test
    void find_success() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("Nike");
        when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);
        BrandDto response = brandService.findByIdentifier("Nike");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Nike", response.getIdentifier());
    }

    @Test
    void delete_test() {
        doNothing().when(brandRepository).deleteByIdentifier("Nike");
        brandService.delete("Nike");
        verify(brandRepository).deleteByIdentifier("Nike");
    }

    @Test
    void find_all_paged() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        Page<Brand> page = new PageImpl<>(List.of(brand));
        when(brandRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(new BrandDto()));
        List<BrandDto> result = brandService.findAll(PageRequest.of(0, 5));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void find_active_brands() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setStatus(true);
        when(brandRepository.findAll()).thenReturn(List.of(brand));
        when(modelMapper.map(any(Brand.class), eq(BrandDto.class))).thenReturn(new BrandDto());
        List<BrandDto> result = brandService.findActiveBrands();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void toggle_success() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setStatus(false);
        when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        BrandDto response = brandService.toggleStatus("Nike");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void toggle_not_found() {
        when(brandRepository.findByIdentifier("Nike")).thenReturn(null);
        BrandDto response = brandService.toggleStatus("Nike");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand not found", response.getMessage());
    }
}