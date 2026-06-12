package com.ust.pos;

import com.ust.pos.dto.ShelvesDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelves;
import com.ust.pos.model.ShelvesRepository;
import com.ust.pos.shelves.service.impl.ShelvesServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelvesServiceTest {

    @InjectMocks
    private ShelvesServiceImpl shelvesService;

    @Mock
    private ShelvesRepository shelvesRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifier() {

        Shelves entity = new Shelves();
        entity.setIdentifier("SH001");

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("SH001");

        when(shelvesRepository.findByIdentifier("SH001")).thenReturn(entity);
        when(modelMapper.map(entity, ShelvesDto.class)).thenReturn(dto);

        ShelvesDto result = shelvesService.findByIdentifier("SH001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("SH001", result.getIdentifier());
    }

    @Test
    void saveSuccess() {

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("SH001");

        Shelves entity = new Shelves();
        entity.setIdentifier("SH001");

        when(shelvesRepository.findByIdentifier("SH001")).thenReturn(null);
        when(modelMapper.map(dto, Shelves.class)).thenReturn(entity);

        ShelvesDto result = shelvesService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("SH001", result.getIdentifier());

        verify(shelvesRepository).save(entity);
    }

    @Test
    void saveFailure() {

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("SH001");

        Shelves existing = new Shelves();

        when(shelvesRepository.findByIdentifier("SH001")).thenReturn(existing);

        ShelvesDto result = shelvesService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(shelvesRepository, never()).save(any());
    }

    @Test
    void updateSuccess() {

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("SH001");

        Shelves entity = new Shelves();
        entity.setIdentifier("SH001");

        when(shelvesRepository.findByIdentifier("SH001")).thenReturn(entity);
        when(shelvesRepository.save(entity)).thenReturn(entity);

        ShelvesDto result = shelvesService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("SH001", result.getIdentifier());

        verify(modelMapper).map(dto, entity);
        verify(shelvesRepository).save(entity);
    }

    @Test
    void updateFailure() {

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("SH001");

        when(shelvesRepository.findByIdentifier("SH001")).thenReturn(null);

        ShelvesDto result = shelvesService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(shelvesRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        doNothing().when(shelvesRepository).deleteByIdentifier("SH001");

        shelvesService.delete("SH001");

        verify(shelvesRepository).deleteByIdentifier("SH001");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Shelves> list = List.of(new Shelves(), new Shelves());

        Page<Shelves> page = new PageImpl<>(list, pageable, 2);

        List<ShelvesDto> dtoList = List.of(new ShelvesDto(), new ShelvesDto());

        when(shelvesRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(dtoList);

        WsDto<ShelvesDto> result = shelvesService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Shelves> list = List.of();

        Page<Shelves> page = new PageImpl<>(list, pageable, 0);

        when(shelvesRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(List.of());

        WsDto<ShelvesDto> result = shelvesService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());

        verify(shelvesRepository).findAll(pageable);
    }

    @Test
    void findActiveShelvesTest() {

        List<Shelves> active = List.of(new Shelves(), new Shelves());

        when(shelvesRepository.findByStatus("Active")).thenReturn(active);

        List<Shelves> result = shelvesService.findActiveShelves();

        Assertions.assertEquals(2, result.size());
        verify(shelvesRepository).findByStatus("Active");
    }

    @Test
    void toggleStatusFound() {

        Shelves shelves = new Shelves();
        shelves.setStatus(true);

        when(shelvesRepository.findByIdentifier("SH001")).thenReturn(shelves);

        shelvesService.toggleStatus("SH001");

        Assertions.assertFalse(shelves.isStatus());
        verify(shelvesRepository).save(shelves);
    }

    @Test
    void toggleStatusNotFound() {

        when(shelvesRepository.findByIdentifier("SH001")).thenReturn(null);

        shelvesService.toggleStatus("SH001");

        verify(shelvesRepository, never()).save(any());
    }
}