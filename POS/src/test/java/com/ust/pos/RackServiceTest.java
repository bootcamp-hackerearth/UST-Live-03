package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
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
        rackDto.setIdentifier("Admin");

        Mockito.when(rackRepository.findByIdentifier("Admin")).thenReturn(null);
        Rack rack = new Rack();
        Mockito.when(modelMapper.map(rackDto, Rack.class)).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        RackDto response = rackService.save(rackDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Admin");
        Rack rack = new Rack();

        Mockito.when(rackRepository.findByIdentifier("Admin")).thenReturn(rack);
        RackDto response = rackService.save(rackDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());

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

        Mockito.when(rackRepository.findByIdentifier("Admin"))
                .thenReturn(existingRack);
        Mockito.when(rackRepository.save(existingRack))
                .thenReturn(existingRack);

        RackDto response = rackService.update(rackDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Admin");

        Mockito.when(rackRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        RackDto response = rackService.update(rackDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(rackRepository)
                .deleteByIdentifier("Admin");

        rackService.delete("Admin");

        Mockito.verify(rackRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {

        Rack rack = new Rack();
        rack.setIdentifier("Admin");

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Admin");

        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);

        Page<Rack> rackPage = new PageImpl<>(racks);

        Mockito.when(rackRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(rackPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(racks),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(rackDtos);

        PaginatedResponseDto<RackDto> response = rackService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
    }

    @Test
    void findAllActiveTest() {

        Rack rack = new Rack();
        rack.setIdentifier("Admin");
        rack.setStatus(true);

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Admin");

        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);

        Mockito.when(rackRepository.findByStatus(true)).thenReturn(racks);
        Mockito.when(modelMapper.map(
                Mockito.eq(racks),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(rackDtos);

        List<RackDto> response = rackService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Rack rack = new Rack();
        rack.setIdentifier("Admin");
        rack.setStatus(false);

        Mockito.when(rackRepository.findByIdentifier("Admin"))
                .thenReturn(rack);

        Mockito.when(rackRepository.save(rack))
                .thenReturn(rack);

        rackService.changeStatus("Admin", true);

        Assertions.assertTrue(rack.getStatus());

        Mockito.verify(rackRepository).save(rack);
    }
}