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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ShelfsServiceTest {

    @Mock
    private ShelfsRepository shelfRepository;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    @InjectMocks
    private ShelfsServiceImpl shelfsService;

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
        Assertions.assertEquals("Shelf with identifier - SHELF1 already exists", result.getMessage()
        );

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
        shelfsService.delete("SHELF3");
        Mockito.verify(shelfRepository).deleteByIdentifier("SHELF3");
    }

    @Test
    void findAllSuccessTest() {
        Shelfs s1 = new Shelfs();
        s1.setIdentifier("S1");

        Shelfs s2 = new Shelfs();
        s2.setIdentifier("S2");

        List<Shelfs> shelves = List.of(s1, s2);

        ShelfsDto d1 = new ShelfsDto();
        d1.setIdentifier("S1");

        ShelfsDto d2 = new ShelfsDto();
        d2.setIdentifier("S2");

        List<ShelfsDto> dtoList = List.of(d1, d2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelfs> shelfsPage = new PageImpl<>(shelves, pageable, shelves.size());

        Mockito.when(shelfRepository.findAll(pageable)).thenReturn(shelfsPage);
        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<ShelfsDto> result = shelfsService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());

        Mockito.verify(shelfRepository).findAll(pageable);

        Mockito.verify(modelMapper).map(Mockito.eq(shelves), Mockito.any(Type.class));
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
    void findActiveShelvesTest() {
        Shelfs s1 = new Shelfs();
        s1.setIdentifier("A1");
        s1.setStatus(true);

        List<Shelfs> activeShelves = List.of(s1);

        ShelfsDto d1 = new ShelfsDto();
        d1.setIdentifier("A1");
        d1.setStatus(true);

        List<ShelfsDto> dtoList = List.of(d1);

        Mockito.when(shelfRepository.findByStatusTrue()).thenReturn(activeShelves);
        Mockito.when(modelMapper.map(Mockito.eq(activeShelves), Mockito.any(Type.class))).thenReturn(dtoList);

        List<ShelfsDto> result = shelfsService.findActiveShelves();

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.get(0).getStatus());
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
}