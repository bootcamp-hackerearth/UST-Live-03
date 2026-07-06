package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
        brandDto.setIdentifier("Nike");

        Brand brand = new Brand();

        Mockito.when(
                brandRepository.findByIdentifier("Nike")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(brandDto, Brand.class)
        ).thenReturn(brand);

        Mockito.when(
                brandRepository.save(brand)
        ).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Brand created successfully",
                response.getMessage()
        );

        Mockito.verify(brandRepository)
                .save(brand);
    }

    @Test
    void saveTestFailureExistingBrand() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand existingBrand = new Brand();
        existingBrand.setDeleted(false);

        Mockito.when(
                brandRepository.findByIdentifier("Nike")
        ).thenReturn(existingBrand);

        BrandDto response =
                brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("already exists")
        );
    }

    @Test
    void saveTestFailureSoftDeletedBrand() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand deletedBrand = new Brand();
        deletedBrand.setDeleted(true);

        Mockito.when(
                brandRepository.findByIdentifier("Nike")
        ).thenReturn(deletedBrand);

        BrandDto response =
                brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("soft deleted")
        );
    }

    @Test
    void findByIdentifierTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(
                brandRepository.findByIdentifier("Nike")
        ).thenReturn(brand);

        Mockito.when(
                modelMapper.map(
                        brand,
                        BrandDto.class
                )
        ).thenReturn(brandDto);

        BrandDto response =
                brandService.findByIdentifier("Nike");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "Nike",
                response.getIdentifier()
        );
    }

    @Test
    void deleteByIdentifierTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        Mockito.when(
                brandRepository.findByIdentifier("Nike")
        ).thenReturn(brand);

        brandService.deleteByIdentifier("Nike");

        Mockito.verify(brandRepository)
                .findByIdentifier("Nike");

        Mockito.verify(brandRepository)
                .save(brand);
    }

    @Test
    void findAllWithPageableTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<Brand> brandPage =
                new PageImpl<>(
                        brands,
                        pageable,
                        1
                );

        Mockito.when(
                brandRepository.findByDeletedFalse(pageable)
        ).thenReturn(brandPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(brands),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<BrandDto> response =
                brandService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "BR001",
                response.getDtoList()
                        .get(0)
                        .getIdentifier()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        List<BrandDto> dtos =
                List.of(dto);

        Page<Brand> brandPage =
                new PageImpl<>(
                        List.of(brand)
                );

        Mockito.when(
                brandRepository.findByDeletedFalse(null)
        ).thenReturn(brandPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(brandPage),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<BrandDto> response =
                brandService.findAll(null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );
    }

    @Test
    void updateTest() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Nike");

        Mockito.when(
                brandRepository.findByIdentifier("Nike")
        ).thenReturn(existingBrand);

        Mockito.when(
                brandRepository.save(existingBrand)
        ).thenReturn(existingBrand);

        BrandDto response =
                brandService.update(brandDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Brand updated successfully",
                response.getMessage()
        );

        Mockito.verify(brandRepository)
                .save(existingBrand);
    }

    @Test
    void updateTestFailure() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(
                brandRepository.findByIdentifier("Nike")
        ).thenReturn(null);

        BrandDto response =
                brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("not found")
        );
    }

    @Test
    void toggleStatusSuccessTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(false);

        BrandDto mappedDto =
                new BrandDto();

        mappedDto.setIdentifier("Admin");
        mappedDto.setStatus(true);

        Mockito.when(
                brandRepository.findByIdentifier("Admin")
        ).thenReturn(brand);

        Mockito.when(
                modelMapper.map(
                        brand,
                        BrandDto.class
                )
        ).thenReturn(mappedDto);

        BrandDto response =
                brandService.toggleStatus(
                        "Admin",
                        true
                );

        Assertions.assertTrue(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Status updated successfully",
                response.getMessage()
        );

        Mockito.verify(brandRepository)
                .save(brand);
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(
                brandRepository.findByIdentifier("Admin")
        ).thenReturn(null);

        BrandDto response =
                brandService.toggleStatus(
                        "Admin",
                        true
                );

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Brand not found",
                response.getMessage()
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Brand> page = new PageImpl<>(
                brands,
                pageable,
                1
        );

        Specification<Brand> specification = Mockito.mock(Specification.class);

        Mockito.when(
                brandRepository.findAll(specification, pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(brands),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<BrandDto> response =
                brandService.findAll(specification, pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("BR001",
                response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(0, response.getPage());
        Assertions.assertEquals(5, response.getSizePerPage());

        Mockito.verify(brandRepository)
                .findAll(specification, pageable);
    }
}