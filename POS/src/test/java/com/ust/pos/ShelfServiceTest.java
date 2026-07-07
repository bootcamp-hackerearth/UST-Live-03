package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    private static final Long ID = 1L;
    private static final String IDENTIFIER = "SHELF001";
    @Mock
    private ShelfRepository shelfRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Test
    void createShelfSuccess() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier(IDENTIFIER);

        Shelf shelf = new Shelf();

        when(shelfRepository.existsByIdentifier(IDENTIFIER)).thenReturn(false);

        when(modelMapper.map(dto, Shelf.class)).thenReturn(shelf);

        ShelfDto result = shelfService.createShelf(dto);

        verify(shelfRepository).save(shelf);

        assertNotNull(result);

    }

    @Test
    void createShelfAlreadyExists() {

        ShelfDto dto = new ShelfDto();

        dto.setIdentifier(IDENTIFIER);

        when(shelfRepository.existsByIdentifier(IDENTIFIER)).thenReturn(true);

        ShelfDto result = shelfService.createShelf(dto);

        assertFalse(result.isSuccess());

        assertEquals("Shelf already exists", result.getMessage());

        verify(shelfRepository, never()).save(any());

    }

    @Test
    void updateShelfSuccess() {

        Shelf shelf = new Shelf();

        shelf.setId(ID);

        ShelfDto request = new ShelfDto();

        request.setId(ID);
        request.setIdentifier("NEW");

        when(shelfRepository.findById(ID)).thenReturn(Optional.of(shelf));

        ShelfDto result = shelfService.updateShelf(request);

        verify(shelfRepository).save(shelf);

        verify(modelMapper).map(eq(shelf), any(ShelfDto.class));

        assertTrue(result.isSuccess());

    }

    @Test
    void updateShelfNotFound() {

        ShelfDto dto = new ShelfDto();

        dto.setId(ID);

        when(shelfRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> shelfService.updateShelf(dto));

    }

    @Test
    void getShelfSuccess() {

        Shelf shelf = new Shelf();

        shelf.setId(ID);

        when(shelfRepository.findById(ID)).thenReturn(Optional.of(shelf));

        ShelfDto dto = shelfService.getShelf(ID);

        verify(modelMapper).map(eq(shelf), any(ShelfDto.class));

        assertTrue(dto.isSuccess());

    }

    @Test
    void getShelfNotFound() {

        when(shelfRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> shelfService.getShelf(ID));

    }

    @Test
    void findAllSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Shelf shelf = new Shelf();

        Page<Shelf> page = new PageImpl<>(List.of(shelf), pageable, 1);

        List<ShelfDto> dtos = List.of(new ShelfDto());

        when(shelfRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        WsDto<ShelfDto> result = shelfService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        assertEquals(1, result.getTotalRecords());

    }

    @Test
    void findAllEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelf> page = new PageImpl<>(Collections.emptyList());

        when(shelfRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<ShelfDto> result = shelfService.findAll(pageable);

        assertEquals(0, result.getDtoList().size());

    }

    @Test
    void findAllSpecificationSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Shelf> spec = mock(Specification.class);

        Shelf shelf = new Shelf();

        Page<Shelf> page = new PageImpl<>(List.of(shelf));

        List<ShelfDto> dtos = List.of(new ShelfDto());

        when(shelfRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        WsDto<ShelfDto> result = shelfService.findAll(spec, pageable, "abc");

        assertEquals("abc", result.getKeyword());

        assertEquals(1, result.getDtoList().size());

    }

    @Test
    void findAllSpecificationEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Shelf> spec = mock(Specification.class);

        Page<Shelf> page = new PageImpl<>(Collections.emptyList());

        when(shelfRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<ShelfDto> result = shelfService.findAll(spec, pageable, "keyword");

        assertEquals(0, result.getDtoList().size());

        assertEquals("keyword", result.getKeyword());

    }

    @Test
    void deleteShelfSuccess() {

        Shelf shelf = new Shelf();

        when(shelfRepository.findById(ID)).thenReturn(Optional.of(shelf));

        boolean result = shelfService.deleteShelf(ID);

        assertTrue(result);

        verify(shelfRepository).save(shelf);

    }

    @Test
    void deleteShelfNotFound() {

        when(shelfRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> shelfService.deleteShelf(ID));

    }

    @Test
    void toggleStatusTrueToFalse() {

        Shelf shelf = new Shelf();

        shelf.setActive(true);

        when(shelfRepository.findById(ID)).thenReturn(Optional.of(shelf));

        ShelfDto result = shelfService.toggleStatus(ID);

        assertFalse(shelf.isActive());

        verify(shelfRepository).save(shelf);

        assertTrue(result.isSuccess());

    }

    @Test
    void toggleStatusFalseToTrue() {

        Shelf shelf = new Shelf();

        shelf.setActive(false);

        when(shelfRepository.findById(ID)).thenReturn(Optional.of(shelf));

        ShelfDto result = shelfService.toggleStatus(ID);

        assertTrue(shelf.isActive());

        assertTrue(result.isSuccess());

    }

    @Test
    void toggleStatusNotFound() {

        when(shelfRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> shelfService.toggleStatus(ID));

    }

    @Test
    void getActiveShelvesSuccess() {

        Shelf shelf = new Shelf();

        ShelfDto dto = new ShelfDto();

        when(shelfRepository.findByActiveTrue()).thenReturn(List.of(shelf));

        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);

        List<ShelfDto> result = shelfService.getActiveShelves();

        assertEquals(1, result.size());

    }

    @Test
    void getActiveShelvesEmpty() {

        when(shelfRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        List<ShelfDto> result = shelfService.getActiveShelves();

        assertTrue(result.isEmpty());

    }

}