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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BrandServiceIT {

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

        assertNotNull(saved);
        assertEquals("BR001", saved.getIdentifier());
        assertNotNull(response);
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
                " Brand with identifier - BR001 already exists.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);

        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setDescription("Updated Brand");

        BrandDto response = brandService.update(dto);

        Brand updated =
                brandRepository.findByIdentifier("BR001");

        assertNotNull(response);
        assertEquals(
                "Updated Brand",
                updated.getDescription()
        );
    }

    @Test
    void findByIdentifier_shouldReturnBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        brandRepository.save(brand);

        BrandDto result =
                brandService.findByIdentifier("BR001");

        assertEquals("BR001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> brandService.findByIdentifier("BR001")
                );

        assertEquals(
                "Brand with identifier 'BR001' not found",
                exception.getMessage()
        );
    }

    @Test
    void toggleStatus_shouldToggleValue() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);

        brandRepository.save(brand);

        brandService.toggleStatus("BR001");

        Brand updated =
                brandRepository.findByIdentifier("BR001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {

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
    void findAll_shouldReturnPagedBrands() {

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
        assertEquals(2, result.getContent().size());
    }

    @Test
    void findAllWithSpecification_shouldReturnFilteredBrands() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        brandRepository.save(brand);

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Brand> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "BR001");

        WsDto<BrandDto> result =
                brandService.findAll(
                        specification,
                        pageable,
                        "BR001"
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getContent().size());
        assertEquals(
                "BR001",
                result.getContent().get(0).getIdentifier()
        );
        assertEquals("BR001", result.getKeyword());
    }
}