package com.ust.pos;

import com.ust.pos.dto.WareHouseDto;
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
import org.springframework.data.domain.*;

import java.util.ArrayList;
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
    void saveTest() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("Admin");
        Mockito.when(wareHouseRepository.findByIdentifier("Admin")).thenReturn(null);
        WareHouse wareHouse = new WareHouse();
        Mockito.when(modelMapper.map(wareHouseDto, WareHouse.class)).thenReturn(wareHouse);
        Mockito.when(wareHouseRepository.save(wareHouse)).thenReturn(wareHouse);
        WareHouseDto response = wareHouseService.save(wareHouseDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("Admin");
        WareHouse existingWareHouse = new WareHouse();
        existingWareHouse.setIdentifier("Admin");
        Mockito.when(wareHouseRepository.findByIdentifier("Admin"))
                .thenReturn(existingWareHouse);
        WareHouseDto response = wareHouseService.save(wareHouseDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("Admin");
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("Admin");
        Mockito.when(wareHouseRepository.findByIdentifier("Admin")).thenReturn(wareHouse);
        Mockito.when(modelMapper.map(wareHouse, WareHouseDto.class)).thenReturn(wareHouseDto);
        WareHouseDto response = wareHouseService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("Admin");
        WareHouse existingWareHouse = new WareHouse();
        existingWareHouse.setIdentifier("Admin");
        Mockito.when(wareHouseRepository.findByIdentifier("Admin"))
                .thenReturn(existingWareHouse);
        Mockito.when(wareHouseRepository.save(existingWareHouse))
                .thenReturn(existingWareHouse);
        WareHouseDto response = wareHouseService.update(wareHouseDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("Admin");
        Mockito.when(wareHouseRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        WareHouseDto response = wareHouseService.update(wareHouseDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(wareHouseRepository)
                .deleteByIdentifier("Admin");
        boolean response = wareHouseService.delete("Admin");
        Assertions.assertEquals(true, response);
    }

    @Test
    void findAllTest() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("Admin");
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("Admin");
        List<WareHouse> wareHouses = List.of(wareHouse);
        List<WareHouseDto> wareHouseDtos = List.of(wareHouseDto);
        Page<WareHouse> wareHousePage =
                new PageImpl<>(wareHouses, PageRequest.of(0, 2), wareHouses.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(wareHouseRepository.findAll(pageable)).thenReturn(wareHousePage);
        Mockito.when(modelMapper.map(
                Mockito.eq(wareHouses),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(wareHouseDtos);
        List<WareHouseDto> response = wareHouseService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByStatusTest() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("Admin");
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("Admin");
        List<WareHouse> wareHouses = List.of(wareHouse);
        List<WareHouseDto> wareHouseDtos = List.of(wareHouseDto);
        Mockito.when(wareHouseRepository.findByStatusIsTrue()).thenReturn(wareHouses);
        Mockito.when(modelMapper.map(
                Mockito.eq(wareHouses),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(wareHouseDtos);
        List<WareHouseDto> response = wareHouseService.findIfTrue();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setStatus(false);
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setStatus(true);
        Mockito.when(wareHouseRepository.findByIdentifier("Admin")).thenReturn(wareHouse);
        Mockito.when(modelMapper.map(wareHouse, WareHouseDto.class)).thenReturn(wareHouseDto);
        WareHouseDto response = wareHouseService.toggleStatus("Admin");
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setStatus(true);
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setStatus(false);
        Mockito.when(wareHouseRepository.findByIdentifier("Admin")).thenReturn(wareHouse);
        Mockito.when(modelMapper.map(wareHouse, WareHouseDto.class)).thenReturn(wareHouseDto);
        WareHouseDto response = wareHouseService.toggleStatus("Admin");
        Assertions.assertFalse(response.isStatus());
    }
}