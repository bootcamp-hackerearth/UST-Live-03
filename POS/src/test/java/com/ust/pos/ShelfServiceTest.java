package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ShelfRepository shelfRepository;

    @Test
    void saveSuccessTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF1");

        Shelf shelf = new Shelf();

        when(shelfRepository.findByIdentifier("SHELF1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Shelf.class))
                .thenReturn(shelf);

        ShelfDto result = shelfService.save(dto);

        Assertions.assertEquals("SHELF1", result.getIdentifier());

        verify(shelfRepository).save(shelf);
    }

    @Test
    void saveAlreadyExistsTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF1");

        Shelf shelf = new Shelf();
        shelf.setDeleted(false);

        when(shelfRepository.findByIdentifier("SHELF1"))
                .thenReturn(shelf);

        ShelfDto result = shelfService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Shelf already exists",
                result.getMessage()
        );

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void saveDeletedShelfTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF1");

        Shelf shelf = new Shelf();
        shelf.setDeleted(true);

        when(shelfRepository.findByIdentifier("SHELF1"))
                .thenReturn(shelf);

        ShelfDto result = shelfService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Shelf with identifier - SHELF1 was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void updateShelfNotFoundTest() {
        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("SHELF1");

        when(shelfRepository.findById(1L))
                .thenReturn(Optional.empty());

        ShelfDto result = shelfService.update(dto);

        Assertions.assertFalse(result.isSuccess());

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void updateDuplicateIdentifierTest() {
        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("SHELF2");

        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("SHELF1");

        Shelf duplicateShelf = new Shelf();

        when(shelfRepository.findById(1L))
                .thenReturn(Optional.of(existingShelf));

        when(shelfRepository.findByIdentifier("SHELF2"))
                .thenReturn(duplicateShelf);

        ShelfDto result = shelfService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Shelf already exists",
                result.getMessage()
        );

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void updateSuccessSameIdentifierTest() {
        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("SHELF1");

        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("SHELF1");

        when(shelfRepository.findById(1L))
                .thenReturn(Optional.of(existingShelf));

        ShelfDto result = shelfService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        verify(modelMapper).map(dto, existingShelf);
        verify(shelfRepository).save(existingShelf);
    }

    @Test
    void updateSuccessDifferentIdentifierButUniqueTest() {
        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("SHELF2");

        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("SHELF1");

        when(shelfRepository.findById(1L))
                .thenReturn(Optional.of(existingShelf));

        when(shelfRepository.findByIdentifier("SHELF2"))
                .thenReturn(null);

        ShelfDto result = shelfService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        verify(modelMapper).map(dto, existingShelf);
        verify(shelfRepository).save(existingShelf);
    }

    @Test
    void findByIdentifierSuccessTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF1");

        when(shelfRepository.findByIdentifierAndIsDeletedFalse("SHELF1"))
                .thenReturn(shelf);

        when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        ShelfDto result = shelfService.findByIdentifier("SHELF1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("SHELF1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(shelfRepository.findByIdentifierAndIsDeletedFalse("SHELF1"))
                .thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> shelfService.findByIdentifier("SHELF1")
        );

        Assertions.assertEquals(
                "Shelf with identifier 'SHELF1' not found",
                exception.getMessage()
        );
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Shelf> shelves = List.of(
                new Shelf(),
                new Shelf()
        );

        Page<Shelf> page = new PageImpl<>(shelves, pageable, 2);

        List<ShelfDto> dtoList = List.of(
                new ShelfDto(),
                new ShelfDto()
        );

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();

        when(shelfRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(shelves, listType))
                .thenReturn(dtoList);

        WsDto<ShelfDto> result = shelfService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void deleteTest() {
        Shelf shelf = new Shelf();

        when(shelfRepository.findByIdentifier("SHELF1"))
                .thenReturn(shelf);

        shelfService.delete("SHELF1");

        verify(shelfRepository).findByIdentifier("SHELF1");
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Shelf shelf = new Shelf();
        shelf.setStatus(true);

        when(shelfRepository.findByIdentifier("SHELF1"))
                .thenReturn(shelf);

        shelfService.toggleStatus("SHELF1");

        Assertions.assertFalse(shelf.getStatus());

        verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Shelf shelf = new Shelf();
        shelf.setStatus(false);

        when(shelfRepository.findByIdentifier("SHELF1"))
                .thenReturn(shelf);

        shelfService.toggleStatus("SHELF1");

        Assertions.assertTrue(shelf.getStatus());

        verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusShelfNotFoundTest() {
        when(shelfRepository.findByIdentifier("SHELF1"))
                .thenReturn(null);

        shelfService.toggleStatus("SHELF1");

        verify(shelfRepository, never()).save(any());
    }
}