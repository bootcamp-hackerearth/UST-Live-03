package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.jpa.domain.Specification;

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
        shelf.setDeleted(false);
    }

    @Test
    void testSave_ShelfAlreadyExists() {
        when(shelfRepository.findByIdentifier("SHELF-001")).thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Shelf with identifier - SHELF-001 already exists", result.getMessage());

        verify(shelfRepository, times(1)).findByIdentifier("SHELF-001");
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testSave_ShelfAlreadyExists_ButIsSoftDeleted() {
        shelf.setDeleted(true);
        when(shelfRepository.findByIdentifier("SHELF-001")).thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Shelf identifier - SHELF-001 not available", result.getMessage());

        verify(shelfRepository, times(1)).findByIdentifier("SHELF-001");
        verify(shelfRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testSave_NewShelf() {
        when(shelfRepository.findByIdentifier("SHELF-001")).thenReturn(null);
        when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        verify(shelfRepository, times(1)).findByIdentifier("SHELF-001");
        verify(modelMapper, times(1)).map(shelfDto, Shelf.class);
        verify(shelfRepository, times(1)).save(shelf);
    }

    @Test
    void testUpdate_ShelfNotFound() {
        when(shelfRepository.findByIdentifier("SHELF-001")).thenReturn(null);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("shelf with identifier - SHELF-001 not found", result.getMessage());

        verify(shelfRepository, times(1)).findByIdentifier("SHELF-001");
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testUpdate_ShelfFound() {
        when(shelfRepository.findByIdentifier("SHELF-001")).thenReturn(shelf);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);
        verify(shelfRepository, times(1)).findByIdentifier("SHELF-001");
        verify(modelMapper, times(1)).map(shelfDto, shelf);
        verify(shelfRepository, times(1)).save(shelf);
    }

    @Test
    void testDelete() {
        shelf.setDeleted(false);

        when(shelfRepository.findByIdentifierAndDeletedFalse("SHELF-001")).thenReturn(shelf);

        assertDoesNotThrow(() -> shelfService.delete("SHELF-001"));

        verify(shelfRepository, times(1)).findByIdentifierAndDeletedFalse("SHELF-001");
        assertTrue(shelf.getDeleted());
    }

    @Test
    void testFindAll_WithData() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Shelf> shelfList = Collections.singletonList(shelf);
        List<ShelfDto> shelfDtoList = Collections.singletonList(shelfDto);
        Page<Shelf> shelfPage = new PageImpl<>(shelfList, pageable, shelfList.size());
        Type listType = new TypeToken<List<ShelfDto>>() {}.getType();

        when(shelfRepository.findAllByDeletedFalse(pageable)).thenReturn(shelfPage);
        when(modelMapper.map(shelfPage.getContent(), listType)).thenReturn(shelfDtoList);

        WsDto<ShelfDto> result = shelfService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(shelfRepository, times(1)).findAllByDeletedFalse(pageable);
        verify(modelMapper, times(1)).map(shelfPage.getContent(), listType);
    }

    @Test
    void testFindAll_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shelf> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Type listType = new TypeToken<List<ShelfDto>>() {}.getType();

        when(shelfRepository.findAllByDeletedFalse(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(emptyPage.getContent(), listType)).thenReturn(Collections.emptyList());

        WsDto<ShelfDto> result = shelfService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(shelfRepository, times(1)).findAllByDeletedFalse(pageable);
        verify(modelMapper, times(1)).map(emptyPage.getContent(), listType);
    }

    @Test
    void testFindByIdentifier_Found() {
        when(shelfRepository.findByIdentifierAndDeletedFalse("SHELF-001")).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);

        ShelfDto result = shelfService.findByIdentifier("SHELF-001");

        assertNotNull(result);
        assertEquals("SHELF-001", result.getIdentifier());

        verify(shelfRepository, times(1)).findByIdentifierAndDeletedFalse("SHELF-001");
        verify(modelMapper, times(1)).map(shelf, ShelfDto.class);
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(shelfRepository.findByIdentifierAndDeletedFalse("SHELF-001")).thenReturn(null);
        when(modelMapper.map(null, ShelfDto.class)).thenReturn(null);

        ShelfDto result = shelfService.findByIdentifier("SHELF-001");

        assertNull(result);

        verify(shelfRepository, times(1)).findByIdentifierAndDeletedFalse("SHELF-001");
        verify(modelMapper, times(1)).map(null, ShelfDto.class);
    }

    @Test
    void testFindActiveShelves() {
        List<Shelf> shelfEntities = Collections.singletonList(shelf);
        List<ShelfDto> shelfDtos = Collections.singletonList(shelfDto);

        when(shelfRepository.findByStatusTrueAndDeletedFalse()).thenReturn(shelfEntities);
        when(modelMapper.map(eq(shelfEntities), any(Type.class))).thenReturn(shelfDtos);

        List<ShelfDto> result = shelfService.findActiveShelves();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(shelfRepository, times(1)).findByStatusTrueAndDeletedFalse();
    }

    @Test
    void testToggleStatus_TrueToFalse() {
        shelf.setStatus(true);
        when(shelfRepository.findByIdentifier("SHELF-001")).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);

        shelfService.toggleStatus("SHELF-001");

        assertFalse(shelf.isStatus());
        verify(shelfRepository, times(1)).save(shelf);
    }

    @Test
    void testToggleStatus_FalseToTrue() {
        shelf.setStatus(false);
        when(shelfRepository.findByIdentifier("SHELF-001")).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);

        shelfService.toggleStatus("SHELF-001");

        assertTrue(shelf.isStatus());
        verify(shelfRepository, times(1)).save(shelf);
    }

    @Test
    void testToggleStatus_ShelfNotFound() {
        when(shelfRepository.findByIdentifier("SHELF-001")).thenReturn(null);

        ShelfDto result = shelfService.toggleStatus("SHELF-001");

        assertNull(result);
        verify(shelfRepository, times(1)).findByIdentifier("SHELF-001");
        verify(shelfRepository, never()).save(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindAll_WithSpecification_Success() {
        Specification<Shelf> mockSpec = mock(Specification.class);
        Pageable pageable = PageRequest.of(1, 15);
        List<Shelf> shelfList = Collections.singletonList(shelf);
        List<ShelfDto> shelfDtoList = Collections.singletonList(shelfDto);
        Page<Shelf> shelfPage = new PageImpl<>(shelfList, pageable, 45);
        Type listType = new TypeToken<List<ShelfDto>>() {}.getType();

        when(shelfRepository.findAll(mockSpec, pageable)).thenReturn(shelfPage);
        when(modelMapper.map(shelfPage.getContent(), listType)).thenReturn(shelfDtoList);

        WsDto<ShelfDto> result = shelfService.findAll(mockSpec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(45L, result.getTotalRecords());
        assertEquals(3, result.getTotalPages());
        assertEquals(15, result.getSizePerPage());
        assertEquals(1, result.getPage());

        verify(shelfRepository, times(1)).findAll(mockSpec, pageable);
        verify(modelMapper, times(1)).map(shelfPage.getContent(), listType);
    }
}