package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Shelf;
import com.ust.pos.modell.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_success() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        Shelf entity = new Shelf();
        when(modelMapper.map(dto, Shelf.class)).thenReturn(entity);
        ShelfDto result = shelfService.save(dto);
        verify(shelfRepository).save(entity);
        assertEquals("S1", result.getIdentifier());
    }

    @Test
    void save_failure_alreadyExists() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        when(shelfRepository.findByIdentifier("S1")).thenReturn(new Shelf());
        ShelfDto result = shelfService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Shelf with identifier - S1 already exists", result.getMessage());
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void save_failure_deleted() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Shelf deleted = new Shelf();
        deleted.setDeleted(true);
        when(shelfRepository.findByIdentifier("S1")).thenReturn(deleted);
        ShelfDto result = shelfService.save(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("was deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Shelf entity = new Shelf();
        entity.setIdentifier("S1");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(entity);
        when(modelMapper.map(entity, ShelfDto.class)).thenReturn(dto);
        ShelfDto response = shelfService.findByIdentifier("S1");
        assertEquals("S1", response.getIdentifier());
    }

    @Test
    void update_success() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Shelf existing = new Shelf();
        existing.setIdentifier("S1");
        when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(existing);
        ShelfDto result = shelfService.update(dto);
        verify(shelfRepository).save(existing);
        assertNull(result.getMessage());
    }

    @Test
    void update_failure_notFound() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        ShelfDto result = shelfService.update(dto);
        assertFalse(result.isSuccess());
        assertEquals("Shelf with identifier - S1 not found", result.getMessage());
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Shelf shelf = new Shelf();
        when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(shelf);
        shelfService.delete("S1");
        verify(shelfRepository).save(shelf);
    }

    @Test
    void findAllTest() {
        Shelf shelf = new Shelf();
        List<Shelf> list = List.of(shelf);
        Pageable pageable = PageRequest.of(0, 50);
        Page<Shelf> page = new PageImpl<>(list, pageable, 1);
        when(shelfRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(list), any(java.lang.reflect.Type.class))).thenReturn(List.of(new ShelfDto()));
        WsDto<ShelfDto> result = shelfService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findAllActiveTest() {
        Shelf shelf = new Shelf();
        when(shelfRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(shelf));
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(new ShelfDto());
        List<ShelfDto> result = shelfService.findAllActive();
        assertEquals(1, result.size());
    }

    @Test
    void toggleStatus_trueToFalse() {
        Shelf shelf = new Shelf();
        shelf.setStatus(true);
        ShelfDto dto = new ShelfDto();
        when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(shelf);
        when(shelfRepository.save(shelf)).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto result = shelfService.toggleStatus("S1");
        assertFalse(shelf.getStatus());
        assertNotNull(result);
    }

    @Test
    void toggleStatus_falseToTrue() {
        Shelf shelf = new Shelf();
        shelf.setStatus(false);
        ShelfDto dto = new ShelfDto();
        when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(shelf);
        when(shelfRepository.save(shelf)).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto result = shelfService.toggleStatus("S1");
        assertTrue(shelf.getStatus());
        assertNotNull(result);
    }

    @Test
    void toggleStatus_nullToTrue() {
        Shelf shelf = new Shelf();
        shelf.setStatus(null);
        ShelfDto dto = new ShelfDto();
        when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(shelf);
        when(shelfRepository.save(shelf)).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto result = shelfService.toggleStatus("S1");
        assertTrue(shelf.getStatus());
        assertNotNull(result);
    }

    @Test
    void toggleStatus_failure_notFound() {
        when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> shelfService.toggleStatus("S1"));
        assertTrue(ex.getMessage().contains("Shelf not found"));
        verify(shelfRepository, never()).save(any());
    }
}