package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
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

@ActiveProfiles("test")
@SpringBootTest
class BrandServiceImplIntegrationTest {

    @Autowired
    private BrandServiceImpl brandService;

    @Autowired
    private BrandRepository brandRepository;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
        Brand brand = new Brand();
        brand.setIdentifier("BRAND1");
        brand.setDescription("Test Brand");
        brand.setStatus(true);
        brand.setDeleted(false);
        brandRepository.save(brand);
    }

    @Test
    void testSaveSuccess() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND2");
        dto.setDescription("New Brand");
        dto.setStatus(true);
        BrandDto response = brandService.save(dto);
        assertTrue(response.isSuccess());
        Brand saved = brandRepository.findByIdentifier("BRAND2");
        assertNotNull(saved);
        assertEquals("BRAND2", saved.getIdentifier());
        assertEquals("New Brand", saved.getDescription());
        assertTrue(saved.getStatus());
        assertFalse(saved.getDeleted());
    }

    @Test
    void testSaveAlreadyExists() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND1");
        BrandDto response = brandService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - BRAND1 already exists",
                response.getMessage()
        );
    }

    @Test
    void testSaveDeletedBrand() {
        Brand brand = brandRepository.findByIdentifier("BRAND1");
        brand.setDeleted(true);
        brandRepository.save(brand);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND1");
        BrandDto response = brandService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - BRAND1 was deleted and cannot be re-created",
                response.getMessage()
        );
    }

    @Test
    void testUpdateSuccess() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRAND1");
        dto.setDescription("Updated Brand");
        dto.setStatus(false);
        brandService.update(dto);
        Brand updated = brandRepository.findByIdentifier("BRAND1");
        assertEquals("Updated Brand", updated.getDescription());
        assertFalse(updated.getStatus());
    }

    @Test
    void testUpdateNotFound() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("UNKNOWN");
        BrandDto response = brandService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void testDelete() {
        brandService.delete("BRAND1");
        Brand deleted = brandRepository.findByIdentifier("BRAND1");
        assertTrue(deleted.getDeleted());
    }

    @Test
    void testDeleteNotFound() {
        assertDoesNotThrow(() -> brandService.delete("UNKNOWN"));
    }

    @Test
    void testFindByIdentifierSuccess() {
        BrandDto dto = brandService.findByIdentifier("BRAND1");
        assertNotNull(dto);
        assertEquals("BRAND1", dto.getIdentifier());
        assertEquals("Test Brand", dto.getDescription());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindByIdentifierNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> brandService.findByIdentifier("UNKNOWN")
        );
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        WsDto<BrandDto> response = brandService.findAll(pageable);
        assertNotNull(response);
        assertEquals(1, response.getDtoList().size());
        assertEquals(1, response.getTotalRecords());
        assertEquals(1, response.getTotalPages());
        assertEquals(10, response.getSizePerPage());
        assertEquals(0, response.getPage());
    }

    @Test
    void testFindAllActive() {
        List<BrandDto> brands = brandService.findAllActive();
        assertEquals(1, brands.size());
        BrandDto dto = brands.get(0);
        assertEquals("BRAND1", dto.getIdentifier());
        assertEquals("Test Brand", dto.getDescription());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindAllActiveNoResults() {
        Brand brand = brandRepository.findByIdentifier("BRAND1");
        brand.setStatus(false);
        brandRepository.save(brand);
        List<BrandDto> brands = brandService.findAllActive();
        assertTrue(brands.isEmpty());
    }

    @Test
    void testUpdateStatusFalse() {
        brandService.updateStatus("BRAND1", false);
        Brand brand = brandRepository.findByIdentifier("BRAND1");
        assertFalse(brand.getStatus());
    }

    @Test
    void testUpdateStatusTrue() {
        Brand brand = brandRepository.findByIdentifier("BRAND1");
        brand.setStatus(false);
        brandRepository.save(brand);
        brandService.updateStatus("BRAND1", true);
        Brand updated = brandRepository.findByIdentifier("BRAND1");
        assertTrue(updated.getStatus());
    }

    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Brand> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "BRAND1");
        WsDto<BrandDto> response =
                brandService.findAll(specification, pageable);
        assertEquals(1, response.getDtoList().size());
        assertEquals(
                "BRAND1",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void testFindAllWithSpecificationNoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Brand> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "XYZ");
        WsDto<BrandDto> response =
                brandService.findAll(specification, pageable);
        assertEquals(0, response.getDtoList().size());
        assertEquals(0, response.getTotalRecords());
        assertEquals(0, response.getTotalPages());
    }
}