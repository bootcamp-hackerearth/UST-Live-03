package com.ust.pos;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WareHouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WareHouseServiceImpl wareHouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(null);

        Warehouse warehouse = new Warehouse();

        Mockito.when(modelMapper.map(warehouseDto, Warehouse.class)).thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);

        WareHouseDto response = wareHouseService.save(warehouseDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Warehouse1", response.getIdentifier());

        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveExistingWarehouseTest() {

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("Warehouse1");
        existingWarehouse.setDeleted(false);

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(existingWarehouse);

        WareHouseDto response = wareHouseService.save(warehouseDto);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Warehouse with identifier - Warehouse1 already exists", response.getMessage());

        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedWarehouseTest() {

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("Warehouse1");
        existingWarehouse.setDeleted(true);

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(existingWarehouse);

        WareHouseDto response = wareHouseService.save(warehouseDto);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Warehouse with identifier - Warehouse1 has been soft deleted. Restore it by changing status.", response.getMessage());

        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(warehouse);

        Mockito.when(modelMapper.map(warehouse, WareHouseDto.class)).thenReturn(warehouseDto);

        WareHouseDto response = wareHouseService.findByIdentifier("Warehouse1");

        Assertions.assertNotNull(response);

        Assertions.assertEquals("Warehouse1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> wareHouseService.findByIdentifier("Warehouse1"));

        Assertions.assertEquals("Warehouse with identifier 'Warehouse1' not found", exception.getMessage());

        Mockito.verify(modelMapper, Mockito.never()).map(Mockito.any(), Mockito.eq(WareHouseDto.class));
    }

    @Test
    void updateTest() {

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");
        warehouseDto.setSuccess(true);

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(existingWarehouse);

        Mockito.when(warehouseRepository.save(existingWarehouse)).thenReturn(existingWarehouse);

        WareHouseDto response = wareHouseService.update(warehouseDto);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper).map(warehouseDto, existingWarehouse);

        Mockito.verify(warehouseRepository).save(existingWarehouse);
    }

    @Test
    void updateTestFailure() {

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(null);

        WareHouseDto response = wareHouseService.update(warehouseDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Warehouse with identifier - Warehouse1 not found", response.getMessage());

        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");
        warehouse.setDeleted(false);
        warehouse.setStatus(true);

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);

        boolean result = wareHouseService.delete("Warehouse1");

        Assertions.assertTrue(result);

        Assertions.assertTrue(warehouse.getDeleted());

        Assertions.assertFalse(warehouse.getStatus());

        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void deleteFailureTest() {

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(null);

        boolean result = wareHouseService.delete("Warehouse1");

        Assertions.assertFalse(result);

        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");
        warehouse.setStatus(true);

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);

        wareHouseService.toggleStatus("Warehouse1");

        Assertions.assertFalse(warehouse.getStatus());

        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void toggleStatusTestFailure() {

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(null);

        wareHouseService.toggleStatus("Warehouse1");

        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Warehouse> warehousePage = new PageImpl<>(List.of(warehouse), pageable, 1);

        Mockito.when(warehouseRepository.findByDeletedFalse(pageable)).thenReturn(warehousePage);

        Type listType = new TypeToken<List<WareHouseDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(warehousePage.getContent(), listType)).thenReturn(List.of(warehouseDto));

        PageDto<WareHouseDto> response = wareHouseService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(1, response.getDtoList().size());

        Assertions.assertEquals("Warehouse1", response.getDtoList().get(0).getIdentifier());

        Assertions.assertEquals(1, response.getTotalRecords());

        Assertions.assertEquals(1, response.getTotalPages());

        Assertions.assertEquals(10, response.getSizePerPage());

        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllWithSpecificationTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Warehouse> spec = Mockito.mock(Specification.class);

        Page<Warehouse> warehousePage = new PageImpl<>(List.of(warehouse), pageable, 1);

        Mockito.when(warehouseRepository.findAll(spec, pageable)).thenReturn(warehousePage);

        Type listType = new TypeToken<List<WareHouseDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(warehousePage.getContent(), listType)).thenReturn(List.of(warehouseDto));

        PageDto<WareHouseDto> response = wareHouseService.findAll(spec, pageable, "Warehouse1");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(1, response.getDtoList().size());

        Assertions.assertEquals("Warehouse1", response.getDtoList().get(0).getIdentifier());

        Assertions.assertEquals(1, response.getTotalRecords());

        Assertions.assertEquals(1, response.getTotalPages());

        Assertions.assertEquals(10, response.getSizePerPage());

        Assertions.assertEquals(0, response.getPage());

        Assertions.assertEquals("Warehouse1", response.getKeyword());
    }

    @Test
    void findActiveWarehousesTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");
        warehouse.setStatus(true);

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        List<Warehouse> warehouseList = List.of(warehouse);

        Type listType = new TypeToken<List<WareHouseDto>>() {
                }.getType();

        Mockito.when(warehouseRepository.findByStatusTrue()).thenReturn(warehouseList);

        Mockito.when(modelMapper.map(warehouseList, listType)).thenReturn(List.of(warehouseDto));

        List<WareHouseDto> response = wareHouseService.findActiveWarehouses();

        Assertions.assertNotNull(response);

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("Warehouse1", response.get(0).getIdentifier());
    }
}