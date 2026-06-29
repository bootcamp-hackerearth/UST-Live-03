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
    void save_success() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("S1");
        Mockito.when(brandRepository.findByIdentifier("S1")).thenReturn(null);
        Brand brand = new Brand();
        Mockito.when(modelMapper.map(dto, Brand.class)).thenReturn(brand);
        Mockito.when(brandRepository.save(brand)).thenReturn(brand);
        BrandDto response = brandService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void save_failure_already_exists() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("S1");
        Brand existing = new Brand();
        existing.setDeleted(false);
        Mockito.when(brandRepository.findByIdentifier("S1")).thenReturn(existing);
        BrandDto response = brandService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void save_failure_deleted_record() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("S1");
        Brand existing = new Brand();
        existing.setDeleted(true);
        Mockito.when(brandRepository.findByIdentifier("S1")).thenReturn(existing);
        BrandDto response = brandService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("deleted"));
    }

    @Test
    void update_success() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("S1");
        Brand existing = new Brand();
        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(existing);
        Mockito.when(brandRepository.save(existing)).thenReturn(existing);
        BrandDto response = brandService.update(dto);
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void update_failure_not_found() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("S1");
        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(null);
        BrandDto response = brandService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void delete_success() {
        Brand brand = new Brand();
        brand.setDeleted(false);
        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(brand);
        brandService.delete("S1");
        Assertions.assertTrue(brand.getDeleted()); // soft delete happened
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void delete_not_found() {
        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(null);
        brandService.delete("S1");
        Mockito.verify(brandRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Brand brand = new Brand();
        List<Brand> brandList = List.of(brand);
        Page<Brand> page = new PageImpl<>(brandList, pageable, 1);
        Mockito.when(brandRepository.findByDeletedFalse(pageable))
                .thenReturn(page);
        List<BrandDto> dtoList = List.of(new BrandDto());
        Mockito.when(modelMapper.map(
                        Mockito.eq(brandList),
                        Mockito.any(Type.class)))
                .thenReturn(dtoList);
        WsDto<BrandDto> result = brandService.findAll(pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findByIdentifier_success() {
        Brand brand = new Brand();
        BrandDto dto = new BrandDto();
        dto.setIdentifier("S1");
        Mockito.when(brandRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(brand);
        Mockito.when(modelMapper.map(brand, BrandDto.class))
                .thenReturn(dto);
        BrandDto result = brandService.findByIdentifier("S1");
        Assertions.assertEquals("S1", result.getIdentifier());
    }

    @Test
    void updateStatus_success() {
        Brand brand = new Brand();
        Mockito.when(brandRepository.findByIdentifier("S1"))
                .thenReturn(brand);
        brandService.updateStatus("S1", true);
        Assertions.assertTrue(brand.getStatus());
        Mockito.verify(brandRepository).save(brand);
    }

    @Test
    void findAllActive_success() {
        Brand brand = new Brand();
        List<Brand> list = List.of(brand);
        List<BrandDto> dtoList = List.of(new BrandDto());
        Mockito.when(brandRepository.findByStatusAndDeletedFalse(true))
                .thenReturn(list);
        Mockito.when(modelMapper.map(
                        Mockito.eq(list),
                        Mockito.any(Type.class)))
                .thenReturn(dtoList);
        List<BrandDto> result = brandService.findAllActive();
        Assertions.assertEquals(1, result.size());
    }
}