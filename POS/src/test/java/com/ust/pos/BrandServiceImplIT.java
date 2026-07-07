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
        dto.setBrandName("Nike");
        dto.setDescription("Sports Brand");

        BrandDto response = brandService.save(dto);

        assertTrue(response.isSuccess());

        Brand saved = brandRepository.findByIdentifier("Nike");
        assertNotNull(saved);
        assertEquals("Nike", saved.getIdentifier());
        assertEquals("Nike", saved.getBrandName());
        assertEquals("Sports Brand", saved.getDescription());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {

        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setBrandName("Nike");
        brand.setDeleted(false);
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setBrandName("Nike");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Brand already exists", response.getMessage());
    }

    @Test
    void update_shouldUpdateDescription() {

        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setBrandName("Nike");
        brand.setDescription("Old Description");
        brand.setDeleted(false);
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("Nike");
        dto.setBrandName("Nike");
        dto.setDescription("Updated Description");

        BrandDto response = brandService.update(dto);

        assertTrue(response.isSuccess());

        Brand updated = brandRepository.findByIdentifier("Nike");
        assertEquals("Updated Description", updated.getDescription());
    }

    @Test
    void findByIdentifier_shouldReturnBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setBrandName("Nike");
        brandRepository.save(brand);

        BrandDto result = brandService.findByIdentifier("Nike");

        assertEquals("Nike", result.getIdentifier());
        assertEquals("Nike", result.getBrandName());
    }

    @Test
    void delete_shouldSoftDelete() {

        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setBrandName("Nike");
        brand.setDeleted(false);
        brandRepository.save(brand);

        brandService.delete("Nike");

        Brand deleted = brandRepository.findByIdentifier("Nike");
        assertTrue(deleted.getDeleted());
    }

    @Test
    void toggleStatus_shouldToggleBrandStatus() {

        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setBrandName("Nike");
        brand.setStatus(true);
        brand.setDeleted(false);
        brandRepository.save(brand);

        BrandDto response = brandService.toggleStatus("Nike");

        assertTrue(response.isSuccess());

        Brand updated = brandRepository.findByIdentifier("Nike");
        assertFalse(updated.getStatus());
    }

    @Test
    void findActiveBrands_shouldReturnOnlyActiveBrands() {

        Brand active = new Brand();
        active.setIdentifier("Nike");
        active.setBrandName("Nike");
        active.setStatus(true);
        active.setDeleted(false);

        Brand inactive = new Brand();
        inactive.setIdentifier("Puma");
        inactive.setBrandName("Puma");
        inactive.setStatus(false);
        inactive.setDeleted(false);

        brandRepository.save(active);
        brandRepository.save(inactive);

        assertEquals(1, brandService.findActiveBrands().size());
        assertEquals("Nike",brandService.findActiveBrands().get(0).getIdentifier()
        );
    }
}