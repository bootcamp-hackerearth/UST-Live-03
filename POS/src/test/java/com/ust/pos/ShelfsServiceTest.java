package com.ust.pos;

import com.ust.pos.dto.WsDto;
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
        shelfsDto.setIdentifier("SH1");

        Shelfs shelfs = new Shelfs();

        Mockito.when(shelfsRepository.findByIdentifier("SH1")).thenReturn(null);
        Mockito.when(modelMapper.map(shelfsDto, Shelfs.class)).thenReturn(shelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertEquals("SH1", response.getIdentifier());
        verify(shelfsRepository).save(shelfs);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SH1");

        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setDeleted(false);

        Mockito.when(shelfsRepository.findByIdentifier("SH1")).thenReturn(existingShelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertEquals("SH1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelfs with identifier - SH1 already exists", response.getMessage());
        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SH1");

        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setDeleted(true);

        Mockito.when(shelfsRepository.findByIdentifier("SH1")).thenReturn(existingShelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertEquals("SH1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Models with identifier - SH1 was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SH1");

        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setIdentifier("SH1");

        Mockito.when(shelfsRepository.findByIdentifier("SH1")).thenReturn(existingShelfs);

        ShelfsDto response = shelfsService.update(shelfsDto);

        Assertions.assertEquals("SH1", response.getIdentifier());
        verify(modelMapper).map(shelfsDto, existingShelfs);
        verify(shelfsRepository).save(existingShelfs);
    }

    @Test
    void updateFailureTest() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SH1");

        Mockito.when(shelfsRepository.findByIdentifier("SH1")).thenReturn(null);

        ShelfsDto response = shelfsService.update(shelfsDto);

        Assertions.assertEquals("SH1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelfs with identifier - SH1 not found", response.getMessage());
        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Shelfs shelfs = new Shelfs();
        Mockito.when(shelfsRepository.findByIdentifier("SH1")).thenReturn(shelfs);

        shelfsService.delete("SH1");

        verify(shelfsRepository).findByIdentifier("SH1");
    }

    @Test
    void findAllSuccessTest() {
        Shelfs shelf = new Shelfs();
        List<Shelfs> shelfList = List.of(shelf);

        ShelfsDto dto = new ShelfsDto();
        List<ShelfsDto> shelfDtos = List.of(dto);

        Page<Shelfs> page = new PageImpl<>(shelfList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(shelfsRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(shelfList), Mockito.any(Type.class))).thenReturn(shelfDtos);

        WsDto<ShelfsDto> result = shelfsService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Shelfs shelfs = new Shelfs();
        ShelfsDto shelfsDto = new ShelfsDto();

        Mockito.when(shelfsRepository.findByIdentifier("SH1")).thenReturn(shelfs);
        Mockito.when(modelMapper.map(shelfs, ShelfsDto.class)).thenReturn(shelfsDto);

        ShelfsDto response = shelfsService.findByIdentifier("SH1");

        Assertions.assertNotNull(response);
    }

    @Test
    void findAllActiveSuccessTest() {
        Shelfs shelf = new Shelfs();
        List<Shelfs> activeShelfs = List.of(shelf);

        ShelfsDto dto = new ShelfsDto();
        List<ShelfsDto> dtos = List.of(dto);

        Mockito.when(shelfsRepository.findByStatusTrueAndIsDeletedFalse()).thenReturn(activeShelfs);
        Mockito.when(modelMapper.map(Mockito.eq(activeShelfs), Mockito.any(Type.class))).thenReturn(dtos);

        List<ShelfsDto> result = shelfsService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void toggleStatusSuccessTest() {
        Shelfs shelfs = new Shelfs();
        shelfs.setStatus(false);

        Mockito.when(shelfsRepository.findByIdentifier("SH1")).thenReturn(shelfs);

        shelfsService.toggleStatus("SH1");

        Assertions.assertTrue(shelfs.isStatus());
        verify(shelfsRepository).save(shelfs);
    }

    @Test
    void toggleStatusShelfsNotFoundTest() {
        Mockito.when(shelfsRepository.findByIdentifier("SH1")).thenReturn(null);

        shelfsService.toggleStatus("SH1");

        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }
}