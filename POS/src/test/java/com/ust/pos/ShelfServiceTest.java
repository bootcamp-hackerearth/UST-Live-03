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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

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
        dto.setIdentifier("S1");

        Shelf shelf = new Shelf();

        when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        when(modelMapper.map(dto, Shelf.class)).thenReturn(shelf);

        ShelfDto result = shelfService.save(dto);

        Assertions.assertEquals("S1", result.getIdentifier());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void saveTestFailure() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);

        ShelfDto result = shelfService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void updateTestIdNotFound() {
        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("S1");

        when(shelfRepository.findById(1L)).thenReturn(Optional.empty());

        ShelfDto result = shelfService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void updateTestDuplicateIdentifier() {
        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("S2");

        Shelf existing = new Shelf();
        existing.setIdentifier("S1");

        Shelf duplicate = new Shelf();
        duplicate.setIdentifier("S2");

        when(shelfRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(shelfRepository.findByIdentifier("S2")).thenReturn(duplicate);

        ShelfDto result = shelfService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("S1");

        Shelf existing = new Shelf();
        existing.setIdentifier("S1");

        when(shelfRepository.findById(1L)).thenReturn(Optional.of(existing));

        ShelfDto result = shelfService.update(dto);

        Assertions.assertTrue(result.isSuccess());
        verify(modelMapper).map(dto, existing);
        verify(shelfRepository).save(existing);
    }

    @Test
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);

        ShelfDto result = shelfService.findByIdentifier("S1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("S1", result.getIdentifier());
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Shelf> page = mock(Page.class);

        List<Shelf> shelves = List.of(new Shelf(), new Shelf());
        List<ShelfDto> dtoList = List.of(new ShelfDto(), new ShelfDto());

        when(shelfRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(shelves);
        when(modelMapper.map(eq(shelves), any(Type.class))).thenReturn(dtoList);

        List<ShelfDto> result = shelfService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
        verify(shelfRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(shelves), any(Type.class));
    }

    @Test
    void deleteTest() {
        shelfService.delete("S1");
        verify(shelfRepository).deleteByIdentifier("S1");
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Shelf shelf = new Shelf();
        shelf.setStatus(true);

        when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);

        shelfService.toggleStatus("S1");

        Assertions.assertFalse(shelf.isStatus());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Shelf shelf = new Shelf();
        shelf.setStatus(false);

        when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);

        shelfService.toggleStatus("S1");

        Assertions.assertTrue(shelf.isStatus());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusNullTest() {
        when(shelfRepository.findByIdentifier("S1")).thenReturn(null);

        shelfService.toggleStatus("S1");

        verify(shelfRepository, never()).save(any());
    }
}