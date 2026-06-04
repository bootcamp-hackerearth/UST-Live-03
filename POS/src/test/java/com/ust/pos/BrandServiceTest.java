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

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    void saveTestSuccess()
    {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);
        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Nike", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveTestFailure()
    {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");
        Brand existingBrand = new Brand();
        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(existingBrand);
        BrandDto response = brandService.save(brandDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand with identifier - "+brandDto.getIdentifier()+" already exists", response.getMessage());
    }

    @Test
    void updateTestSuccess()
    {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");
        brandDto.setDescription("Sports Brand");

        Brand brand = new Brand();
        brand.setStatus(false);

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
        Assertions.assertEquals("Sports Brand", brand.getDescription());
        Assertions.assertTrue(brand.getStatus());
    }

    @Test
    void updateTestFailure()
    {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand not found", response.getMessage());
    }

    @Test
    void deleteTest()
    {
        String identifier = "Nike";
        Mockito.doNothing().when(brandRepository).deleteByIdentifier(identifier);
        brandService.delete(identifier);
        Mockito.verify(brandRepository).deleteByIdentifier(identifier);
    }

    @Test
    void findAllBrandTest()
    {
        Brand brand1 = new Brand();
        Brand brand2 = new Brand();

        List<Brand> brands = new ArrayList<>();
        brands.add(brand1);
        brands.add(brand2);

        BrandDto dto1 = new BrandDto();
        BrandDto dto2 = new BrandDto();

        List<BrandDto> dtoList = new ArrayList<>();
        dtoList.add(dto1);
        dtoList.add(dto2);

        Mockito.when(brandRepository.findAll())
                .thenReturn(brands);
        Mockito.when(modelMapper.map(
                Mockito.eq(brands),
                Mockito.any(Type.class))
        ).thenReturn(dtoList);

        List<BrandDto> response  = brandService.findAll();

        Assertions.assertEquals(2, response.size());
    }
    @Test
    void findAll_WithPagination_ShouldReturnBrandDtos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        List<Brand> brands = List.of(new Brand());
        Page<Brand> brandPage = new PageImpl<>(brands);

        List<BrandDto> brandDtos = List.of(new BrandDto());

        Type listType = new TypeToken<List<BrandDto>>() {}.getType();

        Mockito.when(brandRepository.findAll(pageable))
                .thenReturn(brandPage);

        Mockito.when(modelMapper.map(brands, listType))
                .thenReturn(brandDtos);

        // Act
        List<BrandDto> response = brandService.findAll(pageable);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(brandRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(brands, listType);
    }

    @Test
    void findByIdentifierTest()
    {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto response = brandService.findByIdentifier("Nike");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Nike", response.getIdentifier());
    }

    @Test
    void toggleTestSuccess()
    {
        Brand brand = new Brand();
        brand.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);

        brandService.toggleStatus("Nike");

        Assertions.assertFalse(brand.getStatus());
    }

    @Test
    void toggleTestFailure()
    {
        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);
        brandService.toggleStatus("Nike");
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }
}
