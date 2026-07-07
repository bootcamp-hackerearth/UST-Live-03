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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest_Success() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Brand brand = new Brand();

        when(
                brandRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(null);

        when(
                modelMapper.map(brandDto, Brand.class)
        ).thenReturn(brand);

        when(
                brandRepository.save(brand)
        ).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertNotNull(response);

        verify(modelMapper)
                .map(brandDto, Brand.class);

        verify(brandRepository)
                .save(brand);
    }

    @Test
    void saveTest_Failure_WhenBrandExists() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Brand existingBrand = new Brand();

        when(
                brandRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(existingBrand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        verify(
                brandRepository,
                never()
        ).save(any());
    }

    @Test
    void updateTest_Success() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        brandDto.setDescription("Updated Description");
        brandDto.setStatus(true);

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Admin");

        when(
                brandRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(existingBrand);

        when(
                brandRepository.save(existingBrand)
        ).thenReturn(existingBrand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                "Updated Description",
                existingBrand.getDescription()
        );

        Assertions.assertTrue(existingBrand.isStatus());

        verify(brandRepository)
                .save(existingBrand);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        when(
                brandRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        verify(
                brandRepository,
                never()
        ).save(any());
    }

    @Test
    void findByIdentifierTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        when(
                brandRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(brand);

        when(
                modelMapper.map(brand, BrandDto.class)
        ).thenReturn(brandDto);

        BrandDto response =
                brandService.findByIdentifier("Admin");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                "Admin",
                response.getIdentifier()
        );
    }

    @Test
    void findByIdentifier_WhenNotFound() {

        when(
                brandRepository.findByIdentifierAndDeletedFalse("XYZ")
        ).thenReturn(null);

        when(
                modelMapper.map(null, BrandDto.class)
        ).thenReturn(null);

        BrandDto response =
                brandService.findByIdentifier("XYZ");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        List<Brand> brands =
                List.of(new Brand());

        List<BrandDto> brandDtos =
                List.of(new BrandDto());

        Type listType =
                new TypeToken<List<BrandDto>>() {
                }.getType();

        when(
                brandRepository.findByDeletedFalse()
        ).thenReturn(brands);

        when(
                modelMapper.map(brands, listType)
        ).thenReturn(brandDtos);

        List<BrandDto> response =
                brandService.findAll();

        Assertions.assertEquals(
                1,
                response.size()
        );
    }

    @Test
    void findAll_EmptyList() {

        Type listType =
                new TypeToken<List<BrandDto>>() {
                }.getType();

        when(
                brandRepository.findByDeletedFalse()
        ).thenReturn(List.of());

        when(
                modelMapper.map(List.of(), listType)
        ).thenReturn(List.of());

        List<BrandDto> response =
                brandService.findAll();

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void findAll_WithPagination_NoSearch() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Brand brand = new Brand();
        brand.setIdentifier("Admin");

        Page<Brand> brandPage =
                new PageImpl<>(List.of(brand));

        when(
                brandRepository.findByDeletedFalse(pageable)
        ).thenReturn(brandPage);

        when(
                modelMapper.map(any(Brand.class), eq(BrandDto.class))
        ).thenReturn(new BrandDto());

        Page<BrandDto> response =
                brandService.findAll(pageable, "");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAll_WithPagination_Search() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Brand brand = new Brand();
        brand.setIdentifier("Admin");

        Page<Brand> brandPage =
                new PageImpl<>(List.of(brand));

        Mockito.when(brandRepository.findAll(
                        Mockito.<Specification<Brand>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(brandPage);

        when(
                modelMapper.map(any(Brand.class), eq(BrandDto.class))
        ).thenReturn(new BrandDto());

        Page<BrandDto> response =
                brandService.findAll(pageable, "Admin");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void toggleStatusTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(true);
        when(
                brandRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(brand);
        brandService.toggleStatus("Admin");
        Assertions.assertFalse(
                brand.isStatus()
        );
        verify(brandRepository)
                .save(brand);
    }

    @Test
    void toggleStatus_WhenBrandNotFound() {
        when(
                brandRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(null);
        brandService.toggleStatus("Admin");
        verify(
                brandRepository,
                never()
        ).save(any());
    }

    @Test
    void deleteTest() {
        Brand brand = new Brand();
        brand.setDeleted(false);
        when(
                brandRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(brand);
        brandService.delete("Admin");
        Assertions.assertTrue(
                brand.isDeleted()
        );
        verify(brandRepository)
                .save(brand);
    }

    @Test
    void delete_WhenBrandNotFound() {
        when(
                brandRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(null);
        brandService.delete("Admin");
        verify(
                brandRepository,
                never()
        ).save(any());
    }
}