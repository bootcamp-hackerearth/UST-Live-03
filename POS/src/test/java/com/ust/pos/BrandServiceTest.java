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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
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
    void findAll_success() {

        Brand brand = new Brand();
        BrandDto dto = new BrandDto();

        Page<Brand> page = new PageImpl<>(List.of(brand));

        Mockito.when(brandRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(brand)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<BrandDto> result = brandService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        BrandDto input = new BrandDto();
        input.setIdentifier("BR01");

        Mockito.when(brandRepository.findByIdentifier("BR01"))
                .thenReturn(null);

        Brand entity = new Brand();
        Mockito.when(modelMapper.map(input, Brand.class))
                .thenReturn(entity);

        Mockito.when(brandRepository.save(entity))
                .thenReturn(entity);

        BrandDto result = brandService.save(input);

        Assertions.assertEquals("BR01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        BrandDto input = new BrandDto();
        input.setIdentifier("BR01");

        Mockito.when(brandRepository.findByIdentifier("BR01"))
                .thenReturn(new Brand());

        BrandDto result = brandService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Brand brand = new Brand();
        brand.setIdentifier("BR01");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR01");

        Mockito.when(brandRepository.findByIdentifier("BR01"))
                .thenReturn(brand);

        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        BrandDto result = brandService.findByIdentifier("BR01");

        Assertions.assertEquals("BR01", result.getIdentifier());
    }

    @Test
    void update_success() {

        BrandDto input = new BrandDto();
        input.setIdentifier("BR01");

        Brand existing = new Brand();

        Mockito.when(brandRepository.findByIdentifier("BR01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(brandRepository.save(existing))
                .thenReturn(existing);

        BrandDto result = brandService.update(input);

        Assertions.assertEquals("BR01", result.getIdentifier());
    }

    @Test
    void update_failure_notFound() {

        BrandDto input = new BrandDto();
        input.setIdentifier("BR01");

        Mockito.when(brandRepository.findByIdentifier("BR01"))
                .thenReturn(null);

        BrandDto result = brandService.update(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(brandRepository).deleteByIdentifier("BR01");

        brandService.delete("BR01");

        Mockito.verify(brandRepository)
                .deleteByIdentifier("BR01");
    }

    @Test
    void changeToggleStatus_success() {

        Brand brand = new Brand();
        brand.setStatus(false);

        BrandDto dto = new BrandDto();

        Mockito.when(brandRepository.findByIdentifier("BR01"))
                .thenReturn(brand);

        Mockito.when(brandRepository.save(brand))
                .thenReturn(brand);

        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        BrandDto result = brandService.changeToggleStatus("BR01", true);

        Assertions.assertTrue(brand.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void findActiveStatus_success() {

        Brand active = new Brand();
        active.setStatus(true);

        Brand inactive = new Brand();
        inactive.setStatus(false);

        Mockito.when(brandRepository.findAll())
                .thenReturn(List.of(active, inactive));

        BrandDto dto = new BrandDto();

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<BrandDto> result = brandService.findActiveStatus();

        Assertions.assertEquals(1, result.size());
    }
}