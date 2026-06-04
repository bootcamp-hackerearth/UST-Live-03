package com.ust.pos;

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
        rackDto.setIdentifier("RACK_01");

        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(null);
        Rack rack = new Rack();
        Mockito.when(modelMapper.map(rackDto, Rack.class)).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        RackDto response = rackService.save(rackDto);
        Assertions.assertEquals("RACK_01", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK_01");
        Rack rack = new Rack();

        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(rack);
        RackDto response = rackService.save(rackDto);
        Assertions.assertEquals("RACK_01", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK_01");
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK_01");

        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);
        RackDto response = rackService.findByIdentifier("RACK_01");
        Assertions.assertEquals("RACK_01", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK_01");
        Rack existingRack = new Rack();
        existingRack.setIdentifier("RACK_01");

        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(existingRack);
        Mockito.when(rackRepository.save(existingRack)).thenReturn(existingRack);
        RackDto response = rackService.update(rackDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK_01");
        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(null);
        RackDto response = rackService.update(rackDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(rackRepository).deleteByIdentifier("RACK_01");
        rackService.delete("RACK_01");
        Mockito.verify(rackRepository).deleteByIdentifier("RACK_01");
    }

    @Test
    void findAllTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK_01");
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK_01");

        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rack> rackPage = new PageImpl<>(racks, pageable, racks.size());

        Mockito.when(rackRepository.findAll(pageable)).thenReturn(rackPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(racks),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(rackDtos);
        List<RackDto> response = rackService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("RACK_01", response.get(0).getIdentifier());
    }

    @Test
    void toggleStatusSuccessTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK_01");
        rack.setStatus(false);
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK_01");
        rackDto.setStatus(true);

        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);
        RackDto response = rackService.toggleStatus("RACK_01", true);
        Assertions.assertEquals("RACK_01", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK_01");
        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(null);
        RackDto response = rackService.toggleStatus("RACK_01", true);
        Assertions.assertNull(response);
        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveRacksTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK_01");
        rack.setStatus(true);

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK_01");
        rackDto.setStatus(true);

        List<Rack> activeRacks = List.of(rack);
        List<RackDto> activeRackDtos = List.of(rackDto);
        Mockito.when(rackRepository.findByStatusTrue()).thenReturn(activeRacks);
        Mockito.when(modelMapper.map(
                Mockito.eq(activeRacks),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(activeRackDtos);
        List<RackDto> response = rackService.findActiveRacks();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("RACK_01", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }
}