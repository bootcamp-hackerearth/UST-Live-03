package com.ust.pos;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BrandServiceImplIT {

    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandRepository brandRepository;

    @BeforeEach
    void cleanUp() {
        brandRepository.deleteAll();
    }

    @Test
    void save_shouldCreateBrand() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND001");
        dto.setStatus(true);
        BrandDto response = brandService.save(dto);
        Brand saved = brandRepository.findByIdentifier("BRAND001");
        assertTrue(response.isSuccess());
        assertEquals("Brand created successfully", response.getMessage());
        assertNotNull(saved);
        assertEquals("BRAND001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brand.setDeleted(false);
        brandRepository.save(brand);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND001");
        BrandDto response = brandService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals("Brand with identifier BRAND001 already exists", response.getMessage());
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brand.setDeleted(true);
        brandRepository.save(brand);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND001");
        BrandDto response = brandService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals("Brand with identifier BRAND001 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void update_shouldModifyBrandDetails() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brand.setDeleted(false);
        brandRepository.save(brand);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND001");
        brandService.update(dto);
        Brand updated = brandRepository.findByIdentifier("BRAND001");
        assertNotNull(updated);
        assertEquals("BRAND001", updated.getIdentifier());
    }

    @Test
    void update_shouldFailWhenNotFound() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND_MISSING");
        BrandDto response = brandService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals("Brand with identifier - BRAND_MISSING not found", response.getMessage());
    }

    @Test
    void findByIdentifier_shouldReturnBrand() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brandRepository.save(brand);
        BrandDto result = brandService.findByIdentifier("BRAND001");
        assertNotNull(result);
        assertEquals("BRAND001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> brandService.findByIdentifier("BRAND_MISSING"));
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brand.setStatus(true);
        brandRepository.save(brand);
        brandService.toggleStatus("BRAND001");
        Brand updated = brandRepository.findByIdentifier("BRAND001");
        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brand.setDeleted(false);
        brandRepository.save(brand);
        boolean result = brandService.delete("BRAND001");
        Brand deleted = brandRepository.findByIdentifier("BRAND001");
        assertTrue(result);
        assertTrue(deleted.isDeleted());
    }

    @Test
    void delete_shouldReturnFalseWhenNotFound() {
        boolean result = brandService.delete("BRAND_MISSING");
        assertFalse(result);
    }

    @Test
    void findAll_pageable_shouldReturnOnlyNonDeletedBrands() {
        Brand active = new Brand();
        active.setIdentifier("BRAND001");
        active.setDeleted(false);
        brandRepository.save(active);
        Brand deleted = new Brand();
        deleted.setIdentifier("BRAND002");
        deleted.setDeleted(true);
        brandRepository.save(deleted);
        Pageable pageable = PageRequest.of(0, 10);
        WsDto<BrandDto> result = brandService.findAll(pageable);
        assertEquals(1, result.getTotalRecords());
        assertEquals("BRAND001", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAll_specification_shouldReturnFilteredResults() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brand.setDeleted(false);
        brandRepository.save(brand);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Brand> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        WsDto<BrandDto> result = brandService.findAll(spec, pageable, "BRAND001");
        assertEquals(1, result.getTotalRecords());
        assertEquals("BRAND001", result.getKeyword());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedBrands() {
        Brand validBrand = new Brand();
        validBrand.setIdentifier("BRAND001");
        validBrand.setStatus(true);
        validBrand.setDeleted(false);
        brandRepository.save(validBrand);
        Brand inactiveBrand = new Brand();
        inactiveBrand.setIdentifier("BRAND002");
        inactiveBrand.setStatus(false);
        inactiveBrand.setDeleted(false);
        brandRepository.save(inactiveBrand);
        List<BrandDto> result = brandService.findIfTrue();
        assertEquals(1, result.size());
        assertEquals("BRAND001", result.get(0).getIdentifier());
    }
}