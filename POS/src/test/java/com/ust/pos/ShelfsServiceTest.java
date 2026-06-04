package com.ust.pos;

import com.ust.pos.dto.ShelfsDto;
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

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShelfsServiceTest {

    @InjectMocks
    private ShelfsServiceImpl shelfsService;

    @Mock
    private ShelfsRepository shelfsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {

        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SHELF1");

        Shelfs shelfs = new Shelfs();

        Mockito.when(shelfsRepository.findByIdentifier("SHELF1")).thenReturn(null);
        Mockito.when(modelMapper.map(shelfsDto, Shelfs.class)).thenReturn(shelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertEquals("SHELF1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(shelfsRepository).save(shelfs);
    }

    @Test
    void saveFailureTest() {

        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SHELF1");

        Mockito.when(shelfsRepository.findByIdentifier("SHELF1")).thenReturn(new Shelfs());

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertEquals("SHELF1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateSuccessTest() {

        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SHELF1");

        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setIdentifier("SHELF1");

        Mockito.when(shelfsRepository.findByIdentifier("SHELF1")).thenReturn(existingShelfs);

        ShelfsDto response = shelfsService.update(shelfsDto);

        Assertions.assertEquals("SHELF1", response.getIdentifier());
        verify(shelfsRepository).save(existingShelfs);
    }

    @Test
    void updateFailureTest() {

        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SHELF1");

        Mockito.when(shelfsRepository.findByIdentifier("SHELF1")).thenReturn(null);

        ShelfsDto response = shelfsService.update(shelfsDto);

        Assertions.assertEquals("SHELF1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteSuccessTest() {

        shelfsService.delete("SHELF1");

        verify(shelfsRepository).deleteByIdentifier("SHELF1");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("SHELF1");

        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SHELF1");

        Mockito.when(shelfsRepository.findByIdentifier("SHELF1")).thenReturn(shelfs);
        Mockito.when(modelMapper.map(shelfs, ShelfsDto.class)).thenReturn(shelfsDto);

        ShelfsDto response = shelfsService.findByIdentifier("SHELF1");

        Assertions.assertEquals("SHELF1", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(shelfsRepository.findByIdentifier("SHELF1")).thenReturn(null);

        ShelfsDto response = shelfsService.findByIdentifier("SHELF1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSuccessTest() {

        Shelfs s1 = new Shelfs();
        s1.setIdentifier("SHELF1");

        Shelfs s2 = new Shelfs();
        s2.setIdentifier("SHELF2");

        List<Shelfs> shelfsList = List.of(s1, s2);

        ShelfsDto d1 = new ShelfsDto();
        d1.setIdentifier("SHELF1");

        ShelfsDto d2 = new ShelfsDto();
        d2.setIdentifier("SHELF2");

        List<ShelfsDto> shelfsDtos = List.of(d1, d2);

        Page<Shelfs> page = new PageImpl<>(shelfsList);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(shelfsRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(shelfsList), Mockito.any(Type.class))).thenReturn(shelfsDtos);

        List<ShelfsDto> result = shelfsService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void findAllActiveSuccessTest() {

        Shelfs s1 = new Shelfs();
        s1.setIdentifier("SHELF1");
        s1.setStatus(true);

        Shelfs s2 = new Shelfs();
        s2.setIdentifier("SHELF2");
        s2.setStatus(true);

        List<Shelfs> activeShelfs = List.of(s1, s2);

        ShelfsDto d1 = new ShelfsDto();
        d1.setIdentifier("SHELF1");

        ShelfsDto d2 = new ShelfsDto();
        d2.setIdentifier("SHELF2");

        List<ShelfsDto> shelfsDtos = List.of(d1, d2);

        Mockito.when(shelfsRepository.findByStatus(true)).thenReturn(activeShelfs);

        Mockito.when(modelMapper.map(Mockito.eq(activeShelfs), Mockito.any(Type.class))).thenReturn(shelfsDtos);

        List<ShelfsDto> result = shelfsService.findAllActive();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void toggleStatusSuccessTest() {

        Shelfs shelfs = new Shelfs();
        shelfs.setStatus(true);

        Mockito.when(shelfsRepository.findByIdentifier("SHELF1")).thenReturn(shelfs);

        shelfsService.toggleStatus("SHELF1");

        Assertions.assertFalse(shelfs.isStatus());
        verify(shelfsRepository).save(shelfs);
    }

    @Test
    void toggleStatusShelfsNotFoundTest() {

        Mockito.when(shelfsRepository.findByIdentifier("SHELF1")).thenReturn(null);

        shelfsService.toggleStatus("SHELF1");

        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }
}