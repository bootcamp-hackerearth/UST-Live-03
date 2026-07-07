package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
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
class BrandServiceImplIT {

    @Autowired
    private BrandServiceImpl brandService;

    @Autowired
    private BrandRepository brandRepository;

    @BeforeEach
    void setup() {
        brandRepository.deleteAll();
    }

    @Test
    void save_ShouldCreateBrand() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setStatus(true);

        BrandDto response = brandService.save(dto);

        assertTrue(response.isSuccess());

        Brand saved = brandRepository.findByIdentifier("BR001");
        assertNotNull(saved);
        assertEquals("BR001", saved.getIdentifier());
    }

    @Test
    void save_ShouldFail_WhenBrandAlreadyExists() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier 'BR001' already exists",
                response.getMessage()
        );
    }

    @Test
    void save_ShouldFail_WhenDeletedBrandExists() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(true);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        BrandDto response = brandService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_ShouldReturnBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        brandRepository.save(brand);

        BrandDto result =
                brandService.findByIdentifier("BR001");

        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_ShouldThrowException_WhenNotFound() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> brandService.findByIdentifier("UNKNOWN")
        );
    }

    @Test
    void update_ShouldUpdateBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);
        brand.setStatus(true);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setStatus(false);

        BrandDto response = brandService.update(dto);

        assertTrue(response.getIdentifier().equals("BR001"));

        Brand updated =
                brandRepository.findByIdentifier("BR001");

        assertFalse(updated.getStatus());
    }

    @Test
    void update_ShouldReturnFailure_WhenBrandNotFound() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("UNKNOWN");

        BrandDto response = brandService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void delete_ShouldSoftDeleteBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);

        brandRepository.save(brand);

        brandService.delete("BR001");

        Brand deleted =
                brandRepository.findByIdentifier("BR001");

        assertTrue(deleted.getDeleted());
    }

    @Test
    void toggleStatus_ShouldChangeStatus() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);

        brandRepository.save(brand);

        BrandDto response =
                brandService.toggleStatus("BR001");

        assertFalse(response.isStatus());

        Brand updated =
                brandRepository.findByIdentifier("BR001");

        assertFalse(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldReturnFailure_WhenBrandNotFound() {

        BrandDto response =
                brandService.toggleStatus("UNKNOWN");

        assertFalse(response.isSuccess());
        assertEquals(
                "Brand with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void findIfTrue_ShouldReturnOnlyActiveBrands() {

        Brand active = new Brand();
        active.setIdentifier("BR001");
        active.setStatus(true);
        active.setDeleted(false);

        Brand inactive = new Brand();
        inactive.setIdentifier("BR002");
        inactive.setStatus(false);
        inactive.setDeleted(false);

        brandRepository.save(active);
        brandRepository.save(inactive);

        List<BrandDto> result = brandService.findIfTrue();

        assertEquals(1, result.size());
        assertEquals("BR001", result.get(0).getIdentifier());
    }

    @Test
    void findAll_ShouldReturnPagedResults() {

        Brand brand1 = new Brand();
        brand1.setIdentifier("BR001");
        brand1.setDeleted(false);

        Brand brand2 = new Brand();
        brand2.setIdentifier("BR002");
        brand2.setDeleted(false);

        brandRepository.save(brand1);
        brandRepository.save(brand2);

        WsDto<BrandDto> result =
                brandService.findAll(PageRequest.of(0, 10));

        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }
}