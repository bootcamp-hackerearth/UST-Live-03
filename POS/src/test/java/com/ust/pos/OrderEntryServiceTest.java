package com.ust.pos;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.orderentry.service.impl.OrderEntryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEntryServiceTest {

    @InjectMocks
    @Spy
    private OrderEntryServiceImpl orderEntryService;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {

        OrderEntryDto dto = new OrderEntryDto();
        dto.setOrderIdentifier("ORDER1");
        dto.setProductIdentifier("PROD1");
        dto.setQuantity(2);
        dto.setSellingPrice(BigDecimal.valueOf(100));
        dto.setMrpPrice(BigDecimal.valueOf(120));
        dto.setTotalPrice(BigDecimal.valueOf(200));
        dto.setDiscount(BigDecimal.valueOf(40));

        OrderEntryDto response = new OrderEntryDto();

        when(modelMapper.map(any(OrderEntry.class), eq(OrderEntryDto.class))).thenReturn(response);

        OrderEntryDto result = orderEntryService.save(dto);

        assertNotNull(result);

        verify(orderEntryRepository).save(any(OrderEntry.class));

        verify(modelMapper).map(any(OrderEntry.class), eq(OrderEntryDto.class));

    }

    @Test
    void findAllByOrderIdentifierTest() {

        OrderEntry orderEntry = new OrderEntry();

        List<OrderEntry> entries = List.of(orderEntry);

        OrderEntryDto dto = new OrderEntryDto();

        dto.setProductIdentifier("PROD1");

        Product product = new Product();

        ProductDto productDto = new ProductDto();

        when(orderEntryRepository.findAllByOrderIdentifier("ORDER1")).thenReturn(entries);

        when(modelMapper.map(eq(entries), any(Type.class))).thenReturn(List.of(dto));

        when(productRepository.findByIdentifier("PROD1")).thenReturn(product);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        List<OrderEntryDto> result = orderEntryService.findAllByOrderIdentifier("ORDER1");

        assertEquals(1, result.size());

        assertNotNull(result.get(0).getProduct());

        verify(productRepository).findByIdentifier("PROD1");

    }

    @Test
    void findAllByOrderIdentifierProductNullTest() {

        OrderEntry orderEntry = new OrderEntry();

        List<OrderEntry> entries = List.of(orderEntry);

        OrderEntryDto dto = new OrderEntryDto();

        dto.setProductIdentifier(null);

        when(orderEntryRepository.findAllByOrderIdentifier("ORDER1")).thenReturn(entries);

        when(modelMapper.map(eq(entries), any(Type.class))).thenReturn(List.of(dto));

        List<OrderEntryDto> result = orderEntryService.findAllByOrderIdentifier("ORDER1");

        assertEquals(1, result.size());

        assertNull(result.get(0).getProduct());

        verify(productRepository, never()).findByIdentifier(any());

    }

    @Test
    void findAllByOrderIdentifierProductNotFoundTest() {

        OrderEntry orderEntry = new OrderEntry();

        List<OrderEntry> entries = List.of(orderEntry);

        OrderEntryDto dto = new OrderEntryDto();

        dto.setProductIdentifier("PROD1");

        when(orderEntryRepository.findAllByOrderIdentifier("ORDER1")).thenReturn(entries);

        when(modelMapper.map(eq(entries), any(Type.class))).thenReturn(List.of(dto));

        when(productRepository.findByIdentifier("PROD1")).thenReturn(null);

        List<OrderEntryDto> result = orderEntryService.findAllByOrderIdentifier("ORDER1");

        assertEquals(1, result.size());

        assertNull(result.get(0).getProduct());

    }

    @Test
    void findAllByOrderIdentifierEmptyTest() {

        when(orderEntryRepository.findAllByOrderIdentifier("ORDER1")).thenReturn(List.of());

        when(modelMapper.map(eq(List.of()), any(Type.class))).thenReturn(List.of());

        List<OrderEntryDto> result = orderEntryService.findAllByOrderIdentifier("ORDER1");

        assertTrue(result.isEmpty());

    }

    @Test
    void deleteByOrderIdentifierTest() {

        orderEntryService.deleteByOrderIdentifier("ORDER1");

        verify(orderEntryRepository).deleteByOrderIdentifier("ORDER1");

    }

}