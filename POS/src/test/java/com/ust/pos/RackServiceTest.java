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
    RackServiceImpl rackService;

    @Mock
    RackRepository rackRepository;

    @Mock
    ModelMapper modelMapper;

    @Test
    void saveTest_Success() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Rack rack = new Rack();
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(null);
        Mockito.when(modelMapper.map(rackDto, Rack.class)).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        RackDto response = rackService.save(rackDto);
        Assertions.assertEquals("Rack1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Rack rack = new Rack();
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(rack);
        RackDto response = rackService.save(rackDto);
        Assertions.assertEquals("Rack1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
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
        Mockito.when(rackRepository.findAll(pageable)).thenReturn(rackPage);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<RackDto> response = rackService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Rack1", response.getDtoList().get(0).getIdentifier());
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
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Rack1", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void updateTest() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        Mockito.when(rackRepository.findByIdentifier(rackDto.getIdentifier())).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        RackDto response = rackService.update(rackDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        Mockito.when(rackRepository.findByIdentifier(rackDto.getIdentifier())).thenReturn(null);
        RackDto response = rackService.update(rackDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        Mockito.when(rackRepository.findByIdentifier("Rack1")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);
        RackDto response = rackService.findByIdentifier("Rack1");
        Assertions.assertEquals("Rack1", response.getIdentifier());
    }


    @Test
    void deleteTest() {
        Mockito.doNothing().when(rackRepository).deleteByIdentifier("Rack1");
        rackService.delete("Rack1");
        Mockito.verify(rackRepository).deleteByIdentifier("Rack1");
    }

    @Test
    void toggleStatusSuccessTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK001");
        rack.setStatus(false);
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK001");
        dto.setStatus(true);
        Mockito.when(rackRepository.findByIdentifier("RACK001")).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);
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
        Mockito.verify(modelMapper).map(null, RackDto.class);
    }

    @Test
    void findAllActiveTest() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        List<RackDto> rackDtos = List.of(rackDto);
        List<Rack> racks = List.of(rack);
        Mockito.when(rackRepository.findByStatusTrue()).thenReturn(racks);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(rackDtos);
        List<RackDto> response = rackService.findActiveRacks();
        Assertions.assertEquals(1, response.size());
    }
}