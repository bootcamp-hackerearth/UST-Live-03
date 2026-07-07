package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.WarehouseDto;
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
    void saveTestSuccess() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Warehouse warehouse = new Warehouse();

        Mockito.when(
                warehouseRepository.findByIdentifier("Warehouse1")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(warehouseDto, Warehouse.class)
        ).thenReturn(warehouse);

        WarehouseDto response =
                warehouseService.save(warehouseDto);

        Mockito.verify(modelMapper)
                .map(warehouseDto, Warehouse.class);

        Mockito.verify(warehouseRepository)
                .save(warehouse);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Successfully added the warehouse",
                response.getMessage()
        );
    }

    @Test
    void saveTestFailure() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Warehouse1");
        Warehouse warehouse = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(warehouse);
        WarehouseDto response = warehouseService.save(warehouseDto);

        Assertions.assertEquals("Warehouse1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void saveDeletedWarehouseTest() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("Warehouse1");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");
        warehouse.setDeleted(true);

        Mockito.when(
                warehouseRepository.findByIdentifier("Warehouse1")
        ).thenReturn(warehouse);

        WarehouseDto response =
                warehouseService.save(dto);

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Warehouse Warehouse1 has been deleted. Please contact the administrator.",
                response.getMessage()
        );

        Mockito.verify(
                warehouseRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto response = warehouseService.findByIdentifier("Warehouse1");

        Assertions.assertEquals("Warehouse1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(existingWarehouse);

        Mockito.doNothing()
                .when(modelMapper)
                .map(warehouseDto, existingWarehouse);

        Mockito.when(warehouseRepository.save(existingWarehouse))
                .thenReturn(existingWarehouse);

        WarehouseDto response = warehouseService.update(warehouseDto);

        Mockito.verify(modelMapper).map(warehouseDto, existingWarehouse);
        Mockito.verify(warehouseRepository).save(existingWarehouse);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestFailure() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.update(warehouseDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateDeletedWarehouseTest() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("Warehouse1");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");
        warehouse.setDeleted(true);

        Mockito.when(
                warehouseRepository.findByIdentifier("Warehouse1")
        ).thenReturn(warehouse);

        WarehouseDto response =
                warehouseService.update(dto);

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Warehouse Warehouse1 has been deleted. Please contact the administrator.",
                response.getMessage()
        );

        Mockito.verify(
                warehouseRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void deleteTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");
        warehouse.setDeleted(false);

        Mockito.when(
                warehouseRepository.findByIdentifier("Warehouse1")
        ).thenReturn(warehouse);

        Mockito.when(
                warehouseRepository.save(warehouse)
        ).thenReturn(warehouse);

        warehouseService.delete("Warehouse1");

        Assertions.assertTrue(warehouse.isDeleted());

        Mockito.verify(warehouseRepository)
                .findByIdentifier("Warehouse1");

        Mockito.verify(warehouseRepository)
                .save(warehouse);
    }

    @Test
    void findAllWithPageableTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Warehouse> warehousePage =
                new PageImpl<>(
                        warehouses,
                        pageable,
                        warehouses.size()
                );

        Mockito.when(
                warehouseRepository.findByIsDeletedFalse(pageable)
        ).thenReturn(warehousePage);

        Mockito.when(modelMapper.map(
                Mockito.eq(warehouses),
                Mockito.any(Type.class)
        )).thenReturn(warehouseDtos);

        PaginationResponseDto<WarehouseDto> response =
                warehouseService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Warehouse1",
                response.getDtoList().get(0).getIdentifier()
        );

        Assertions.assertEquals(
                5,
                response.getSizePerPage()
        );

        Assertions.assertEquals(
                1,
                response.getTotalPages()
        );

        Assertions.assertEquals(0, response.getPage());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Specification<Warehouse> specification =
                Mockito.mock(Specification.class);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("Warehouse1");


        Page<Warehouse> page =
                new PageImpl<>(
                        List.of(warehouse),
                        pageable,
                        1
                );


        Mockito.when(
                warehouseRepository.findAll(
                        Mockito.eq(specification),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);


        Mockito.when(
                modelMapper.map(
                        Mockito.eq(page.getContent()),
                        Mockito.any(Type.class)
                )
        ).thenReturn(List.of(dto));

        PaginationResponseDto<WarehouseDto> response =
                warehouseService.findAll(
                        specification,
                        pageable
                );

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Warehouse1",
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
                5,
                response.getSizePerPage()
        );

        Assertions.assertEquals(
                0,
                response.getPage()
        );
    }

    @Test
    void findByStatusTrueTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Warehouse1");

        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto);

        Mockito.when(warehouseRepository.findByStatusTrue())
                .thenReturn(warehouses);

        Mockito.when(modelMapper.map(
                Mockito.eq(warehouses),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(warehouseDtos);

        List<WarehouseDto> response = warehouseService.findByStatusTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void updateStatusTestSuccess() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Warehouse1");
        warehouse.setStatus(false);

        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.updateStatus("Warehouse1", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
        Assertions.assertTrue(warehouse.isStatus());
    }

    @Test
    void updateStatusTestFailure() {
        Mockito.when(warehouseRepository.findByIdentifier("Warehouse1"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.updateStatus("Warehouse1", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Warehouse not found", response.getMessage());
    }
}