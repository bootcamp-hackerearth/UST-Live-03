package com.ust.pos;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.orderentry.impl.OrderEntryServiceImplementation;
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
class OrderEntryServiceTest {

    @InjectMocks
    private OrderEntryServiceImplementation orderEntryService;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveOrderEntrySuccess() {

        OrderEntryDto dto = new OrderEntryDto();
        dto.setIdentifier("OE1");

        OrderEntry entity = new OrderEntry();

        Mockito.when(
                modelMapper.map(dto, OrderEntry.class)
        ).thenReturn(entity);

        OrderEntryDto response =
                orderEntryService.save(dto);

        Assertions.assertEquals(
                "OE1",
                response.getIdentifier()
        );

        Mockito.verify(orderEntryRepository)
                .save(entity);
    }

    @Test
    void updateOrderEntrySuccess() {

        OrderEntryDto dto = new OrderEntryDto();
        dto.setIdentifier("OE1");

        OrderEntry existing =
                new OrderEntry();

        existing.setIdentifier("OE1");

        Mockito.when(
                orderEntryRepository.findByIdentifier("OE1")
        ).thenReturn(existing);

        OrderEntryDto response =
                orderEntryService.update(dto);

        Assertions.assertNull(
                response.getMessage()
        );

        Mockito.verify(modelMapper)
                .map(dto, existing);

        Mockito.verify(orderEntryRepository)
                .save(existing);
    }

    @Test
    void updateOrderEntryNotFound() {

        OrderEntryDto dto = new OrderEntryDto();
        dto.setIdentifier("OE1");

        Mockito.when(
                orderEntryRepository.findByIdentifier("OE1")
        ).thenReturn(null);

        OrderEntryDto response =
                orderEntryService.update(dto);

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "OrderEntry with identifier - OE1 not found",
                response.getMessage()
        );

        Mockito.verify(orderEntryRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteOrderEntryTest() {

        orderEntryService.delete("OE1");

        Mockito.verify(orderEntryRepository)
                .deleteByIdentifier("OE1");
    }

    @Test
    void findAllOrderEntriesTest() {

        List<OrderEntry> entities =
                List.of(
                        new OrderEntry(),
                        new OrderEntry()
                );

        List<OrderEntryDto> dtos =
                List.of(
                        new OrderEntryDto(),
                        new OrderEntryDto()
                );

        Mockito.when(
                orderEntryRepository.findAll()
        ).thenReturn(entities);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(entities),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        List<OrderEntryDto> response =
                orderEntryService.findAll();

        Assertions.assertEquals(
                2,
                response.size()
        );
    }

    @Test
    void findByIdentifierTest() {

        OrderEntry entity =
                new OrderEntry();

        entity.setIdentifier("OE1");

        OrderEntryDto dto =
                new OrderEntryDto();

        dto.setIdentifier("OE1");

        Mockito.when(
                orderEntryRepository.findByIdentifier("OE1")
        ).thenReturn(entity);

        Mockito.when(
                modelMapper.map(
                        entity,
                        OrderEntryDto.class
                )
        ).thenReturn(dto);

        OrderEntryDto response =
                orderEntryService.findByIdentifier("OE1");

        Assertions.assertEquals(
                "OE1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<OrderEntry> entities =
                List.of(new OrderEntry());

        Page<OrderEntry> page =
                new PageImpl<>(entities);

        List<OrderEntryDto> dtoList =
                List.of(new OrderEntryDto());

        Type listType =
                new TypeToken<List<OrderEntryDto>>() {
                }.getType();

        Mockito.when(
                orderEntryRepository.findAll(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        entities,
                        listType
                )
        ).thenReturn(dtoList);

        WsDto<OrderEntryDto> response =
                orderEntryService.findAll(pageable);

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
    void findByOrderIdTest() {

        List<OrderEntry> entities =
                List.of(
                        new OrderEntry(),
                        new OrderEntry()
                );

        List<OrderEntryDto> dtos =
                List.of(
                        new OrderEntryDto(),
                        new OrderEntryDto()
                );

        Mockito.when(
                orderEntryRepository.findByOrderId("ORD1")
        ).thenReturn(entities);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(entities),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        List<OrderEntryDto> response =
                orderEntryService.findByOrderId("ORD1");

        Assertions.assertEquals(
                2,
                response.size()
        );
    }
}