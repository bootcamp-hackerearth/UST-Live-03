package com.ust.pos;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
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
    void findByIdentifierTestSuccess() {
        Shelfs shelfs = new Shelfs();
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SHF01");

        Mockito.when(shelfsRepository.findByIdentifier("SHF01")).thenReturn(shelfs);
        Mockito.when(modelMapper.map(shelfs, ShelfsDto.class)).thenReturn(shelfsDto);

        ShelfsDto response = shelfsService.findByIdentifier("SHF01");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("SHF01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestNotFoundException() {
        Mockito.when(shelfsRepository.findByIdentifier("SHF01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            shelfsService.findByIdentifier("SHF01");
        });
    }

    @Test
    void toggleStatusTest() {
        Shelfs shelfs = new Shelfs();
        shelfs.setStatus(false);
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setStatus(true);

        Mockito.when(shelfsRepository.findByIdentifier("SHF01")).thenReturn(shelfs);
        Mockito.when(shelfsRepository.save(shelfs)).thenReturn(shelfs);
        Mockito.when(modelMapper.map(shelfs, ShelfsDto.class)).thenReturn(shelfsDto);

        ShelfsDto response = shelfsService.toggleStatus("SHF01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findIfTrueTest() {
        Shelfs shelfs = new Shelfs();
        List<Shelfs> shelfsList = List.of(shelfs);
        ShelfsDto shelfsDto = new ShelfsDto();
        List<ShelfsDto> shelfsDtos = List.of(shelfsDto);

        Mockito.when(shelfsRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(shelfsList);
        Mockito.when(modelMapper.map(Mockito.eq(shelfsList), Mockito.any(Type.class))).thenReturn(shelfsDtos);

        List<ShelfsDto> response = shelfsService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void saveTestSuccess() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier(" SHF01 "); // Verifying trim block logic

        Mockito.when(shelfsRepository.findByIdentifier("SHF01")).thenReturn(null);
        Shelfs shelfs = new Shelfs();
        Mockito.when(modelMapper.map(shelfsDto, Shelfs.class)).thenReturn(shelfs);
        Mockito.when(shelfsRepository.save(shelfs)).thenReturn(shelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("SHF01", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SHF01");

        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setIdentifier("SHF01");
        existingShelfs.setDeleted(false);

        Mockito.when(shelfsRepository.findByIdentifier("SHF01")).thenReturn(existingShelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelfs with identifier - SHF01 already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SHF01");

        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setIdentifier("SHF01");
        existingShelfs.setDeleted(true);

        Mockito.when(shelfsRepository.findByIdentifier("SHF01")).thenReturn(existingShelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelfs with identifier SHF01 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SHF01");

        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setIdentifier("SHF01");

        Mockito.when(shelfsRepository.findByIdentifier("SHF01")).thenReturn(existingShelfs);
        Mockito.when(shelfsRepository.save(existingShelfs)).thenReturn(existingShelfs);

        ShelfsDto response = shelfsService.update(shelfsDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("SHF01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SHF01");

        Mockito.when(shelfsRepository.findByIdentifier("SHF01")).thenReturn(null);

        ShelfsDto response = shelfsService.update(shelfsDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelfs with identifier - SHF01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Shelfs shelfs = new Shelfs();

        Mockito.when(shelfsRepository.findByIdentifier("SHF01")).thenReturn(shelfs);
        Mockito.when(shelfsRepository.save(shelfs)).thenReturn(shelfs);

        boolean response = shelfsService.delete("SHF01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(shelfsRepository.findByIdentifier("SHF01")).thenReturn(null);

        boolean response = shelfsService.delete("SHF01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Shelfs shelfs = new Shelfs();
        List<Shelfs> shelfsList = List.of(shelfs);
        Page<Shelfs> shelfsPage = new PageImpl<>(shelfsList, pageable, shelfsList.size());

        ShelfsDto shelfsDto = new ShelfsDto();
        List<ShelfsDto> shelfsDtos = List.of(shelfsDto);

        Mockito.when(shelfsRepository.findByDeletedFalse(pageable)).thenReturn(shelfsPage);
        Mockito.when(modelMapper.map(Mockito.eq(shelfsList), Mockito.any(Type.class))).thenReturn(shelfsDtos);

        WsDto<ShelfsDto> response = shelfsService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Shelfs> specification = Mockito.mock(Specification.class);
        Shelfs shelfs = new Shelfs();
        List<Shelfs> shelfsList = List.of(shelfs);
        Page<Shelfs> page = new PageImpl<>(shelfsList, pageable, shelfsList.size());

        ShelfsDto shelfsDto = new ShelfsDto();
        List<ShelfsDto> shelfsDtos = List.of(shelfsDto);

        Mockito.when(shelfsRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(shelfsList), Mockito.any(Type.class))).thenReturn(shelfsDtos);

        WsDto<ShelfsDto> response = shelfsService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}