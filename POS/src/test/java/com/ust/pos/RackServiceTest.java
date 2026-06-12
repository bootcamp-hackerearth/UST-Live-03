package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.impl.RackServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
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
    void save_success() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Rack.class)).thenReturn(new Rack());

        RackDto response = rackService.save(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_failure_alreadyExists() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(new Rack());

        RackDto response = rackService.save(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifier() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto response = rackService.findByIdentifier("R1");

        Assertions.assertEquals("R1", response.getIdentifier());
    }


    @Test
    void update_success() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(new Rack());

        RackDto response = rackService.update(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void update_failure() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        RackDto response = rackService.update(dto);

        Assertions.assertFalse(response.isSuccess());
    }


    @Test
    void delete_success() {
        Assertions.assertDoesNotThrow(() -> rackService.delete("R1"));

        Mockito.verify(rackRepository).deleteByIdentifier("R1");
    }

    @Test
    void findAllTest() {
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack2");

        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);

        Page<Rack> rackPage = new PageImpl<>(racks, PageRequest.of(0, 2), racks.size());

        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));

        Mockito.when(rackRepository.findAll(pageable)).thenReturn(rackPage);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(java.lang.reflect.Type.class))).thenReturn(rackDtos);

        List<RackDto> response = rackService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void findByStatusTest() {
        Rack rack = new Rack();
        RackDto dto = new RackDto();

        List<Rack> racks = List.of(rack);
        List<RackDto> dtos = List.of(dto);

        Mockito.when(rackRepository.findByStatusTrue()).thenReturn(racks);

        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(java.lang.reflect.Type.class))).thenReturn(dtos);

        List<RackDto> response = rackService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void toggle_activeToInactive() {
        Rack rack = new Rack();
        rack.setStatus(true);

        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(rack);

        rackService.toggleStatus("R1");

        Assertions.assertFalse(rack.isStatus());
    }

    @Test
    void toggle_inactiveToActive() {
        Rack rack = new Rack();
        rack.setStatus(false);

        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(rack);

        rackService.toggleStatus("R1");

        Assertions.assertTrue(rack.isStatus());
    }
}
