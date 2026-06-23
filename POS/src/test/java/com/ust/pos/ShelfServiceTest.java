package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
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

    private Shelf shelfEntity;
    private ShelfDto shelfDto;

    @BeforeEach
    void setUp() {
        shelfEntity = new Shelf();
        shelfEntity.setId(1L);
        shelfEntity.setIdentifier("SHF-001");
        shelfEntity.setStatus(true);
        shelfEntity.setDeleted(false);

        shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHF-001");
    }

    @Test
    void testFindByIdentifier() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelfEntity);

        ShelfDto result = shelfService.findByIdentifier("SHF-001");

        assertNotNull(result);
        assertEquals("SHF-001", result.getIdentifier());
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
    void testSave_WhenShelfExistsAndNotDeleted() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelfEntity);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenShelfWasPreviouslyDeleted() {
        shelfEntity.setDeleted(true);
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelfEntity);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelfEntity);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Shelf created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenShelfNotFound() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenShelfIsDeleted() {
        shelfEntity.setDeleted(true);
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelfEntity);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelfEntity);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelfEntity);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Shelf updated successfully", result.getMessage());
    }

    @Test
    void testDelete_WhenShelfNotFound() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);

        shelfService.delete("SHF-001");

        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void testDelete_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelfEntity);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelfEntity);

        shelfService.delete("SHF-001");

        verify(shelfRepository, times(1)).save(shelfEntity);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Shelf> entityList = Collections.singletonList(shelfEntity);
        Page<Shelf> page = new PageImpl<>(entityList, pageable, 1);

        when(shelfRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<ShelfDto> result = shelfService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_WhenShelfNotFound() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);

        ShelfDto result = shelfService.toggleStatus("SHF-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        shelfEntity.setStatus(true);
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelfEntity);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelfEntity);

        ShelfDto result = shelfService.toggleStatus("SHF-001");

        assertNotNull(result);
        assertFalse(result.isStatus());
    }

    @Test
    void testFindIfTrue() {
        List<Shelf> activeShelves = Collections.singletonList(shelfEntity);
        when(shelfRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activeShelves);

        List<ShelfDto> result = shelfService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}