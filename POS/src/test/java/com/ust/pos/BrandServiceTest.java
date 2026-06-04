package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BrandServiceImpl brandService;

    @Test
    void saveTestSuccess() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(brandDto, Brand.class))
                .thenReturn(brand);
        Mockito.when(brandRepository.save(brand))
                .thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Nike", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure_duplicateIdentifier() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(existingBrand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Brand with identifier - Nike already exists",
                response.getMessage()
        );
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(brandRepository.findById(1L))
                .thenReturn(Optional.of(brand));
        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(brandDto);

        BrandDto response = brandService.findById(1L);

        Assertions.assertEquals("Nike", response.getIdentifier());
    }

    @Test
    void findByIdNotFoundTest() {
        Mockito.when(brandRepository.findById(99L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class,
                () -> brandService.findById(99L));
    }

    @Test
    void findAllTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Page<Brand> page = new PageImpl<>(List.of(brand));

        Mockito.when(brandRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(brandDto));

        Pageable pageable = PageRequest.of(0, 10);
        List<BrandDto> response = brandService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Nike", response.get(0).getIdentifier());
    }

    @Test
    void updateTestSuccess() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(brand);
        Mockito.when(brandRepository.save(brand))
                .thenReturn(brand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Brand updated successfully", response.getMessage());
    }

    @Test
    void updateTestFailure_notFound() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand not found", response.getMessage());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(brandRepository).deleteById(1L);

        brandService.deleteById(1L);

        Mockito.verify(brandRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        BrandDto response = brandService.findByIdentifier("Nike");

        Assertions.assertEquals("Nike", response.getIdentifier());
    }

    @Test
    void changeBrandStatusSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setStatus(false);

        BrandDto brandDto = new BrandDto();
        brandDto.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(brand);
        Mockito.when(brandRepository.save(brand))
                .thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(brandDto);

        BrandDto response = brandService.changeBrandStatus("Nike", true);

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void changeBrandStatusFailureTest() {
        Mockito.when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(null);

        BrandDto response = brandService.changeBrandStatus("Nike", true);

        Assertions.assertNull(response);
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }
}