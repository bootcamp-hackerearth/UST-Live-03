package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.modell.Rack;
import com.ust.pos.modell.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
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

    @InjectMocks
    private RackServiceImpl rackService;

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        Rack entity = new Rack();
        Mockito.when(modelMapper.map(dto, Rack.class)).thenReturn(entity);
        Mockito.when(rackRepository.save(entity)).thenReturn(entity);
        RackDto response = rackService.save(dto);
        Assertions.assertEquals("R1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Rack existing = new Rack();
        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(existing);
        RackDto response = rackService.save(dto);
        Assertions.assertEquals("R1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Rack entity = new Rack();
        entity.setIdentifier("R1");
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(entity);
        Mockito.when(modelMapper.map(entity, RackDto.class)).thenReturn(dto);
        RackDto response = rackService.findByIdentifier("R1");
        Assertions.assertEquals("R1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Rack existing = new Rack();
        existing.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(existing);
        Mockito.when(rackRepository.save(existing)).thenReturn(existing);
        RackDto response = rackService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        RackDto response = rackService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Mockito.when(rackRepository.deleteByIdentifier(Mockito.anyString())).thenReturn(1L);
        rackService.delete("R1");
        Mockito.verify(rackRepository).deleteByIdentifier("R1");
    }

    @Test
    void findAllTest() {
        Rack rack = new Rack();
        rack.setIdentifier("Admin");
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Admin");
        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);
        Page<Rack> rackPage = new PageImpl<>(racks, PageRequest.of(0, 2), racks.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(rackRepository.findAll(pageable)).thenReturn(rackPage);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(java.lang.reflect.Type.class))).thenReturn(rackDtos);
        List<RackDto> response = rackService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllActiveTest() {
        Rack entity = new Rack();
        entity.setIdentifier("R1");
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        List<Rack> list = List.of(entity);
        Mockito.when(rackRepository.findByStatusTrue()).thenReturn(list);
        Mockito.when(modelMapper.map(entity, RackDto.class)).thenReturn(dto);
        List<RackDto> response = rackService.findAllActive();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("R1", response.get(0).getIdentifier());
    }
}
