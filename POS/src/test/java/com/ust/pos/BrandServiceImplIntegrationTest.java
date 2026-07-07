package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BrandServiceImplIntegrationTest {

    @Autowired
    private BrandServiceImpl brandService;

    @Autowired
    private BrandRepository brandRepository;

    private static final String IDENTIFIER = "BRAND001";

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        brandRepository.deleteAll();
    }

    @Test
    void save_newBrand_persistsSuccessfully() {
        BrandDto dto = buildDto(IDENTIFIER, "Test Brand", true);

        brandService.save(dto);

        Brand saved = brandRepository.findByIdentifier(IDENTIFIER);
        assertNotNull(saved);
        assertEquals("Test Brand", saved.getDescription());
        assertFalse(saved.isDeleted());
    }

    @Test
    void save_duplicateIdentifier_returnsFailureWithoutPersistingDuplicate() {
        brandService.save(buildDto(IDENTIFIER, "Original", true));

        BrandDto duplicate = buildDto(IDENTIFIER, "Duplicate attempt", true);
        BrandDto result = brandService.save(duplicate);

        assertFalse(result.isSuccess());
        assertEquals("Brand with identifier - " + IDENTIFIER + " already exists", result.getMessage());
        assertEquals(1, brandRepository.findAll().size());
    }

    @Test
    void save_identifierBelongingToSoftDeletedBrand_returnsNotAvailableFailure() {
        Brand brand = new Brand();
        brand.setIdentifier(IDENTIFIER);
        brand.setDescription("Old brand");
        brand.setStatus(true);
        brand.setDeleted(true);
        brandRepository.save(brand);

        BrandDto dto = buildDto(IDENTIFIER, "New brand", true);
        BrandDto result = brandService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Brand identifier - " + IDENTIFIER + " not available", result.getMessage());
    }

    @Test
    void update_existingBrand_updatesDescriptionAndStatus() {
        brandService.save(buildDto(IDENTIFIER, "Initial description", true));

        BrandDto updateDto = buildDto(IDENTIFIER, "Updated description", false);
        BrandDto result = brandService.update(updateDto);

        Brand updated = brandRepository.findByIdentifier(IDENTIFIER);
        assertEquals("Updated description", updated.getDescription());
        assertFalse(updated.getStatus());
        assertNotNull(result);
    }

    @Test
    void update_nonExistentBrand_returnsFailure() {
        BrandDto updateDto = buildDto("DOES_NOT_EXIST", "desc", true);

        BrandDto result = brandService.update(updateDto);

        assertFalse(result.isSuccess());
        assertEquals("Brand not found", result.getMessage());
    }

    @Test
    void delete_softDeletesBrandInsteadOfRemovingRow() {
        brandService.save(buildDto(IDENTIFIER, "To be deleted", true));

        brandService.delete(IDENTIFIER);

        Brand brand = brandRepository.findByIdentifier(IDENTIFIER);
        assertNotNull(brand);
        assertTrue(brand.isDeleted());
    }

    @Test
    void delete_trimsWhitespaceFromIdentifier() {
        brandService.save(buildDto(IDENTIFIER, "Trim test", true));
        brandService.delete("  " + IDENTIFIER + "  ");

        Brand brand = brandRepository.findByIdentifier(IDENTIFIER);
        assertTrue(brand.isDeleted());
    }

    @Test
    void findAll_pageable_excludesSoftDeletedBrands() {
        brandService.save(buildDto("ACTIVE1", "Active brand", true));
        brandService.save(buildDto("DELETED1", "Deleted brand", true));
        brandService.delete("DELETED1");

        var result = brandService.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalRecords());
        assertEquals("ACTIVE1", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findByIdentifier_existingActiveBrand_returnsDto() {
        brandService.save(buildDto(IDENTIFIER, "Findable brand", true));

        BrandDto found = brandService.findByIdentifier(IDENTIFIER);

        assertEquals(IDENTIFIER, found.getIdentifier());
        assertEquals("Findable brand", found.getDescription());
    }

    @Test
    void findByIdentifier_softDeletedBrand_throwsResourceNotFoundException() {
        brandService.save(buildDto(IDENTIFIER, "Will be deleted", true));
        brandService.delete(IDENTIFIER);

        assertThrows(ResourceNotFoundException.class,
                () -> brandService.findByIdentifier(IDENTIFIER));
    }

    @Test
    void findByIdentifier_nonExistentIdentifier_throwsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class,
                () -> brandService.findByIdentifier("NOPE"));
    }

    @Test
    void toggleStatus_flipsBrandStatus() {
        brandService.save(buildDto(IDENTIFIER, "Toggle test", true));

        brandService.toggleStatus(IDENTIFIER);

        Brand brand = brandRepository.findByIdentifier(IDENTIFIER);
        assertFalse(brand.getStatus());

        brandService.toggleStatus(IDENTIFIER);
        brand = brandRepository.findByIdentifier(IDENTIFIER);
        assertTrue(brand.getStatus());
    }

    @Test
    void findAllActive_returnsOnlyActiveNonDeletedBrands() {
        brandService.save(buildDto("ACTIVE_ON", "Active on", true));
        brandService.save(buildDto("ACTIVE_OFF", "Active off", false));
        brandService.save(buildDto("DELETED_ON", "Deleted but on", true));
        brandService.delete("DELETED_ON");

        List<BrandDto> activeBrands = brandService.findAllActive();

        assertEquals(1, activeBrands.size());
        assertEquals("ACTIVE_ON", activeBrands.get(0).getIdentifier());
    }

    @Test
    void findAll_withKeywordSpecification_filtersByDescription() {
        brandService.save(buildDto("SEARCH1", "Nike shoes", true));
        brandService.save(buildDto("SEARCH2", "Adidas shoes", true));

        Specification<Brand> spec = (root, query, cb) ->
                cb.like(cb.lower(root.get("description")), "%nike%");

        var result = brandService.findAll(spec, PageRequest.of(0, 10), "nike");

        assertEquals(1, result.getTotalRecords());
        assertEquals("SEARCH1", result.getDtoList().get(0).getIdentifier());
        assertEquals("nike", result.getKeyword());
    }

    private BrandDto buildDto(String identifier, String description, boolean status) {
        BrandDto dto = new BrandDto();
        dto.setIdentifier(identifier);
        dto.setDescription(description);
        dto.setStatus(status);
        return dto;
    }
}