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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Shelf.class)).thenReturn(shelf);

        ShelfDto response = shelfService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(shelfRepository).save(shelf);
    }

    @Test
    void saveTestFailure() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(new Shelf());
        ShelfDto response = shelfService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(shelf);
        ShelfDto response = shelfService.update(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(shelfRepository).save(shelf);
    }

    @Test
    void updateTestFailure() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        ShelfDto response = shelfService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        shelfService.delete("Admin");
        verify(shelfRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        ShelfDto response = shelfService.findByIdentifier("Admin");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ShelfDto response = shelfService.findByIdentifier("Admin");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Shelf> shelfs = List.of(new Shelf());
        Page<Shelf> page = new PageImpl<>(shelfs);

        List<ShelfDto> dtos = List.of(new ShelfDto());

        when(shelfRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(
                eq(shelfs),
                any(Type.class)
        )).thenReturn(dtos);

        List<ShelfDto> result = shelfService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(shelfRepository).findAll(pageable);
    }

    @Test
    void toggleStatus_falseToTrue() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        shelf.setStatus(false);

        when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(shelf);
        shelfService.toggleStatus("Admin");

        Assertions.assertTrue(shelf.isStatus());

        verify(shelfRepository).save(argThat(saved ->
                saved.isStatus()
        ));
    }

    @Test
    void toggleStatusFailureTest() {

        when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        shelfService.toggleStatus("Admin");

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void toggleStatus_trueToFalse() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        shelf.setStatus(true);

        when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(shelf);
        shelfService.toggleStatus("Admin");

        Assertions.assertFalse(shelf.isStatus());
        verify(shelfRepository).save(argThat(saved ->
                !saved.isStatus()
        ));
    }

    @Test
    void findActiveShelfTest() {

        List<Shelf> shelfList = List.of(new Shelf());
        Mockito.when(shelfRepository.findByStatus(true))
                .thenReturn(shelfList);

        List<Shelf> response = shelfService.findActiveShelf();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
    }
}