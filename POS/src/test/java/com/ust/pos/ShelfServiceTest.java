package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
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

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF001");
        Shelf shelf = new Shelf();
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(null);
        Mockito.when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("SHELF001", response.getIdentifier());
    }

    @Test
    void saveDuplicateTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF001");
        Shelf existingShelf = new Shelf();
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(existingShelf);
        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveSoftDeletedShelfTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF001");
        Shelf existingShelf = new Shelf();
        existingShelf.setDeleted(true);
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(existingShelf);
        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF001");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF001");
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto response = shelfService.findByIdentifier("SHELF001");
        Assertions.assertEquals("SHELF001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF001");
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF001");
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        ShelfDto response = shelfService.update(shelfDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateFailureTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF001");
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(null);
        ShelfDto response = shelfService.update(shelfDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF001");
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(shelf);
        shelfService.delete("SHELF001");
        Assertions.assertTrue(shelf.isDeleted());
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(null);
        Assertions.assertThrows(RuntimeException.class, () -> shelfService.delete("SHELF001"));
    }

    @Test
    void findAllWithPageableTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF001");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF001");
        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Shelf> page = new PageImpl<>(shelves);
        Mockito.when(shelfRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<ShelfDto> response = shelfService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findAllWithoutPageableTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF001");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF001");
        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> dtos = List.of(dto);
        Mockito.when(shelfRepository.findAll()).thenReturn(shelves);
        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<ShelfDto> response = shelfService.findAll(null);
        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void toggleStatusSuccessTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF001");
        shelf.setStatus(false);
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF001");
        dto.setStatus(true);
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto response = shelfService.toggleStatus("SHELF001", true);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(shelf.isStatus());
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusShelfNotFoundTest() {
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(null);
        Mockito.when(modelMapper.map(null, ShelfDto.class)).thenReturn(null);
        ShelfDto response = shelfService.toggleStatus("SHELF001", true);
        Assertions.assertNull(response);
    }

    @Test
    void findActiveShelvesTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF001");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF001");
        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> dtos = List.of(dto);
        Mockito.when(shelfRepository.findByStatusTrue()).thenReturn(shelves);
        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(Type.class))).thenReturn(dtos);
        List<ShelfDto> response = shelfService.findActiveShelves();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("SHELF001", response.get(0).getIdentifier());
    }
}