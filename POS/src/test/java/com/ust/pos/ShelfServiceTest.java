package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    public static final String INVALID = "INVALID";
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

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier(INVALID)
                );

        assertEquals(
                "Shelf with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveTest() {

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

        assertTrue(shelf.getStatus());
        assertNotNull(result);

        Shelf duplicate = new Shelf();
        duplicate.setDeleted(false);

        when(repository.findByIdentifier("S1"))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - S1 already exists",
                result.getMessage()
        );

        duplicate.setDeleted(true);

        when(repository.findByIdentifier("S1"))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with Identifier S1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateAndDeleteTest() {

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

        ShelfDto invalidDto = new ShelfDto();
        invalidDto.setIdentifier(INVALID);

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        result = service.update(invalidDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - INVALID not found",
                result.getMessage()
        );

        service.delete("S1");

        verify(repository, atLeast(2))
                .save(any(Shelf.class));

        when(repository.findByIdentifierAndDeletedFalse("NOTFOUND"))
                .thenReturn(null);

        service.delete("NOTFOUND");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelf> page =
                new PageImpl<>(
                        List.of(new Shelf()),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new ShelfDto()));

        WsDto<ShelfDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());

        Specification<Shelf> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<ShelfDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());

        verify(repository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findAllActiveTest() {

        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(shelf));

        when(mapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        List<ShelfDto> result =
                service.findAllActive();

        assertEquals(1, result.size());

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        result = service.findAllActive();

        assertTrue(result.isEmpty());
    }

    @Test
    void toggleStatusTest() {

        Shelf activeShelf = new Shelf();
        activeShelf.setStatus(true);

        ShelfDto dto = new ShelfDto();

        when(repository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(activeShelf);

        when(repository.save(activeShelf))
                .thenReturn(activeShelf);

        when(mapper.map(activeShelf, ShelfDto.class))
                .thenReturn(dto);

        service.toggleStatus("S1");

        assertFalse(activeShelf.getStatus());

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

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.toggleStatus("S3")
                );

        assertEquals(
                "Shelf not found with identifier: S3",
                exception.getMessage()
        );
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Shelf shelf = new Shelf();
        shelf.setStatus(false);

        ShelfDto dto = new ShelfDto();

        when(repository.findByIdentifierAndDeletedFalse("S4"))
                .thenReturn(shelf);

        when(repository.save(shelf))
                .thenReturn(shelf);

        when(mapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        ShelfDto result = service.toggleStatus("S4");

        assertNotNull(result);
        assertTrue(shelf.getStatus());

        verify(repository).save(shelf);
    }
}