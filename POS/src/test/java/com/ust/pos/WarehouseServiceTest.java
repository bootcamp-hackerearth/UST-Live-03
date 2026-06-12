package com.ust.pos;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.WareHouseDto;
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

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(null);

        Warehouse warehouse = new Warehouse();
        Mockito.when(modelMapper.map(warehouseDto, Warehouse.class))
                .thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        WareHouseDto response = wareHouseService.save(warehouseDto);

        Assertions.assertEquals("Warehouse1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(new Warehouse());

        WareHouseDto response = wareHouseService.save(warehouseDto);

        Assertions.assertEquals("Warehouse1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WareHouseDto.class))
                .thenReturn(warehouseDto);

        WareHouseDto response = wareHouseService.findByIdentifier("Warehouse1");

        Assertions.assertEquals("Warehouse1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(existingWarehouse);
        Mockito.when(warehouseRepository.save(existingWarehouse))
                .thenReturn(existingWarehouse);

        WareHouseDto response = wareHouseService.update(warehouseDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(null);

        WareHouseDto response = wareHouseService.update(warehouseDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(warehouseRepository)
                .deleteByIdentifier("Warehouse1");

        wareHouseService.delete("Warehouse1");

        Mockito.verify(warehouseRepository)
                .deleteByIdentifier("Warehouse1");
    }

    @Test
    void toggleStatusTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");
        warehouse.setStatus(true);

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        wareHouseService.toggleStatus("Warehouse1");

        Assertions.assertFalse(warehouse.getStatus());
        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(null);

        wareHouseService.toggleStatus("Warehouse1");

        Mockito.verify(warehouseRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");

        WareHouseDto warehouseDto = new WareHouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Warehouse> warehousePage = new PageImpl<>(List.of(warehouse), pageable, 1);

        Mockito.when(warehouseRepository.findAll(pageable)).thenReturn(warehousePage);

        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();

        Mockito.when(modelMapper.map(Mockito.eq(warehousePage.getContent()), Mockito.eq(listType))).thenReturn(List.of(warehouseDto));

        PageDto<WareHouseDto> response = wareHouseService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(1, response.getDtoList().size());

        Assertions.assertEquals("Warehouse1", response.getDtoList().get(0).getIdentifier());

        Assertions.assertEquals(1, response.getTotalRecords());

        Assertions.assertEquals(1, response.getTotalPages());

        Assertions.assertEquals(10, response.getSizePerPage());

        Assertions.assertEquals(0, response.getPage());
    }
}