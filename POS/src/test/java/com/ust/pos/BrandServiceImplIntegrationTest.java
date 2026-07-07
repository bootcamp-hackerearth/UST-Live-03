package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Brand;
import com.ust.pos.models.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BrandServiceImplIntegrationTest {

    @Autowired
    private BrandServiceImpl brandService;

    @Autowired
    private BrandRepository brandRepository;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
        brandRepository.flush();
    }

    private void createBrand(
            String identifier,
            String description,
            Boolean status,
            Boolean deleted) {
        Brand brand = new Brand();
        brand.setIdentifier(identifier);
        brand.setDescription(description);
        brand.setStatus(status);
        brand.setDeleted(deleted);
        brandRepository.saveAndFlush(brand);
    }

    @Test
    void save_ShouldCreateBrandSuccessfully() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setDescription("Nike");
        BrandDto result = brandService.save(dto);
        assertTrue(result.isSuccess());
        assertEquals("Brand created successfully", result.getMessage());
        Brand saved = brandRepository.findByIdentifier("BR001");
        assertNotNull(saved);
        assertEquals("BR001", saved.getIdentifier());
        assertEquals("Nike", saved.getDescription());
        assertTrue(saved.getStatus());
    }

    @Test
    void save_ShouldFail_WhenBrandAlreadyExists() {
        createBrand("BR001", "Nike", true, false);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        BrandDto result = brandService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Brand with identifier - BR001 already exists", result.getMessage());
    }

    @Test
    void save_ShouldFail_WhenDeletedBrandExists() {
        createBrand("BR001", "Nike", true, true);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        BrandDto result = brandService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Brand with identifier - BR001 was deleted and cannot be created again.", result.getMessage());
    }

    @Test
    void update_ShouldUpdateBrand() {
        createBrand("BR001", "Nike", true, false);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setDescription("Adidas");
        brandService.update(dto);
        Brand updated = brandRepository.findByIdentifierAndDeletedFalse("BR001");
        assertNotNull(updated);
        assertEquals("Adidas", updated.getDescription());
    }

    @Test
    void update_ShouldReturnNotFound_WhenBrandDoesNotExist() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("INVALID");
        BrandDto result = brandService.update(dto);
        assertFalse(result.isSuccess());
        assertEquals("Brand with identifier - INVALID not found", result.getMessage());
    }

    @Test
    void findByIdentifier_ShouldReturnBrand() {
        createBrand("BR001", "Nike", true, false);
        BrandDto dto = brandService.findByIdentifier("BR001");
        assertNotNull(dto);
        assertEquals("BR001", dto.getIdentifier());
        assertEquals("Nike", dto.getDescription());
    }

    @Test
    void delete_ShouldSoftDeleteBrand() {
        createBrand("BR001", "Nike", true, false);
        brandService.delete("BR001");
        Brand brand = brandRepository.findByIdentifier("BR001");
        assertNotNull(brand);
        assertTrue(brand.getDeleted());
    }

    @Test
    void toggleStatus_ShouldDisableBrand() {
        createBrand("BR001", "Nike", true, false);
        BrandDto result = brandService.toggleStatus("BR001");
        assertFalse(result.getStatus());
        Brand updated = brandRepository.findByIdentifier("BR001");
        assertFalse(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldEnableBrand() {
        createBrand("BR001", "Nike", false, false);
        BrandDto result = brandService.toggleStatus("BR001");
        assertTrue(result.getStatus());
        Brand updated = brandRepository.findByIdentifier("BR001");
        assertTrue(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldThrowException_WhenBrandNotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> brandService.toggleStatus("INVALID"));
        assertEquals("Brand not found with identifier: INVALID", exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnBrands() {
        createBrand("BR001", "Nike", true, false);
        createBrand("BR002", "Adidas", true, false);
        WsDto<BrandDto> result = brandService.findAll(PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findAllActive_ShouldReturnOnlyActiveBrands() {
        createBrand("BR001", "Nike", true, false);
        createBrand("BR002", "Adidas", false, false);
        List<BrandDto> result = brandService.findAllActive();
        assertEquals(1, result.size());
        assertEquals("BR001", result.get(0).getIdentifier());
    }
}