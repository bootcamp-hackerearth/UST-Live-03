package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.Assertions;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void createShelfTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.existsByIdentifier("S1")).thenReturn(false);

        Mockito.when(modelMapper.map(dto, Shelf.class)).thenReturn(shelf);

        ShelfDto response = shelfService.createShelf(dto);

        Assertions.assertEquals("S1", response.getIdentifier());

        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void createShelfDuplicateTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.existsByIdentifier("S1")).thenReturn(true);

        ShelfDto response = shelfService.createShelf(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Shelf already exists", response.getMessage());
    }

    @Test
    void updateShelfTest() {

        Shelf shelf = new Shelf();
        shelf.setId(1L);
        shelf.setIdentifier("OLD");

        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");

        Mockito.when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));

        ShelfDto response = shelfService.updateShelf(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void updateShelfNotFoundTest() {

        ShelfDto dto = new ShelfDto();
        dto.setId(1L);

        Mockito.when(shelfRepository.findById(1L)).thenReturn(Optional.empty());

        ShelfDto response = shelfService.updateShelf(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(ShelfServiceImpl.SHELF_NOT_FOUND, response.getMessage());
    }

    @Test
    void getShelfTest() {

        Shelf shelf = new Shelf();
        shelf.setId(1L);

        Mockito.when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));

        ShelfDto response = shelfService.getShelf(1L);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void getShelfNotFoundTest() {

        Mockito.when(shelfRepository.findById(1L)).thenReturn(Optional.empty());

        ShelfDto response = shelfService.getShelf(1L);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(ShelfServiceImpl.SHELF_NOT_FOUND, response.getMessage());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> dtos = List.of(dto);

        Page<Shelf> shelfPage = new PageImpl<>(shelves);

        Mockito.when(shelfRepository.findAll(pageable)).thenReturn(shelfPage);

        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(Type.class))).thenReturn(dtos);

        List<ShelfDto> response = shelfService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteShelfTest() {

        Mockito.doNothing().when(shelfRepository).deleteById(1L);

        boolean response = shelfService.deleteShelf(1L);

        Assertions.assertTrue(response);

        Mockito.verify(shelfRepository).deleteById(1L);
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Shelf shelf = new Shelf();
        shelf.setId(1L);
        shelf.setActive(true);

        Mockito.when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));

        ShelfDto response = shelfService.toggleStatus(1L);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertFalse(shelf.isActive());

        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Shelf shelf = new Shelf();
        shelf.setId(1L);
        shelf.setActive(false);

        Mockito.when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));

        ShelfDto response = shelfService.toggleStatus(1L);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertTrue(shelf.isActive());

        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusNotFoundTest() {

        Mockito.when(shelfRepository.findById(1L)).thenReturn(Optional.empty());

        ShelfDto response = shelfService.toggleStatus(1L);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(ShelfServiceImpl.SHELF_NOT_FOUND, response.getMessage());
    }

    @Test
    void getActiveShelvesTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        List<Shelf> shelves = List.of(shelf);

        Mockito.when(shelfRepository.findByActiveTrue()).thenReturn(shelves);

        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);

        List<ShelfDto> response = shelfService.getActiveShelves();

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("S1", response.get(0).getIdentifier());
    }
}