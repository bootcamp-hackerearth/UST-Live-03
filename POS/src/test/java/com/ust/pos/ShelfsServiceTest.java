package com.ust.pos;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.service.impl.ShelfsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("S1");
        dto.setSuccess(true);

        Mockito.when(shelfsRepository.findByIdentifier("S1")).thenReturn(null);

        Shelfs entity = new Shelfs();

        Mockito.when(modelMapper.map(dto, Shelfs.class)).thenReturn(entity);

        Mockito.when(shelfsRepository.save(entity)).thenReturn(entity);

        ShelfsDto response = shelfsService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("S1", response.getIdentifier());

        Mockito.verify(shelfsRepository).save(entity);
    }

    @Test
    void saveTestFailure() {

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("S1");

        Shelfs existing = new Shelfs();
        existing.setDeleted(false);

        Mockito.when(shelfsRepository.findByIdentifier("S1")).thenReturn(existing);

        ShelfsDto response = shelfsService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelfs with identifier - S1 already exists", response.getMessage());

        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveTestSoftDeletedRecord() {

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("S1");

        Shelfs existing = new Shelfs();
        existing.setDeleted(true);

        Mockito.when(shelfsRepository.findByIdentifier("S1")).thenReturn(existing);

        ShelfsDto response = shelfsService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelfs with identifier - S1 has been soft deleted. Restore it by changing status.", response.getMessage());

        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTest() {

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("S1");
        dto.setSuccess(true);

        Shelfs existing = new Shelfs();
        existing.setIdentifier("S1");

        Mockito.when(shelfsRepository.findByIdentifier("S1")).thenReturn(existing);

        Mockito.when(shelfsRepository.save(existing)).thenReturn(existing);

        ShelfsDto response = shelfsService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper).map(dto, existing);

        Mockito.verify(shelfsRepository).save(existing);
    }

    @Test
    void updateTestFailure() {

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfsRepository.findByIdentifier("S1")).thenReturn(null);

        ShelfsDto response = shelfsService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelfs with identifier - S1 not found", response.getMessage());

        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {

        Shelfs entity = new Shelfs();
        entity.setIdentifier("S1");
        entity.setDeleted(false);
        entity.setStatus(true);

        Mockito.when(shelfsRepository.findByIdentifier("S1")).thenReturn(entity);

        Mockito.when(shelfsRepository.save(entity)).thenReturn(entity);

        boolean result = shelfsService.delete("S1");

        Assertions.assertTrue(result);
        Assertions.assertTrue(entity.getDeleted());
        Assertions.assertFalse(entity.getStatus());

        Mockito.verify(shelfsRepository).save(entity);
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(shelfsRepository.findByIdentifier("S1")).thenReturn(null);

        boolean result = shelfsService.delete("S1");

        Assertions.assertFalse(result);

        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {

        Shelfs entity = new Shelfs();
        entity.setIdentifier("S1");
        entity.setStatus(true);

        Mockito.when(shelfsRepository.findByIdentifier("S1")).thenReturn(entity);

        Mockito.when(shelfsRepository.save(entity)).thenReturn(entity);

        shelfsService.toggleStatus("S1");

        Assertions.assertFalse(entity.getStatus());

        Mockito.verify(shelfsRepository).save(entity);
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(shelfsRepository.findByIdentifier("S1")).thenReturn(null);

        shelfsService.toggleStatus("S1");

        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {

        Shelfs entity = new Shelfs();
        entity.setIdentifier("S1");

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfsRepository.findByIdentifier("S1")).thenReturn(entity);

        Mockito.when(modelMapper.map(entity, ShelfsDto.class)).thenReturn(dto);

        ShelfsDto response = shelfsService.findByIdentifier("S1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(shelfsRepository.findByIdentifier("S1"))
                .thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> shelfsService.findByIdentifier("S1"));

        Assertions.assertEquals("Shelfs with identifier 'S1' not found", exception.getMessage());

        Mockito.verify(modelMapper, Mockito.never()).map(Mockito.any(), Mockito.eq(ShelfsDto.class));
    }

    @Test
    void findAllPaginationTest() {

        Shelfs entity = new Shelfs();
        entity.setIdentifier("S1");

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("S1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelfs> page = new PageImpl<>(List.of(entity), pageable, 1);

        Mockito.when(shelfsRepository.findByDeletedFalse(pageable)).thenReturn(page);

        Type listType = new TypeToken<List<ShelfsDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(dto));

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
    void findAllWithSpecificationTest() {

        Shelfs entity = new Shelfs();
        entity.setIdentifier("S1");

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("S1");

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Shelfs> spec = Mockito.mock(Specification.class);

        Page<Shelfs> page = new PageImpl<>(List.of(entity), pageable, 1);

        Mockito.when(shelfsRepository.findAll(spec, pageable)).thenReturn(page);

        Type listType = new TypeToken<List<ShelfsDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(dto));

        PageDto<ShelfsDto> response = shelfsService.findAll(spec, pageable, "S1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("S1", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Assertions.assertEquals("S1", response.getKeyword());
    }

    @Test
    void findActiveShelvesTest() {

        Shelfs entity = new Shelfs();
        entity.setIdentifier("S1");
        entity.setStatus(true);

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("S1");

        List<Shelfs> list = List.of(entity);

        Type listType = new TypeToken<List<ShelfsDto>>() {
                }.getType();

        Mockito.when(shelfsRepository.findByStatusTrue()).thenReturn(list);

        Mockito.when(modelMapper.map(list, listType)).thenReturn(List.of(dto));

        List<ShelfsDto> response = shelfsService.findActiveShelves();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("S1", response.get(0).getIdentifier()
        );
    }
}