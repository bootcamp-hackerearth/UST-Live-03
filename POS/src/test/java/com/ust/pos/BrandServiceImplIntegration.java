package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BrandServiceImplIntegration {

    @Autowired
    private BrandServiceImpl brandService;

    @Autowired
    private BrandRepository brandRepository;

    @BeforeEach
    void setup() {
        brandRepository.deleteAll();
    }

    @Test
    void shouldSaveBrandSuccessfully() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setDescription("Nike");

        BrandDto saved = brandService.save(dto);

        Brand brand = brandRepository.findByIdentifier("BR001");

        assertThat(saved.isSuccess()).isNotEqualTo(false);
        assertThat(brand).isNotNull();
        assertThat(brand.getDescription()).isEqualTo("Nike");
    }

    @Test
    void shouldNotSaveWhenBrandAlreadyExists() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDescription("Nike");
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setDescription("Duplicate Nike");

        BrandDto response = brandService.save(dto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage())
                .contains("Brand with identifier - BR001 already exists");
    }

    @Test
    void shouldFindBrandByIdentifier() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDescription("Nike");
        brand.setDeleted(false);

        brandRepository.save(brand);

        BrandDto dto = brandService.findByIdentifier("BR001");

        assertThat(dto).isNotNull();
        assertThat(dto.getIdentifier()).isEqualTo("BR001");
        assertThat(dto.getDescription()).isEqualTo("Nike");
    }

    @Test
    void shouldUpdateBrandSuccessfully() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDescription("Nike");
        brandRepository.save(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");
        dto.setDescription("Nike Updated");

        BrandDto response = brandService.update(dto);

        Brand updated = brandRepository.findByIdentifier("BR001");

        assertThat(response.isSuccess()).isNotEqualTo(false);
        assertThat(updated.getDescription()).isEqualTo("Nike Updated");
    }

    @Test
    void shouldReturnErrorWhenUpdatingNonExistingBrand() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("UNKNOWN");

        BrandDto response = brandService.update(dto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage())
                .contains("Brand with identifier - UNKNOWN not found");
    }

    @Test
    void shouldSoftDeleteBrand() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);
        brandRepository.save(brand);

        brandService.delete("BR001");

        Brand deletedBrand = brandRepository.findByIdentifier("BR001");

        assertThat(deletedBrand.getDeleted()).isTrue();
    }

    @Test
    void shouldToggleBrandStatus() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);
        brandRepository.save(brand);

        BrandDto response = brandService.toggleStatus("BR001");

        assertThat(response.isStatus()).isFalse();

        Brand updated = brandRepository.findByIdentifier("BR001");
        assertThat(updated.isStatus()).isFalse();
    }

    @Test
    void shouldReturnActiveBrandsOnly() {

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

        List<BrandDto> brands = brandService.findActiveBrands();

        assertThat(brands).hasSize(1);
        assertThat(brands.get(0).getIdentifier()).isEqualTo("BR001");
    }

    @Test
    void shouldReturnPagedBrands() {

        for (int i = 1; i <= 5; i++) {
            Brand brand = new Brand();
            brand.setIdentifier("BR00" + i);
            brand.setDeleted(false);
            brandRepository.save(brand);
        }

        WsDto<BrandDto> result =
                brandService.findAll(PageRequest.of(0, 2));

        assertThat(result.getDtoList()).hasSize(2);
        assertThat(result.getTotalRecords()).isEqualTo(5);
    }
}