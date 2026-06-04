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
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ShelfsServiceTest {
    @Mock
    private ShelfsRepository shelfsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShelfsServiceImpl shelfsService;

    @Test
    void saveTest() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("Admin");
        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(null);
        Shelfs shelfs = new Shelfs();
        Mockito.when(modelMapper.map(shelfsDto, Shelfs.class)).thenReturn(shelfs);
        Mockito.when(shelfsRepository.save(shelfs)).thenReturn(shelfs);
        ShelfsDto response = shelfsService.save(shelfsDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("Admin");
        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setIdentifier("Admin");
        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(existingShelfs);
        ShelfsDto response = shelfsService.save(shelfsDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("Admin");
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("Admin");
        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(shelfs);
        Mockito.when(modelMapper.map(shelfs, ShelfsDto.class)).thenReturn(shelfsDto);
        ShelfsDto response = shelfsService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("Admin");
        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setIdentifier("Admin");
        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(existingShelfs);
        Mockito.when(shelfsRepository.save(existingShelfs)).thenReturn(existingShelfs);
        ShelfsDto response = shelfsService.update(shelfsDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("Admin");
        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(null);
        ShelfsDto response = shelfsService.update(shelfsDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(shelfsRepository).deleteByIdentifier("Admin");
        boolean response = shelfsService.delete("Admin");
        Assertions.assertEquals(true, response);
    }

    @Test
    void findAllTest() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("Admin");
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("Admin");
        List<Shelfs> shelfss = List.of(shelfs);
        List<ShelfsDto> shelfsDtos = List.of(shelfsDto);
        Page<Shelfs> shelfsPage = new PageImpl<>(shelfss, PageRequest.of(0, 2), shelfss.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(shelfsRepository.findAll(pageable)).thenReturn(shelfsPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelfss),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(shelfsDtos);
        List<ShelfsDto> response = shelfsService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByStatusTest() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("Admin");
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("Admin");
        List<Shelfs> shelfss = List.of(shelfs);
        List<ShelfsDto> shelfsDtos = List.of(shelfsDto);
        Mockito.when(shelfsRepository.findByStatusIsTrue()).thenReturn(shelfss);
        Mockito.when(modelMapper.map(Mockito.eq(shelfss), Mockito.any(java.lang.reflect.Type.class))).thenReturn(shelfsDtos);
        List<ShelfsDto> response = shelfsService.findIfTrue();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {
        Shelfs shelfs = new Shelfs();
        shelfs.setStatus(false);
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setStatus(true);
        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(shelfs);
        Mockito.when(modelMapper.map(shelfs, ShelfsDto.class)).thenReturn(shelfsDto);
        ShelfsDto response = shelfsService.toggleStatus("Admin");
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        Shelfs shelfs = new Shelfs();
        shelfs.setStatus(true);
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setStatus(false);
        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(shelfs);
        Mockito.when(modelMapper.map(shelfs, ShelfsDto.class)).thenReturn(shelfsDto);
        ShelfsDto response = shelfsService.toggleStatus("Admin");
        Assertions.assertFalse(response.isStatus());
    }
}
