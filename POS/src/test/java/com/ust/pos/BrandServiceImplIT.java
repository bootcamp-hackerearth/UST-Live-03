package com.ust.pos;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        dto.setIdentifier("BRD001");
        dto.setDescription("Test Description");
        dto.setStatus(true);

        Brand saved = brandRepository.findByIdentifier("BRD001");

        assertNotNull(saved);
        assertEquals("BRD001", saved.getIdentifier());
        assertEquals("Test Description", saved.getDescription());
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
    void save_shouldFailWhenPreviouslyDeleted() {

        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setDeleted(true);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRD001");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier BRD001 was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateBrandDetails() {

        Brand brand = new Brand();
        brand.setIdentifier("BRD001");
        brand.setDescription("Old Description");
        brand.setDeleted(false);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRD001");
        dto.setDescription("New Description");

        BrandDto response = brandService.update(dto);

        assertTrue(response.isSuccess());

        Brand updated = brandRepository.findByIdentifier("BRD001");

        assertEquals("New Description", updated.getDescription());
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
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {

        assertThrows(ResourceNotFoundException.class, () -> {
            brandService.findByIdentifier("NON-EXISTENT");
        });
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

        boolean isDeleted = brandService.delete("BRD001");

        assertTrue(isDeleted);

        Brand deleted = brandRepository.findByIdentifier("BRD001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {

        Brand brand1 = new Brand();
        brand1.setIdentifier("BRD001");
        brand1.setDeleted(false);
        brandRepository.save(brand1);

        Brand brand2 = new Brand();
        brand2.setIdentifier("BRD002");
        brand2.setDeleted(false);
        brandRepository.save(brand2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<BrandDto> response = brandService.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalRecords());
        assertEquals(2, response.getDtoList().size());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {

        Brand activeBrand = new Brand();
        activeBrand.setIdentifier("BRD001");
        activeBrand.setStatus(true);
        activeBrand.setDeleted(false);
        brandRepository.save(activeBrand);

        Brand inactiveBrand = new Brand();
        inactiveBrand.setIdentifier("BRD002");
        inactiveBrand.setStatus(false);
        inactiveBrand.setDeleted(false);
        brandRepository.save(inactiveBrand);

        List<BrandDto> activeList = brandService.findIfTrue();

        assertEquals(1, activeList.size());
        assertEquals("BRD001", activeList.get(0).getIdentifier());
    }
}