package com.ust.pos;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.service.impl.ShelfsServiceImpl;
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

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @Mock
    private ShelfsRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShelfsServiceImpl shelfsService;

    @Test
    void constructorTest() {
        ShelfsServiceImpl service = new ShelfsServiceImpl(shelfRepository, modelMapper);
        Assertions.assertNotNull(service);
    }

    @Test
    void saveSuccessTest() {
        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHELF1");
        Shelfs shelf = new Shelfs();
        shelf.setIdentifier("SHELF1");

        Mockito.when(shelfRepository.findByIdentifier("SHELF1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Shelfs.class)).thenReturn(shelf);

        ShelfsDto result = shelfsService.save(dto);

        Assertions.assertEquals("SHELF1", result.getIdentifier());

        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Shelfs existing = new Shelfs();
        existing.setIdentifier("SHELF1");
        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHELF1");

        Mockito.when(shelfRepository.findByIdentifier("SHELF1")).thenReturn(existing);

        ShelfsDto result = shelfsService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Shelf with identifier - SHELF1 already exists", result.getMessage());

        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureDeletedIdentifierTest() {
        Shelfs existing = new Shelfs();
        existing.setIdentifier("SHELF1");
        existing.setDeleted(true);
        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHELF1");

        Mockito.when(shelfRepository.findByIdentifier("SHELF1")).thenReturn(existing);

        ShelfsDto result = shelfsService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Shelf identifier - SHELF1 not available", result.getMessage());

        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Shelfs existing = new Shelfs();
        existing.setIdentifier("SHELF2");
        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHELF2");

        Mockito.when(shelfRepository.findByIdentifier("SHELF2")).thenReturn(existing);

        ShelfsDto result = shelfsService.update(dto);

        Assertions.assertEquals("SHELF2", result.getIdentifier());

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(shelfRepository).save(existing);
    }

    @Test
    void updateFailureNotFoundTest() {
        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(shelfRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        ShelfsDto result = shelfsService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Shelf not found", result.getMessage());
    }

    @Test
    void deleteTest() {
        Shelfs shelf = new Shelfs();
        shelf.setIdentifier("SHELF3");

        Mockito.when(shelfRepository.findByIdentifier("SHELF3")).thenReturn(shelf);

        shelfsService.delete("SHELF3");

        Assertions.assertTrue(shelf.isDeleted());
    }

    @Test
    void findAllTest() {
        Shelfs shelf = new Shelfs();
        shelf.setIdentifier("S1");
        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("S1");

        List<Shelfs> shelves = List.of(shelf);
        List<ShelfsDto> dtoList = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelfs> page = new PageImpl<>(shelves, pageable, shelves.size());

        Mockito.when(shelfRepository.findByIsDeletedFalse(pageable)).thenReturn(page);

        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<ShelfsDto> result = shelfsService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());

        Mockito.verify(shelfRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findByIdentifierSuccessTest() {
        Shelfs shelf = new Shelfs();
        shelf.setIdentifier("SHELF4");
        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHELF4");

        Mockito.when(shelfRepository.findByIdentifier("SHELF4")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfsDto.class)).thenReturn(dto);

        ShelfsDto result = shelfsService.findByIdentifier("SHELF4");

        Assertions.assertEquals("SHELF4", result.getIdentifier());
    }

    @Test
    void findAllActiveTest() {
        Shelfs shelf = new Shelfs();
        shelf.setIdentifier("A1");
        shelf.setStatus(true);
        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("A1");

        List<Shelfs> shelves = List.of(shelf);
        List<ShelfsDto> dtoList = List.of(dto);

        Mockito.when(shelfRepository.findByStatusTrueAndIsDeletedFalse()).thenReturn(shelves);
        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(Type.class))).thenReturn(dtoList);

        List<ShelfsDto> result = shelfsService.findAllActive();

        Assertions.assertEquals(1, result.size());

        Mockito.verify(shelfRepository).findByStatusTrueAndIsDeletedFalse();
    }

    @Test
    void toggleStatusTest() {
        Shelfs shelf = new Shelfs();
        shelf.setIdentifier("SHELF1");
        shelf.setStatus(true);

        Mockito.when(shelfRepository.findByIdentifier("SHELF1")).thenReturn(shelf);

        shelfsService.toggleStatus("SHELF1");

        Assertions.assertFalse(shelf.getStatus());

        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Shelfs shelf = new Shelfs();
        shelf.setIdentifier("SHELF2");
        shelf.setStatus(false);

        Mockito.when(shelfRepository.findByIdentifier("SHELF2")).thenReturn(shelf);

        shelfsService.toggleStatus("SHELF2");

        Assertions.assertTrue(shelf.getStatus());

        Mockito.verify(shelfRepository).save(shelf);
    }
}