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
        rackDto.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        Rack rack = new Rack();
        Mockito.when(modelMapper.map(rackDto, Rack.class)).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        RackDto response = rackService.save(rackDto);
        Assertions.assertEquals("R1", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("R1");
        Rack existingRack = new Rack();
        Mockito.when(rackRepository.findByIdentifier("R1"))
                .thenReturn(existingRack);
        RackDto response = rackService.save(rackDto);
        Assertions.assertEquals("R1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifier("R1"))
                .thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class))
                .thenReturn(rackDto);
        RackDto response = rackService.findByIdentifier("R1");
        Assertions.assertEquals("R1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("R1");
        Rack existingRack = new Rack();
        existingRack.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifier("R1"))
                .thenReturn(existingRack);
        Mockito.when(rackRepository.save(existingRack))
                .thenReturn(existingRack);
        RackDto response = rackService.update(rackDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifier("R1"))
                .thenReturn(null);
        RackDto response = rackService.update(rackDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(rackRepository)
                .deleteByIdentifier("R1");
        rackService.delete("R1");
        Mockito.verify(rackRepository)
                .deleteByIdentifier("R1");
    }

    @Test
    void findAllTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("R1");
        List<Rack> rackList = List.of(rack);
        List<RackDto> rackDtoList = List.of(rackDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rack> rackPage = new PageImpl<>(rackList);
        Mockito.when(rackRepository.findAll(pageable))
                .thenReturn(rackPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(rackList),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(rackDtoList);
        List<RackDto> response = rackService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("R1", response.get(0).getIdentifier());
    }

    @Test
    void updateStatusTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifier("R1"))
                .thenReturn(rack);
        Mockito.when(rackRepository.save(rack))
                .thenReturn(rack);
        rackService.updateStatus("R1", true);
        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void findAllActiveTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("R1");
        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);
        Mockito.when(rackRepository.findByStatus(true))
                .thenReturn(racks);
        Mockito.when(modelMapper.map(
                Mockito.eq(racks),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(rackDtos);
        List<RackDto> response = rackService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}