package com.ust.pos;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        dto.setIdentifier("BR001");
        dto.setStatus(true);

        brandService.save(dto);
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
        assertEquals("Brand with identifier - BR001 already exists", response.getMessage());
    }

    @Test
    void update_shouldUpdateStatus() {

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
        assertFalse(updated.isStatus());
    }

    @Test
    void update_shouldFailWhenBrandNotFound() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("INVALID");
        BrandDto response = brandService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals("Brand with identifier - INVALID not found", response.getMessage());
    }

    @Test
    void findByIdentifier_shouldReturnBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);
        brandRepository.save(brand);
        BrandDto result = brandService.findByIdentifier("BR001");
        assertNotNull(result);
        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void delete_shouldSoftDelete() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);
        brandRepository.save(brand);
        brandService.delete("BR001");
        Brand deleted = brandRepository.findByIdentifier("BR001");
        assertTrue(deleted.isDeleted());
    }

    @Test
    void toggleStatus_shouldUpdateStatus() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(false);

        brandRepository.save(brand);

        BrandDto response = brandService.toggleStatus("BR001", true);
        assertNotNull(response);
        assertTrue(response.isStatus());
        Brand updated = brandRepository.findByIdentifier("BR001");
        assertTrue(updated.isStatus());
    }

    @Test
    void findActiveBrands_shouldReturnOnlyActiveBrands() {

        Brand activeBrand = new Brand();
        activeBrand.setIdentifier("BR001");
        activeBrand.setStatus(true);
        Brand inactiveBrand = new Brand();
        inactiveBrand.setIdentifier("BR002");
        inactiveBrand.setStatus(false);
        brandRepository.save(activeBrand);
        brandRepository.save(inactiveBrand);
        List<BrandDto> activeBrands = brandService.findActiveBrands();
        assertEquals(1, activeBrands.size());
        assertEquals("BR001", activeBrands.get(0).getIdentifier());
    }
}