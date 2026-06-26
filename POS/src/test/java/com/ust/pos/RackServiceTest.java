package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RackDto;
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

    @InjectMocks
    private RackServiceImpl rackService;

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Rack rack = new Rack();
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(null);
        Mockito.when(modelMapper.map(rackDto, Rack.class)).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        RackDto response = rackService.save(rackDto);
        Assertions.assertEquals("Rack1", response.getIdentifier());
        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void saveDuplicateRackTest() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Rack existingRack = new Rack();
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(existingRack);
        RackDto response = rackService.save(rackDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedRackTest() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Rack existingRack = new Rack();
        existingRack.setDeleted(true);
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(existingRack);
        RackDto response = rackService.save(rackDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);
        RackDto response = rackService.findByIdentifier("Rack1");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Rack1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(null);
        Mockito.when(modelMapper.map(null, RackDto.class)).thenReturn(null);
        RackDto response = rackService.findByIdentifier("Rack1");
        Assertions.assertNull(response);
    }

    @Test
    void updateTest() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Rack existingRack = new Rack();
        existingRack.setIdentifier("Rack1");
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(existingRack);
        Mockito.when(rackRepository.save(existingRack)).thenReturn(existingRack);
        RackDto response = rackService.update(rackDto);
        Assertions.assertNotNull(response);
        Mockito.verify(rackRepository).save(existingRack);
    }

    @Test
    void updateTestFailure() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(null);
        RackDto response = rackService.update(rackDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(rack);
        rackService.delete("Rack1");
        Assertions.assertTrue(rack.isDeleted());
        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(null);
        Assertions.assertThrows(RuntimeException.class, () -> rackService.delete("Rack1"));
    }

    @Test
    void findAllWithPageableTest() {
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        RackDto dto = new RackDto();
        dto.setIdentifier("Rack1");
        List<Rack> racks = List.of(rack);
        List<RackDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Rack> rackPage = new PageImpl<>(racks);
        Mockito.when(rackRepository.findByDeletedFalse(pageable)).thenReturn(rackPage);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<RackDto> response = rackService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Rack1", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllWithoutPageableTest() {
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        RackDto dto = new RackDto();
        dto.setIdentifier("Rack1");
        List<Rack> racks = List.of(rack);
        List<RackDto> dtos = List.of(dto);
        Mockito.when(rackRepository.findAll()).thenReturn(racks);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<RackDto> response = rackService.findAll(null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Rack1", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void toggleStatusSuccessTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK001");
        rack.setStatus(false);
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK001");
        rackDto.setStatus(true);
        Mockito.when(rackRepository.findByIdentifier("RACK001")).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);
        RackDto response = rackService.toggleStatus("RACK001", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("RACK001", response.getIdentifier());
        Assertions.assertTrue(rack.isStatus());
        Assertions.assertTrue(response.isStatus());
        Mockito.verify(rackRepository).findByIdentifier("RACK001");
        Mockito.verify(rackRepository).save(rack);
        Mockito.verify(modelMapper).map(rack, RackDto.class);
    }

    @Test
    void toggleStatusRackNotFoundTest() {
        Mockito.when(rackRepository.findByIdentifier("RACK001")).thenReturn(null);
        Mockito.when(modelMapper.map(null, RackDto.class)).thenReturn(null);
        RackDto response = rackService.toggleStatus("RACK001", true);
        Assertions.assertNull(response);
        Mockito.verify(rackRepository).findByIdentifier("RACK001");
        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveRacksTest() {
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);
        Mockito.when(rackRepository.findByStatusTrue()).thenReturn(racks);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(Type.class))).thenReturn(rackDtos);
        List<RackDto> response = rackService.findActiveRacks();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Rack1", response.get(0).getIdentifier());
        Mockito.verify(rackRepository).findByStatusTrue();
    }
}