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
        shelfDto.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        Shelf shelf = new Shelf();
        Mockito.when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("S1");
        Shelf existingShelf = new Shelf();
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(existingShelf);
        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);
        ShelfDto response = shelfService.findByIdentifier("S1");
        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("S1");
        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1"))
                .thenReturn(existingShelf);
        Mockito.when(shelfRepository.save(existingShelf))
                .thenReturn(existingShelf);
        ShelfDto response = shelfService.update(shelfDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1"))
                .thenReturn(null);
        ShelfDto response = shelfService.update(shelfDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(shelfRepository)
                .deleteByIdentifier("S1");
        shelfService.delete("S1");
        Mockito.verify(shelfRepository).deleteByIdentifier("S1");
    }

    @Test
    void findAllTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SH1");
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SH1");
        List<Shelf> shelfList = List.of(shelf);
        List<ShelfDto> shelfDtoList = List.of(shelfDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shelf> shelfPage = new PageImpl<>(shelfList);
        Mockito.when(shelfRepository.findAll(pageable))
                .thenReturn(shelfPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelfList),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(shelfDtoList);
        WsDto<ShelfDto> response = shelfService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("SH1", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void updateStatusTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1"))
                .thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf))
                .thenReturn(shelf);
        shelfService.updateStatus("S1", true);
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void findAllActiveTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("S1");
        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);
        Mockito.when(shelfRepository.findByStatus(true)).thenReturn(shelves);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(shelfDtos);
        List<ShelfDto> response = shelfService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}