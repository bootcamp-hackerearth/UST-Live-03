package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Shelf;
import com.ust.pos.models.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    void saveTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Shelf shelf = new Shelf();
        when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        when(modelMapper.map(dto, Shelf.class)).thenReturn(shelf);
        shelfService.save(dto);
        verify(shelfRepository).save(shelf);
        Shelf existing = new Shelf();
        existing.setDeleted(false);
        when(shelfRepository.findByIdentifier("S2")).thenReturn(existing);
        ShelfDto duplicateDto = new ShelfDto();
        duplicateDto.setIdentifier("S2");
        ShelfDto duplicateResult = shelfService.save(duplicateDto);
        assertFalse(duplicateResult.isSuccess());
        assertEquals("Shelf with identifier - S2 already exists", duplicateResult.getMessage());
        Shelf deleted = new Shelf();
        deleted.setDeleted(true);
        when(shelfRepository.findByIdentifier("S3")).thenReturn(deleted);
        ShelfDto deletedDto = new ShelfDto();
        deletedDto.setIdentifier("S3");
        ShelfDto deletedResult = shelfService.save(deletedDto);
        assertFalse(deletedResult.isSuccess());
        assertTrue(deletedResult.getMessage().contains("was deleted"));
    }

    @Test
    void findByIdentifierUpdateAndDeleteTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto foundResult = shelfService.findByIdentifier("S1");
        assertNotNull(foundResult);
        assertEquals("S1", foundResult.getIdentifier());
        shelfService.update(dto);
        verify(modelMapper).map(dto, shelf);
        verify(shelfRepository).save(shelf);
        shelfService.delete("S1");
        assertTrue(shelf.getDeleted());
        verify(shelfRepository, atLeastOnce()).save(shelf);
        when(shelfRepository.findByIdentifierAndDeletedFalse("S2")).thenReturn(null);
        ShelfDto notFoundUpdate = new ShelfDto();
        notFoundUpdate.setIdentifier("S2");
        ShelfDto updateResult = shelfService.update(notFoundUpdate);
        assertFalse(updateResult.isSuccess());
        assertEquals("Shelf with identifier - S2 not found", updateResult.getMessage());
        when(shelfRepository.findByIdentifierAndDeletedFalse("S3")).thenReturn(null);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> shelfService.findByIdentifier("S3"));
        assertEquals("Shelf with identifier 'S3' not found", ex.getMessage());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Shelf shelf = new Shelf();
        Page<Shelf> page = new PageImpl<>(List.of(shelf), pageable, 1);
        when(shelfRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new ShelfDto()));
        WsDto<ShelfDto> result = shelfService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Shelf> specification = mock(Specification.class);
        Page<Shelf> page = new PageImpl<>(List.of(new Shelf()), pageable, 1);
        when(shelfRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new ShelfDto()));
        WsDto<ShelfDto> result = shelfService.findAll(specification, pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        verify(shelfRepository).findAll(specification, pageable);
    }

    @Test
    void findAllActiveTest() {
        Shelf shelf = new Shelf();
        when(shelfRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(shelf));
        when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(new ShelfDto());
        List<ShelfDto> activeResult = shelfService.findAllActive();
        assertEquals(1, activeResult.size());
        when(shelfRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        List<ShelfDto> emptyResult = shelfService.findAllActive();
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    void toggleStatusTest() {
        when(shelfRepository.save(any(Shelf.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Shelf.class), eq(ShelfDto.class)));
        Shelf shelf = new Shelf();
        shelf.setStatus(true);
        when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(shelf);
        shelfService.toggleStatus("S1");
        assertFalse(shelf.getStatus());
        shelf.setStatus(false);
        when(shelfRepository.findByIdentifierAndDeletedFalse("S2")).thenReturn(shelf);
        shelfService.toggleStatus("S2");
        assertTrue(shelf.getStatus());
        shelf.setStatus(null);
        when(shelfRepository.findByIdentifierAndDeletedFalse("S3")).thenReturn(shelf);
        shelfService.toggleStatus("S3");
        assertTrue(shelf.getStatus());
        when(shelfRepository.findByIdentifierAndDeletedFalse("S4")).thenReturn(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> shelfService.toggleStatus("S4"));
        assertEquals("Shelf not found with identifier: S4", exception.getMessage());
        verify(shelfRepository, atLeast(3)).save(any(Shelf.class));
    }
}