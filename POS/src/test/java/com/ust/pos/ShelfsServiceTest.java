package com.ust.pos;

import com.ust.pos.dto.PageDto;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ShelfsServiceTest {

    @InjectMocks
    private ShelfsServiceImpl shelfsService;

    @Mock
    private ShelfsRepository shelfsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("S1");

        Mockito.when(shelfsRepository.findByIdentifier("S1"))
                .thenReturn(null);

        Shelfs shelfs = new Shelfs();
        Mockito.when(modelMapper.map(shelfsDto, Shelfs.class))
                .thenReturn(shelfs);
        Mockito.when(shelfsRepository.save(shelfs))
                .thenReturn(shelfs);

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("S1");

        Mockito.when(shelfsRepository.findByIdentifier("S1"))
                .thenReturn(new Shelfs());

        ShelfsDto response = shelfsService.save(shelfsDto);

        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("S1");

        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("S1");

        Mockito.when(shelfsRepository.findByIdentifier("S1"))
                .thenReturn(shelfs);
        Mockito.when(modelMapper.map(shelfs, ShelfsDto.class))
                .thenReturn(shelfsDto);

        ShelfsDto response = shelfsService.findByIdentifier("S1");

        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("S1");

        Shelfs existingShelfs = new Shelfs();
        existingShelfs.setIdentifier("S1");

        Mockito.when(shelfsRepository.findByIdentifier("S1"))
                .thenReturn(existingShelfs);
        Mockito.when(shelfsRepository.save(existingShelfs))
                .thenReturn(existingShelfs);

        ShelfsDto response = shelfsService.update(shelfsDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("S1");

        Mockito.when(shelfsRepository.findByIdentifier("S1"))
                .thenReturn(null);

        ShelfsDto response = shelfsService.update(shelfsDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(shelfsRepository)
                .deleteByIdentifier("S1");

        shelfsService.delete("S1");

        Mockito.verify(shelfsRepository)
                .deleteByIdentifier("S1");
    }

    @Test
    void toggleStatusTest() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("S1");
        shelfs.setStatus(true);

        Mockito.when(shelfsRepository.findByIdentifier("S1"))
                .thenReturn(shelfs);
        Mockito.when(shelfsRepository.save(shelfs))
                .thenReturn(shelfs);

        shelfsService.toggleStatus("S1");

        Assertions.assertFalse(shelfs.getStatus());
        Mockito.verify(shelfsRepository).save(shelfs);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(shelfsRepository.findByIdentifier("S1"))
                .thenReturn(null);

        shelfsService.toggleStatus("S1");

        Mockito.verify(shelfsRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("S1");

        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("S1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelfs> shelfsPage = new PageImpl<>(List.of(shelfs), pageable, 1);

        Mockito.when(shelfsRepository.findAll(pageable)).thenReturn(shelfsPage);

        Type listType = new TypeToken<List<ShelfsDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(Mockito.eq(shelfsPage.getContent()), Mockito.eq(listType))).thenReturn(List.of(shelfsDto));

        PageDto<ShelfsDto> response = shelfsService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("S1", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findActiveShelvesTest() {
        Shelfs activeShelfs = new Shelfs();
        activeShelfs.setIdentifier("S1");
        activeShelfs.setStatus(true);

        ShelfsDto shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("S1");

        Mockito.when(shelfsRepository.findByStatusTrue())
                .thenReturn(List.of(activeShelfs));

        Mockito.when(modelMapper.map(
                Mockito.eq(List.of(activeShelfs)),
                Mockito.any(Type.class)
        )).thenReturn(List.of(shelfsDto));

        List<ShelfsDto> response = shelfsService.findActiveShelves();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("S1", response.get(0).getIdentifier());
    }
}