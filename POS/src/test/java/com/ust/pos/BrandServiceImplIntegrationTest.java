package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginatedResponseDto;
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
        brand.setIdentifier("APPLE");
        brand.setDescription("Apple Brand");
        brand.setStatus(true);
        brand.setIsDeleted(false);

        brandRepository.save(brand);
    }

    @Test
    void testSaveSuccess() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("SAMSUNG");
        dto.setDescription("Samsung Brand");
        dto.setStatus(true);

        BrandDto response = brandService.save(dto);

        assertTrue(response.isSuccess());

        Brand saved = brandRepository.findByIdentifier("SAMSUNG");

        assertNotNull(saved);
        assertEquals("SAMSUNG", saved.getIdentifier());
        assertEquals("Samsung Brand", saved.getDescription());
        assertTrue(saved.getStatus());
        assertFalse(saved.getIsDeleted());
    }

    @Test
    void testSaveAlreadyExists() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - APPLE already exists",
                response.getMessage()
        );
    }

    @Test
    void testSaveDeletedBrand() {

        Brand brand = brandRepository.findByIdentifier("APPLE");
        brand.setIsDeleted(true);
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - APPLE was deleted. Contact admin for further support or try with a different identifier.",
                response.getMessage()
        );
    }

    @Test
    void testUpdateSuccess() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("APPLE");
        dto.setDescription("Updated Description");
        dto.setStatus(false);

        BrandDto response = brandService.update(dto);

        assertTrue(response.isSuccess());

        Brand updated = brandRepository.findByIdentifier("APPLE");

        assertEquals("Updated Description", updated.getDescription());
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
    void testDeleteSuccess() {

        BrandDto response = brandService.delete("APPLE");

        assertTrue(response.isSuccess());
        assertEquals("Brand deleted successfully", response.getMessage());

        Brand deleted = brandRepository.findByIdentifier("APPLE");

        assertTrue(deleted.getIsDeleted());
        assertFalse(deleted.getStatus());
    }

    @Test
    void testDeleteNotFound() {

        BrandDto response = brandService.delete("UNKNOWN");

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void testFindByIdentifierSuccess() {

        BrandDto dto = brandService.findByIdentifier("APPLE");

        assertNotNull(dto);
        assertEquals("APPLE", dto.getIdentifier());
        assertEquals("Apple Brand", dto.getDescription());
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

        PaginatedResponseDto<BrandDto> response =
                brandService.findAll(pageable);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
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

        assertEquals("APPLE", dto.getIdentifier());
        assertTrue(dto.getStatus());
        assertFalse(dto.getIsDeleted());
    }

    @Test
    void testFindAllActiveNoResults() {

        Brand brand = brandRepository.findByIdentifier("APPLE");
        brand.setStatus(false);
        brandRepository.save(brand);

        List<BrandDto> brands = brandService.findAllActive();

        assertTrue(brands.isEmpty());
    }

    @Test
    void testChangeStatusToFalse() {

        brandService.changeStatus("APPLE", false);

        Brand brand = brandRepository.findByIdentifier("APPLE");

        assertFalse(brand.getStatus());
    }

    @Test
    void testChangeStatusToTrue() {

        Brand brand = brandRepository.findByIdentifier("APPLE");
        brand.setStatus(false);
        brandRepository.save(brand);

        brandService.changeStatus("APPLE", true);

        Brand updated = brandRepository.findByIdentifier("APPLE");

        assertTrue(updated.getStatus());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Brand> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "APPLE");

        PaginatedResponseDto<BrandDto> response =
                brandService.findAll(specification, pageable);

        assertEquals(1, response.getItems().size());
        assertEquals("APPLE",
                response.getItems().get(0).getIdentifier());
    }

    @Test
    void testFindAllWithSpecificationNoResults() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Brand> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "XYZ");

        PaginatedResponseDto<BrandDto> response =
                brandService.findAll(specification, pageable);

        assertEquals(0, response.getItems().size());
        assertEquals(0, response.getTotalRecords());
        assertEquals(0, response.getTotalPages());
    }
}