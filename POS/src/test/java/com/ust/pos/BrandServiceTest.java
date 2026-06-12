package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PageDto;
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

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");
        brandDto.setSuccess(true);

        Mockito.when(brandRepository.findByIdentifier("BRAND001"))
                .thenReturn(null);

        Brand brand = new Brand();

        Mockito.when(modelMapper.map(brandDto, Brand.class))
                .thenReturn(brand);

        Mockito.when(brandRepository.save(brand))
                .thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("BRAND001", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");

        Mockito.when(brandRepository.findByIdentifier("BRAND001"))
                .thenReturn(new Brand());

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("BRAND001", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");

        Mockito.when(brandRepository.findByIdentifier("BRAND001"))
                .thenReturn(brand);

        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(brandDto);

        BrandDto response = brandService.findByIdentifier("BRAND001");

        Assertions.assertEquals("BRAND001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");
        brandDto.setSuccess(true);

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("BRAND001");

        Mockito.when(brandRepository.findByIdentifier("BRAND001"))
                .thenReturn(existingBrand);

        Mockito.when(brandRepository.save(existingBrand))
                .thenReturn(existingBrand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");

        Mockito.when(brandRepository.findByIdentifier("BRAND001"))
                .thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(brandRepository)
                .deleteByIdentifier("BRAND001");

        brandService.delete("BRAND001");

        Mockito.verify(brandRepository)
                .deleteByIdentifier("BRAND001");
    }

    @Test
    void toggleStatusTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brand.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("BRAND001"))
                .thenReturn(brand);

        Mockito.when(brandRepository.save(brand))
                .thenReturn(brand);

        brandService.toggleStatus("BRAND001");

        Assertions.assertFalse(brand.getStatus());

        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(brandRepository.findByIdentifier("BRAND001"))
                .thenReturn(null);

        brandService.toggleStatus("BRAND001");

        Mockito.verify(brandRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findActiveBrandsTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brand.setStatus(true);

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");

        List<Brand> brandList = List.of(brand);

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        Mockito.when(brandRepository.findByStatusTrue())
                .thenReturn(brandList);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(brandList),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(brandDto));

        List<BrandDto> response = brandService.findActiveBrands();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "BRAND001",
                response.get(0).getIdentifier()
        );
    }

    @Test
    void findAllPaginationTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Brand> brandPage =
                new PageImpl<>(List.of(brand), pageable, 1);

        Mockito.when(brandRepository.findAll(pageable))
                .thenReturn(brandPage);

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(brandPage.getContent()),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(brandDto));

        PageDto<BrandDto> response =
                brandService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(
                "BRAND001",
                response.getDtoList().get(0).getIdentifier()
        );
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}