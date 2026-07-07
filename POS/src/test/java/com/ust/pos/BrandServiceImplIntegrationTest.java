package com.ust.pos;

import com.ust.pos.brand.service.BrandService;

import com.ust.pos.dto.BrandDto;

import com.ust.pos.dto.WsDto;

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

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertNull;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest

@ActiveProfiles("test")

@Transactional

class BrandServiceImplIntegrationTest {

    @Autowired

    private BrandService brandService;

    @Autowired

    private BrandRepository brandRepository;

    @BeforeEach

    void setUp() {

        brandRepository.deleteAll();

    }

    private BrandDto newBrandDto(String identifier, boolean status) {

        BrandDto dto = new BrandDto();

        dto.setIdentifier(identifier);

        dto.setStatus(status);

        return dto;

    }

    @Test

    void save_createsNewBrand_whenIdentifierNotExists() {

        BrandDto dto = newBrandDto("BR-NEW", true);

        BrandDto result = brandService.save(dto);

        assertNotNull(result);

        assertNotNull(brandRepository.findByIdentifier("BR-NEW"));

    }

    @Test

    void save_returnsFailure_whenIdentifierAlreadyExistsAndNotDeleted() {

        Brand existing = new Brand();

        existing.setIdentifier("BR-DUP");

        existing.setStatus(true);

        existing.setDeleted(false);

        brandRepository.save(existing);

        BrandDto dto = newBrandDto("BR-DUP", true);

        BrandDto result = brandService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Brand with identifier - BR-DUP already exists", result.getMessage());

    }

    @Test

    void save_returnsFailure_whenIdentifierExistsAndIsDeleted() {

        Brand existing = new Brand();

        existing.setIdentifier("BR-DEL");

        existing.setStatus(true);

        existing.setDeleted(true);

        brandRepository.save(existing);

        BrandDto dto = newBrandDto("BR-DEL", true);

        BrandDto result = brandService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Brand identifier - BR-DEL not available", result.getMessage());

    }

    @Test

    void update_updatesExistingBrand() {

        Brand existing = new Brand();

        existing.setIdentifier("BR-UPD");

        existing.setStatus(false);

        existing.setDeleted(false);

        brandRepository.save(existing);

        BrandDto dto = newBrandDto("BR-UPD", true);

        BrandDto result = brandService.update(dto);

        assertNotNull(result);

        assertTrue(brandRepository.findByIdentifier("BR-UPD").isStatus());

    }

    @Test

    void update_returnsFailure_whenBrandNotFound() {

        BrandDto dto = newBrandDto("BR-MISSING", true);

        BrandDto result = brandService.update(dto);

        assertFalse(result.isSuccess());

        assertEquals("brand with identifier - BR-MISSING not found", result.getMessage());

    }

    @Test

    void delete_softDeletesBrand() {

        Brand existing = new Brand();

        existing.setIdentifier("BR-DELETE");

        existing.setStatus(true);

        existing.setDeleted(false);

        brandRepository.save(existing);

        brandService.delete("BR-DELETE");

        Brand deleted = brandRepository.findByIdentifier("BR-DELETE");

        assertTrue(Boolean.TRUE.equals(deleted.getDeleted()));

    }

    @Test

    void findAll_withPageable_returnsWsDto() {

        Brand active = new Brand();

        active.setIdentifier("BR-ACTIVE");

        active.setStatus(true);

        active.setDeleted(false);

        brandRepository.save(active);

        Brand deleted = new Brand();

        deleted.setIdentifier("BR-HIDDEN");

        deleted.setStatus(true);

        deleted.setDeleted(true);

        brandRepository.save(deleted);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<BrandDto> result = brandService.findAll(pageable);

        assertNotNull(result);

        assertEquals(1, result.getTotalRecords());

        assertEquals(1, result.getDtoList().size());

    }

    @Test

    void findByIdentifier_returnsMatchingBrand() {

        Brand existing = new Brand();

        existing.setIdentifier("BR-FIND");

        existing.setStatus(true);

        existing.setDeleted(false);

        brandRepository.save(existing);

        BrandDto result = brandService.findByIdentifier("BR-FIND");

        assertNotNull(result);

        assertEquals("BR-FIND", result.getIdentifier());

    }

    @Test

    void findActiveBrands_returnsOnlyActiveAndNotDeleted() {

        Brand active = new Brand();

        active.setIdentifier("BR-ACT1");

        active.setStatus(true);

        active.setDeleted(false);

        brandRepository.save(active);

        Brand inactive = new Brand();

        inactive.setIdentifier("BR-INACT");

        inactive.setStatus(false);

        inactive.setDeleted(false);

        brandRepository.save(inactive);

        List<Brand> result = brandService.findActiveBrands();

        assertEquals(1, result.size());

        assertEquals("BR-ACT1", result.get(0).getIdentifier());

    }

    @Test

    void toggleStatus_flipsStatus_whenBrandExists() {

        Brand existing = new Brand();

        existing.setIdentifier("BR-TOGGLE");

        existing.setStatus(true);

        existing.setDeleted(false);

        brandRepository.save(existing);

        BrandDto result = brandService.toggleStatus("BR-TOGGLE");

        assertNotNull(result);

        assertFalse(brandRepository.findByIdentifier("BR-TOGGLE").isStatus());

    }

    @Test

    void toggleStatus_returnsNull_whenBrandNotFound() {

        BrandDto result = brandService.toggleStatus("BR-NOT-THERE");

        assertNull(result);

    }

    @Test

    void findAll_withSpecificationAndPageable_returnsWsDto() {

        Brand active = new Brand();

        active.setIdentifier("BR-SPEC");

        active.setStatus(true);

        active.setDeleted(false);

        brandRepository.save(active);

        Specification<Brand> spec = (root, query, cb) -> cb.equal(root.get("identifier"), "BR-SPEC");

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<BrandDto> result = brandService.findAll(spec, pageable);

        assertNotNull(result);

        assertEquals(1, result.getTotalRecords());

        assertEquals("BR-SPEC", result.getDtoList().get(0).getIdentifier());

    }

}
