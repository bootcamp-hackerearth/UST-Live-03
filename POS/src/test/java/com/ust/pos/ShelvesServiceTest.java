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
    void findByIdentifier_Found() {

        Shelves shelves = new Shelves();
        shelves.setIdentifier("S1");

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("S1");

        when(shelvesRepository.findByIdentifier("S1"))
                .thenReturn(shelves);

        when(modelMapper.map(shelves, ShelvesDto.class))
                .thenReturn(dto);

        ShelvesDto result = shelvesService.findByIdentifier("S1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("S1", result.getIdentifier());
    }

    @Test
    void save_NewShelves() {

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("S1");

        Shelves shelves = new Shelves();

        when(shelvesRepository.findByIdentifier("S1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Shelves.class))
                .thenReturn(shelves);

        when(shelvesRepository.save(shelves))
                .thenReturn(shelves);

        ShelvesDto result = shelvesService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("S1", result.getIdentifier());
        verify(shelvesRepository).save(shelves);
    }

    @Test
    void save_ShelvesExists() {

        Shelves existing = new Shelves();

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("S1");

        when(shelvesRepository.findByIdentifier("S1"))
                .thenReturn(existing);

        ShelvesDto result = shelvesService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(shelvesRepository, never()).save(any());
    }

    @Test
    void update_ShelvesExists() {

        Shelves existing = new Shelves();

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("S1");

        when(shelvesRepository.findByIdentifier("S1"))
                .thenReturn(existing);

        when(shelvesRepository.save(existing))
                .thenReturn(existing);

        ShelvesDto result = shelvesService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("S1", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(shelvesRepository).save(existing);
    }

    @Test
    void update_ShelvesNotFound() {

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("S1");

        when(shelvesRepository.findByIdentifier("S1"))
                .thenReturn(null);

        ShelvesDto result = shelvesService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(shelvesRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        Shelves shelves = new Shelves();

        when(shelvesRepository.findByIdentifier("S1"))
                .thenReturn(shelves);

        shelvesService.delete("S1");

        verify(shelvesRepository).findByIdentifier("S1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Shelves shelves1 = new Shelves();
        Shelves shelves2 = new Shelves();

        Page<Shelves> page = new PageImpl<>(
                List.of(shelves1, shelves2),
                pageable,
                2
        );

        List<ShelvesDto> dtoList =
                List.of(new ShelvesDto(), new ShelvesDto());

        when(shelvesRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<ShelvesDto> result =
                shelvesService.findAll(pageable);

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

        verify(shelvesRepository)
                .findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelves> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(shelvesRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<ShelvesDto> result =
                shelvesService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(
                result.getContent().isEmpty());
        Assertions.assertEquals(0,
                result.getTotalRecords());

        verify(shelvesRepository)
                .findByIsDeletedFalse(pageable);
    }

    @Test
    void findActiveShelves_Test() {

        List<Shelves> shelvesList =
                List.of(new Shelves(), new Shelves());

        when(shelvesRepository.findByStatus("Active"))
                .thenReturn(shelvesList);

        List<Shelves> result =
                shelvesService.findActiveShelves();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        verify(shelvesRepository).findByStatus("Active");
    }

    @Test
    void toggleStatus_Test() {

        Shelves shelves = new Shelves();
        shelves.setStatus(true);

        when(shelvesRepository.findByIdentifier("S1"))
                .thenReturn(shelves);

        shelvesService.toggleStatus("S1");

        Assertions.assertFalse(shelves.isStatus());
        verify(shelvesRepository).save(shelves);
    }

    @Test
    void toggleStatus_NotFound() {

        when(shelvesRepository.findByIdentifier("S1"))
                .thenReturn(null);

        shelvesService.toggleStatus("S1");

        verify(shelvesRepository, never()).save(any());
    }
}