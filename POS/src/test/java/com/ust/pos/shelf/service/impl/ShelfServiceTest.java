package com.ust.pos.shelf.service.impl;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.modell.Shelf;
import com.ust.pos.modell.ShelfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceImplTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShelfServiceImpl shelfService;

    private Shelf shelf;
    private ShelfDto shelfDto;

    @BeforeEach
    void setUp() {
        shelf = new Shelf();
        shelf.setIdentifier("S001");
        shelf.setStatus(true);
        shelfDto = new ShelfDto();
        shelfDto.setIdentifier("S001");
        shelfDto.setStatus(true);
    }

    @Test
    void testFindByIdentifier() {
        when(shelfRepository.findByIdentifier("S001")).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);
        ShelfDto result = shelfService.findByIdentifier("S001");
        assertNotNull(result);
        assertEquals("S001", result.getIdentifier());
        verify(shelfRepository).findByIdentifier("S001");
    }

    @Test
    void testSave_WhenShelfAlreadyExists() {
        when(shelfRepository.findByIdentifier("S001")).thenReturn(shelf);
        ShelfDto result = shelfService.save(shelfDto);
        assertFalse(result.isSuccess());
        assertEquals("Shelf with identifier - S001 already exists", result.getMessage());
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testSave_WhenShelfDoesNotExist() {
        when(shelfRepository.findByIdentifier("S001")).thenReturn(null);
        when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);
        ShelfDto result = shelfService.save(shelfDto);
        assertNotNull(result);
        verify(shelfRepository).save(shelf);
    }

    @Test
    void testUpdate_WhenShelfNotFound() {
        when(shelfRepository.findByIdentifier("S001")).thenReturn(null);
        ShelfDto result = shelfService.update(shelfDto);
        assertFalse(result.isSuccess());
        assertEquals("Shelf with identifier - S001 not found", result.getMessage());
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testUpdate_WhenShelfExists() {
        when(shelfRepository.findByIdentifier("S001")).thenReturn(shelf);
        ShelfDto result = shelfService.update(shelfDto);
        assertNotNull(result);
        verify(modelMapper).map(shelfDto, shelf);
        verify(shelfRepository).save(shelf);
    }

    @Test
    void testDelete() {
        when(shelfRepository.deleteByIdentifier("S001")).thenReturn(1L);
        shelfService.delete("S001");
        verify(shelfRepository).deleteByIdentifier("S001");
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shelf> page = new PageImpl<>(List.of(shelf));
        List<ShelfDto> dtoList = List.of(shelfDto);
        when(shelfRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(java.lang.reflect.Type.class))).thenReturn(dtoList);
        List<ShelfDto> result = shelfService.findAll(pageable);
        assertEquals(1, result.size());
        verify(shelfRepository).findAll(pageable);
    }

    @Test
    void testFindAllActive() {
        when(shelfRepository.findByStatusTrue()).thenReturn(List.of(shelf));
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);
        List<ShelfDto> result = shelfService.findAllActive();
        assertEquals(1, result.size());
        assertEquals("S001", result.get(0).getIdentifier());
        verify(shelfRepository).findByStatusTrue();
    }

    @Test
    void testToggleStatus_FromTrueToFalse() {
        shelf.setStatus(true);
        when(shelfRepository.findByIdentifier("S001")).thenReturn(shelf);
        shelfService.toggleStatus("S001");
        assertFalse(shelf.getStatus());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void testToggleStatus_FromFalseToTrue() {
        shelf.setStatus(false);
        when(shelfRepository.findByIdentifier("S001")).thenReturn(shelf);
        shelfService.toggleStatus("S001");
        assertTrue(shelf.getStatus());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void testToggleStatus_WhenShelfNotFound() {
        when(shelfRepository.findByIdentifier("S001")).thenReturn(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> shelfService.toggleStatus("S001"));
        assertEquals("Shelf not found", exception.getMessage());
        verify(shelfRepository, never()).save(any());
    }
}