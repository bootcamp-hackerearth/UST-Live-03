package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
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
    void saveTestSuccess() {

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

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Rack created successfully",
                response.getMessage()
        );

        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void saveDuplicateTest() {

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");

        Rack existingRack = new Rack();
        existingRack.setDeleted(false);

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(existingRack);

        RackDto response = rackService.save(rackDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Rack with identifier - Rack1 already exists",
                response.getMessage()
        );

        Mockito.verify(rackRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void saveSoftDeletedRackTest() {

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");

        Rack existingRack = new Rack();
        existingRack.setDeleted(true);

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(existingRack);

        RackDto response = rackService.save(rackDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Rack with identifier Rack1 has been soft deleted. (Rollback by changing status)",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifierTest() {

        Rack rack = new Rack();
        rack.setIdentifier("Rack1");

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(rack);

        Mockito.when(modelMapper.map(rack, RackDto.class))
                .thenReturn(rackDto);

        RackDto response =
                rackService.findByIdentifier("Rack1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "Rack1",
                response.getIdentifier()
        );
    }

    @Test
    void updateTest() {

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");

        Rack existingRack = new Rack();
        existingRack.setIdentifier("Rack1");

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(existingRack);

        Mockito.when(rackRepository.save(existingRack))
                .thenReturn(existingRack);

        RackDto response =
                rackService.update(rackDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Rack updated successfully",
                response.getMessage()
        );

        Mockito.verify(rackRepository)
                .save(existingRack);
    }

    @Test
    void updateNotFoundTest() {

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(null);

        RackDto response =
                rackService.update(rackDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Rack with identifier - Rack1 not found",
                response.getMessage()
        );
    }

    @Test
    void deleteTest() {

        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        rack.setDeleted(false);

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(rack);

        rackService.delete("Rack1");

        Mockito.verify(rackRepository)
                .save(rack);
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

        Page<Rack> rackPage =
                new PageImpl<>(racks);

        Mockito.when(
                rackRepository.findByDeletedFalse(pageable)
        ).thenReturn(rackPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(racks),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<RackDto> response =
                rackService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Rack1",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Rack rack = new Rack();
        rack.setIdentifier("Rack1");

        RackDto dto = new RackDto();
        dto.setIdentifier("Rack1");

        List<Rack> racks = List.of(rack);
        List<RackDto> dtos = List.of(dto);

        Page<Rack> rackPage =
                new PageImpl<>(racks);

        Mockito.when(
                rackRepository.findByDeletedFalse(null)
        ).thenReturn(rackPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(rackPage),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<RackDto> response =
                rackService.findAll(null);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getDtoList());

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Rack1",
                response.getDtoList().get(0).getIdentifier()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void toggleStatusSuccessTest() {

        Rack rack = new Rack();
        rack.setIdentifier("Rack1");
        rack.setStatus(false);

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");
        rackDto.setStatus(true);

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(rack);

        Mockito.when(rackRepository.save(rack))
                .thenReturn(rack);

        Mockito.when(modelMapper.map(rack, RackDto.class))
                .thenReturn(rackDto);

        RackDto response =
                rackService.toggleStatus("Rack1", true);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Status updated successfully",
                response.getMessage()
        );

        Mockito.verify(rackRepository)
                .save(rack);
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(rackRepository.findByIdentifier("Rack1"))
                .thenReturn(null);

        RackDto response =
                rackService.toggleStatus("Rack1", true);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Rack not found",
                response.getMessage()
        );
    }

    @Test
    void findActiveRacksTest() {

        Rack rack = new Rack();
        rack.setIdentifier("Rack1");

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");

        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);

        Mockito.when(rackRepository.findByStatusTrue())
                .thenReturn(racks);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(racks),
                        Mockito.any(Type.class)
                )
        ).thenReturn(rackDtos);

        List<RackDto> response =
                rackService.findActiveRacks();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "Rack1",
                response.get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        Rack rack = new Rack();
        rack.setIdentifier("Rack1");

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("Rack1");

        List<Rack> racks = List.of(rack);
        List<RackDto> rackDtos = List.of(rackDto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Rack> page =
                new PageImpl<>(
                        racks,
                        pageable,
                        1
                );

        Specification<Rack> specification =
                Mockito.mock(Specification.class);

        Mockito.when(
                rackRepository.findAll(
                        specification,
                        pageable
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(racks),
                        Mockito.any(Type.class)
                )
        ).thenReturn(rackDtos);

        WsDto<RackDto> response =
                rackService.findAll(
                        specification,
                        pageable
                );

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Rack1",
                response.getDtoList()
                        .get(0)
                        .getIdentifier()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );

        Assertions.assertEquals(
                1,
                response.getTotalPages()
        );

        Assertions.assertEquals(
                0,
                response.getPage()
        );

        Assertions.assertEquals(
                5,
                response.getSizePerPage()
        );

        Mockito.verify(rackRepository)
                .findAll(
                        specification,
                        pageable
                );
    }
}