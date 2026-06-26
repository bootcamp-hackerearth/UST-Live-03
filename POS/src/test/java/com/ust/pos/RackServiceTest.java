package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
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
class RackServiceTest {

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RackServiceImpl rackService;

    @Test
    void saveSuccessTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK1");
        Rack rack = new Rack();
        rack.setIdentifier("RACK1");

        Mockito.when(rackRepository.findByIdentifier("RACK1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Rack.class)).thenReturn(rack);

        RackDto result = rackService.save(dto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("RACK1", result.getIdentifier());

        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Rack existing = new Rack();
        existing.setIdentifier("RACK1");
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK1");

        Mockito.when(rackRepository.findByIdentifier("RACK1")).thenReturn(existing);

        RackDto result = rackService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Rack with identifier - RACK1 already exists", result.getMessage());

        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureDeletedIdentifierTest() {
        Rack existing = new Rack();
        existing.setIdentifier("RACK1");
        existing.setDeleted(true);
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK1");

        Mockito.when(rackRepository.findByIdentifier("RACK1")).thenReturn(existing);

        RackDto result = rackService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Rack identifier - RACK1 not available", result.getMessage());

        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Rack existing = new Rack();
        existing.setIdentifier("RACK2");
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK2");

        Mockito.when(rackRepository.findByIdentifier("RACK2")).thenReturn(existing);

        RackDto result = rackService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(rackRepository).save(existing);
    }

    @Test
    void updateFailureNotFoundTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(rackRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        RackDto result = rackService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Rack with identifier - UNKNOWN is not found", result.getMessage());
    }

    @Test
    void findAllTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Rack> rackPage = new PageImpl<>(racks, pageable, racks.size());

        Mockito.when(rackRepository.findByIsDeletedFalse(pageable)).thenReturn(rackPage);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(Type.class))).thenReturn(rackDtos);

        WsDto<RackDto> result = rackService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());

        Mockito.verify(rackRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findByIdentifierSuccessTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK4");
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK4");

        Mockito.when(rackRepository.findByIdentifier("RACK4")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto result = rackService.findByIdentifier("RACK4");

        Assertions.assertEquals("RACK4", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        Mockito.when(rackRepository.findByIdentifier("RACK4")).thenReturn(null);

        RackDto result = rackService.findByIdentifier("RACK4");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Rack with identifier - RACK4 is not found", result.getMessage());
    }

    @Test
    void deleteTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK3");

        Mockito.when(rackRepository.findByIdentifier("RACK3")).thenReturn(rack);

        rackService.delete("RACK3");

        Assertions.assertTrue(rack.isDeleted());

        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void deleteRackNotFoundTest() {
        Mockito.when(rackRepository.findByIdentifier("UNKNOWN")).thenReturn(null);
        rackService.delete("UNKNOWN");
        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK1");
        rack.setStatus(true);

        Mockito.when(rackRepository.findByIdentifier("RACK1")).thenReturn(rack);

        rackService.toggleStatus("RACK1");

        Assertions.assertFalse(rack.getStatus());

        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK2");
        rack.setStatus(false);

        Mockito.when(rackRepository.findByIdentifier("RACK2")).thenReturn(rack);

        rackService.toggleStatus("RACK2");

        Assertions.assertTrue(rack.getStatus());

        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void toggleStatusRackNotFoundTest() {
        Mockito.when(rackRepository.findByIdentifier("UNKNOWN")).thenReturn(null);
        rackService.toggleStatus("UNKNOWN");
        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void constructorTest() {
        RackServiceImpl service = new RackServiceImpl(rackRepository, modelMapper);
        Assertions.assertNotNull(service);
    }
}