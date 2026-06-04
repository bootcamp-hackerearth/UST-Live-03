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

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RackServiceTest {

    @InjectMocks
    RackServiceImpl rackService;

    @Mock
    RackRepository rackRepository;
    @Mock
    ModelMapper modelMapper;

    @Test
    void saveTest_success() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");

        Rack rack = new Rack();

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(rackDto, Rack.class))
                .thenReturn(rack);
        Mockito.when(rackRepository.save(rack))
                .thenReturn(rack);

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

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");

        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Rack> rackPage = new PageImpl<>(racks);

        Mockito.when(rackRepository.findAll(pageable))
                .thenReturn(rackPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(racks),
                Mockito.any(Type.class)
        )).thenReturn(rackDtos);

        List<RackDto> response = rackService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Rack1", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Rack rack = new Rack();
        rack.setIdentifier("Rack1");

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");

        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);

        Mockito.when(rackRepository.findAll())
                .thenReturn(racks);
        Mockito.when(modelMapper.map(
                Mockito.eq(racks),
                Mockito.any(Type.class)
        )).thenReturn(rackDtos);

        List<RackDto> response = rackService.findAll(null);

        Assertions.assertEquals(1, response.size());
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
        rack.setIdentifier("Rack1");
        rack.setStatus(false);

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(rack);

        RackDto response = rackService.toggleStatus("Rack1", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());

        Mockito.verify(rackRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(null);

        RackDto response = rackService.toggleStatus("Rack1", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack not found", response.getMessage());
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
        Mockito.when(modelMapper.map(
                Mockito.eq(racks),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(rackDtos);

        List<RackDto> response = rackService.findActiveRacks();
        Assertions.assertEquals(1, response.size());
    }

}