package com.ust.pos;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
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
    void save_shouldCreateBrand() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setStatus(true);

        BrandDto response = brandService.save(dto);

        Brand saved =
                brandRepository.findByIdentifier("BR001");

        assertTrue(response.isSuccess());
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
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenBrandIsSoftDeleted() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(true);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier BR001 has been soft deleted. (Rollback by changing status)",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setStatus(false);

        BrandDto response = brandService.update(dto);

        assertTrue(response.isSuccess());

        Brand updated =
                brandRepository.findByIdentifier("BR001");

        assertFalse(updated.isStatus());
    }

    @Test
    void update_shouldFailWhenBrandNotFound() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR999");

        BrandDto response = brandService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - BR999 not found",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_shouldReturnBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        brandRepository.save(brand);

        BrandDto result =
                brandService.findByIdentifier("BR001");

        assertNotNull(result);
        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void deleteByIdentifier_shouldSoftDelete() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);

        brandRepository.save(brand);

        brandService.deleteByIdentifier("BR001");

        Brand deleted =
                brandRepository.findByIdentifier("BR001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void toggleStatus_shouldUpdateStatus() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);

        brandRepository.save(brand);

        BrandDto response =
                brandService.toggleStatus("BR001", false);

        assertTrue(response.isSuccess());

        Brand updated =
                brandRepository.findByIdentifier("BR001");

        assertFalse(updated.isStatus());
        assertEquals(
                "Status updated successfully",
                response.getMessage()
        );
    }

    @Test
    void toggleStatus_shouldFailWhenBrandNotFound() {

        BrandDto response =
                brandService.toggleStatus("BR999", false);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand not found",
                response.getMessage()
        );
    }

    @Test
    void findAll_shouldReturnBrands() {

        Brand brand1 = new Brand();
        brand1.setIdentifier("BR001");
        brand1.setDeleted(false);

        Brand brand2 = new Brand();
        brand2.setIdentifier("BR002");
        brand2.setDeleted(false);

        brandRepository.save(brand1);
        brandRepository.save(brand2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<BrandDto> result =
                brandService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }
}