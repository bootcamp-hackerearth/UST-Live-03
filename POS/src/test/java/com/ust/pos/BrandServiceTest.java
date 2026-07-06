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
import org.springframework.data.jpa.domain.Specification;

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
        brand.setIdentifier("BR001");
        brand.setDeleted(false);
        brand.setStatus(true);

        brandDto = new BrandDto();
        brandDto.setIdentifier("BR001");
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
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Brand> specification = mock(Specification.class);

        Page<Brand> page = new PageImpl<>(Collections.singletonList(brand));

        when(brandRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(brandDto));

        WsDto<BrandDto> result =
                brandService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());

        verify(brandRepository).findAll(specification, pageable);
    }

    @Test
    void testSave_NewBrand() {

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(null);

        when(modelMapper.map(brandDto, Brand.class))
                .thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertNotNull(result);

        verify(brandRepository).save(brand);
    }

    @Test
    void testSave_AlreadyExists() {

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeletedBrand() {

        brand.setDeleted(true);

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        BrandDto result = brandService.save(brandDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testDelete() {

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        brandService.delete("BR001");

        assertTrue(brand.isDeleted());
        assertFalse(brand.isStatus());

        verify(brandRepository).save(brand);
    }

    @Test
    void testFindByIdentifier_Success() {

        when(brandRepository.findByIdentifierAndDeletedFalse("BR001"))
                .thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(brandDto);

        BrandDto result = brandService.findByIdentifier("BR001");

        assertNotNull(result);
        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(brandRepository.findByIdentifierAndDeletedFalse("BR001"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> brandService.findByIdentifier("BR001"));
    }

    @Test
    void testUpdate_Success() {

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        doNothing().when(modelMapper).map(brandDto, brand);

        BrandDto result = brandService.update(brandDto);

        assertNotNull(result);

        verify(brandRepository).save(brand);
    }

    @Test
    void testUpdate_BrandNotFound() {

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(null);

        BrandDto result = brandService.update(brandDto);

        assertFalse(result.isSuccess());
    }

    @Test
    void testChangeToggleStatus() {

        when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(brandDto);

        BrandDto result =
                brandService.changeToggleStatus("BR001", false);

        assertNotNull(result);
        assertFalse(brand.isStatus());

        verify(brandRepository).save(brand);
    }

    @Test
    void testFindActiveStatus() {

        Brand inactiveBrand = new Brand();
        inactiveBrand.setStatus(false);

        when(brandRepository.findAll())
                .thenReturn(List.of(brand, inactiveBrand));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(brandDto));

        List<BrandDto> result = brandService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}