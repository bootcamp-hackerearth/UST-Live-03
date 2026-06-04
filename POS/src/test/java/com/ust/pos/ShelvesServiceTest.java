package com.ust.pos;

import com.ust.pos.dto.ShelvesDto;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelvesServiceTest {

    @InjectMocks
    private ShelvesServiceImpl shelvesService;

    @Mock
    private ShelvesRepository shelvesRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("SH01");

        Shelves shelves = new Shelves();
        shelves.setIdentifier("SH01");

        when(shelvesRepository.findByIdentifier("SH01")).thenReturn(null);
        when(modelMapper.map(dto, Shelves.class)).thenReturn(shelves);

        ShelvesDto response = shelvesService.save(dto);

        assertEquals("SH01", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        verify(shelvesRepository).save(shelves);
    }

    @Test
    void saveFailureTest() {
        Shelves shelves = new Shelves();
        shelves.setIdentifier("SH01");

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("SH01");

        when(shelvesRepository.findByIdentifier("SH01")).thenReturn(shelves);
        ShelvesDto response = shelvesService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(shelvesRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        Shelves shelves = new Shelves();
        shelves.setIdentifier("SH01");

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("SH01");

        when(shelvesRepository.findByIdentifier("SH01")).thenReturn(shelves);
        ShelvesDto response = shelvesService.update(dto);

        assertEquals("SH01", response.getIdentifier());
        verify(modelMapper).map(dto, shelves);
        verify(shelvesRepository).save(shelves);
    }

    @Test
    void updateFailureTest() {
        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("SH01");

        when(shelvesRepository.findByIdentifier("SH01")).thenReturn(null);
        ShelvesDto response = shelvesService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(shelvesRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        shelvesService.delete("SH01");
        verify(shelvesRepository).deleteByIdentifier("SH01");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Shelves shelves = new Shelves();
        shelves.setIdentifier("SH01");

        ShelvesDto dto = new ShelvesDto();
        dto.setIdentifier("SH01");

        when(shelvesRepository.findByIdentifier("SH01")).thenReturn(shelves);
        when(modelMapper.map(shelves, ShelvesDto.class)).thenReturn(dto);

        ShelvesDto response = shelvesService.findByIdentifier("SH01");

        assertNotNull(response);
        assertEquals("SH01", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(shelvesRepository.findByIdentifier("SH01")).thenReturn(null);
        ShelvesDto response = shelvesService.findByIdentifier("SH01");
        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Shelves> shelvesList = List.of(new Shelves(), new Shelves());
        Page<Shelves> page = new PageImpl<>(shelvesList);

        List<ShelvesDto> dtoList = List.of(new ShelvesDto(), new ShelvesDto());

        when(shelvesRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(shelvesList),
                any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<ShelvesDto> result = shelvesService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(shelvesRepository).findAll(pageable);
    }

    @Test
    void findActiveShelvesTest() {
        List<Shelves> activeShelves = List.of(new Shelves(), new Shelves());

        when(shelvesRepository.findByStatus(true)).thenReturn(activeShelves);
        List<Shelves> result = shelvesService.findActiveShelves();

        assertEquals(2, result.size());
        verify(shelvesRepository).findByStatus(true);
    }

    @Test
    void toggleStatusSuccessTest() {
        Shelves shelves = new Shelves();
        shelves.setIdentifier("SH01");
        shelves.setStatus(true);

        when(shelvesRepository.findByIdentifier("SH01")).thenReturn(shelves);
        shelvesService.toggleStatus("SH01");

        Assertions.assertFalse(shelves.isStatus());
        verify(shelvesRepository).save(shelves);
    }

    @Test
    void toggleStatusFailureTest() {
        when(shelvesRepository.findByIdentifier("SH01")).thenReturn(null);
        shelvesService.toggleStatus("SH01");
        verify(shelvesRepository, never()).save(any());
    }
}