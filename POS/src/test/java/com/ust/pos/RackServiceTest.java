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
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    @InjectMocks
    private RackServiceImpl rackService;

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void createRackTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Rack rack = new Rack();

        Mockito.when(rackRepository.existsByIdentifier("R1")).thenReturn(false);
        Mockito.when(modelMapper.map(dto, Rack.class)).thenReturn(rack);

        RackDto response = rackService.createRack(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("R1", response.getIdentifier());

        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void createRackDuplicateTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Mockito.when(rackRepository.existsByIdentifier("R1")).thenReturn(true);

        RackDto response = rackService.createRack(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack already exists", response.getMessage());

        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateRackTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Rack rack = new Rack();

        Mockito.when(modelMapper.map(dto, Rack.class)).thenReturn(rack);

        RackDto response = rackService.updateRack(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("R1", response.getIdentifier());

        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void getRackTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");

        Mockito.when(rackRepository.findById(1L)).thenReturn(Optional.of(rack));

        Mockito.doAnswer(invocation -> {
            Rack source = invocation.getArgument(0);
            RackDto target = invocation.getArgument(1);
            target.setIdentifier(source.getIdentifier());
            return null;
        }).when(modelMapper).map(Mockito.any(Rack.class), Mockito.any(RackDto.class));

        RackDto response = rackService.getRack(1L);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("R1", response.getIdentifier());
    }

    @Test
    void getRackNotFoundTest() {
        Mockito.when(rackRepository.findById(1L)).thenReturn(Optional.empty());

        RackDto response = rackService.getRack(1L);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack not found", response.getMessage());
    }

    @Test
    void findAllTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        List<Rack> racks = List.of(rack);
        List<RackDto> dtos = List.of(dto);

        Page<Rack> rackPage = new PageImpl<>(racks);

        Mockito.when(rackRepository.findByDeletedFalse(Mockito.any(Pageable.class))).thenReturn(rackPage);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(Type.class))).thenReturn(dtos);

        List<RackDto> response = rackService.findAll(Pageable.unpaged());

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("R1", response.get(0).getIdentifier());
    }

    @Test
    void findAllEmptyTest() {
        Page<Rack> rackPage = new PageImpl<>(List.of());

        Mockito.when(rackRepository.findByDeletedFalse(Mockito.any(Pageable.class))).thenReturn(rackPage);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());

        List<RackDto> response = rackService.findAll(Pageable.unpaged());

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void deleteRackTest() {
        Rack rack = new Rack();

        Mockito.when(rackRepository.findById(1L)).thenReturn(Optional.of(rack));

        boolean response = rackService.deleteRack(1L);

        Assertions.assertTrue(response);

        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void deleteRackNotFoundTest() {
        Mockito.when(rackRepository.findById(1L)).thenReturn(Optional.empty());

        boolean response = rackService.deleteRack(1L);

        Assertions.assertFalse(response);

        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

}
