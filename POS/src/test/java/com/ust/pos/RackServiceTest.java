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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    @Mock
    private RackRepository rackRepository;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    @InjectMocks
    private RackServiceImpl rackService;

    @Test
    void saveSuccessTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK1");

        Rack rack = new Rack();
        rack.setIdentifier("RACK1");

        Mockito.when(rackRepository.findByIdentifier("RACK1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Rack.class)).thenReturn(rack);

        RackDto result = rackService.save(dto);

        Assertions.assertEquals("RACK1", result.getIdentifier());

        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Rack existing = new Rack();
        existing.setIdentifier("RACK1");

        RackDto dto = new RackDto();
        dto.setIdentifier("RACK1");

        Mockito.when(rackRepository.findByIdentifier("RACK1")).thenReturn(existing);

        RackDto result = rackService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Rack with identifier - RACK1 already exists", result.getMessage());

        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Rack existing = new Rack();
        existing.setIdentifier("RACK2");

        RackDto dto = new RackDto();
        dto.setIdentifier("RACK2");

        Rack mapped = new Rack();
        mapped.setIdentifier("RACK2");

        Mockito.when(rackRepository.findByIdentifier("RACK2")).thenReturn(existing);
        Mockito.when(modelMapper.map(dto, Rack.class)).thenReturn(mapped);

        RackDto result = rackService.update(dto);

        Assertions.assertEquals("RACK2", result.getIdentifier());

        Mockito.verify(rackRepository).save(mapped);
    }

    @Test
    void updateFailureNotFoundTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(rackRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        RackDto result = rackService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Rack with identifier - UNKNOWN is not found", result.getMessage());
    }

    @Test
    void deleteTest() {
        rackService.delete("RACK3");
        Mockito.verify(rackRepository).deleteByIdentifier("RACK3");
    }

    @Test
    void findAllSuccessTest() {
        Rack r1 = new Rack();
        r1.setIdentifier("R1");

        Rack r2 = new Rack();
        r2.setIdentifier("R2");

        List<Rack> racks = List.of(r1, r2);

        RackDto d1 = new RackDto();
        d1.setIdentifier("R1");

        RackDto d2 = new RackDto();
        d2.setIdentifier("R2");

        List<RackDto> rackDtos = List.of(d1, d2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Rack> rackPage = new PageImpl<>(racks, pageable, racks.size());

        Mockito.when(rackRepository.findAll(pageable)).thenReturn(rackPage);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(Type.class))).thenReturn(rackDtos);

        List<RackDto> result = rackService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        Mockito.verify(rackRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(Mockito.eq(racks), Mockito.any(Type.class));
    }

    @Test
    void findByIdentifierSuccessTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK4");

        RackDto dto = new RackDto();
        dto.setIdentifier("RACK4");

        Mockito.when(rackRepository.findByIdentifier("RACK4")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto result = rackService.findByIdentifier("RACK4");

        Assertions.assertEquals("RACK4", result.getIdentifier());
    }

    @Test
    void toggleStatusTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK1");
        rack.setStatus(true);

        Mockito.when(rackRepository.findByIdentifier("RACK1")).thenReturn(rack);

        rackService.toggleStatus("RACK1");

        Assertions.assertFalse(rack.getStatus());

        Mockito.verify(rackRepository).save(rack);
    }
}