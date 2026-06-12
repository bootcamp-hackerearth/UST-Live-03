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

        Brand brand = new Brand();

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);

        BrandDto response = brandService.save(brandDto);

        Assertions.assertEquals("Nike", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class)).thenReturn(brandDto);

        BrandDto response = brandService.findByIdentifier("Nike");

        Assertions.assertEquals("Nike", response.getIdentifier());
    }

    @Test
    void deleteByIdentifierTest() {
        Mockito.doNothing().when(brandRepository).deleteByIdentifier("Nike");
        brandService.deleteByIdentifier("Nike");
        Mockito.verify(brandRepository).deleteByIdentifier("Nike");
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

        WsDto<BrandDto> response =
                brandService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("BR001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Brand brand = new Brand();
        brand.setIdentifier("BR001");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR001");

        List<Brand> brands = List.of(brand);
        List<BrandDto> dtos = List.of(dto);

        Mockito.when(brandRepository.findAll()).thenReturn(brands);
        Mockito.when(modelMapper.map(Mockito.eq(brands), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<BrandDto> response = brandService.findAll(null);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("BR001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void updateTest() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Brand brand = new Brand();
        brand.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier("Nike");

        Mockito.when(brandRepository.findByIdentifier("Nike")).thenReturn(null);

        BrandDto response = brandService.update(brandDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void toggleStatusSuccessTest() {
        Brand brand = new Brand();
        brand.setIdentifier("Admin");
        brand.setStatus(false);

        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(brand);

        BrandDto response = brandService.toggleStatus("Admin", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());

        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(brandRepository.findByIdentifier("Admin")).thenReturn(null);

        BrandDto response = brandService.toggleStatus("Admin", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Brand not found", response.getMessage());
    }

}