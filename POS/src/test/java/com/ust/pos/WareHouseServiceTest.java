package com.ust.pos;

import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.WareHouse;
import com.ust.pos.model.WareHouseRepository;
import com.ust.pos.warehouse.service.impl.WareHouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

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
        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("Supply chain centre");

        WareHouse warehouse = new WareHouse();

        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, WareHouse.class)).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        WareHouseDto response = warehouseService.save(dto);
        Assertions.assertEquals("Supply chain centre", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveFailure_existingActive() {
        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("Supply chain centre");

        WareHouse existing = new WareHouse();
        existing.setDeleted(false);

        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(existing);
        WareHouseDto response = warehouseService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("Supply chain centre");

        WareHouse existing = new WareHouse();
        existing.setDeleted(true);

        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(existing);
        WareHouseDto response = warehouseService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        WareHouse warehouse = new WareHouse();
        warehouse.setIdentifier("Supply chain centre");

        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("Supply chain centre");

        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WareHouseDto.class)).thenReturn(dto);
        WareHouseDto response = warehouseService.findByIdentifier("Supply chain centre");
        Assertions.assertEquals("Supply chain centre", response.getIdentifier());
    }

    @Test
    void updateTest() {
        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("Supply chain centre");

        WareHouse existing = new WareHouse();
        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(warehouseRepository.save(existing)).thenReturn(existing);

        WareHouseDto response = warehouseService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(warehouseRepository).save(existing);
    }

    @Test
    void updateFailureTest() {
        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("Supply chain centre");
        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(null);
        WareHouseDto response = warehouseService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        WareHouse warehouse = new WareHouse();
        warehouse.setDeleted(false);

        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        warehouseService.delete("Supply chain centre");
        Mockito.verify(warehouseRepository).findByIdentifier("Supply chain centre");
        Mockito.verify(warehouseRepository).save(warehouse);
        Assertions.assertTrue(warehouse.isDeleted());
    }

    @Test
    void findAllTest() {
        WareHouse warehouse = new WareHouse();
        warehouse.setIdentifier("Supply chain centre");

        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("Supply chain centre");

        List<WareHouse> list = List.of(warehouse);
        Page<WareHouse> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(warehouseRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        WsDto<WareHouseDto> response = warehouseService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Supply chain centre", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findActiveWareHousesTest() {
        WareHouse warehouse = new WareHouse();
        warehouse.setIdentifier("Supply chain centre");
        warehouse.setStatus(true);

        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("Supply chain centre");
        dto.setStatus(true);
        List<WareHouse> list = List.of(warehouse);

        Mockito.when(warehouseRepository.findByStatusTrue()).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        List<WareHouseDto> response = warehouseService.findActiveWareHouse();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Supply chain centre", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }

    @Test
    void toggleStatusSuccessTest() {
        WareHouse warehouse = new WareHouse();
        warehouse.setIdentifier("Supply chain centre");
        warehouse.setStatus(false);

        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("Supply chain centre");
        dto.setStatus(true);

        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WareHouseDto.class)).thenReturn(dto);
        WareHouseDto response = warehouseService.toggleStatus("Supply chain centre", true);
        Assertions.assertEquals("Supply chain centre", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(warehouseRepository.findByIdentifier("Supply chain centre")).thenReturn(null);
        WareHouseDto response = warehouseService.toggleStatus("Supply chain centre", true);
        Assertions.assertNull(response);
        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }
}