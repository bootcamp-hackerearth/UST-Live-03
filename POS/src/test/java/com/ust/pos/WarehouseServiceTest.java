package com.ust.pos;

import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
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
    void findByIdentifierTest() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("WH-001");
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH-001");

        Mockito.when(wareHouseRepository.findByIdentifier("WH-001")).thenReturn(wareHouse);
        Mockito.when(modelMapper.map(wareHouse, WareHouseDto.class)).thenReturn(wareHouseDto);

        WareHouseDto response = wareHouseService.findByIdentifier("WH-001");

        Assertions.assertEquals("WH-001", response.getIdentifier());
    }

    @Test
    void saveTest() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH-001");

        Mockito.when(wareHouseRepository.findByIdentifier("WH-001")).thenReturn(null);
        WareHouse wareHouse = new WareHouse();
        Mockito.when(modelMapper.map(wareHouseDto, WareHouse.class)).thenReturn(wareHouse);
        Mockito.when(wareHouseRepository.save(wareHouse)).thenReturn(wareHouse);

        WareHouseDto response = wareHouseService.save(wareHouseDto);

        Assertions.assertEquals("WH-001", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH-001");

        WareHouse existingWareHouse = new WareHouse();
        existingWareHouse.setIdentifier("WH-001");
        existingWareHouse.setDeleted(false);

        Mockito.when(wareHouseRepository.findByIdentifier("WH-001")).thenReturn(existingWareHouse);

        WareHouseDto response = wareHouseService.save(wareHouseDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH-001");

        WareHouse existingWareHouse = new WareHouse();
        existingWareHouse.setIdentifier("WH-001");
        existingWareHouse.setDeleted(true);

        Mockito.when(wareHouseRepository.findByIdentifier("WH-001")).thenReturn(existingWareHouse);

        WareHouseDto response = wareHouseService.save(wareHouseDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH-001");

        WareHouse existingWareHouse = new WareHouse();
        existingWareHouse.setIdentifier("WH-001");

        Mockito.when(wareHouseRepository.findByIdentifier("WH-001")).thenReturn(existingWareHouse);
        Mockito.when(wareHouseRepository.save(existingWareHouse)).thenReturn(existingWareHouse);

        WareHouseDto response = wareHouseService.update(wareHouseDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("WH-001");

        Mockito.when(wareHouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        WareHouseDto response = wareHouseService.update(wareHouseDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("WH-001");

        Mockito.when(wareHouseRepository.findByIdentifier("WH-001")).thenReturn(wareHouse);
        Mockito.when(wareHouseRepository.save(wareHouse)).thenReturn(wareHouse);

        boolean response = wareHouseService.delete("WH-001");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(wareHouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        boolean response = wareHouseService.delete("WH-001");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        WareHouse wareHouse = new WareHouse();
        List<WareHouse> wareHouseList = List.of(wareHouse);
        Page<WareHouse> wareHousePage = new PageImpl<>(wareHouseList, pageable, wareHouseList.size());

        WareHouseDto wareHouseDto = new WareHouseDto();
        List<WareHouseDto> wareHouseDtos = List.of(wareHouseDto);

        Mockito.when(wareHouseRepository.findByDeletedFalse(pageable)).thenReturn(wareHousePage);
        Mockito.when(modelMapper.map(Mockito.eq(wareHouseList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(wareHouseDtos);

        WsDto<WareHouseDto> response = wareHouseService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findByStatusTest() {
        WareHouse wareHouse = new WareHouse();
        List<WareHouse> wareHouseList = List.of(wareHouse);
        WareHouseDto wareHouseDto = new WareHouseDto();
        List<WareHouseDto> wareHouseDtos = List.of(wareHouseDto);

        Mockito.when(wareHouseRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(wareHouseList);
        Mockito.when(modelMapper.map(Mockito.eq(wareHouseList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(wareHouseDtos);

        List<WareHouseDto> response = wareHouseService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setStatus(false);
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setStatus(true);

        Mockito.when(wareHouseRepository.findByIdentifier("WH-001")).thenReturn(wareHouse);
        Mockito.when(modelMapper.map(wareHouse, WareHouseDto.class)).thenReturn(wareHouseDto);

        WareHouseDto response = wareHouseService.toggleStatus("WH-001");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setStatus(true);
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setStatus(false);

        Mockito.when(wareHouseRepository.findByIdentifier("WH-001")).thenReturn(wareHouse);
        Mockito.when(modelMapper.map(wareHouse, WareHouseDto.class)).thenReturn(wareHouseDto);

        WareHouseDto response = wareHouseService.toggleStatus("WH-001");

        Assertions.assertFalse(response.isStatus());
    }
}