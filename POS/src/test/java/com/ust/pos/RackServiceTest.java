package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RackServiceImpl rackService;

    @Test
    @DisplayName("Save Rack - Success")
    void saveTest_Success() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK-01");
        Rack rack = new Rack();

        Mockito.when(rackRepository.findByIdentifier("RACK-01")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Rack.class)).thenReturn(rack);

        RackDto result = rackService.save(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    @DisplayName("Save Rack - Already Exists Case")
    void saveTest_AlreadyExists() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK-01");
        Mockito.when(rackRepository.findByIdentifier("RACK-01")).thenReturn(new Rack());

        RackDto result = rackService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Rack with identifier - RACK-01 already exists", result.getMessage());
        Mockito.verify(rackRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Update Rack - Success")
    void updateTest_Success() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK-01");
        Rack existingRack = new Rack();

        Mockito.when(rackRepository.findByIdentifier("RACK-01")).thenReturn(existingRack);

        RackDto result = rackService.update(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(modelMapper).map(dto, existingRack);
        Mockito.verify(rackRepository).save(existingRack);
    }

    @Test
    @DisplayName("Update Rack - Not Found Case")
    void updateTest_NotFound() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK-01");
        Mockito.when(rackRepository.findByIdentifier("RACK-01")).thenReturn(null);

        RackDto result = rackService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Rack with identifier - RACK-01 not found", result.getMessage());
    }

    @Test
    @DisplayName("Find All - Paginated Success")
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Rack> racks = List.of(new Rack());
        Page<Rack> rackPage = new PageImpl<>(racks);
        List<RackDto> dtos = List.of(new RackDto());

        Mockito.when(rackRepository.findAll(pageable)).thenReturn(rackPage);
        Mockito.when(modelMapper.map(eq(racks), any(Type.class))).thenReturn(dtos);

        List<RackDto> result = rackService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
        Mockito.verify(rackRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find All Active - Success")
    void findAllActiveTest() {
        List<Rack> racks = List.of(new Rack());
        List<RackDto> dtos = List.of(new RackDto());

        Mockito.when(rackRepository.findAllByStatus(true)).thenReturn(racks);
        Mockito.when(modelMapper.map(eq(racks), any(Type.class))).thenReturn(dtos);

        List<RackDto> result = rackService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifierTest() {
        Rack rack = new Rack();
        RackDto dto = new RackDto();
        Mockito.when(rackRepository.findByIdentifier("RACK-01")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto result = rackService.findByIdentifier("RACK-01");
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Toggle Status - Boolean Flip Case")
    void toggleStatusTest() {
        Rack rack = new Rack();
        rack.setStatus(true);
        RackDto dto = new RackDto();

        Mockito.when(rackRepository.findByIdentifier("RACK-01")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto result = rackService.toggleStatus("RACK-01");

        Assertions.assertFalse(rack.isStatus());
        Mockito.verify(rackRepository).save(rack);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Delete Rack - Success")
    void deleteTest() {
        boolean result = rackService.delete("RACK-01");
        Assertions.assertTrue(result);
        Mockito.verify(rackRepository).deleteByIdentifier("RACK-01");
    }
}