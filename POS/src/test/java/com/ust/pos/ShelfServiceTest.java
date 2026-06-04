package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_success() {
        ShelfDto dto = new ShelfDto();
        dto.setName("Shelf1");
        Shelf shelf = new Shelf();
        ShelfDto responseDto = new ShelfDto();
        when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(null);
        when(modelMapper.map(dto, Shelf.class)).thenReturn(shelf);
        when(shelfRepository.save(shelf)).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(responseDto);
        ShelfDto response = shelfService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Shelf saved successfully", response.getMessage());
    }

    @Test
    void save_failure_empty_name() {
        ShelfDto dto = new ShelfDto();
        ShelfDto response = shelfService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf name is required", response.getMessage());
    }

    @Test
    void save_failure_exists() {
        ShelfDto dto = new ShelfDto();
        dto.setName("Shelf1");
        when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(new Shelf());
        ShelfDto response = shelfService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf already exists", response.getMessage());
    }

    @Test
    void update_success() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf1");
        dto.setStatus(true);
        Shelf shelf = new Shelf();
        ShelfDto mapped = new ShelfDto();
        when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(shelf);
        when(shelfRepository.save(shelf)).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(mapped);
        ShelfDto response = shelfService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Shelf updated successfully", response.getMessage());
    }

    @Test
    void update_failure() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf1");
        when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(null);
        ShelfDto response = shelfService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf not found", response.getMessage());
    }

    @Test
    void find_success() {
        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();
        when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto response = shelfService.findByIdentifier("Shelf1");
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void find_failure() {
        when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(null);
        ShelfDto response = shelfService.findByIdentifier("Shelf1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf not found", response.getMessage());
    }

    @Test
    void findAll_test() {
        Shelf shelf = new Shelf();
        List<Shelf> list = List.of(shelf);
        Page<Shelf> page = new PageImpl<>(list);
        List<ShelfDto> mappedList = List.of(new ShelfDto());
        when(shelfRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(eq(list), ArgumentMatchers.<Type>any()))
                .thenReturn(mappedList);
        List<ShelfDto> result = shelfService.findAll(Pageable.unpaged());
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getActiveShelves_test() {
        List<Shelf> list = List.of(new Shelf());
        List<ShelfDto> mappedList = List.of(new ShelfDto());
        when(shelfRepository.findByStatusTrue()).thenReturn(list);
        when(modelMapper.map(eq(list), ArgumentMatchers.<Type>any()))
                .thenReturn(mappedList);
        List<ShelfDto> result = shelfService.getActiveShelves();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void delete_test() {
        shelfService.delete("Shelf1");
        verify(shelfRepository).deleteByIdentifier("Shelf1");
    }

    @Test
    void toggle_success() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");
        shelf.setStatus(true);
        ShelfDto dto = new ShelfDto();
        when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(shelf);
        when(shelfRepository.save(shelf)).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto response = shelfService.toggleStatus("Shelf1");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void toggle_failure() {
        when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(null);
        ShelfDto response = shelfService.toggleStatus("Shelf1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf not found", response.getMessage());
    }
}