package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShelfServiceImpl shelfService;

    private ShelfDto shelfDto;
    private Shelf shelf;

    @BeforeEach
    void setUp() {
        shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHF-001");

        shelf = new Shelf();
        shelf.setIdentifier("SHF-001");
        shelf.setStatus(true);
        shelf.setDeleted(false);
    }

    @Test
    @DisplayName("Save Shelf - Success")
    void save_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);
        when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Shelf created successfully", result.getMessage());
        verify(shelfRepository).save(shelf);
    }

    @Test
    @DisplayName("Save Shelf - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        shelf.setDeleted(false);
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    @DisplayName("Save Shelf - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        shelf.setDeleted(true);
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    @DisplayName("Find All Shelves - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shelf> shelfPage = new PageImpl<>(List.of(shelf));

        when(shelfRepository.findByDeletedFalse(pageable)).thenReturn(shelfPage);
        when(modelMapper.map(eq(shelfPage.getContent()), any(Type.class))).thenReturn(List.of(shelfDto));

        WsDto<ShelfDto> result = shelfService.findAll(pageable);

        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    @DisplayName("Find All Active Shelves - Success")
    void findAllActive_Success() {
        List<Shelf> activeShelves = List.of(shelf);
        when(shelfRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activeShelves);
        when(modelMapper.map(eq(activeShelves), any(Type.class))).thenReturn(List.of(shelfDto));

        List<ShelfDto> result = shelfService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);

        ShelfDto result = shelfService.findByIdentifier("SHF-001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Update Shelf - Success")
    void update_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);

        ShelfDto result = shelfService.update(shelfDto);

        Assertions.assertNotNull(result);
        verify(shelfRepository).save(shelf);
    }

    @Test
    @DisplayName("Update Shelf - Failure: Not Found")
    void update_Failure_NotFound() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);

        ShelfDto result = shelfService.update(shelfDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);

        ShelfDto result = shelfService.toggleStatus("SHF-001");

        Assertions.assertFalse(shelf.isStatus());
        verify(shelfRepository).save(shelf);
    }

    @Test
    @DisplayName("Delete Shelf - Success")
    void delete_Success() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(shelf);

        boolean result = shelfService.delete("SHF-001");

        Assertions.assertTrue(result);
        verify(shelfRepository).save(shelf);
    }

    @Test
    @DisplayName("Delete Shelf - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(shelfRepository.findByIdentifier("SHF-001")).thenReturn(null);

        boolean result = shelfService.delete("SHF-001");

        Assertions.assertFalse(result);
        verify(shelfRepository, never()).save(any(Shelf.class));
    }
}