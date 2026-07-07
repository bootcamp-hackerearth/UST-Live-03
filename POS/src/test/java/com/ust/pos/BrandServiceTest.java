package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandServiceImpl service;

    @Mock
    private BrandRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierSuccess() {

        Brand brand = new Brand();
        brand.setIdentifier("BR1");

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR1");

        when(repository.findByIdentifier("BR1")).thenReturn(brand);

        when(mapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto result = service.findByIdentifier("BR1");

        assertEquals("BR1", result.getIdentifier());

        verify(repository).findByIdentifier("BR1");
    }

    @Test
    void findByIdentifierNotFound() {

        when(repository.findByIdentifier("BR1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.findByIdentifier("BR1"));
    }

    @Test
    void toggleStatusTrueToFalse() {

        Brand brand = new Brand();
        brand.setStatus(true);

        BrandDto dto = new BrandDto();

        when(repository.findByIdentifier("BR1")).thenReturn(brand);

        when(mapper.map(brand, BrandDto.class)).thenReturn(dto);

        service.toggleStatus("BR1");

        assertFalse(brand.isStatus());

        verify(repository).save(brand);
    }

    @Test
    void toggleStatusFalseToTrue() {

        Brand brand = new Brand();
        brand.setStatus(false);

        BrandDto dto = new BrandDto();

        when(repository.findByIdentifier("BR1")).thenReturn(brand);

        when(mapper.map(brand, BrandDto.class)).thenReturn(dto);

        service.toggleStatus("BR1");

        assertTrue(brand.isStatus());

        verify(repository).save(brand);
    }

    @Test
    void toggleStatusNotFound() {

        when(repository.findByIdentifier("BR1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.toggleStatus("BR1"));
    }

    @Test
    void saveSuccess() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier(" BR1 ");

        Brand brand = new Brand();

        when(repository.findByIdentifier("BR1")).thenReturn(null);

        when(mapper.map(dto, Brand.class)).thenReturn(brand);

        BrandDto result = service.save(dto);

        assertEquals("BR1", result.getIdentifier());

        verify(repository).save(brand);
    }

    @Test
    void saveAlreadyExists() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR1");

        Brand existing = new Brand();

        when(repository.findByIdentifier("BR1")).thenReturn(existing);

        BrandDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Brand with identifier - BR1 already exists", result.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    void saveSoftDeleted() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR1");

        Brand existing = new Brand();
        existing.setDeleted(true);

        when(repository.findByIdentifier("BR1")).thenReturn(existing);

        BrandDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Brand with identifier - BR1 has been soft deleted.(Rollback by changing status", result.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    void updateSuccess() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR1");

        Brand brand = new Brand();

        when(repository.findByIdentifier("BR1")).thenReturn(brand);

        doNothing().when(mapper).map(dto, brand);

        BrandDto result = service.update(dto);

        assertEquals("BR1", result.getIdentifier());

        verify(repository).save(brand);
    }

    @Test
    void updateNotFound() {

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BR1");

        when(repository.findByIdentifier("BR1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.update(dto));
    }

    @Test
    void deleteSuccess() {

        Brand brand = new Brand();

        when(repository.findByIdentifier("BR1")).thenReturn(brand);

        boolean result = service.delete("BR1");

        assertTrue(result);

        assertTrue(brand.isDeleted());

        verify(repository).save(brand);
    }

    @Test
    void deleteNotFound() {

        when(repository.findByIdentifier("BR1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.delete("BR1"));
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Brand> brands = List.of(new Brand());

        List<BrandDto> dtos = List.of(new BrandDto());

        Page<Brand> page = new PageImpl<>(brands);

        when(repository.findByDeletedFalse(pageable)).thenReturn(page);

        when(mapper.map(eq(brands), any(Type.class))).thenReturn(dtos);

        WsDto<BrandDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        assertEquals(0, result.getPage());
    }

    @Test
    void findAllSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Brand> spec = (root, query, cb) -> null;

        List<Brand> brands = List.of(new Brand());

        List<BrandDto> dtos = List.of(new BrandDto());

        Page<Brand> page = new PageImpl<>(brands);

        when(repository.findAll(spec, pageable)).thenReturn(page);

        when(mapper.map(eq(brands), any(Type.class))).thenReturn(dtos);

        WsDto<BrandDto> result = service.findAll(spec, pageable, "abc");

        assertEquals("abc", result.getKeyword());

        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void findIfTrueTest() {

        List<Brand> brands = List.of(new Brand());

        List<BrandDto> dtos = List.of(new BrandDto());

        when(repository.findByStatusIsTrue()).thenReturn(brands);

        when(mapper.map(eq(brands), any(Type.class))).thenReturn(dtos);

        List<BrandDto> result = service.findIfTrue();

        assertEquals(1, result.size());

        verify(repository).findByStatusIsTrue();
    }

}