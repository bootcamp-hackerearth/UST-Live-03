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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class WareHouseServiceTest {

    @InjectMocks
    private WareHouseServiceImpl warehouseService;
    @Mock
    private WareHouseRepository warehouseRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Supply chain centre");
        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(null);
        WareHouse warehouse = new WareHouse();

        Mockito.when(modelMapper.map(warehouseDto, WareHouse.class)).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        WareHouseDto response = warehouseService.save(warehouseDto);
        Assertions.assertEquals("Supply chain centre", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Supply chain centre");
        WareHouse warehouse = new WareHouse();
        
        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(warehouse);
        WareHouseDto response = warehouseService.save(warehouseDto);
        Assertions.assertEquals("Supply chain centre", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        WareHouse warehouse = new WareHouse();
        warehouse.setIdentifier("Supply chain centre");
        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Supply chain centre");
        
        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WareHouseDto.class)).thenReturn(warehouseDto);
        WareHouseDto response = warehouseService.findByIdentifier("Supply chain centre");
        Assertions.assertEquals("Supply chain centre", response.getIdentifier());
    }

    @Test
    void updateTest() {
        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Supply chain centre");
        WareHouse existingWareHouse = new WareHouse();
        existingWareHouse.setIdentifier("Supply chain centre");
        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre"))
                .thenReturn(existingWareHouse);
        Mockito.when(warehouseRepository.save(existingWareHouse))
                .thenReturn(existingWareHouse);
        WareHouseDto response = warehouseService.update(warehouseDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Supply chain centre");
        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(null);
        WareHouseDto response = warehouseService.update(warehouseDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(warehouseRepository).deleteByIdentifier("Supply chain centre");
        warehouseService.delete("Supply chain centre");
        Mockito.verify(warehouseRepository).deleteByIdentifier("Supply chain centre");
    }

    @Test
    void findAllTest() {
        WareHouse warehouse = new WareHouse();
        warehouse.setIdentifier("Supply chain centre");
        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Supply chain centre");
        List<WareHouse> warehouses = List.of(warehouse);
        List<WareHouseDto> warehouseDtos = List.of(warehouseDto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<WareHouse> wareHousePage = new PageImpl<>(warehouses, pageable, warehouses.size());

        Mockito.when(warehouseRepository.findAll(pageable)).thenReturn(wareHousePage);
        Mockito.when(modelMapper.map(
                Mockito.eq(warehouses),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(warehouseDtos);
        List<WareHouseDto> response = warehouseService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Supply chain centre", response.get(0).getIdentifier());
    }

    @Test
    void findActiveWareHousesTest() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("Supply chain centre");
        wareHouse.setStatus(true);
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("Supply chain centre");
        wareHouseDto.setStatus(true);
        
        List<WareHouse> activeWareHouses = List.of(wareHouse);
        List<WareHouseDto> activeWareHouseDtos = List.of(wareHouseDto);

        Mockito.when(warehouseRepository.findByStatusTrue()).thenReturn(activeWareHouses);
        Mockito.when(modelMapper.map(
                Mockito.eq(activeWareHouses),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(activeWareHouseDtos);
        List<WareHouseDto> response = warehouseService.findActiveWareHouse();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Supply chain centre", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }

    @Test
    void toggleStatusSuccessTest() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("Supply chain centre");
        wareHouse.setStatus(false);
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("Supply chain centre");
        wareHouseDto.setStatus(true); 
        
        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(wareHouse);
        Mockito.when(warehouseRepository.save(wareHouse)).thenReturn(wareHouse);
        Mockito.when(modelMapper.map(wareHouse, WareHouseDto.class)).thenReturn(wareHouseDto);
        WareHouseDto response = warehouseService.toggleStatus("Supply chain centre", true);
        Assertions.assertEquals("Supply chain centre", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setIdentifier("Supply chain centre");
        
        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(null);
        WareHouseDto response = warehouseService.toggleStatus("Supply chain centre", true);
        Assertions.assertNull(response);
        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }
}