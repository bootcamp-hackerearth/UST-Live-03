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
        brandDto.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);
        Brand brand = new Brand();
        Mockito.when(modelMapper.map(brandDto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Nike", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike"))
                .thenReturn(new Brand());

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Nike", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto response = brandService.findByIdentifier("Nike");

        Assertions.assertEquals("Nike", response.getIdentifier());
    }

    @Test
    void updateTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("Nike");

        BrandDto response = brandService.update(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("Nike");

        BrandDto response = brandService.update(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(brandRepository).deleteByIdentifier("Nike");

        brandService.delete("Nike");

        Mockito.verify(brandRepository).deleteByIdentifier("Nike");
    }

    @Test
    void toggleStatusTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");
        brand.setStatus(true);

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        brandService.toggleStatus("Nike");

        Assertions.assertFalse(brand.getStatus());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);

        brandService.toggleStatus("Nike");

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> brandPage = new PageImpl<>(List.of(brand), pageable, 1);

        Mockito.when(brandRepository.findAll(pageable))
                .thenReturn(brandPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(brandPage.getContent()),
                Mockito.any(Type.class)
        )).thenReturn(List.of(brandDto));

        List<BrandDto> response = brandService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Nike", response.get(0).getIdentifier());
    }
}