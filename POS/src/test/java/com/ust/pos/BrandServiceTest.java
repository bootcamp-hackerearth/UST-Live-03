package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Brand;
import com.ust.pos.models.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    void saveTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B001");
        dto.setStatus(true);
        Brand brand = new Brand();
        when(brandRepository.findByIdentifier("B001")).thenReturn(null);
        when(modelMapper.map(dto, Brand.class)).thenReturn(brand);
        BrandDto result = brandService.save(dto);
        assertTrue(result.isSuccess());
        assertEquals("Brand created successfully", result.getMessage());
        verify(brandRepository).save(brand);
        BrandDto dto2 = new BrandDto();
        dto2.setIdentifier("B002");
        dto2.setStatus(null);
        Brand brand2 = new Brand();
        when(brandRepository.findByIdentifier("B002")).thenReturn(null);
        when(modelMapper.map(dto2, Brand.class)).thenReturn(brand2);
        brandService.save(dto2);
        assertTrue(brand2.getStatus());
        Brand existing = new Brand();
        existing.setDeleted(false);
        when(brandRepository.findByIdentifier("B003")).thenReturn(existing);
        BrandDto existingResult = brandService.save(new BrandDto() {{setIdentifier("B003");}});
        assertFalse(existingResult.isSuccess());
        assertEquals("Brand with identifier - B003 already exists", existingResult.getMessage());
        Brand deletedBrand = new Brand();
        deletedBrand.setDeleted(true);
        when(brandRepository.findByIdentifier("B004")).thenReturn(deletedBrand);
        BrandDto deletedResult = brandService.save(new BrandDto() {{setIdentifier("B004");}});
        assertFalse(deletedResult.isSuccess());
        assertEquals("Brand with identifier - B004 was deleted and cannot be created again.", deletedResult.getMessage());
    }

    @Test
    void updateTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B001");
        Brand existing = new Brand();
        when(brandRepository.findByIdentifierAndDeletedFalse("B001")).thenReturn(existing);
        BrandDto result = brandService.update(dto);
        assertNotNull(result);
        verify(modelMapper).map(dto, existing);
        verify(brandRepository).save(existing);
        BrandDto dto2 = new BrandDto();
        dto2.setIdentifier("B002");
        when(brandRepository.findByIdentifierAndDeletedFalse("B002")).thenReturn(null);
        BrandDto notFoundResult = brandService.update(dto2);
        assertFalse(notFoundResult.isSuccess());
        assertEquals("Brand with identifier - B002 not found", notFoundResult.getMessage());
    }

    @Test
    void deleteAndFindByIdentifierTest() {
        Brand brand = new Brand();
        when(brandRepository.findByIdentifierAndDeletedFalse("B001")).thenReturn(brand);
        brandService.delete("B001");
        assertTrue(brand.getDeleted());
        verify(brandRepository).save(brand);
        BrandDto dto = new BrandDto();
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);
        BrandDto result = brandService.findByIdentifier("B001");
        assertNotNull(result);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> page = new PageImpl<>(List.of(new Brand()));
        when(brandRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(new BrandDto()));
        WsDto<BrandDto> result = brandService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Brand> specification = mock(Specification.class);
        Page<Brand> page = new PageImpl<>(List.of(new Brand()), pageable, 1);
        when(brandRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new BrandDto()));
        WsDto<BrandDto> result = brandService.findAll(specification, pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        verify(brandRepository).findAll(specification, pageable);
    }

    @Test
    void toggleStatusTest() {
        BrandDto dto = new BrandDto();
        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Brand.class), eq(BrandDto.class))).thenReturn(dto);
        Brand activeBrand = new Brand();
        activeBrand.setStatus(true);
        when(brandRepository.findByIdentifierAndDeletedFalse("B001")).thenReturn(activeBrand);
        brandService.toggleStatus("B001");
        assertFalse(activeBrand.getStatus());
        Brand inactiveBrand = new Brand();
        inactiveBrand.setStatus(false);
        when(brandRepository.findByIdentifierAndDeletedFalse("B002")).thenReturn(inactiveBrand);
        brandService.toggleStatus("B002");
        assertTrue(inactiveBrand.getStatus());
        Brand nullStatusBrand = new Brand();
        nullStatusBrand.setStatus(null);
        BrandDto mappedDto = new BrandDto();
        mappedDto.setStatus(true);
        when(brandRepository.findByIdentifierAndDeletedFalse("B003")).thenReturn(nullStatusBrand);
        when(modelMapper.map(any(Brand.class), eq(BrandDto.class))).thenReturn(mappedDto);
        BrandDto result = brandService.toggleStatus("B003");
        assertNotNull(result);
        assertTrue(result.getStatus());
        assertTrue(nullStatusBrand.getStatus());
        when(brandRepository.findByIdentifierAndDeletedFalse("B004")).thenReturn(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> brandService.toggleStatus("B004"));
        assertEquals("Brand not found with identifier: B004", exception.getMessage());
    }

    @Test
    void findAllActiveTest() {
        Brand brand = new Brand();
        BrandDto dto = new BrandDto();
        when(brandRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(brand));
        when(modelMapper.map(brand, BrandDto.class)).thenReturn(dto);
        List<BrandDto> result = brandService.findAllActive();
        assertEquals(1, result.size());
        when(brandRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        List<BrandDto> emptyResult = brandService.findAllActive();
        assertTrue(emptyResult.isEmpty());
    }
}