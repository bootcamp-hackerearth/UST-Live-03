package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BrandServiceImpl brandService;


    @Test
    void save() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND1");

        Mockito.when(brandRepository.findByIdentifier("BRAND1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(brandDto, Brand.class))
                .thenReturn(new Brand());

        BrandDto response = brandService.save(brandDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_failure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND1");

        Mockito.when(brandRepository.findByIdentifier("BRAND1"))
                .thenReturn(new Brand());

        BrandDto response = brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());
    }


    @Test
    void findByIdentifier() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND1");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND1");

        Mockito.when(brandRepository.findByIdentifier("BRAND1"))
                .thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(brandDto);

        BrandDto response = brandService.findByIdentifier("BRAND1");

        Assertions.assertEquals("BRAND1", response.getIdentifier());
    }


    @Test
    void update_success() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND1");

        Mockito.when(brandRepository.findByIdentifier("BRAND1"))
                .thenReturn(new Brand());

        BrandDto response = brandService.update(brandDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void update_failure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND1");

        Mockito.when(brandRepository.findByIdentifier("BRAND1"))
                .thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());
    }


    @Test
    void delete_success() {
        Assertions.assertDoesNotThrow(() ->
                brandService.delete("BRAND1"));

        Mockito.verify(brandRepository)
                .deleteByIdentifier("BRAND1");
    }


    @Test
    void findAllTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);

        Page<Brand> brandPage = new PageImpl<>(brands, PageRequest.of(0, 2), brands.size());

        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));

        Mockito.when(brandRepository.findAll(pageable)).thenReturn(brandPage);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(java.lang.reflect.Type.class))).thenReturn(brandDtos);

        List<BrandDto> response = brandService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void findByStatusTrue() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND1");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND1");

        Mockito.when(brandRepository.findByStatusTrue())
                .thenReturn(List.of(brand));

        Mockito.when(modelMapper.map(
                Mockito.any(Brand.class),
                Mockito.eq(BrandDto.class)
        )).thenReturn(brandDto);

        List<BrandDto> response = brandService.findIfTrue();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("BRAND1", response.get(0).getIdentifier());
    }


    @Test
    void toggle_activeToInactive() {
        Brand brand = new Brand();
        brand.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("BRAND1"))
                .thenReturn(brand);

        brandService.toggleStatus("BRAND1");

        Assertions.assertFalse(brand.isStatus());
    }

    @Test
    void toggle_inactiveToActive() {
        Brand brand = new Brand();
        brand.setStatus(false);

        Mockito.when(brandRepository.findByIdentifier("BRAND1"))
                .thenReturn(brand);

        brandService.toggleStatus("BRAND1");

        Assertions.assertTrue(brand.isStatus());
    }
}