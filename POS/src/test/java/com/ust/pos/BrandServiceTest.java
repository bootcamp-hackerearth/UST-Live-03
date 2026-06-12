package com.ust.pos;

import com.ust.pos.brand.service.impl.BrandServiceImpl;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Brand;
import com.ust.pos.modell.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    void saveTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");
        Brand brand = new Brand();
        brand.setIdentifier("B1");

        when(repository.findByIdentifier("B1")).thenReturn(null);
        when(mapper.map(dto, Brand.class)).thenReturn(brand);

        BrandDto result = service.save(dto);
        verify(repository).save(brand);
        assertTrue(result.getMessage() == null || result.isSuccess());

        when(repository.findByIdentifier("B1")).thenReturn(brand);
        BrandDto existing = service.save(dto);
        assertFalse(existing.isSuccess());
        assertEquals("Brand with identifier - B1 already exists", existing.getMessage());
    }

    @Test
    void updateTest() {
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");
        Brand brand = new Brand();
        brand.setIdentifier("B1");

        when(repository.findByIdentifier("B1")).thenReturn(brand);

        BrandDto updated = service.update(dto);
        verify(mapper).map(dto, brand);
        verify(repository).save(brand);
        assertTrue(updated.getMessage() == null || updated.isSuccess());

        dto.setIdentifier("B2");
        when(repository.findByIdentifier("B2")).thenReturn(null);

        BrandDto notFound = service.update(dto);
        assertFalse(notFound.isSuccess());
        assertEquals("Brand with identifier - B2 not found", notFound.getMessage());
    }

    @Test
    void deleteTest() {
        service.delete("B1");
        verify(repository).deleteByIdentifier("B1");
    }

    @Test
    void findByIdentifierTest() {
        Brand brand = new Brand();
        brand.setIdentifier("B1");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        when(repository.findByIdentifier("B1")).thenReturn(brand);
        when(mapper.map(brand, BrandDto.class)).thenReturn(dto);

        BrandDto result = service.findByIdentifier("B1");
        assertEquals("B1", result.getIdentifier());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Brand brand = new Brand();
        brand.setIdentifier("B1");
        BrandDto dto = new BrandDto();
        dto.setIdentifier("B1");

        Page<Brand> page = new PageImpl<>(List.of(brand), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);

        Type listType = new TypeToken<List<BrandDto>>() {}.getType();
        when(mapper.map(page.getContent(), listType)).thenReturn(List.of(dto));

        WsDto<BrandDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());

        Page<Brand> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(emptyPage.getContent(), listType)).thenReturn(List.of());

        WsDto<BrandDto> emptyResult = service.findAll(pageable);
        assertTrue(emptyResult.getDtoList().isEmpty());
    }
}