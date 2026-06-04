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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

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
    void saveTestSuccess() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Brand brand = new Brand();

        when(brandRepository.findByIdentifier("B1")).thenReturn(null);
        when(modelMapper.map(dto, Brand.class)).thenReturn(brand);

        BrandDto result = brandService.save(dto);

        Assertions.assertEquals("B1", result.getIdentifier());
        verify(brandRepository).save(brand);
    }

    @Test
    void saveTestFailure() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Brand existing = new Brand();
        existing.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1")).thenReturn(existing);

        BrandDto result = brandService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(brandRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Brand brand = new Brand();

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        BrandDto result = brandService.update(dto);

        Assertions.assertEquals("B1", result.getIdentifier());
        verify(modelMapper).map(dto, brand);
        verify(brandRepository).save(brand);
    }

    @Test
    void updateTestFailure() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        BrandDto result = brandService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(brandRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        brandService.delete("B1");
        verify(brandRepository).deleteByIdentifier("B1");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B1");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto result = brandService.findByIdentifier("B1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("B1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        BrandDto result = brandService.findByIdentifier("B1");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Brand> page = mock(Page.class);

        List<Brand> brands = List.of(new Brand(), new Brand());
        List<BrandDto> dtoList = List.of(new BrandDto(), new BrandDto());

        when(brandRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(brands);
        when(modelMapper.map(eq(brands), any(Type.class))).thenReturn(dtoList);

        List<BrandDto> result = brandService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        verify(brandRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(brands), any(Type.class));
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Brand brand = new Brand();
        brand.setStatus(true);

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        brandService.toggleStatus("B1");

        Assertions.assertFalse(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Brand brand = new Brand();
        brand.setStatus(false);

        when(brandRepository.findByIdentifier("B1")).thenReturn(brand);

        brandService.toggleStatus("B1");

        Assertions.assertTrue(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusNullTest() {
        when(brandRepository.findByIdentifier("B1")).thenReturn(null);

        brandService.toggleStatus("B1");

        verify(brandRepository, never()).save(any());
    }
}