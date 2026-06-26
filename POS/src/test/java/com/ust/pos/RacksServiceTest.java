package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.model.CommonFields;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.racks.service.impl.RacksServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    @InjectMocks
    private RacksServiceImpl racksService;

    @Mock
    private RacksRepository racksRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");
        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Racks.class)).thenReturn(racks);
        RacksDto response = racksService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(racksRepository).save(racks);
    }

    @Test
    void saveTestFailure() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(new Racks());
        RacksDto response = racksService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");
        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(racks);
        RacksDto response = racksService.update(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(racksRepository).save(racks);
    }

    @Test
    void updateTestFailure() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        RacksDto response = racksService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        Racks racks = new Racks();

        when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(racks);

        racksService.delete("Admin");

        assertTrue(racks.isDeleted());

        verify(racksRepository)
                .findByIdentifier("Admin");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Racks racks = new Racks();
        racks.setIdentifier("Admin");
        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto response = racksService.findByIdentifier("Admin");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        RacksDto response = racksService.findByIdentifier("Admin");

        Assertions.assertNull(response);
    }

    @Test
    void saveTestFailure_deletedRack() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");

        Racks existing = new Racks();
        existing.setDeleted(true);

        when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        RacksDto response = racksService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(
                response.getMessage()
                        .contains("already exists but was deleted")
        );
    }

    @Test
    void toggleStatus_trueToFalse() {

        Racks racks = new Racks();
        racks.setIdentifier("Admin");
        racks.setStatus(true);

        when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(racks);
        racksService.toggleStatus("Admin");

        Assertions.assertFalse(racks.isStatus());
        verify(racksRepository).save(argThat(saved ->
                !saved.isStatus()
        ));
    }

    @Test
    void toggleStatus_falseToTrue() {

        Racks racks = new Racks();
        racks.setIdentifier("Admin");
        racks.setStatus(false);

        when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(racks);
        racksService.toggleStatus("Admin");

        assertTrue(racks.isStatus());
        verify(racksRepository).save(argThat(CommonFields::isStatus
        ));
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Racks racks = new Racks();
        racks.setIdentifier("Admin");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");

        Page<Racks> page =
                new PageImpl<>(List.of(racks), pageable, 1);

        when(racksRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        var result = racksService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Admin",
                result.getContent().getFirst().getIdentifier());

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalRecords());

        verify(racksRepository)
                .findByIsDeletedFalse(pageable);

        verify(modelMapper)
                .map(racks, RacksDto.class);
    }

    @Test
    void toggleStatusNotFoundTest() {

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        racksService.toggleStatus("Admin");

        verify(racksRepository, never()).save(any());
    }

    @Test
    void findActiveRacksTest() {

        List<Racks> racksList = List.of(new Racks());
        Mockito.when(racksRepository.findByStatus(true))
                .thenReturn(racksList);

        List<Racks> response = racksService.findActiveRacks();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
    }
}
