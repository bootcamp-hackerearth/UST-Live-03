package com.ust.pos;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
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
class BrandServiceTestIT {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandRepository brandRepository;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
    }

    @Test
    void save_shouldCreateBrand() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setStatus(true);

        BrandDto response = brandService.save(dto);

        Brand saved = brandRepository.findByIdentifier("BR001");

        assertNotNull(saved);
        assertEquals("BR001", saved.getIdentifier());
        assertTrue(response.isSuccess());
    }

    @Test
    void save_shouldThrowExceptionWhenDuplicateExists() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> brandService.save(dto)
        );

        assertEquals(
                "Brand with identifier - BR001 already exists.",
                exception.getMessage()
        );
    }

    @Test
    void save_shouldThrowExceptionWhenPreviouslyDeleted() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(true);
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> brandService.save(dto)
        );

        assertEquals(
                "Brand with identifier - BR001 already exists but was deleted, Please contact Administrator.",
                exception.getMessage()
        );
    }

    @Test
    void update_shouldUpdateBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);
        brand.setDeleted(false);
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setStatus(false);

        BrandDto response = brandService.update(dto);

        Brand updated = brandRepository.findByIdentifier("BR001");

        assertTrue(response.isSuccess());
        assertFalse(updated.isStatus());
    }

    @Test
    void update_shouldReturnFailureWhenBrandNotFound() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR999");

        BrandDto response = brandService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - BR999 not found",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_shouldReturnBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brandRepository.save(brand);

        BrandDto result = brandService.findByIdentifier("BR001");

        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {

        assertThrows(
                ResourseNotFoundException.class,
                () -> brandService.findByIdentifier("BR999")
        );
    }

    @Test
    void delete_shouldSoftDeleteBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);
        brandRepository.save(brand);

        brandService.delete("BR001");

        Brand deleted = brandRepository.findByIdentifier("BR001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void toggleStatus_shouldToggleBrandStatus() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);
        brandRepository.save(brand);

        brandService.toggleStatus("BR001");

        Brand updated = brandRepository.findByIdentifier("BR001");

        assertFalse(updated.isStatus());
    }

    @Test
    void findAll_pageable_shouldReturnOnlyNonDeletedBrands() {

        Brand active = new Brand();
        active.setIdentifier("BR001");
        active.setDeleted(false);
        brandRepository.save(active);

        Brand deleted = new Brand();
        deleted.setIdentifier("BR002");
        deleted.setDeleted(true);
        brandRepository.save(deleted);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<BrandDto> result = brandService.findAll(pageable);

        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getContent().size());
        assertEquals("BR001", result.getContent().get(0).getIdentifier());
    }

    @Test
    void findAll_specification_shouldReturnFilteredResults() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);
        brandRepository.save(brand);

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Brand> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "BR001");

        WsDto<BrandDto> result =
                brandService.findAll(specification, pageable);

        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getContent().size());
        assertEquals("BR001", result.getContent().get(0).getIdentifier());
    }

    @Test
    void findActiveBrands_shouldReturnOnlyActiveBrands() {

        Brand active = new Brand();
        active.setIdentifier("BR001");
        active.setStatus(true);
        active.setDeleted(false);
        brandRepository.save(active);

        Brand inactive = new Brand();
        inactive.setIdentifier("BR002");
        inactive.setStatus(false);
        inactive.setDeleted(false);
        brandRepository.save(inactive);

        List<BrandDto> result = brandService.findActiveBrands();

        assertEquals(1, result.size());
        assertEquals("BR001", result.get(0).getIdentifier());
    }
}