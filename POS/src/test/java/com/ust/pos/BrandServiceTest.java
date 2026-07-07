package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    public static final String INVALID = "INVALID";
    public static final String BR_002 = "BR002";
    public static final String BR_001 = "BR001";
    @InjectMocks
    private BrandServiceImpl service;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {

        Brand brand = new Brand();
        BrandDto dto = new BrandDto();

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001))
                .thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier(BR_001));

        when(brandRepository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier(INVALID)
                );

        assertEquals(
                "Brand with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier(BR_001);

        Brand brand = new Brand();
        brand.setStatus(null);

        when(brandRepository.findByIdentifier(BR_001))
                .thenReturn(null);

        when(modelMapper.map(dto, Brand.class))
                .thenReturn(brand);

        BrandDto result = service.save(dto);

        verify(brandRepository).save(brand);

        assertTrue(brand.getStatus());

        Brand duplicate = new Brand();
        duplicate.setDeleted(false);

        when(brandRepository.findByIdentifier(BR_001))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Brand with identifier - BR001 already exists",
                result.getMessage()
        );

        duplicate.setDeleted(true);

        when(brandRepository.findByIdentifier(BR_001))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Brand with Identifier BR001 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Brand brand = new Brand();
        brand.setStatus(false);

        BrandDto dto = new BrandDto();

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_002))
                .thenReturn(brand);

        when(brandRepository.save(brand))
                .thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        BrandDto result = service.toggleStatus(BR_002);

        assertNotNull(result);
        assertTrue(brand.getStatus());

        verify(brandRepository).save(brand);
    }

    @Test
    void updateAndDeleteTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier(BR_001);

        Brand brand = new Brand();
        brand.setIdentifier(BR_001);
        brand.setCreatedBy("admin");
        brand.setCreatedOn(LocalDateTime.now());

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001))
                .thenReturn(brand);

        BrandDto result = service.update(dto);

        verify(modelMapper).map(dto, brand);
        verify(brandRepository).save(brand);

        assertNotNull(result);

        BrandDto invalidDto = new BrandDto();
        invalidDto.setIdentifier(INVALID);

        when(brandRepository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        result = service.update(invalidDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Brand with identifier - INVALID not found",
                result.getMessage()
        );

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001))
                .thenReturn(brand)
                .thenReturn(null);

        service.delete(BR_001);
        service.delete(BR_001);

        verify(brandRepository, atLeast(2))
                .save(any(Brand.class));
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Brand> page =
                new PageImpl<>(
                        List.of(new Brand()),
                        pageable,
                        1
                );

        when(brandRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(brandRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new BrandDto()));

        WsDto<BrandDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());

        Specification<Brand> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<BrandDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());

        verify(brandRepository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findAllActiveTest() {

        Brand brand = new Brand();
        BrandDto dto = new BrandDto();

        when(brandRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(brand));

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        List<BrandDto> result =
                service.findAllActive();

        assertEquals(1, result.size());

        when(brandRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        result = service.findAllActive();

        assertTrue(result.isEmpty());
    }

    @Test
    void toggleStatusTest() {

        Brand brand = new Brand();
        brand.setStatus(true);

        BrandDto dto = new BrandDto();

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_001))
                .thenReturn(brand);

        when(brandRepository.save(brand))
                .thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        BrandDto result = service.toggleStatus(BR_001);

        assertNotNull(result);
        assertFalse(brand.getStatus());

        Brand nullStatusBrand = new Brand();
        nullStatusBrand.setStatus(null);

        when(brandRepository.findByIdentifierAndDeletedFalse(BR_002))
                .thenReturn(nullStatusBrand);

        when(brandRepository.save(nullStatusBrand))
                .thenReturn(nullStatusBrand);

        when(modelMapper.map(nullStatusBrand, BrandDto.class))
                .thenReturn(dto);

        service.toggleStatus(BR_002);

        assertTrue(nullStatusBrand.getStatus());

        when(brandRepository.findByIdentifierAndDeletedFalse("BR003"))
                .thenReturn(null);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.toggleStatus("BR003")
                );

        assertEquals(
                "brand not found with identifier: BR003",
                exception.getMessage()
        );

        assertEquals(
                "brand not found",
                BrandServiceImpl.BRAND_NOT_FOUND.getMessage()
        );
    }
}