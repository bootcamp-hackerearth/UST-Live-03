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
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Test
    void saveTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Shelf.class)).thenReturn(new Shelf());

        ShelfDto response = shelfService.save(dto);

        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(new Shelf());

        ShelfDto response = shelfService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);

        ShelfDto response = shelfService.findByIdentifier("S1");

        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);

        ShelfDto response = shelfService.update(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(null);

        ShelfDto response = shelfService.update(dto);

        Assertions.assertFalse(response.isSuccess());
    }


    @Test
    void deleteTest() {
        Mockito.doNothing().when(shelfRepository).deleteByIdentifier("S1");

        shelfService.delete("S1");

        Mockito.verify(shelfRepository).deleteByIdentifier("S1");
    }

    @Test
    void findAllTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        List<Shelf> shelfs = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);

        Page<Shelf> shelfPage = new PageImpl<>(shelfs, PageRequest.of(0, 2), shelfs.size());

        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));

        Mockito.when(shelfRepository.findAll(pageable)).thenReturn(shelfPage);
        Mockito.when(modelMapper.map(Mockito.eq(shelfs), Mockito.any(java.lang.reflect.Type.class))).thenReturn(shelfDtos);

        List<ShelfDto> response = shelfService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleActive() {
        Shelf shelf = new Shelf();
        shelf.setStatus(true);

        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(new ShelfDto());

        shelfService.toggleStatus("S1");

        Assertions.assertFalse(shelf.isStatus());
    }

    @Test
    void toggleInactive() {
        Shelf shelf = new Shelf();
        shelf.setStatus(false);

        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(new ShelfDto());

        shelfService.toggleStatus("S1");

        Assertions.assertTrue(shelf.isStatus());
    }

    @Test
    void findByStatusTest() {
        List<Shelf> shelves = List.of(new Shelf());
        List<ShelfDto> shelfDtos = List.of(new ShelfDto());

        Mockito.when(shelfRepository.findByStatusIsTrue()).thenReturn(shelves);
        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(java.lang.reflect.Type.class))).thenReturn(shelfDtos);

        List<ShelfDto> response = shelfService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }
}
