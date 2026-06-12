package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShelfServiceImpl shelfService;

    private Shelf shelf;
    private ShelfDto shelfDto;

    @BeforeEach
    void setUp() {

        shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF-001");
        shelfDto.setSuccess(true);

        shelf = new Shelf();
        shelf.setIdentifier("SHELF-001");
        shelf.setStatus(true);
    }

    @Test
    void testSave_ShelfAlreadyExists() {

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - SHELF-001 already exists",
                result.getMessage()
        );

        verify(shelfRepository, times(1))
                .findByIdentifier("SHELF-001");

        verify(shelfRepository, never())
                .save(any());

        verify(modelMapper, never())
                .map(any(), any());
    }

    @Test
    void testSave_NewShelf() {

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(null);

        when(modelMapper.map(shelfDto, Shelf.class))
                .thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);

        verify(shelfRepository, times(1))
                .findByIdentifier("SHELF-001");

        verify(modelMapper, times(1))
                .map(shelfDto, Shelf.class);

        verify(shelfRepository, times(1))
                .save(shelf);
    }

    @Test
    void testUpdate_ShelfNotFound() {

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(null);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());

        assertEquals(
                "shelf with identifier - SHELF-001 not found",
                result.getMessage()
        );

        verify(shelfRepository, times(1))
                .findByIdentifier("SHELF-001");

        verify(shelfRepository, never())
                .save(any());

        verify(modelMapper, never())
                .map(any(), any());
    }

    @Test
    void testUpdate_ShelfFound() {

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(shelf);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);

        verify(shelfRepository, times(1))
                .findByIdentifier("SHELF-001");

        verify(modelMapper, times(1))
                .map(shelfDto, shelf);

        verify(shelfRepository, times(1))
                .save(shelf);
    }

    @Test
    void testDelete() {

        doNothing().when(shelfRepository)
                .deleteByIdentifier("SHELF-001");

        shelfService.delete("SHELF-001");

        verify(shelfRepository, times(1))
                .deleteByIdentifier("SHELF-001");
    }

    @Test
    void testFindAll_WithData() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Shelf> shelfList =
                Collections.singletonList(shelf);

        List<ShelfDto> shelfDtoList =
                Collections.singletonList(shelfDto);

        Page<Shelf> shelfPage =
                new PageImpl<>(shelfList, pageable, shelfList.size());

        Type listType =
                new TypeToken<List<ShelfDto>>() {
                }.getType();

        when(shelfRepository.findAll(pageable))
                .thenReturn(shelfPage);

        when(modelMapper.map(shelfPage.getContent(), listType))
                .thenReturn(shelfDtoList);

        List<ShelfDto> result =
                shelfService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(shelfRepository, times(1))
                .findAll(pageable);

        verify(modelMapper, times(1))
                .map(shelfPage.getContent(), listType);
    }

    @Test
    void testFindAll_EmptyList() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelf> emptyPage =
                new PageImpl<>(Collections.emptyList());

        Type listType =
                new TypeToken<List<ShelfDto>>() {
                }.getType();

        when(shelfRepository.findAll(pageable))
                .thenReturn(emptyPage);

        when(modelMapper.map(emptyPage.getContent(), listType))
                .thenReturn(Collections.emptyList());

        List<ShelfDto> result =
                shelfService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(shelfRepository, times(1))
                .findAll(pageable);

        verify(modelMapper, times(1))
                .map(emptyPage.getContent(), listType);
    }

    @Test
    void testFindByIdentifier_Found() {

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(shelf);

        when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(shelfDto);

        ShelfDto result =
                shelfService.findByIdentifier("SHELF-001");

        assertNotNull(result);
        assertEquals("SHELF-001", result.getIdentifier());

        verify(shelfRepository, times(1))
                .findByIdentifier("SHELF-001");

        verify(modelMapper, times(1))
                .map(shelf, ShelfDto.class);
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(null);

        when(modelMapper.map(null, ShelfDto.class))
                .thenReturn(null);

        ShelfDto result =
                shelfService.findByIdentifier("SHELF-001");

        assertNull(result);

        verify(shelfRepository, times(1))
                .findByIdentifier("SHELF-001");

        verify(modelMapper, times(1))
                .map(null, ShelfDto.class);
    }

    @Test
    void testFindActiveShelves() {

        when(shelfRepository.findByStatus(true))
                .thenReturn(Collections.singletonList(shelfDto));

        List<ShelfDto> result =
                shelfService.findActiveShelves();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(shelfRepository, times(1))
                .findByStatus(true);
    }

    @Test
    void testToggleStatus_TrueToFalse() {

        shelf.setStatus(true);

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(shelf);

        shelfService.toggleStatus("SHELF-001");

        assertFalse(shelf.isStatus());

        verify(shelfRepository, times(1))
                .save(shelf);
    }

    @Test
    void testToggleStatus_FalseToTrue() {

        shelf.setStatus(false);

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(shelf);

        shelfService.toggleStatus("SHELF-001");

        assertTrue(shelf.isStatus());

        verify(shelfRepository, times(1))
                .save(shelf);
    }

    @Test
    void testToggleStatus_ShelfNotFound() {

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(null);

        shelfService.toggleStatus("SHELF-001");

        verify(shelfRepository, times(1))
                .findByIdentifier("SHELF-001");

        verify(shelfRepository, never())
                .save(any());
    }
}