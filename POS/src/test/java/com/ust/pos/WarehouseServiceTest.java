package com.ust.pos;

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
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;


    @Test
    void saveTestSuccess() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(null);

        Mockito.when(modelMapper.map(dto, Warehouse.class))
                .thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertEquals(
                "WH1",
                response.getIdentifier()
        );

        Mockito.verify(warehouseRepository)
                .save(warehouse);
    }


    @Test
    void saveTestFailureWhenAlreadyExists() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(new Warehouse());

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertNotNull(
                response.getMessage()
        );

        Mockito.verify(
                warehouseRepository,
                Mockito.never()
        ).save(Mockito.any());
    }


    @Test
    void updateTestSuccess() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse existing = new Warehouse();
        existing.setIdentifier("WH1");

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, existing);

        Mockito.when(warehouseRepository.save(existing))
                .thenReturn(existing);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertEquals(
                "WH1",
                response.getIdentifier()
        );

        Mockito.verify(modelMapper)
                .map(dto, existing);

        Mockito.verify(warehouseRepository)
                .save(existing);
    }


    @Test
    void updateTestFailureWhenNotFound() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(null);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertNotNull(
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
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(warehouse);

        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto response =
                warehouseService.findByIdentifier("WH1");

        Assertions.assertEquals(
                "WH1",
                response.getIdentifier()
        );
    }


    @Test
    void findAllTest() {
        List<Warehouse> warehouses =
                List.of(new Warehouse());

        List<WarehouseDto> warehouseDtos =
                List.of(new WarehouseDto());

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        Mockito.when(warehouseRepository.findByDeletedFalse())
                .thenReturn(warehouses);

        Mockito.when(modelMapper.map(warehouses, listType))
                .thenReturn(warehouseDtos);

        List<WarehouseDto> response =
                warehouseService.findAll();

        Assertions.assertEquals(
                1,
                response.size()
        );
    }


    @Test
    void deleteTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        Mockito.when(
                warehouseRepository.findByIdentifierAndDeletedFalse("WH1")
        ).thenReturn(warehouse);

        warehouseService.delete("WH1");

        Assertions.assertTrue(
                warehouse.isDeleted()
        );

        Mockito.verify(warehouseRepository)
                .save(warehouse);
    }


    @Test
    void deleteTestWhenNotFound() {
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
    void findAllWithPaginationShouldReturnWarehouseDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Page<Warehouse> page =
                new PageImpl<>(List.of(warehouse));

        Mockito.when(
                warehouseRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(warehouse, WarehouseDto.class)
        ).thenReturn(dto);

        Page<WarehouseDto> response =
                warehouseService.findAll(pageable, null);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Assertions.assertEquals(
                "WH1",
                response.getContent()
                        .get(0)
                        .getIdentifier()
        );

        Mockito.verify(warehouseRepository)
                .findByDeletedFalse(pageable);
    }


    @Test
    void findAllWithSearchShouldReturnWarehouseDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Page<Warehouse> page =
                new PageImpl<>(List.of(warehouse));

        Mockito.when(
                warehouseRepository
                        .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                                "WH1",
                                pageable
                        )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(warehouse, WarehouseDto.class)
        ).thenReturn(dto);

        Page<WarehouseDto> response =
                warehouseService.findAll(pageable, "WH1");

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Assertions.assertEquals(
                "WH1",
                response.getContent()
                        .get(0)
                        .getIdentifier()
        );

        Mockito.verify(warehouseRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        "WH1",
                        pageable
                );
    }
}