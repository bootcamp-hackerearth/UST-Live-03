package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Rack;
import com.ust.pos.modell.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    @InjectMocks
    private RackServiceImpl rackService;

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        Rack rack = new Rack();
        when(modelMapper.map(dto, Rack.class)).thenReturn(rack);
        rackService.save(dto);
        verify(rackRepository).save(any(Rack.class));
    }

    @Test
    void saveDeletedTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Rack deletedRack = new Rack();
        deletedRack.setDeleted(true);
        when(rackRepository.findByIdentifier("R1")).thenReturn(deletedRack);
        RackDto result = rackService.save(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Rack with identifier - R1 was deleted and cannot be created again.", result.getMessage());
        verify(rackRepository, never()).save(any());
    }

    @Test
    void findByIdentifierTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);
        RackDto result = rackService.findByIdentifier("R1");
        Assertions.assertEquals("R1", result.getIdentifier());
    }

    @Test
    void updateSuccessTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        rackService.update(dto);
        verify(rackRepository).save(rack);
    }

    @Test
    void updateNotFoundTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(null);
        RackDto result = rackService.update(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Rack with identifier - R1 not found",result.getMessage()
        );
        verify(rackRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        rackService.delete("R1");
        verify(rackRepository).save(rack);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        List<Rack> racks = List.of(rack);
        List<RackDto> dtos = List.of(dto);
        Page<Rack> page = new PageImpl<>(racks, pageable, racks.size());
        when(rackRepository.findALlByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(racks), any(Type.class))).thenReturn(dtos);
        WsDto<RackDto> result = rackService.findAll(pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPage());
        Assertions.assertEquals(50, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void findAllActiveTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        when(rackRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(rack));
        when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);
        List<RackDto> result = rackService.findAllActive();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("R1", result.get(0).getIdentifier());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        rack.setStatus(true);
        when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        when(rackRepository.save(rack)).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(new RackDto());
        rackService.toggleStatus("R1");
        Assertions.assertFalse(rack.getStatus());
        verify(rackRepository).save(rack);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        rack.setStatus(false);
        when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        when(rackRepository.save(rack)).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(new RackDto());
        rackService.toggleStatus("R1");
        Assertions.assertTrue(rack.getStatus());
    }

    @Test
    void toggleStatusNullToTrueTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        rack.setStatus(null);
        when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        when(rackRepository.save(rack)).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(new RackDto());
        rackService.toggleStatus("R1");
        Assertions.assertTrue(rack.getStatus());
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(null);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> rackService.toggleStatus("R1"));
        Assertions.assertEquals("Rack not found with identifier: R1", exception.getMessage());
    }
}