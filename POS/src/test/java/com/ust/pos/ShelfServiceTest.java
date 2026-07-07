package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.domain.*;
import com.ust.pos.exception.ResourceNotFoundException;
import static org.junit.jupiter.api.Assertions.*;


import java.lang.reflect.Type;
import java.util.List;

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
    void save_success() {

        ShelfDto dto = new ShelfDto();
        dto.setName("Shelf1");

        Shelf shelf = new Shelf();
        ShelfDto responseDto = new ShelfDto();
        responseDto.setSuccess(true);
        responseDto.setMessage("Shelf saved successfully");

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Shelf.class))
                .thenReturn(shelf);

        when(shelfRepository.save(shelf))
                .thenReturn(shelf);

        when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(responseDto);

        ShelfDto response = shelfService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Shelf saved successfully", response.getMessage());

        verify(shelfRepository).save(shelf);
    }

    @Test
    void save_failure_empty_name() {

        ShelfDto dto = new ShelfDto();

        ShelfDto response = shelfService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf name is required", response.getMessage());

        verifyNoInteractions(shelfRepository);
    }

    @Test
    void save_failure_exists() {

        ShelfDto dto = new ShelfDto();
        dto.setName("Shelf1");

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(new Shelf());

        ShelfDto response = shelfService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf already exists", response.getMessage());

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void update_success() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf1");
        dto.setName("Shelf2");

        Shelf shelf = new Shelf();
        ShelfDto mapped = new ShelfDto();
        mapped.setSuccess(true);
        mapped.setMessage("Shelf updated successfully");

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(shelf);

        when(shelfRepository.save(shelf))
                .thenReturn(shelf);

        when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(mapped);

        ShelfDto response = shelfService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Shelf updated successfully", response.getMessage());

        verify(shelfRepository).save(shelf);
    }

    @Test
    void update_failure() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf1");

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> shelfService.update(dto)
        );

        assertEquals(
                "Shelf with identifier 'Shelf1' not found",
                ex.getMessage()
        );

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void find_success() {

        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();
        dto.setSuccess(true);

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(shelf);

        when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        ShelfDto response = shelfService.findByIdentifier("Shelf1");

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void find_failure() {

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> shelfService.findByIdentifier("Shelf1")
        );

        assertEquals(
                "Shelf with identifier 'Shelf1' not found",
                ex.getMessage()
        );
    }

    @Test
    void findAll_test() {

        Shelf shelf = new Shelf();
        List<Shelf> list = List.of(shelf);
        Page<Shelf> page = new PageImpl<>(list);

        List<ShelfDto> mappedList = List.of(new ShelfDto());

        when(shelfRepository.findByDeletedFalse(any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(eq(list), ArgumentMatchers.<Type>any()))
                .thenReturn(mappedList);

        WsDto<ShelfDto> result =
                shelfService.findAll(PageRequest.of(0, 5));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1L, result.getTotalRecords());
    }

    @Test
    void getActiveShelves_test() {

        Shelf shelf = new Shelf();

        when(shelfRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(shelf));

        when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(new ShelfDto());

        List<ShelfDto> result = shelfService.getActiveShelves();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void delete_test() {

        Shelf shelf = new Shelf();

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(shelf);

        shelfService.delete("Shelf1");

        verify(shelfRepository).save(shelf);
    }

    @Test
    void delete_failure_not_found() {

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> shelfService.delete("Shelf1")
        );

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void toggle_success() {

        Shelf shelf = new Shelf();
        shelf.setStatus(true);

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(shelf);

        when(shelfRepository.save(shelf))
                .thenReturn(shelf);

        when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(new ShelfDto());

        ShelfDto response = shelfService.toggleStatus("Shelf1");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void toggle_failure() {

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> shelfService.toggleStatus("Shelf1")
        );

        assertEquals(
                "Shelf with identifier 'Shelf1' not found",
                ex.getMessage()
        );
    }

    @Test
    void toggle_failure_deleted() {

        Shelf deleted = new Shelf();
        deleted.setDeleted(true);

        when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(deleted);

        assertThrows(
                ResourceNotFoundException.class,
                () -> shelfService.toggleStatus("Shelf1")
        );

        verify(shelfRepository, never()).save(any());
    }
}