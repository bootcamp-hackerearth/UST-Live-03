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

        Brand saved =
                brandRepository.findByIdentifier("BR001");

        assertNotNull(saved);
        assertEquals("BR001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenBrandAlreadyExists() {

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
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenBrandSoftDeleted() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(true);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertTrue(
                response.getMessage().contains("soft deleted")
        );
    }

    @Test
    void findByIdentifier_shouldReturnBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);

        brandRepository.save(brand);

        BrandDto result =
                brandService.findByIdentifier("BR001");

        assertNotNull(result);
        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> brandService.findByIdentifier("UNKNOWN")
        );
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

        brandService.update(dto);

        Brand updated =
                brandRepository.findByIdentifier("BR001");

        assertFalse(updated.isStatus());
    }

    @Test
    void update_shouldFailWhenBrandNotFound() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        BrandDto response = brandService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - BR001 already exists",
                response.getMessage()
        );
    }

    @Test
    void delete_shouldSoftDeleteBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);

        brandRepository.save(brand);

        brandService.delete("BR001");

        Brand deleted =
                brandRepository.findByIdentifier("BR001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void changeToggleStatus_shouldUpdateStatus() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);

        brandRepository.save(brand);

        brandService.changeToggleStatus(
                "BR001",
                false
        );

        Brand updated =
                brandRepository.findByIdentifier("BR001");

        assertFalse(updated.isStatus());
    }

    @Test
    void findActiveStatus_shouldReturnOnlyActiveBrands() {

        Brand active = new Brand();
        active.setIdentifier("BR001");
        active.setStatus(true);

        Brand inactive = new Brand();
        inactive.setIdentifier("BR002");
        inactive.setStatus(false);

        brandRepository.save(active);
        brandRepository.save(inactive);

        List<BrandDto> result =
                brandService.findActiveStatus();

        assertEquals(1, result.size());
        assertEquals("BR001",
                result.getFirst().getIdentifier());
    }
}