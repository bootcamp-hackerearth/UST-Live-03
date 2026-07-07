package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
    void saveSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Brand.class))
                .thenReturn(new Brand());

        BrandDto response = brandService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Successfully added the brand", response.getMessage());
    }

    @Test
    void saveFailureTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(new Brand());

        IllegalArgumentException exception =
                Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> brandService.save(dto)
                );

        Assertions.assertEquals(
                "Brand BR001 already exists",
                exception.getMessage()
        );
    }

    @Test
    void saveSoftDeletedBrandTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Brand brand = new Brand();
        brand.setDeleted(true);

        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> brandService.save(dto)
        );
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        BrandDto response = brandService.findByIdentifier("BR001");

        Assertions.assertEquals("BR001", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(null);

        ResourceNotFoundException exception =
                Assertions.assertThrows(
                        ResourceNotFoundException.class,
                        () -> brandService.findByIdentifier("BR001")
                );

        Assertions.assertEquals(
                "Brand does not exist",
                exception.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, brand);

        Mockito.when(brandRepository.save(brand))
                .thenReturn(brand);

        BrandDto response = brandService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Successfully updated the brand", response.getMessage());

        Mockito.verify(modelMapper).map(dto, brand);
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void updateFailureTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(null);

        ResourceNotFoundException exception =
                Assertions.assertThrows(
                        ResourceNotFoundException.class,
                        () -> brandService.update(dto)
                );

        Assertions.assertEquals(
                "Brand not found",
                exception.getMessage()
        );
    }

    @Test
    void updateStatusSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(false);

        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        BrandDto response = brandService.updateStatus("BR001", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
        Assertions.assertTrue(brand.isStatus());
    }

    @Test
    void updateSoftDeletedBrandTest() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(true);

        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(brand);

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> brandService.update(dto)
        );
    }

    @Test
    void updateStatusFailureTest() {

        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(null);

        ResourceNotFoundException exception =
                Assertions.assertThrows(
                        ResourceNotFoundException.class,
                        () -> brandService.updateStatus("BR001", true)
                );

        Assertions.assertEquals(
                "Brand not found",
                exception.getMessage()
        );
    }

    @Test
    void updateStatusVerifySaveTest() {

        Brand brand = new Brand();
        brand.setStatus(false);


        Mockito.when(
                brandRepository.findByIdentifier("BR001")
        ).thenReturn(brand);


        BrandDto response =
                brandService.updateStatus(
                        "BR001",
                        true
                );


        Assertions.assertTrue(response.isSuccess());
        Assertions.assertTrue(brand.isStatus());
    }

    @Test
    void findAllWithPageableTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Brand> brandPage =
                new PageImpl<>(brands, pageable, brands.size());

        Mockito.when(
                brandRepository.findByIsDeletedFalse(pageable)
        ).thenReturn(brandPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(brands),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        PaginationResponseDto<BrandDto> response =
                brandService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(
                "BR001",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void findByStatusTrueTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setStatus(true);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);

        Mockito.when(brandRepository.findByStatusTrue())
                .thenReturn(brands);

        Mockito.when(modelMapper.map(
                Mockito.eq(brands),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        List<BrandDto> response = brandService.findByStatusTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0,5);

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        Page<Brand> page =
                new PageImpl<>(
                        List.of(brand),
                        pageable,
                        1
                );

        Specification<Brand> specification =
                Mockito.mock(Specification.class);

        Mockito.when(
                brandRepository.findAll(
                        Mockito.eq(specification),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(List.of(brand)),
                        Mockito.any(Type.class)
                )
        ).thenReturn(List.of(dto));

        PaginationResponseDto<BrandDto> response =
                brandService.findAll(
                        specification,
                        pageable
                );

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void deleteTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(false);

        Mockito.when(
                brandRepository.findByIdentifier("BR001")
        ).thenReturn(brand);

        Mockito.when(
                brandRepository.save(brand)
        ).thenReturn(brand);

        brandService.delete("BR001");

        Assertions.assertTrue(brand.isDeleted());

        Mockito.verify(brandRepository)
                .findByIdentifier("BR001");

        Mockito.verify(brandRepository)
                .save(brand);
    }

    @Test
    void deleteBrandNotFoundTest() {

        Mockito.when(
                brandRepository.findByIdentifier("BR001")
        ).thenReturn(null);


        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> brandService.delete("BR001")
        );


        Mockito.verify(
                brandRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void deleteAlreadyDeletedBrandTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");
        brand.setDeleted(true);


        Mockito.when(
                brandRepository.findByIdentifier("BR001")
        ).thenReturn(brand);


        Assertions.assertThrows(
                IllegalStateException.class,
                () -> brandService.delete("BR001")
        );
    }
}