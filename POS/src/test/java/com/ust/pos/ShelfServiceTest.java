package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
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

import static org.junit.jupiter.api.Assertions.*;

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
        shelfDto.setIdentifier("Admin");
        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);

        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");
        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(shelf);

        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);

        ShelfDto response = shelfService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(existingShelf);
        Mockito.when(shelfRepository.save(existingShelf)).thenReturn(existingShelf);

        ShelfDto response = shelfService.update(shelfDto);
        assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(null);

        ShelfDto response = shelfService.update(shelfDto);
        assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(shelfRepository).deleteByIdentifier("Admin");
        shelfService.delete("Admin");
        Mockito.verify(shelfRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllWithPaginationTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("S1");
        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);
        Page<Shelf> shelfPage = new PageImpl<>(shelves, pageable, shelves.size());

        Mockito.when(shelfRepository.findAll(pageable)).thenReturn(shelfPage);
        Mockito.when(modelMapper.map(Mockito.eq(shelfPage.getContent()), Mockito.any(java.lang.reflect.Type.class))).thenReturn(shelfDtos);

        WsDto<ShelfDto> response = shelfService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("S1", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void toggleStatusTest_TrueToFalse() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("M1");
        shelf.setStatus(true);
        Mockito.when(shelfRepository.findByIdentifier("M1")).thenReturn(shelf);

        shelfService.toggleStatus("M1");
        assertFalse(shelf.isStatus());
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusTest_FalseToTrue() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("M1");
        shelf.setStatus(false);

        Mockito.when(shelfRepository.findByIdentifier("M1")).thenReturn(shelf);

        shelfService.toggleStatus("M1");
        assertTrue(shelf.isStatus());
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusTest_NotFound() {
        Mockito.when(shelfRepository.findByIdentifier("M1")).thenReturn(null);
        assertThrows(NullPointerException.class, () -> shelfService.toggleStatus("M1"));
    }

    @Test
    void findActiveShelfTest() {
        Shelf model = new Shelf();
        model.setIdentifier("M1");
        model.setStatus(true);
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("M1");
        List<Shelf> shelfList = List.of(model);
        List<ShelfDto> dtoList = List.of(dto);

        Mockito.when(shelfRepository.findByStatusTrue()).thenReturn(shelfList);
        Mockito.when(modelMapper.map(Mockito.eq(shelfList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(dtoList);

        List<ShelfDto> response = shelfService.findActiveShelves();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("M1", response.get(0).getIdentifier());
        Mockito.verify(shelfRepository).findByStatusTrue();
    }

}