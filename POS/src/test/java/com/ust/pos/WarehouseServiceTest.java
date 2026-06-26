package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
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

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Lays Warehouse");

        Mockito.when(
                warehouseRepository.findByIdentifier("Lays Warehouse")
        ).thenReturn(null);

        Warehouse warehouse = new Warehouse();

        Mockito.when(
                modelMapper.map(warehouseDto, Warehouse.class)
        ).thenReturn(warehouse);

        Mockito.when(
                warehouseRepository.save(warehouse)
        ).thenReturn(warehouse);

        WarehouseDto response =
                warehouseService.save(warehouseDto);

        Assertions.assertEquals(
                "Lays Warehouse",
                response.getIdentifier()
        );

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Lays Warehouse");

        Warehouse warehouse = new Warehouse();

        Mockito.when(
                warehouseRepository.findByIdentifier("Lays Warehouse")
        ).thenReturn(warehouse);

        WarehouseDto response =
                warehouseService.save(warehouseDto);

        Assertions.assertEquals(
                "Lays Warehouse",
                response.getIdentifier()
        );

        Assertions.assertNotNull(
                response.getMessage(),
                "Message cannot be null"
        );

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Lays Warehouse");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Lays Warehouse");

        Mockito.when(
                warehouseRepository.findByIdentifier("Lays Warehouse")
        ).thenReturn(warehouse);

        Mockito.when(
                modelMapper.map(warehouse, WarehouseDto.class)
        ).thenReturn(warehouseDto);

        WarehouseDto response =
                warehouseService.findByIdentifier("Lays Warehouse");

        Assertions.assertEquals(
                "Lays Warehouse",
                response.getIdentifier()
        );
    }

    @Test
    void updateTest() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Lays Warehouse");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("Lays Warehouse");

        Mockito.when(
                warehouseRepository.findByIdentifier("Lays Warehouse")
        ).thenReturn(existingWarehouse);

        Mockito.when(
                warehouseRepository.save(existingWarehouse)
        ).thenReturn(existingWarehouse);

        WarehouseDto response =
                warehouseService.update(warehouseDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Warehouse updated successfully",
                response.getMessage()
        );
    }

    @Test
    void updateTestFailure() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Lays Warehouse");

        Mockito.when(
                warehouseRepository.findByIdentifier("Lays Warehouse")
        ).thenReturn(null);

        WarehouseDto response =
                warehouseService.update(warehouseDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Lays Warehouse");

        Mockito.when(
                warehouseRepository.findByIdentifier("Lays Warehouse")
        ).thenReturn(warehouse);

        Mockito.when(
                warehouseRepository.save(Mockito.any(Warehouse.class))
        ).thenReturn(warehouse);

        warehouseService.delete("Lays Warehouse");

        Mockito.verify(warehouseRepository)
                .save(Mockito.any(Warehouse.class));

        Assertions.assertTrue(warehouse.isDeleted());
    }

    @Test
    void findAllWithPageableTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Lays Warehouse");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("Lays Warehouse");

        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Warehouse> warehousePage =
                new PageImpl<>(
                        warehouses,
                        pageable,
                        warehouses.size()
                );

        Mockito.when(
                warehouseRepository.findByDeletedFalse(pageable)
        ).thenReturn(warehousePage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(warehouses),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<WarehouseDto> response =
                warehouseService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Lays Warehouse",
                response.getDtoList().get(0).getIdentifier()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Lays Warehouse");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("Lays Warehouse");

        List<WarehouseDto> dtos = List.of(dto);

        Page<Warehouse> warehousePage =
                new PageImpl<>(List.of(warehouse));

        Mockito.when(
                warehouseRepository.findByDeletedFalse(null)
        ).thenReturn(warehousePage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(warehousePage),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<WarehouseDto> response =
                warehouseService.findAll(null);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getDtoList());

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Lays Warehouse",
                response.getDtoList().get(0).getIdentifier()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }
}