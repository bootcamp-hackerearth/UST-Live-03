package com.ust.pos;

import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.WareHouse;
import com.ust.pos.model.WareHouseRepository;
import com.ust.pos.warehouse.service.impl.WareHouseServiceImpl;
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
class WarehouseServiceTest {

    @Mock
    private WareHouseRepository wareHouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WareHouseServiceImpl wareHouseService;

    @Test
    void findByIdentifierTestSuccess() {
        WareHouse warehouse = new WareHouse();
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH01");

        Mockito.when(wareHouseRepository.findByIdentifier("WH01")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WareHouseDto.class)).thenReturn(wareHouseDto);

        WareHouseDto response = wareHouseService.findByIdentifier("WH01");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("WH01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestNotFoundException() {
        Mockito.when(wareHouseRepository.findByIdentifier("WH01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            wareHouseService.findByIdentifier("WH01");
        });
    }

    @Test
    void saveTestSuccess() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH01");

        Mockito.when(wareHouseRepository.findByIdentifier("WH01")).thenReturn(null);
        WareHouse wareHouse = new WareHouse();
        Mockito.when(modelMapper.map(wareHouseDto, WareHouse.class)).thenReturn(wareHouse);
        Mockito.when(wareHouseRepository.save(wareHouse)).thenReturn(wareHouse);

        WareHouseDto response = wareHouseService.save(wareHouseDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("WH01", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH01");

        WareHouse existingWareHouse = new WareHouse();
        existingWareHouse.setIdentifier("WH01");
        existingWareHouse.setDeleted(false);

        Mockito.when(wareHouseRepository.findByIdentifier("WH01")).thenReturn(existingWareHouse);

        WareHouseDto response = wareHouseService.save(wareHouseDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("WareHouse with identifier - WH01 already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH01");

        WareHouse existingWareHouse = new WareHouse();
        existingWareHouse.setIdentifier("WH01");
        existingWareHouse.setDeleted(true);

        Mockito.when(wareHouseRepository.findByIdentifier("WH01")).thenReturn(existingWareHouse);

        WareHouseDto response = wareHouseService.save(wareHouseDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("WareHouse with identifier WH01 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH01");

        WareHouse existingWareHouse = new WareHouse();
        existingWareHouse.setIdentifier("WH01");

        Mockito.when(wareHouseRepository.findByIdentifier("WH01")).thenReturn(existingWareHouse);
        Mockito.when(wareHouseRepository.save(existingWareHouse)).thenReturn(existingWareHouse);

        WareHouseDto response = wareHouseService.update(wareHouseDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("WH01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH01");

        Mockito.when(wareHouseRepository.findByIdentifier("WH01")).thenReturn(null);

        WareHouseDto response = wareHouseService.update(wareHouseDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("WareHouse with identifier - WH01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        WareHouse wareHouse = new WareHouse();

        Mockito.when(wareHouseRepository.findByIdentifier("WH01")).thenReturn(wareHouse);
        Mockito.when(wareHouseRepository.save(wareHouse)).thenReturn(wareHouse);

        boolean response = wareHouseService.delete("WH01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(wareHouseRepository.findByIdentifier("WH01")).thenReturn(null);

        boolean response = wareHouseService.delete("WH01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        WareHouse wareHouse = new WareHouse();
        List<WareHouse> wareHouseList = List.of(wareHouse);
        Page<WareHouse> wareHousePage = new PageImpl<>(wareHouseList, pageable, wareHouseList.size());

        WareHouseDto wareHouseDto = new WareHouseDto();
        List<WareHouseDto> wareHouseDtos = List.of(wareHouseDto);

        Mockito.when(wareHouseRepository.findByDeletedFalse(pageable)).thenReturn(wareHousePage);
        Mockito.when(modelMapper.map(Mockito.eq(wareHouseList), Mockito.any(Type.class))).thenReturn(wareHouseDtos);

        WsDto<WareHouseDto> response = wareHouseService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findIfTrueTest() {
        WareHouse wareHouse = new WareHouse();
        List<WareHouse> wareHouseList = List.of(wareHouse);
        WareHouseDto wareHouseDto = new WareHouseDto();
        List<WareHouseDto> wareHouseDtos = List.of(wareHouseDto);

        Mockito.when(wareHouseRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(wareHouseList);
        Mockito.when(modelMapper.map(Mockito.eq(wareHouseList), Mockito.any(Type.class))).thenReturn(wareHouseDtos);

        List<WareHouseDto> response = wareHouseService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setStatus(false);
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setStatus(true);

        Mockito.when(wareHouseRepository.findByIdentifier("WH01")).thenReturn(wareHouse);
        Mockito.when(wareHouseRepository.save(wareHouse)).thenReturn(wareHouse);
        Mockito.when(modelMapper.map(wareHouse, WareHouseDto.class)).thenReturn(wareHouseDto);

        WareHouseDto response = wareHouseService.toggleStatus("WH01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<WareHouse> specification = Mockito.mock(Specification.class);
        WareHouse wareHouse = new WareHouse();
        List<WareHouse> wareHouseList = List.of(wareHouse);
        Page<WareHouse> page = new PageImpl<>(wareHouseList, pageable, wareHouseList.size());

        WareHouseDto wareHouseDto = new WareHouseDto();
        List<WareHouseDto> wareHouseDtos = List.of(wareHouseDto);

        Mockito.when(wareHouseRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(wareHouseList), Mockito.any(Type.class))).thenReturn(wareHouseDtos);

        WsDto<WareHouseDto> response = wareHouseService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}