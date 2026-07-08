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
        dto.setIdentifier("BRD001");
        dto.setStatus(true);

        BrandDto response = brandService.save(dto);

        Brand saved = brandRepository.findByIdentifier("BRD001");

        assertNotNull(saved);
        assertEquals("BRD001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {

        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setDeleted(false);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRD001");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - BRD001 already exists",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateBrandDetails() {

        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setDeleted(false);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRD001");

        BrandDto response = brandService.update(dto);

        Brand updated = brandRepository.findByIdentifier("BRD001");
        assertNotNull(updated);
    }

    @Test
    void findByIdentifier_shouldReturnBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BRD001");

        brandRepository.save(brand);

        BrandDto result = brandService.findByIdentifier("BRD001");

        assertEquals("BRD001", result.getIdentifier());
    }

    @Test
    void toggleStatus_shouldToggleValue() {

        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setStatus(true);

        brandRepository.save(brand);

        brandService.toggleStatus("BRD001");

        Brand updated = brandRepository.findByIdentifier("BRD001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {

        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setDeleted(false);

        brandRepository.save(brand);

        brandService.delete("BRD001");

        Brand deleted = brandRepository.findByIdentifier("BRD001");

        assertTrue(deleted.isDeleted());
    }
}