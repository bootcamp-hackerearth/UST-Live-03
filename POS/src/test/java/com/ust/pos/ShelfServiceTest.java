package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.CommonFields;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    void saveTestSuccess() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Shelf.class)).thenReturn(shelf);

        ShelfDto response = shelfService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(shelfRepository).save(shelf);
    }

    @Test
    void saveTestFailure() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(new Shelf());
        ShelfDto response = shelfService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(shelf);
        ShelfDto response = shelfService.update(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(shelfRepository).save(shelf);
    }

    @Test
    void updateTestFailure() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        ShelfDto response = shelfService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        Shelf shelf = new Shelf();

        when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(shelf);

        shelfService.delete("Admin");

        assertTrue(shelf.isDeleted());

        verify(shelfRepository)
                .findByIdentifier("Admin");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        ShelfDto response = shelfService.findByIdentifier("Admin");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        assertThrows(
                ResourseNotFoundException.class,
                () -> shelfService.findByIdentifier("Admin")
        );
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Page<Shelf> page =
                new PageImpl<>(List.of(shelf), pageable, 1);

        when(shelfRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        var result = shelfService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Admin",
                result.getContent().getFirst().getIdentifier());

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalRecords());

        verify(shelfRepository)
                .findByIsDeletedFalse(pageable);

        verify(modelMapper)
                .map(shelf, ShelfDto.class);
    }

    @Test
    void saveTestFailure_deletedShelf() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Admin");

        Shelf existing = new Shelf();
        existing.setDeleted(true);

        when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        ShelfDto response = shelfService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(
                response.getMessage()
                        .contains("already exists but was deleted")
        );
    }

    @Test
    void toggleStatus_falseToTrue() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        shelf.setStatus(false);

        when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(shelf);
        shelfService.toggleStatus("Admin");

        assertTrue(shelf.isStatus());

        verify(shelfRepository).save(argThat(CommonFields::isStatus
        ));
    }

    @Test
    void toggleStatusFailureTest() {

        when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        shelfService.toggleStatus("Admin");

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void toggleStatus_trueToFalse() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        shelf.setStatus(true);

        when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(shelf);
        shelfService.toggleStatus("Admin");

        Assertions.assertFalse(shelf.isStatus());
        verify(shelfRepository).save(argThat(saved ->
                !saved.isStatus()
        ));
    }

    @Test
    void findActiveShelfTest() {

        List<Shelf> shelfList = List.of(new Shelf());
        Mockito.when(shelfRepository.findByStatus(true))
                .thenReturn(shelfList);

        List<ShelfDto> response = shelfService.findActiveShelf();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Shelf> specification = mock(Specification.class);

        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF01");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF01");

        List<Shelf> shelfList = List.of(shelf);

        Page<Shelf> page = new PageImpl<>(shelfList, pageable, 1);

        when(shelfRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(shelfList), any(Type.class)))
                .thenReturn(List.of(shelfDto));

        WsDto<ShelfDto> result =
                shelfService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("SHELF01",
                result.getContent().get(0).getIdentifier());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(shelfRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(shelfList), any(Type.class));
    }

    @Test
    void findAllWithSpecificationEmptyResultTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Shelf> specification = mock(Specification.class);

        Page<Shelf> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(shelfRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(List.of()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<ShelfDto> result =
                shelfService.findAll(specification, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(shelfRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(List.of()), any(Type.class));
    }
}