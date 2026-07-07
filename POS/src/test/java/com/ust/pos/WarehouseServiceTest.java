package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Warehouse warehouse = new Warehouse();

        Mockito.when(modelMapper.map(warehouseDto, Warehouse.class))
                .thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(warehouseDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveTestFailure() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Warehouse warehouse = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(warehouseDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveDeletedWarehouseFailureTest() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Warehouse warehouse = new Warehouse();
        warehouse.setIsDeleted(true);

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(warehouseDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("was deleted"));
    }

    @Test
    void findByIdentifierTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(warehouse);

        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);

        WarehouseDto response = warehouseService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(existingWarehouse);

        Mockito.when(warehouseRepository.save(existingWarehouse))
                .thenReturn(existingWarehouse);

        WarehouseDto response = warehouseService.update(warehouseDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.update(warehouseDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");
        warehouse.setStatus(true);
        warehouse.setIsDeleted(false);

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.delete("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Warehouse deleted successfully",
                response.getMessage());

        Assertions.assertTrue(warehouse.getIsDeleted());
        Assertions.assertFalse(warehouse.getStatus());

        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.delete("Admin");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findAllTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Warehouse> warehousePage =
                new PageImpl<>(warehouses, pageable, warehouses.size());

        Mockito.when(
                        warehouseRepository.findByIsDeleted(false, pageable))
                .thenReturn(warehousePage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(warehouses),
                        Mockito.any(java.lang.reflect.Type.class)
                )
        ).thenReturn(warehouseDtos);

        PaginatedResponseDto<WarehouseDto> response =
                warehouseService.findAll(pageable);

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals("Admin",
                response.getItems().get(0).getIdentifier());

        Assertions.assertEquals(1,
                response.getTotalRecords());
    }

    @Test
    void findAllActiveTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");
        warehouse.setStatus(true);

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto);

        Mockito.when(
                        warehouseRepository.findByStatusAndIsDeleted(true, false))
                .thenReturn(warehouses);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(warehouses),
                        Mockito.any(java.lang.reflect.Type.class)
                )
        ).thenReturn(warehouseDtos);

        List<WarehouseDto> response =
                warehouseService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");
        warehouse.setStatus(false);

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        warehouseService.changeStatus("Admin", true);

        Assertions.assertTrue(warehouse.getStatus());

        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(warehouseRepository.findByIdentifier("INVALID"))
                .thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> warehouseService.findByIdentifier("INVALID")
        );

        Assertions.assertEquals(
                "Warehouse with identifier - INVALID not found",
                exception.getMessage()
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> page = new PageImpl<>(warehouses, pageable, warehouses.size());

        @SuppressWarnings("unchecked")
        Specification<Warehouse> specification = Mockito.mock(Specification.class);

        Mockito.when(
                warehouseRepository.findAll(
                        Mockito.eq(specification),
                        Mockito.any(Pageable.class)
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.anyList(),
                        Mockito.any(Type.class)
                )
        ).thenReturn(warehouseDtos);

        PaginatedResponseDto<WarehouseDto> response =
                warehouseService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals("Admin", response.getItems().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());

        Mockito.verify(warehouseRepository).findAll(specification, pageable);
    }
}