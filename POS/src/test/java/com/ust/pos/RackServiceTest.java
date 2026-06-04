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

import static org.junit.jupiter.api.Assertions.*;


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
        rackDto.setIdentifier("Admin");

        Rack rack = new Rack();
        Mockito.when(rackRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(rackDto, Rack.class)).thenReturn(rack);
        RackDto response = rackService.save(rackDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Admin");

        Rack rack = new Rack();
        Mockito.when(rackRepository.findByIdentifier("Admin")).thenReturn(rack);
        RackDto response = rackService.save(rackDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        assertFalse(response.isSuccess());

    }

    @Test
    void findByIdentifierTest() {

        Rack rack = new Rack();
        rack.setIdentifier("Admin");

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Admin");

        Mockito.when(rackRepository.findByIdentifier("Admin")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);

        RackDto response = rackService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());

    }

    @Test
    void updateTest() {

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Admin");

        Rack existingRack = new Rack();
        existingRack.setIdentifier("Admin");

        Mockito.when(rackRepository.findByIdentifier("Admin")).thenReturn(existingRack);
        Mockito.when(rackRepository.save(existingRack)).thenReturn(existingRack);

        RackDto response = rackService.update(rackDto);
        assertTrue(response.isSuccess());

    }

    @Test
    void updateTestFailure() {

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Admin");

        Mockito.when(rackRepository.findByIdentifier("Admin")).thenReturn(null);

        RackDto response = rackService.update(rackDto);
        assertFalse(response.isSuccess());

    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(rackRepository).deleteByIdentifier("Admin");

        rackService.delete("Admin");
        Mockito.verify(rackRepository).deleteByIdentifier("Admin");

    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable = PageRequest.of(0, 5);
        Rack rack = new Rack();
        rack.setIdentifier("R1");

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("R1");

        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);

        Page<Rack> rackPage = new PageImpl<>(racks, pageable, racks.size());

        Mockito.when(rackRepository.findAll(pageable)).thenReturn(rackPage);

        Mockito.when(modelMapper.map(Mockito.eq(rackPage.getContent()), Mockito.any(java.lang.reflect.Type.class))).thenReturn(rackDtos);

        List<RackDto> response = rackService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("R1", response.get(0).getIdentifier());

    }

    @Test
    void toggleStatusTest_TrueToFalse() {

        Rack rack = new Rack();
        rack.setIdentifier("M1");
        rack.setStatus(true);

        Mockito.when(rackRepository.findByIdentifier("M1")).thenReturn(rack);

        rackService.toggleStatus("M1");
        assertFalse(rack.isStatus()); // toggled
        Mockito.verify(rackRepository).save(rack);

    }


    @Test
    void toggleStatusTest_FalseToTrue() {

        Rack rack = new Rack();
        rack.setIdentifier("M1");
        rack.setStatus(false);

        Mockito.when(rackRepository.findByIdentifier("M1")).thenReturn(rack);

        rackService.toggleStatus("M1");

        assertTrue(rack.isStatus()); // toggled
        Mockito.verify(rackRepository).save(rack);

    }

    @Test
    void toggleStatusTest_NotFound() {

        Mockito.when(rackRepository.findByIdentifier("M1")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> rackService.toggleStatus("M1"));
        assertEquals("Rack not found", exception.getMessage());

    }

    @Test
    void findActiveRackTest() {

        Rack model = new Rack();
        model.setIdentifier("M1");
        model.setStatus(true);

        RackDto dto = new RackDto();
        dto.setIdentifier("M1");

        List<Rack> rackList = List.of(model);
        List<RackDto> dtoList = List.of(dto);

        Mockito.when(rackRepository.findByStatusTrue()).thenReturn(rackList);

        Mockito.when(modelMapper.map(Mockito.eq(rackList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(dtoList);

        List<RackDto> response = rackService.findActiveRacks();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("M1", response.get(0).getIdentifier());

        Mockito.verify(rackRepository).findByStatusTrue();

    }

}