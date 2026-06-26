package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.model.CommonFields;
import org.junit.jupiter.api.BeforeEach;
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
    void setup() {

        brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);
        brandDto = new BrandDto();
        brandDto.setIdentifier("BR001");
    }

    @Test
    void testFindByIdentifier_Found() {

        when(brandRepository.findByIdentifier("BR001")).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);
        BrandDto result = brandService.findByIdentifier("BR001");

        assertNotNull(result);
        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(null);

        ResourseNotFoundException exception =
                assertThrows(
                        ResourseNotFoundException.class,
                        () -> brandService.findByIdentifier("BR001")
                );

        assertEquals("Data cannot found",
                exception.getMessage());
    }

    @Test
    void testSave_Success() {

        when(brandRepository.findByIdentifier("BR001")).thenReturn(null);
        when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        BrandDto result = brandService.save(brandDto);
        verify(brandRepository).save(brand);
        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void testSave_AlreadyExists() {

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> brandService.save(brandDto)
                );

        assertTrue(
                exception.getMessage()
                        .contains("already exists")
        );

        verify(brandRepository, never())
                .save(any());
    }
    @Test
    void testUpdate_Success() {

        when(brandRepository.findByIdentifier("BR001")).thenReturn(brand);
        BrandDto result = brandService.update(brandDto);
        verify(modelMapper).map(brandDto, brand);
        verify(brandRepository).save(brand);
        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void testUpdate_NotFound() {

        when(brandRepository.findByIdentifier("BR001")).thenReturn(null);
        BrandDto result = brandService.update(brandDto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(brandRepository, never()).save(any());
    }

    @Test
    void testDelete() {

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        brandService.delete("BR001");

        assertTrue(brand.isDeleted());

        verify(brandRepository)
                .findByIdentifier("BR001");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Brand brand1 = new Brand();
        brand1.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Page<Brand> page =
                new PageImpl<>(List.of(brand1), pageable, 1);

        when(brandRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(brand1, BrandDto.class))
                .thenReturn(dto);

        var result = brandService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("BR001",
                result.getContent().getFirst().getIdentifier());

        verify(brandRepository)
                .findByIsDeletedFalse(pageable);

        verify(modelMapper)
                .map(brand1, BrandDto.class);
    }

    @Test
    void testSave_DeletedBrandExists() {

        brand.setDeleted(true);

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> brandService.save(brandDto)
                );

        assertTrue(
                exception.getMessage()
                        .contains("already exists but was deleted")
        );

        verify(brandRepository, never())
                .save(any());
    }


    @Test
    void toggleStatus_trueToFalse() {

        Brand brand2 = new Brand();
        brand2.setIdentifier("BR001");
        brand2.setStatus(true);

        when(brandRepository.findByIdentifier("BR001")).thenReturn(brand2);
        brandService.toggleStatus("BR001");
        assertFalse(brand2.isStatus());
        verify(brandRepository).save(argThat(saved ->
                !saved.isStatus()
        ));
    }

    @Test
    void toggleStatus_falseToTrue() {

        Brand brand3 = new Brand();
        brand3.setIdentifier("BR001");
        brand3.setStatus(false);

        when(brandRepository.findByIdentifier("BR001")).thenReturn(brand3);
        brandService.toggleStatus("BR001");
        assertTrue(brand3.isStatus());
        verify(brandRepository).save(argThat(CommonFields::isStatus
        ));
    }

    @Test
    void toggleStatus_notFound() {

        when(brandRepository.findByIdentifier("BR001")).thenReturn(null);
        brandService.toggleStatus("BR001");
        verify(brandRepository, never()).save(any());
    }


    @Test
    void testFindActiveBrands() {

        when(brandRepository.findByStatus(true))
                .thenReturn(Collections.singletonList(brand));
        List<BrandDto> result = brandService.findActiveBrands();
        assertEquals(1, result.size());
        verify(brandRepository).findByStatus(true);
    }
}