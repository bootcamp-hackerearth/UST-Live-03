package com.ust.pos;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
        dto.setIdentifier("BR001");
        dto.setStatus(true);
        BrandDto response = brandService.save(dto);
        Brand saved = brandRepository.findByIdentifier("BR001");
        assertNotNull(saved);
        assertEquals("BR001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);
        brandRepository.save(brand);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        BrandDto response = brandService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - BR001 already exists",
                response.getMessage());
    }

    @Test
    void update_shouldUpdateBrand() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);
        brand.setStatus(true);
        brandRepository.save(brand);
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setStatus(false);
        BrandDto response = brandService.update(dto);
        assertTrue(response.isSuccess());
        Brand updated = brandRepository.findByIdentifier("BR001");
        assertFalse(updated.isStatus());
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
    void toggleStatus_shouldToggleValue() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);
        brandRepository.save(brand);
        brandService.toggleStatus("BR001", true);
        Brand updated = brandRepository.findByIdentifier("BR001");
        assertFalse(updated.isStatus());
    }

    @Test
    void deleteByIdentifier_shouldSoftDelete() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);
        brandRepository.save(brand);
        brandService.deleteByIdentifier("BR001");
        Brand deleted = brandRepository.findByIdentifier("BR001");
        assertTrue(deleted.isDeleted());
    }
}