package com.ust.pos;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.orderentry.service.impl.OrderEntryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEntryServiceTest {

    @InjectMocks
    private OrderEntryServiceImpl orderEntryService;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveOrderEntrySuccessTest() {

        OrderEntryDto dto = new OrderEntryDto();
        dto.setOrderIdentifier("ORD-UUID");
        dto.setProductIdentifier("SKU001");
        dto.setQuantity(2);
        dto.setSellingPrice(new BigDecimal("100.00"));
        dto.setMrpPrice(new BigDecimal("120.00"));
        dto.setTotalPrice(new BigDecimal("200.00"));
        dto.setDiscount(new BigDecimal("40.00"));

        OrderEntryDto mappedDto = new OrderEntryDto();
        mappedDto.setIdentifier("OE-UUID");

        when(orderEntryRepository.save(any(OrderEntry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(any(OrderEntry.class), eq(OrderEntryDto.class)))
                .thenReturn(mappedDto);

        OrderEntryDto response = orderEntryService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("OE-UUID", response.getIdentifier());

        verify(orderEntryRepository).save(any(OrderEntry.class));
        verify(modelMapper).map(any(OrderEntry.class), eq(OrderEntryDto.class));
    }

    @Test
    void saveOrderEntryWithNullProductIdentifierTest() {

        OrderEntryDto dto = new OrderEntryDto();
        dto.setOrderIdentifier("ORD-UUID");
        dto.setProductIdentifier(null);

        OrderEntryDto mappedDto = new OrderEntryDto();
        mappedDto.setIdentifier("OE-UUID");

        when(orderEntryRepository.save(any(OrderEntry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(any(OrderEntry.class), eq(OrderEntryDto.class)))
                .thenReturn(mappedDto);

        OrderEntryDto response = orderEntryService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("OE-UUID", response.getIdentifier());

        verify(orderEntryRepository).save(any(OrderEntry.class));
        verify(modelMapper).map(any(OrderEntry.class), eq(OrderEntryDto.class));
    }

    @Test
    void findAllByOrderIdentifierSuccessTest() {

        OrderEntry entry = new OrderEntry();
        entry.setIdentifier("OE-UUID1");
        entry.setOrderIdentifier("ORD-UUID");
        entry.setProductIdentifier("SKU001");

        List<OrderEntry> entries = List.of(entry);

        Product product = new Product();
        product.setIdentifier("SKU001");
        product.setProductName("Samsung");

        OrderEntryDto entryDto = new OrderEntryDto();
        entryDto.setIdentifier("OE-UUID1");
        entryDto.setProductIdentifier("SKU001");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("SKU001");
        productDto.setProductName("Samsung");

        List<OrderEntryDto> dtoList = new ArrayList<>(List.of(entryDto));

        when(orderEntryRepository.findAllByOrderIdentifier("ORD-UUID"))
                .thenReturn(entries);

        when(modelMapper.map(eq(entries), any(Type.class)))
                .thenReturn(dtoList);

        when(productRepository.findByIdentifier("SKU001"))
                .thenReturn(product);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(productDto);

        List<OrderEntryDto> response =
                orderEntryService.findAllByOrderIdentifier("ORD-UUID");

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(productDto, response.get(0).getProduct());

        verify(productRepository).findByIdentifier("SKU001");
    }

    @Test
    void findAllByOrderIdentifierEmptyListTest() {

        List<OrderEntry> entries = new ArrayList<>();

        when(orderEntryRepository.findAllByOrderIdentifier("INVALID"))
                .thenReturn(entries);

        when(modelMapper.map(eq(entries), any(Type.class)))
                .thenReturn(new ArrayList<>());

        List<OrderEntryDto> response =
                orderEntryService.findAllByOrderIdentifier("INVALID");

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isEmpty());

        verifyNoInteractions(productRepository);
    }

    @Test
    void findAllByOrderIdentifierNullProductIdentifierTest() {

        OrderEntry entry = new OrderEntry();
        entry.setIdentifier("OE-UUID1");
        entry.setOrderIdentifier("ORD-UUID");
        entry.setProductIdentifier(null);

        List<OrderEntry> entries = List.of(entry);

        OrderEntryDto dto = new OrderEntryDto();
        dto.setIdentifier("OE-UUID1");
        dto.setProductIdentifier(null);

        List<OrderEntryDto> dtoList =
                new ArrayList<>(List.of(dto));

        when(orderEntryRepository.findAllByOrderIdentifier("ORD-UUID"))
                .thenReturn(entries);

        when(modelMapper.map(eq(entries), any(Type.class)))
                .thenReturn(dtoList);

        List<OrderEntryDto> response =
                orderEntryService.findAllByOrderIdentifier("ORD-UUID");

        Assertions.assertEquals(1, response.size());
        Assertions.assertNull(response.get(0).getProduct());

        verifyNoInteractions(productRepository);
    }

    @Test
    void findAllByOrderIdentifierProductNotFoundTest() {

        OrderEntry entry = new OrderEntry();
        entry.setIdentifier("OE-UUID1");
        entry.setOrderIdentifier("ORD-UUID");
        entry.setProductIdentifier("GHOST-SKU");

        List<OrderEntry> entries = List.of(entry);

        OrderEntryDto dto = new OrderEntryDto();
        dto.setIdentifier("OE-UUID1");
        dto.setProductIdentifier("GHOST-SKU");

        List<OrderEntryDto> dtoList =
                new ArrayList<>(List.of(dto));

        when(orderEntryRepository.findAllByOrderIdentifier("ORD-UUID"))
                .thenReturn(entries);

        when(modelMapper.map(eq(entries), any(Type.class)))
                .thenReturn(dtoList);

        when(productRepository.findByIdentifier("GHOST-SKU"))
                .thenReturn(null);

        List<OrderEntryDto> response =
                orderEntryService.findAllByOrderIdentifier("ORD-UUID");

        Assertions.assertEquals(1, response.size());
        Assertions.assertNull(response.get(0).getProduct());
    }


    @Test
    void deleteByOrderIdentifierSuccessTest() {
        orderEntryService.deleteByOrderIdentifier("ORD-UUID");

        verify(orderEntryRepository).deleteByOrderIdentifier("ORD-UUID");
        verify(orderEntryRepository, never()).findAllByOrderIdentifier(any());
        verify(orderEntryRepository, never()).save(any());
    }
}