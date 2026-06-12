package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationResponseDto;
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

        BrandDto response = brandService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand BR001 already exists", response.getMessage());
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

        BrandDto response = brandService.update(dto);

        Assertions.assertFalse(response.isSuccess());
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
    void updateStatusFailureTest() {
        Mockito.when(brandRepository.findByIdentifier("BR001"))
                .thenReturn(null);

        BrandDto response = brandService.updateStatus("BR001", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand not found", response.getMessage());
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
        Page<Brand> brandPage = new PageImpl<>(brands);

        Mockito.when(brandRepository.findAll(pageable))
                .thenReturn(brandPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(brands),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        PaginationResponseDto<BrandDto> response =
                brandService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "BR001",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);

        Mockito.when(brandRepository.findAll())
                .thenReturn(brands);

        Mockito.when(modelMapper.map(
                Mockito.eq(brands),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        PaginationResponseDto<BrandDto> response =
                brandService.findAll(null);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

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
    void deleteTest() {
        Mockito.doNothing()
                .when(brandRepository)
                .deleteByIdentifier("BR001");

        brandService.delete("BR001");

        Mockito.verify(brandRepository)
                .deleteByIdentifier("BR001");
    }
}