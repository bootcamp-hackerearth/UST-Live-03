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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandServiceImpl;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "BRAND-001";

        Brand brand = new Brand();
        brand.setIdentifier(identifier);

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier(identifier);

        when(brandRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(brandDto);

        BrandDto result = brandServiceImpl.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
    }

    @Test
    void testSave_Success() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND-001");

        Brand brand = new Brand();
        brand.setIdentifier("BRAND-001");

        when(brandRepository.findByIdentifier("BRAND-001")).thenReturn(null);
        when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);

        BrandDto result = brandServiceImpl.save(brandDto);

        assertNotNull(result);
        verify(brandRepository).save(brand);
    }

    @Test
    void testSave_AlreadyExists() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND-001");

        Brand existingBrand = new Brand();
        existingBrand.setDeleted(false);

        when(brandRepository.findByIdentifier("BRAND-001")).thenReturn(existingBrand);

        BrandDto result = brandServiceImpl.save(brandDto);

        assertFalse(result.isSuccess());
        assertEquals("Brand with identifier - BRAND-001 already exists", result.getMessage());
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testSave_DeletedBrandExists() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND-001");

        Brand existingBrand = new Brand();
        existingBrand.setDeleted(true);

        when(brandRepository.findByIdentifier("BRAND-001")).thenReturn(existingBrand);

        BrandDto result = brandServiceImpl.save(brandDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Brand with identifier - BRAND-001 was deleted and cannot be created again.",
                result.getMessage()
        );
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testUpdate_Success() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND-001");

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("BRAND-001");

        when(brandRepository.findByIdentifier("BRAND-001"))
                .thenReturn(existingBrand);

        BrandDto result = brandServiceImpl.update(brandDto);

        assertNotNull(result);
        verify(modelMapper).map(brandDto, existingBrand);
        verify(brandRepository).save(existingBrand);
    }

    @Test
    void testUpdate_NotFound() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND-001");

        when(brandRepository.findByIdentifier("BRAND-001")).thenReturn(null);

        BrandDto result = brandServiceImpl.update(brandDto);

        assertFalse(result.isSuccess());
        assertEquals("Brand with identifier - BRAND-001 not found", result.getMessage());
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "BRAND-001";

        Brand brand = new Brand();
        brand.setIdentifier(identifier);
        brand.setDeleted(false);

        when(brandRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(brand);

        brandServiceImpl.delete(identifier);

        assertTrue(brand.getDeleted());
        verify(brandRepository).save(brand);
    }

    @Test
    void testDelete_NotFound() {
        String identifier = "BRAND-001";

        when(brandRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(null);

        brandServiceImpl.delete(identifier);

        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Brand brand = new Brand();
        brand.setIdentifier("BRAND-001");

        Page<Brand> brandPage = new PageImpl<>(List.of(brand), pageable, 1);

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND-001");

        when(brandRepository.findAllByDeletedFalse(pageable))
                .thenReturn(brandPage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(brandDto));

        WsDto<BrandDto> result = brandServiceImpl.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_Success() {
        String identifier = "BRAND-001";

        Brand brand = new Brand();
        brand.setIdentifier(identifier);
        brand.setStatus(true);

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier(identifier);

        when(brandRepository.findByIdentifier(identifier)).thenReturn(brand);
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto result = brandServiceImpl.toggleStatus(identifier);

        assertFalse(brand.isStatus());
        assertNotNull(result);
        verify(brandRepository).save(brand);
    }

    @Test
    void testFindActiveBrands_Success() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND-001");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND-001");

        when(brandRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(brand));

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(brandDto));

        List<BrandDto> result = brandServiceImpl.findActiveBrands();

        assertEquals(1, result.size());
        assertEquals("BRAND-001", result.get(0).getIdentifier());
    }


}