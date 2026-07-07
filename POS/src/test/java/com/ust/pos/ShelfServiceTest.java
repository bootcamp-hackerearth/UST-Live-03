package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ShelfServiceImpl shelfService;

    private Shelf shelf;
    private ShelfDto shelfDto;

    @BeforeEach
    void setUp() {
        shelf = new Shelf();
        shelf.setId(1L);
        shelf.setIdentifier("SHF-001");
        shelf.setStatus(true);
        shelf.setDeleted(false);

        shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHF-001");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);

        ShelfDto result = shelfService.findByIdentifier("SHF-001");

        assertNotNull(result);
        assertEquals("SHF-001", result.getIdentifier());
        verify(shelfRepository, times(1)).findByIdentifier("SHF-001");
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> shelfService.findByIdentifier("SHF-001"));
        verify(shelfRepository, times(1)).findByIdentifier("SHF-001");
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> shelfService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        shelfDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> shelfService.save(shelfDto));
    }

    @Test
    void testSave_WhenShelfAlreadyExistsAndNotDeleted() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void testSave_WhenShelfAlreadyExistsButDeleted() {
        shelf.setDeleted(true);
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void testSave_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Shelf created successfully", result.getMessage());
        verify(shelfRepository, times(1)).save(any(Shelf.class));
    }

    @Test
    void testUpdate_WhenShelfNotFound() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void testUpdate_WhenShelfDeleted() {
        shelf.setDeleted(true);
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void testUpdate_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelf);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Shelf updated successfully", result.getMessage());
        verify(shelfRepository, times(1)).save(any(Shelf.class));
    }

    @Test
    void testDelete_WhenShelfNotFound() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);

        shelfService.delete("SHF-001");

        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void testDelete_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelf);

        shelfService.delete("SHF-001");

        verify(shelfRepository, times(1)).save(any(Shelf.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shelf> page = new PageImpl<>(Collections.singletonList(shelf), pageable, 1);
        when(shelfRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<ShelfDto> result = shelfService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        verify(shelfRepository, times(1)).findByDeletedFalse(pageable);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shelf> page = new PageImpl<>(Collections.singletonList(shelf), pageable, 1);
        Specification<Shelf> spec = mock(Specification.class);
        when(shelfRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<ShelfDto> result = shelfService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        verify(shelfRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void testToggleStatus_WhenShelfNotFound() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);

        ShelfDto result = shelfService.toggleStatus("SHF-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void testToggleStatus_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelf);

        ShelfDto result = shelfService.toggleStatus("SHF-001");

        assertNotNull(result);
        assertFalse(result.isStatus());
        verify(shelfRepository, times(1)).save(any(Shelf.class));
    }

    @Test
    void testFindIfTrue() {
        when(shelfRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(shelf));

        List<ShelfDto> result = shelfService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SHF-001", result.get(0).getIdentifier());
        verify(shelfRepository, times(1)).findByStatusIsTrueAndDeletedFalse();
    }
}