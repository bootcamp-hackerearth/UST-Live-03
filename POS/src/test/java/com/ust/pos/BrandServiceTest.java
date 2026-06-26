package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.exception.ResourceNotFoundException;
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

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");

        Mockito.when(brandRepository.findByIdentifier("BRAND001")).thenReturn(null);

        Brand brand = new Brand();

        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);

        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertNotNull(response);

        Assertions.assertEquals("BRAND001", response.getIdentifier());

        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void saveTestFailure_BrandAlreadyExists() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");

        Brand existingBrand = new Brand();
        existingBrand.setDeleted(false);

        Mockito.when(brandRepository.findByIdentifier("BRAND001")).thenReturn(existingBrand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Brand with identifier - BRAND001 already exists", response.getMessage());

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedBrandTest() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");

        Brand existingBrand = new Brand();
        existingBrand.setDeleted(true);

        Mockito.when(brandRepository.findByIdentifier("BRAND001")).thenReturn(existingBrand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Brand with identifier - BRAND001 has been soft deleted. Restore it by changing status.", response.getMessage());

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifier_ShouldThrowException_WhenBrandNotFound() {


        Mockito.when(brandRepository.findByIdentifier("BRAND001"))
                .thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> brandService.findByIdentifier("BRAND001"));

        Assertions.assertEquals("Warehouse with identifier 'BRAND001' not found", exception.getMessage());

        Mockito.verify(brandRepository).findByIdentifier("BRAND001");

        Mockito.verifyNoInteractions(modelMapper);
    }

    @Test
    void updateTestSuccess() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");
        brandDto.setSuccess(true);

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("BRAND001");

        Mockito.when(brandRepository.findByIdentifier("BRAND001")).thenReturn(existingBrand);

        Mockito.when(brandRepository.save(existingBrand)).thenReturn(existingBrand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper).map(brandDto, existingBrand);

        Mockito.verify(brandRepository).save(existingBrand);
    }

    @Test
    void updateTestFailure() {

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");

        Mockito.when(
                        brandRepository.findByIdentifier("BRAND001"))
                .thenReturn(null);

        BrandDto response =
                brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Brand with identifier - BRAND001 not found", response.getMessage());

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTestSuccess() {

        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brand.setDeleted(false);

        Mockito.when(brandRepository.findByIdentifier("BRAND001")).thenReturn(brand);

        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        boolean result = brandService.delete("BRAND001");

        Assertions.assertTrue(result);

        Assertions.assertTrue(brand.getDeleted());

        Assertions.assertFalse(brand.getStatus());

        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(brandRepository.findByIdentifier("BRAND001")).thenReturn(null);

        boolean result = brandService.delete("BRAND001");

        Assertions.assertFalse(result);

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");
        brand.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("BRAND001")).thenReturn(brand);

        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        brandService.toggleStatus("BRAND001");

        Assertions.assertFalse(brand.getStatus());

        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusTestFailure() {

        Mockito.when(brandRepository.findByIdentifier("BRAND001")).thenReturn(null);

        brandService.toggleStatus("BRAND001");

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
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

        Mockito.when(
                        brandRepository.findByStatusTrue())
                .thenReturn(brandList);

        Mockito.when(modelMapper.map(Mockito.eq(brandList), Mockito.eq(listType))).thenReturn(List.of(brandDto));

        List<BrandDto> response = brandService.findActiveBrands();

        Assertions.assertNotNull(response);

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("BRAND001", response.get(0).getIdentifier());
    }

    @Test
    void findAllPaginationTest() {

        Brand brand = new Brand();
        brand.setIdentifier("BRAND001");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BRAND001");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Brand> brandPage = new PageImpl<>(List.of(brand), pageable, 1);

        Mockito.when(brandRepository.findByDeletedFalse(pageable)).thenReturn(brandPage);

        Type listType = new TypeToken<List<BrandDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(Mockito.eq(brandPage.getContent()), Mockito.eq(listType))).thenReturn(List.of(brandDto));

        PageDto<BrandDto> response = brandService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(1, response.getDtoList().size());

        Assertions.assertEquals("BRAND001", response.getDtoList().get(0).getIdentifier());

        Assertions.assertEquals(1, response.getTotalRecords());

        Assertions.assertEquals(1, response.getTotalPages());

        Assertions.assertEquals(10, response.getSizePerPage());

        Assertions.assertEquals(0, response.getPage());
    }
}