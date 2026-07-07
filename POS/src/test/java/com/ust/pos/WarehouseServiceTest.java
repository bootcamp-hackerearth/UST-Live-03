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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    void findAllTestSuccess() {

        List<Warehouse> warehouses = new ArrayList<>();
        warehouses.add(new Warehouse());
        warehouses.add(new Warehouse());

        List<WarehouseDto> dtoList = new ArrayList<>();
        dtoList.add(new WarehouseDto());
        dtoList.add(new WarehouseDto());

        Mockito.when(
                warehouseRepository.findByDeletedFalse()
        ).thenReturn(warehouses);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(warehouses),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<WarehouseDto> response =
                warehouseService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void saveTestSuccess() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(dto, Warehouse.class)
        ).thenReturn(warehouse);

        WarehouseDto response =
                warehouseService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(warehouseRepository)
                .save(warehouse);

        Assertions.assertFalse(warehouse.getDeleted());
    }

    @Test
    void saveTestFail() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(new Warehouse());

        WarehouseDto response =
                warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Warehouse already exists",
                response.getMessage()
        );
    }

    @Test
    void findAll_WithPagination_ShouldReturnWarehouseDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Warehouse> warehouses =
                List.of(new Warehouse());

        Page<Warehouse> page =
                new PageImpl<>(warehouses, pageable, 1);

        List<WarehouseDto> warehouseDtos =
                List.of(new WarehouseDto());

        Type listType =
                new TypeToken<List<WarehouseDto>>() {}.getType();

        Mockito.when(
                warehouseRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(warehouses, listType)
        ).thenReturn(warehouseDtos);

        WsDto<WarehouseDto> response =
                warehouseService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void updateTestSuccess() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(warehouse);

        WarehouseDto response =
                warehouseService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper)
                .map(dto, warehouse);

        Mockito.verify(warehouseRepository)
                .save(warehouse);
    }

    @Test
    void updateTestFail() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(null);

        WarehouseDto response =
                warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Warehouse with identifier - WH1 not found",
                response.getMessage()
        );
    }

    @Test
    void findWarehouseByIdentifierTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(warehouse);

        Mockito.when(
                modelMapper.map(warehouse, WarehouseDto.class)
        ).thenReturn(dto);

        WarehouseDto response =
                warehouseService.findByIdentifier("WH1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "WH1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllSearchTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH");

        Example<Warehouse> example = Example.of(
                warehouse,
                ExampleMatcher.matching()
                        .withMatcher(
                                "identifier",
                                ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()
                        )
        );

        Page<Warehouse> page =
                new PageImpl<>(List.of(warehouse));

        Mockito.when(
                warehouseRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        warehouse,
                        WarehouseDto.class
                )
        ).thenReturn(new WarehouseDto());

        Page<WarehouseDto> response =
                warehouseService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAllWithoutSearchTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse = new Warehouse();

        Example<Warehouse> example = Example.of(new Warehouse());

        Page<Warehouse> page =
                new PageImpl<>(List.of(warehouse));

        Mockito.when(
                warehouseRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        warehouse,
                        WarehouseDto.class
                )
        ).thenReturn(new WarehouseDto());

        Page<WarehouseDto> response =
                warehouseService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void deleteWarehouseTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setDeleted(false);

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(warehouse);

        warehouseService.delete("WH1");

        Assertions.assertTrue(
                warehouse.getDeleted()
        );

        Mockito.verify(warehouseRepository)
                .save(warehouse);
    }

    @Test
    void deleteWarehouseNotFoundTest() {

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(null);

        warehouseService.delete("WH1");

        Mockito.verify(
                warehouseRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void toggleWarehouseStatusSuccess() {

        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(true);

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(warehouse);

        warehouseService.toggleStatus("WH1");

        Assertions.assertFalse(
                warehouse.getStatus()
        );

        Mockito.verify(warehouseRepository)
                .save(warehouse);
    }

    @Test
    void toggleWarehouseStatusNotFound() {

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(null);

        warehouseService.toggleStatus("WH1");

        Mockito.verify(
                warehouseRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
}