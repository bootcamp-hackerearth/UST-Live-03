package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Test
    @DisplayName("Save Shelf - Success")
    void saveTest_Success() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH-01");
        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("SH-01")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Shelf.class)).thenReturn(shelf);

        ShelfDto result = shelfService.save(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    @DisplayName("Save Shelf - Failure: Already Exists")
    void saveTest_AlreadyExists() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH-01");
        Mockito.when(shelfRepository.findByIdentifier("SH-01")).thenReturn(new Shelf());

        ShelfDto result = shelfService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Shelf with identifier - SH-01 already exists", result.getMessage());

        Mockito.verify(shelfRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Update Shelf - Success")
    void updateTest_Success() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH-01");
        Shelf existingShelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("SH-01")).thenReturn(existingShelf);

        ShelfDto result = shelfService.update(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(modelMapper).map(dto, existingShelf);
        Mockito.verify(shelfRepository).save(existingShelf);
    }

    @Test
    @DisplayName("Update Shelf - Failure: Not Found")
    void updateTest_NotFound() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH-01");
        Mockito.when(shelfRepository.findByIdentifier("SH-01")).thenReturn(null);

        ShelfDto result = shelfService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Shelf with identifier - SH-01 not found", result.getMessage());
        Mockito.verify(shelfRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Find All - Paginated Success")
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Shelf> shelves = List.of(new Shelf());
        Page<Shelf> shelfPage = new PageImpl<>(shelves);
        List<ShelfDto> dtos = List.of(new ShelfDto());

        Mockito.when(shelfRepository.findAll(pageable)).thenReturn(shelfPage);
        Mockito.when(modelMapper.map(eq(shelves), any(Type.class))).thenReturn(dtos);

        List<ShelfDto> result = shelfService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
        Mockito.verify(shelfRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find All Active - Success")
    void findAllActiveTest() {
        List<Shelf> shelves = List.of(new Shelf());
        List<ShelfDto> dtos = List.of(new ShelfDto());

        Mockito.when(shelfRepository.findAllByStatus(true)).thenReturn(shelves);
        Mockito.when(modelMapper.map(eq(shelves), any(Type.class))).thenReturn(dtos);

        List<ShelfDto> result = shelfService.findAllActive();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();
        Mockito.when(shelfRepository.findByIdentifier("SH-01")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);

        ShelfDto result = shelfService.findByIdentifier("SH-01");
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Toggle Status - Logic Flip")
    void toggleStatusTest() {
        Shelf shelf = new Shelf();
        shelf.setStatus(true);
        ShelfDto dto = new ShelfDto();

        Mockito.when(shelfRepository.findByIdentifier("SH-01")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);

        ShelfDto result = shelfService.toggleStatus("SH-01");

        Assertions.assertFalse(shelf.isStatus());
        Mockito.verify(shelfRepository).save(shelf);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Delete Shelf - Success")
    void deleteTest() {
        boolean result = shelfService.delete("SH-01");
        Assertions.assertTrue(result);
        Mockito.verify(shelfRepository).deleteByIdentifier("SH-01");
    }
}