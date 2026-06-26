package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Shelf;
import com.ust.pos.modell.ShelfRepository;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl service;

    @Mock
    private ShelfRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {

        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();

        when(repository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(shelf);

        when(mapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier("S1"));
    }

    @Test
    void saveSuccessTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf shelf = new Shelf();
        shelf.setStatus(null);

        when(repository.findByIdentifier("S1"))
                .thenReturn(null);

        when(mapper.map(dto, Shelf.class))
                .thenReturn(shelf);

        ShelfDto result = service.save(dto);

        verify(repository).save(shelf);

        assertEquals("S1", result.getIdentifier());
        assertTrue(shelf.getStatus());
    }

    @Test
    void saveDuplicateTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf shelf = new Shelf();
        shelf.setDeleted(false);

        when(repository.findByIdentifier("S1"))
                .thenReturn(shelf);

        ShelfDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - S1 already exists",
                result.getMessage()
        );
    }

    @Test
    void saveSoftDeletedTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf shelf = new Shelf();
        shelf.setDeleted(true);

        when(repository.findByIdentifier("S1"))
                .thenReturn(shelf);

        ShelfDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with Identifier S1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        shelf.setCreatedBy("admin");
        shelf.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(shelf);

        ShelfDto result = service.update(dto);

        verify(mapper).map(dto, shelf);
        verify(repository).save(shelf);

        assertNotNull(result);
    }

    @Test
    void updateNotFoundTest() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        when(repository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(null);

        ShelfDto result = service.update(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - S1 not found",
                result.getMessage()
        );
    }

    @Test
    void deleteTest() {

        Shelf shelf = new Shelf();

        when(repository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(shelf)
                .thenReturn(null);

        service.delete("S1");

        verify(repository).save(shelf);

        service.delete("S1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelf> page =
                new PageImpl<>(List.of(new Shelf()), pageable, 1);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new ShelfDto()));

        WsDto<ShelfDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelf> page =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<ShelfDto> result = service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }

    @Test
    void findAllActiveTest() {

        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(shelf));

        when(mapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        assertEquals(1, service.findAllActive().size());

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        assertTrue(service.findAllActive().isEmpty());
    }

    @Test
    void toggleStatusTest() {

        Shelf shelf = new Shelf();
        shelf.setStatus(true);

        ShelfDto dto = new ShelfDto();

        when(repository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(shelf);

        when(repository.save(shelf))
                .thenReturn(shelf);

        when(mapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        service.toggleStatus("S1");

        assertFalse(shelf.getStatus());

        Shelf nullStatusShelf = new Shelf();
        nullStatusShelf.setStatus(null);

        when(repository.findByIdentifierAndDeletedFalse("S2"))
                .thenReturn(nullStatusShelf);

        when(repository.save(nullStatusShelf))
                .thenReturn(nullStatusShelf);

        when(mapper.map(nullStatusShelf, ShelfDto.class))
                .thenReturn(dto);

        service.toggleStatus("S2");

        assertTrue(nullStatusShelf.getStatus());

        when(repository.findByIdentifierAndDeletedFalse("S3"))
                .thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.toggleStatus("S3")
        );
    }
}