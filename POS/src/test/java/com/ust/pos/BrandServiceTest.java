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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifier_Found() {

        Brand brand = new Brand();
        brand.setIdentifier("B1");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1"))
                .thenReturn(brand);

        when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);

        BrandDto result = brandService.findByIdentifier("B1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("B1", result.getIdentifier());
    }

    @Test
    void save_NewBrand() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Brand brand = new Brand();

        when(brandRepository.findByIdentifier("B1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Brand.class))
                .thenReturn(brand);

        when(brandRepository.save(brand))
                .thenReturn(brand);

        BrandDto result = brandService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("B1", result.getIdentifier());
        verify(brandRepository).save(brand);
    }

    @Test
    void save_BrandExists() {

        Brand existing = new Brand();

        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1"))
                .thenReturn(existing);

        BrandDto result = brandService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(brandRepository, never()).save(any());
    }

    @Test
    void update_BrandExists() {

        Brand existing = new Brand();

        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1"))
                .thenReturn(existing);

        when(brandRepository.save(existing))
                .thenReturn(existing);

        BrandDto result = brandService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("B1", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(brandRepository).save(existing);
    }

    @Test
    void update_BrandNotFound() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        when(brandRepository.findByIdentifier("B1"))
                .thenReturn(null);

        BrandDto result = brandService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(brandRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        Brand brand = new Brand();

        when(brandRepository.findByIdentifier("B1"))
                .thenReturn(brand);

        brandService.delete("B1");

        verify(brandRepository).findByIdentifier("B1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Brand brand1 = new Brand();
        Brand brand2 = new Brand();

        Page<Brand> page = new PageImpl<>(
                List.of(brand1, brand2),
                pageable,
                2
        );

        List<BrandDto> dtoList =
                List.of(new BrandDto(), new BrandDto());

        when(brandRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<BrandDto> result =
                brandService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2,
                result.getContent().size());
        Assertions.assertEquals(0,
                result.getPage());
        Assertions.assertEquals(10,
                result.getSizePerPage());
        Assertions.assertEquals(1,
                result.getTotalPages());
        Assertions.assertEquals(2,
                result.getTotalRecords());

        verify(brandRepository)
                .findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Brand> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(brandRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<BrandDto> result =
                brandService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(
                result.getContent().isEmpty());
        Assertions.assertEquals(0,
                result.getTotalRecords());

        verify(brandRepository)
                .findByIsDeletedFalse(pageable);
    }

    @Test
    void toggleStatus_Test() {

        Brand brand = new Brand();
        brand.setStatus(true);

        when(brandRepository.findByIdentifier("B1"))
                .thenReturn(brand);

        brandService.toggleStatus("B1");

        Assertions.assertFalse(brand.isStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    void toggleStatus_NotFound() {

        when(brandRepository.findByIdentifier("B1"))
                .thenReturn(null);

        brandService.toggleStatus("B1");

        verify(brandRepository, never())
                .save(any());
    }
}

