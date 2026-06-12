package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginatedResponseDto;
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
        brandDto.setIdentifier("Admin");

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(null);
        Brand brand = new Brand();
        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");
        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());

    }

    @Test
    void findByIdentifierTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto response = brandService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Admin");

        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(existingBrand);
        Mockito.when(brandRepository.save(existingBrand))
                .thenReturn(existingBrand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(brandRepository)
                .deleteByIdentifier("Admin");

        brandService.delete("Admin");

        Mockito.verify(brandRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);

        Page<Brand> brandPage = new PageImpl<>(brands);

        Mockito.when(brandRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(brandPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(brands),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(brandDtos);

        PaginatedResponseDto<BrandDto> response = brandService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
    }

    @Test
    void findAllActiveTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(true);

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Admin");

        List<Brand> brands = List.of(brand);
        List<BrandDto> brandDtos = List.of(brandDto);

        Mockito.when(brandRepository.findByStatus(true)).thenReturn(brands);
        Mockito.when(modelMapper.map(
                Mockito.eq(brands),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(brandDtos);

        List<BrandDto> response = brandService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(false);

        Mockito.when(brandRepository.findByIdentifier("Admin"))
                .thenReturn(brand);

        Mockito.when(brandRepository.save(brand))
                .thenReturn(brand);

        brandService.changeStatus("Admin", true);

        Assertions.assertTrue(brand.getStatus());

        Mockito.verify(brandRepository).save(brand);
    }
}