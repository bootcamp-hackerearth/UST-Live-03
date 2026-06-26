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

    @Test
    void findIfTrueTest() {
        Shelfs shelfs = new Shelfs();
        List<Shelfs> shelfsList = List.of(shelfs);
        ShelfsDto shelfsDto = new ShelfsDto();
        List<ShelfsDto> shelfsDtos = List.of(shelfsDto);

        Mockito.when(shelfsRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(shelfsList);
        Mockito.when(modelMapper.map(Mockito.eq(shelfsList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(shelfsDtos);

        List<ShelfsDto> response = shelfsService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void saveTest() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("  Admin  ");

        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(null);
        Shelfs shelfs = new Shelfs();
        Mockito.when(modelMapper.map(shelfsDto, Shelfs.class)).thenReturn(shelfs);
        Mockito.when(shelfsRepository.save(shelfs)).thenReturn(shelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("Admin");

        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setIdentifier("Admin");
        existingShelfs.setDeleted(false);

        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(existingShelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("Admin");

        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setIdentifier("Admin");
        existingShelfs.setDeleted(true);

        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(existingShelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertFalse(response.isSuccess());
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
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("Admin");

        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(shelfs);
        Mockito.when(shelfsRepository.save(shelfs)).thenReturn(shelfs);

        boolean response = shelfsService.delete("Admin");

        Assertions.assertEquals(true, response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(shelfsRepository.findByIdentifier("Admin")).thenReturn(null);

        boolean response = shelfsService.delete("Admin");

        Assertions.assertEquals(false, response);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Shelfs shelfs = new Shelfs();
        List<Shelfs> shelfsList = List.of(shelfs);
        Page<Shelfs> shelfsPage = new PageImpl<>(shelfsList, pageable, shelfsList.size());

        ShelfsDto shelfsDto = new ShelfsDto();
        List<ShelfsDto> shelfsDtos = List.of(shelfsDto);

        Mockito.when(shelfsRepository.findByDeletedFalse(pageable)).thenReturn(shelfsPage);
        Mockito.when(modelMapper.map(Mockito.eq(shelfsList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(shelfsDtos);

        WsDto<ShelfsDto> response = shelfsService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
    }
}