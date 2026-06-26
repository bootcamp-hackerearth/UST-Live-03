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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

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
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BR1");

        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("BR1")).thenReturn(null);
        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("BR1", response.getIdentifier());
        verify(brandRepository).save(brand);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BR1");

        Brand existingBrand = new Brand();
        existingBrand.setDeleted(false);

        Mockito.when(brandRepository.findByIdentifier("BR1")).thenReturn(existingBrand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("BR1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand with identifier - BR1 already exists", response.getMessage());
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BR1");

        Brand existingBrand = new Brand();
        existingBrand.setDeleted(true);

        Mockito.when(brandRepository.findByIdentifier("BR1")).thenReturn(existingBrand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("BR1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand with identifier - BR1 was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BR1");

        Brand existingBrand = new Brand();
        existingBrand.setIdentifier("BR1");

        Mockito.when(brandRepository.findByIdentifier("BR1")).thenReturn(existingBrand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertEquals("BR1", response.getIdentifier());
        verify(modelMapper).map(brandDto, existingBrand);
        verify(brandRepository).save(existingBrand);
    }

    @Test
    void updateFailureTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BR1");

        Mockito.when(brandRepository.findByIdentifier("BR1")).thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertEquals("BR1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand with identifier - BR1 not found", response.getMessage());
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Brand brand = new Brand();
        Mockito.when(brandRepository.findByIdentifier("SH1")).thenReturn(brand);
        brandService.delete("SH1");
        verify(brandRepository).findByIdentifier("SH1");
    }

    @Test
    void findAllSuccessTest() {
        Brand b1 = new Brand();
        b1.setIdentifier("BR1");
        List<Brand> brandList = List.of(b1);

        BrandDto d1 = new BrandDto();
        d1.setIdentifier("BR1");
        List<BrandDto> brandDtos = List.of(d1);

        Page<Brand> page = new PageImpl<>(brandList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(brandRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(brandList), Mockito.any(Type.class))).thenReturn(brandDtos);

        WsDto<BrandDto> result = brandService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR1");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("BR1");

        Mockito.when(brandRepository.findByIdentifier("BR1")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto response = brandService.findByIdentifier("BR1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("BR1", response.getIdentifier());
    }

    @Test
    void findAllActiveSuccessTest() {
        Brand b1 = new Brand();
        List<Brand> activeBrands = List.of(b1);

        BrandDto d1 = new BrandDto();
        List<BrandDto> brandDtos = List.of(d1);

        Mockito.when(brandRepository.findByStatusTrueAndIsDeletedFalse()).thenReturn(activeBrands);
        Mockito.when(modelMapper.map(Mockito.eq(activeBrands), Mockito.any(Type.class))).thenReturn(brandDtos);

        List<BrandDto> result = brandService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void toggleStatusSuccessTest() {
        Brand brand = new Brand();
        brand.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("BR1")).thenReturn(brand);

        brandService.toggleStatus("BR1");

        Assertions.assertFalse(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusBrandNotFoundTest() {
        Mockito.when(brandRepository.findByIdentifier("BR1")).thenReturn(null);

        brandService.toggleStatus("BR1");

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }
}