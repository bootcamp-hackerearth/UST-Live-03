package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Shelf> shelfList = List.of(new Shelf());
        Page<Shelf> page = new PageImpl<>(shelfList);
        List<ShelfDto> dtoList = List.of(new ShelfDto());

        when(shelfRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<ShelfDto> result = shelfService.findAll(pageable);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());

        verify(shelfRepository).findAll(pageable);
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

        assertNotNull(result);
        assertEquals("S1", result.getIdentifier());
    }

    @Test
    void saveSuccessTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        when(modelMapper.map(dto, Shelf.class)).thenReturn(new Shelf());

        ShelfDto result = shelfService.save(dto);

        assertNotNull(result);
        verify(shelfRepository).save(any(Shelf.class));
    }

    @Test
    void saveDuplicateTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        when(shelfRepository.findByIdentifier("S1")).thenReturn(new Shelf());

        ShelfDto result = shelfService.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void updateSuccessTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf existing = new Shelf();

        when(shelfRepository.findByIdentifier("S1")).thenReturn(existing);

        doNothing().when(modelMapper).map(eq(dto), eq(existing));

        ShelfDto result = shelfService.update(dto);

        assertNotNull(result);
        verify(shelfRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        when(shelfRepository.findByIdentifier("S1")).thenReturn(null);

        ShelfDto result = shelfService.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
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

        assertFalse(shelf.isStatus());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Shelf shelf = new Shelf();
        shelf.setStatus(false);

        when(shelfRepository.findByIdentifier("S1")).thenReturn(shelf);

        shelfService.toggleStatus("S1");

        assertTrue(shelf.isStatus());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusNotFoundTest() {

        when(shelfRepository.findByIdentifier("S1")).thenReturn(null);

        shelfService.toggleStatus("S1");

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void findActiveStatusTest() {

        List<Shelf> shelfList = List.of(new Shelf());
        List<ShelfDto> dtoList = List.of(new ShelfDto());

        when(shelfRepository.findByStatusTrue()).thenReturn(shelfList);

        when(modelMapper.map(eq(shelfList), any(Type.class)))
                .thenReturn(dtoList);

        List<ShelfDto> result = shelfService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findActiveShelfTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        List<Shelf> shelfList = List.of(shelf);

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        List<ShelfDto> dtoList = List.of(dto);

        when(shelfRepository.findByStatusTrue()).thenReturn(shelfList);

        when(modelMapper.map(eq(shelfList), any(Type.class)))
                .thenReturn(dtoList);

        List<ShelfDto> result = shelfService.findActiveShelf();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("S1", result.get(0).getIdentifier());
    }
}