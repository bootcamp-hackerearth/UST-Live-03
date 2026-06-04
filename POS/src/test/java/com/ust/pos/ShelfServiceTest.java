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
        shelfDto.setIdentifier("Shelf_001");

        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(null);
        Shelf shelf = new Shelf();
        Mockito.when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertEquals("Shelf_001", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf_001");
        Shelf shelf = new Shelf();
        
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(shelf);
        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertEquals("Shelf_001", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf_001");
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf_001");
        
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);
        ShelfDto response = shelfService.findByIdentifier("Shelf_001");
        Assertions.assertEquals("Shelf_001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf_001");
        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("Shelf_001");
        
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(existingShelf);
        Mockito.when(shelfRepository.save(existingShelf)).thenReturn(existingShelf);
        ShelfDto response = shelfService.update(shelfDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf_001");
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(null);
        ShelfDto response = shelfService.update(shelfDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(shelfRepository).deleteByIdentifier("Shelf_001");
        shelfService.delete("Shelf_001");
        Mockito.verify(shelfRepository).deleteByIdentifier("Shelf_001");
    }

    @Test
    void findAllTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf_001");
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf_001");

        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shelf> shelfPage = new PageImpl<>(shelves, pageable, shelves.size());

        Mockito.when(shelfRepository.findAll(pageable)).thenReturn(shelfPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(shelfDtos);
        List<ShelfDto> response = shelfService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Shelf_001", response.get(0).getIdentifier());
    }

    @Test
    void toggleStatusSuccessTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf_001");
        shelf.setStatus(false); 
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf_001");
        shelfDto.setStatus(true);
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf,ShelfDto.class)).thenReturn(shelfDto);
        ShelfDto response = shelfService.toggleStatus("Shelf_001", true);
        Assertions.assertEquals("Shelf_001", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void changeRoleStatusFailureTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf_001");
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(null);
        ShelfDto response = shelfService.toggleStatus("Shelf_001", true);
        Assertions.assertNull(response);
        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveShelfsTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF_01");
        shelf.setStatus(true);
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF_01");
        shelfDto.setStatus(true);

        List<Shelf> activeShelves = List.of(shelf);
        List<ShelfDto> activeShelfDtos = List.of(shelfDto);
        Mockito.when(shelfRepository.findByStatusTrue()).thenReturn(activeShelves);
        Mockito.when(modelMapper.map(
                Mockito.eq(activeShelves),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(activeShelfDtos);
        List<ShelfDto> response = shelfService.findActiveShelves();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("SHELF_01", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }
}