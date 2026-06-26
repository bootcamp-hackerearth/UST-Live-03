package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl service;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Shelf> page = new PageImpl<>(List.of(new Shelf()), pageable, 1);

        when(shelfRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new ShelfDto()));

        WsDto<ShelfDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();

        when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);

        ShelfDto result = service.findByIdentifier("S1");

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        when(modelMapper.map(dto, Shelf.class)).thenReturn(new Shelf());

        ShelfDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(shelfRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf existing = new Shelf();
        existing.setDeleted(false);

        when(shelfRepository.findByIdentifier("S1")).thenReturn(existing);

        ShelfDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf existing = new Shelf();
        existing.setDeleted(true);

        when(shelfRepository.findByIdentifier("S1")).thenReturn(existing);

        ShelfDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf existing = new Shelf();

        when(shelfRepository.findByIdentifier("S1")).thenReturn(existing);

        ShelfDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(shelfRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        when(shelfRepository.findByIdentifier("S1")).thenReturn(null);

        ShelfDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Shelf shelf = new Shelf();
        shelf.setDeleted(false);

        when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);

        service.delete("S1");

        assertTrue(shelf.isDeleted());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Shelf shelf = new Shelf();
        shelf.setStatus(true);

        when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);

        service.toggleStatus("S1");

        assertFalse(shelf.isStatus());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Shelf shelf = new Shelf();
        shelf.setStatus(false);

        when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);

        service.toggleStatus("S1");

        assertTrue(shelf.isStatus());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(shelfRepository.findByIdentifier("S1")).thenReturn(null);

        service.toggleStatus("S1");

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void findActiveStatusTest() {
        when(shelfRepository.findByStatusTrue()).thenReturn(List.of(new Shelf()));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new ShelfDto()));

        List<ShelfDto> result = service.findActiveStatus();

        assertEquals(1, result.size());
    }

    @Test
    void findActiveShelfTest() {
        Shelf shelf = new Shelf();

        when(shelfRepository.findByStatusTrue()).thenReturn(List.of(shelf));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new ShelfDto()));

        List<ShelfDto> result = service.findActiveShelf();

        assertEquals(1, result.size());
    }
}