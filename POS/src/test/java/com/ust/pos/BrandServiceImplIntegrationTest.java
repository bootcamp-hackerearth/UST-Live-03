package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Brand;
import com.ust.pos.modell.BrandRepository;
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
    }

    private Brand createBrand(String identifier,
                              String description,
                              Boolean status,
                              Boolean deleted) {

        Brand brand = new Brand();
        brand.setIdentifier(identifier);
        brand.setDescription(description);
        brand.setStatus(status);
        brand.setDeleted(deleted);

        return brandRepository.save(brand);
    }

    @Test
    void save_ShouldCreateBrandSuccessfully() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setDescription("Nike");

        BrandDto result = brandService.save(dto);

        assertNotNull(result);

        Brand saved = brandRepository.findByIdentifier("BR001");

        assertNotNull(saved);
        assertEquals("BR001", saved.getIdentifier());
        assertEquals("Nike", saved.getDescription());
        assertTrue(saved.getStatus());
    }

    @Test
    void save_ShouldFail_WhenBrandAlreadyExists() {

        createBrand(
                "BR001",
                "Nike",
                true,
                false
        );

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        BrandDto result = brandService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Brand with identifier - BR001 already exists",
                result.getMessage()
        );
    }

    @Test
    void save_ShouldFail_WhenDeletedBrandExists() {

        createBrand(
                "BR001",
                "Nike",
                true,
                true
        );

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        BrandDto result = brandService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Brand with Identifier BR001 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void update_ShouldUpdateBrand() {

        createBrand(
                "BR001",
                "Nike",
                true,
                false
        );

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setDescription("Adidas");

        BrandDto result = brandService.update(dto);

        Brand updated =
                brandRepository.findByIdentifierAndDeletedFalse("BR001");

        assertNotNull(result);
        assertEquals("Adidas", updated.getDescription());
    }

    @Test
    void update_ShouldReturnNotFound_WhenBrandDoesNotExist() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("INVALID");
        dto.setDescription("Test");

        BrandDto result = brandService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Brand with identifier - INVALID not found",
                result.getMessage()
        );
    }

    @Test
    void findByIdentifier_ShouldReturnBrand() {

        createBrand(
                "BR001",
                "Nike",
                true,
                false
        );

        BrandDto result =
                brandService.findByIdentifier("BR001");

        assertNotNull(result);
        assertEquals("BR001", result.getIdentifier());
        assertEquals("Nike", result.getDescription());
    }

    @Test
    void delete_ShouldSoftDeleteBrand() {

        createBrand(
                "BR001",
                "Nike",
                true,
                false
        );

        brandService.delete("BR001");

        Brand brand =
                brandRepository.findByIdentifier("BR001");

        assertTrue(brand.getDeleted());
    }

    @Test
    void toggleStatus_ShouldDisableBrand() {

        createBrand(
                "BR001",
                "Nike",
                true,
                false
        );

        BrandDto result =
                brandService.toggleStatus("BR001");

        assertFalse(result.getStatus());

        Brand updated =
                brandRepository.findByIdentifier("BR001");

        assertFalse(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldEnableBrand() {

        createBrand(
                "BR001",
                "Nike",
                false,
                false
        );

        BrandDto result =
                brandService.toggleStatus("BR001");

        assertTrue(result.getStatus());

        Brand updated =
                brandRepository.findByIdentifier("BR001");

        assertTrue(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldThrowException_WhenBrandNotFound() {

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> brandService.toggleStatus("INVALID")
                );

        assertEquals(
                "brand not found with identifier: INVALID",
                ex.getMessage()
        );
    }

    @Test
    void findAll_ShouldReturnOnlyNonDeletedBrands() {

        createBrand(
                "BR001",
                "Nike",
                true,
                false
        );

        createBrand(
                "BR002",
                "Adidas",
                true,
                false
        );

        WsDto<BrandDto> result =
                brandService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findAllActive_ShouldReturnOnlyActiveBrands() {

        createBrand(
                "BR001",
                "Nike",
                true,
                false
        );

        createBrand(
                "BR002",
                "Adidas",
                false,
                false
        );

        List<BrandDto> result =
                brandService.findAllActive();

        assertEquals(1, result.size());
        assertEquals("BR001", result.get(0).getIdentifier());
    }
}