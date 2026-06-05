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

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    ShelfServiceImpl shelfService;

    @Mock
    ShelfRepository shelfRepository;

    @Mock
    ModelMapper modelMapper;

    @Test
    void saveSuccessTest_WhenIdIsNull() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setId(null);
        shelfDto.setIdentifier("SHELF001");
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF001");
        Mockito.when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
        Mockito.verify(modelMapper).map(shelfDto, Shelf.class);
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void saveFailureTest_WhenIdIsNotNull() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setId(1L);
        shelfDto.setIdentifier("SHELF001");
        ShelfDto response = shelfService.save(shelfDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Invalid request: ID must be null while creating shelf", response.getMessage());
        Mockito.verifyNoInteractions(modelMapper);
        Mockito.verifyNoInteractions(shelfRepository);
    }

    @Test
    void findAllWithPageableTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf1");
        List<Shelf> shelfs = List.of(shelf);
        List<ShelfDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Shelf> shelfPage = new PageImpl<>(shelfs);
        Mockito.when(shelfRepository.findAll(pageable)).thenReturn(shelfPage);
        Mockito.when(modelMapper.map(Mockito.eq(shelfs), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<ShelfDto> response = shelfService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Shelf1", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf1");
        List<Shelf> shelfs = List.of(shelf);
        List<ShelfDto> dtos = List.of(dto);
        Mockito.when(shelfRepository.findAll()).thenReturn(shelfs);
        Mockito.when(modelMapper.map(Mockito.eq(shelfs), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<ShelfDto> response = shelfService.findAll(null);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void updateTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");
        Mockito.when(shelfRepository.findByIdentifier(shelfDto.getIdentifier())).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        ShelfDto response = shelfService.update(shelfDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");
        Mockito.when(shelfRepository.findByIdentifier(shelfDto.getIdentifier())).thenReturn(null);
        ShelfDto response = shelfService.update(shelfDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");
        Mockito.when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);
        ShelfDto response = shelfService.findByIdentifier("Shelf1");
        Assertions.assertEquals("Shelf1", response.getIdentifier());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(shelfRepository).deleteByIdentifier("Shelf1");
        shelfService.delete("Shelf1");
        Mockito.verify(shelfRepository).deleteByIdentifier("Shelf1");
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
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto response = shelfService.toggleStatus("SHELF001", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("SHELF001", response.getIdentifier());
        Assertions.assertTrue(shelf.isStatus());
        Assertions.assertTrue(response.isStatus());
        Mockito.verify(shelfRepository).findByIdentifier("SHELF001");
        Mockito.verify(shelfRepository).save(shelf);
        Mockito.verify(modelMapper).map(shelf, ShelfDto.class);
    }

    @Test
    void toggleStatusShelfNotFoundTest() {
        Mockito.when(shelfRepository.findByIdentifier("SHELF001")).thenReturn(null);
        Mockito.when(modelMapper.map(null, ShelfDto.class)).thenReturn(null);
        ShelfDto response = shelfService.toggleStatus("SHELF001", true);
        Assertions.assertNull(response);
        Mockito.verify(shelfRepository).findByIdentifier("SHELF001");
        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(modelMapper).map(null, ShelfDto.class);
    }

    @Test
    void findAllActiveTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");
        List<ShelfDto> shelfDtos = List.of(shelfDto);
        List<Shelf> shelfs = List.of(shelf);
        Mockito.when(shelfRepository.findByStatusTrue()).thenReturn(shelfs);
        Mockito.when(modelMapper.map(Mockito.eq(shelfs), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(shelfDtos);
        List<ShelfDto> response = shelfService.findActiveShelves();
        Assertions.assertEquals(1, response.size());
    }
}