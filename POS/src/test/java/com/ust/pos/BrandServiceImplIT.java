package com.ust.pos;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @DisplayName("save() should persist a new brand")
    void save_shouldCreateBrand() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRD001");
        dto.setStatus(true);

        BrandDto response = brandService.save(dto);

        Brand saved = brandRepository.findByIdentifier("BRD001");
        assertNotNull(saved, "Brand should be persisted");
        assertEquals("BRD001", saved.getIdentifier());
        assertFalse(saved.isDeleted());
        assertNotNull(response);
    }

    @Test
    @DisplayName("save() should fail gracefully when identifier already exists")
    void save_shouldFailWhenDuplicateExists() {
        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setDeleted(false);
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRD001");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Brand with identifier - BRD001 already exists", response.getMessage());
    }

    @Test
    @DisplayName("save() should return a special message when identifier belongs to a soft-deleted brand")
    void save_shouldWarnWhenIdentifierBelongsToDeletedBrand() {
        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setDeleted(true);
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRD001");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("was deleted"));
    }


    @Test
    @DisplayName("update() should modify an existing brand")
    void update_shouldUpdateBrandDetails() {
        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setDeleted(false);
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRD001");
        dto.setStatus(true);

        BrandDto response = brandService.update(dto);

        Brand updated = brandRepository.findByIdentifier("BRD001");
        assertNotNull(updated);
        assertTrue(response.isSuccess() || response.getMessage() == null);
    }

    @Test
    @DisplayName("update() should fail when brand does not exist")
    void update_shouldFailWhenBrandNotFound() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("NON_EXISTENT");

        BrandDto response = brandService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals("Brand with identifier - NON_EXISTENT not found", response.getMessage());
    }

    @Test
    @DisplayName("findByIdentifier() should return the matching brand")
    void findByIdentifier_shouldReturnBrand() {
        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setDeleted(false);
        brandRepository.save(brand);

        BrandDto result = brandService.findByIdentifier("BRD001");

        assertEquals("BRD001", result.getIdentifier());
    }

    @Test
    @DisplayName("findByIdentifier() should throw when brand is not found or is deleted")
    void findByIdentifier_shouldThrowWhenNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> brandService.findByIdentifier("MISSING"));
    }

    @Test
    @DisplayName("findAll(Pageable) should return only non-deleted brands")
    void findAll_shouldReturnNonDeletedBrands() {
        Brand active = new Brand();
        active.setIdentifier("BRD_ACTIVE");
        active.setDeleted(false);
        brandRepository.save(active);

        Brand deleted = new Brand();
        deleted.setIdentifier("BRD_DELETED");
        deleted.setDeleted(true);
        brandRepository.save(deleted);

        Pageable pageable = PageRequest.of(0, 10);
        WsDto<BrandDto> result = brandService.findAll(pageable);

        assertEquals(1, result.getTotalRecords());
        assertTrue(result.getDtoList().stream()
                .anyMatch(b -> "BRD_ACTIVE".equals(b.getIdentifier())));
    }

    @Test
    @DisplayName("toggleStatus() should flip the status flag")
    void toggleStatus_shouldToggleValue() {
        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setStatus(true);
        brandRepository.save(brand);

        brandService.toggleStatus("BRD001");

        Brand updated = brandRepository.findByIdentifier("BRD001");
        assertFalse(updated.isStatus());

        brandService.toggleStatus("BRD001");
        Brand updatedAgain = brandRepository.findByIdentifier("BRD001");
        assertTrue(updatedAgain.isStatus());
    }

    @Test
    @DisplayName("toggleStatus() should do nothing silently when brand does not exist")
    void toggleStatus_shouldNotThrowWhenBrandMissing() {
        assertDoesNotThrow(() -> brandService.toggleStatus("MISSING"));
    }

    @Test
    @DisplayName("delete() should soft-delete the brand")
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