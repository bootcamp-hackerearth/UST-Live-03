package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderDto buildOrderDto(boolean withEntries) {
        OrderDto dto = new OrderDto();
        dto.setInvoiceCode("INV-TEST-1234");
        dto.setCustomerIdentifier("9876543210");
        dto.setWarehouseIdentifier("WH-001");
        dto.setOriginalPrice(BigDecimal.valueOf(200.0));
        dto.setTotalDiscount(BigDecimal.valueOf(20.0));
        dto.setTotalPrice(BigDecimal.valueOf(180.0));
        dto.setAmountReceived(BigDecimal.valueOf(200.0));
        dto.setChangeAmount(BigDecimal.valueOf(20.0));
        dto.setDueAmount(BigDecimal.valueOf(0.0));
        dto.setPaymentMethod("Cash");
        dto.setTransactionNotes("POS Sale");
        dto.setCouponCode("POS_SALE");

        if (withEntries) {
            OrderEntryDto entry = new OrderEntryDto();
            entry.setProduct("Apple");
            entry.setQuantity(BigDecimal.valueOf(2));
            entry.setPrice(BigDecimal.valueOf(50.0));
            entry.setSellingPrice(BigDecimal.valueOf(45.0));
            entry.setDiscount(BigDecimal.valueOf(5.0));
            entry.setTotalPrice(BigDecimal.valueOf(90.0));
            entry.setCouponCode("NONE");
            dto.setEntryList(List.of(entry));
        }

        return dto;
    }

    private Order buildOrder(String identifier) {
        Order order = new Order();
        order.setIdentifier(identifier);
        order.setInvoiceCode("INV-TEST-1234");
        order.setCustomerIdentifier("9876543210");
        order.setStatus(true);
        order.setCreatedOn(LocalDateTime.now());
        order.setModifiedOn(LocalDateTime.now());
        return order;
    }

    private OrderEntry buildOrderEntry(String identifier, String orderIdentifier) {
        OrderEntry entry = new OrderEntry();
        entry.setIdentifier(identifier);
        entry.setOrderIdentifier(orderIdentifier);
        entry.setProduct("Apple");
        entry.setQuantity(BigDecimal.valueOf(2));
        entry.setSellingPrice(BigDecimal.valueOf(45.0));
        entry.setTotalPrice(BigDecimal.valueOf(90.0));
        entry.setStatus(true);
        entry.setCreatedOn(LocalDateTime.now());
        return entry;
    }

    @Test
    @DisplayName("processCheckout — happy path with entries: saves order, entries, deletes cart, returns DTO")
    void processCheckout_withEntries_savesAllAndDeletesCart() {
        OrderDto inputDto = buildOrderDto(true);
        Order orderEntity = buildOrder("generated-uuid");
        OrderEntry savedEntry = buildOrderEntry("entry-uuid", "generated-uuid");
        OrderEntryDto savedEntryDto = new OrderEntryDto();
        savedEntryDto.setProduct("Apple");
        OrderDto expectedResponse = new OrderDto();
        expectedResponse.setInvoiceCode("INV-TEST-1234");
        expectedResponse.setEntryList(List.of(savedEntryDto));

        when(modelMapper.map(inputDto, Order.class)).thenReturn(orderEntity);
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(orderEntity);
        when(modelMapper.map(any(OrderEntryDto.class), eq(OrderEntry.class))).thenReturn(savedEntry);
        when(orderEntryRepository.saveAndFlush(any(OrderEntry.class))).thenReturn(savedEntry);
        when(modelMapper.map(savedEntry, OrderEntryDto.class)).thenReturn(savedEntryDto);
        when(modelMapper.map(orderEntity, OrderDto.class)).thenReturn(expectedResponse);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(savedEntryDto));

        OrderDto result = orderService.processCheckout(inputDto);

        verify(orderRepository).saveAndFlush(orderEntity);
        verify(orderEntryRepository).saveAndFlush(savedEntry);
        verify(cartRepository).deleteByIdentifier("9876543210");
        assertThat(result).isNotNull();
        assertThat(result.getEntryList()).hasSize(1);
        assertThat(result.getEntryList().get(0).getProduct()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("processCheckout — sets system fields on order before saving")
    void processCheckout_setsAuditFieldsOnOrder() {
        OrderDto inputDto = buildOrderDto(false);
        Order orderEntity = new Order();

        when(modelMapper.map(inputDto, Order.class)).thenReturn(orderEntity);
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(orderEntity);
        when(modelMapper.map(orderEntity, OrderDto.class)).thenReturn(new OrderDto());
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(Collections.emptyList());

        orderService.processCheckout(inputDto);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).saveAndFlush(captor.capture());

        Order captured = captor.getValue();
        assertThat(captured.getIdentifier()).isNotBlank();
        assertThat(captured.getCreatedOn()).isNotNull();
        assertThat(captured.getModifiedOn()).isNotNull();
        assertThat(captured.isStatus()).isTrue();
        assertThat(captured.getCreatedBy()).isEqualTo("SYSTEM");
    }

    @Test
    @DisplayName("processCheckout — sets system fields on each order entry before saving")
    void processCheckout_setsAuditFieldsOnOrderEntry() {
        OrderDto inputDto = buildOrderDto(true);
        Order order = buildOrder("order-uuid");
        OrderEntry entryEntity = new OrderEntry();

        when(modelMapper.map(inputDto, Order.class)).thenReturn(order);
        when(orderRepository.saveAndFlush(any())).thenReturn(order);
        when(modelMapper.map(any(OrderEntryDto.class), eq(OrderEntry.class))).thenReturn(entryEntity);
        when(orderEntryRepository.saveAndFlush(any())).thenReturn(entryEntity);
        when(modelMapper.map(entryEntity, OrderEntryDto.class)).thenReturn(new OrderEntryDto());
        when(modelMapper.map(order, OrderDto.class)).thenReturn(new OrderDto());
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(Collections.emptyList());

        orderService.processCheckout(inputDto);

        ArgumentCaptor<OrderEntry> captor = ArgumentCaptor.forClass(OrderEntry.class);
        verify(orderEntryRepository).saveAndFlush(captor.capture());

        OrderEntry captured = captor.getValue();
        assertThat(captured.getIdentifier()).isNotBlank();
        assertThat(captured.getOrderIdentifier()).isEqualTo(order.getIdentifier());
        assertThat(captured.getCreatedOn()).isNotNull();
        assertThat(captured.getModifiedOn()).isNotNull();
        assertThat(captured.isStatus()).isTrue();
        assertThat(captured.getCreatedBy()).isEqualTo("SYSTEM");
    }

    @Test
    @DisplayName("processCheckout — no entries in DTO: skips entry saving, still deletes cart")
    void processCheckout_noEntries_stillDeletesCart() {
        OrderDto inputDto = buildOrderDto(false);
        Order orderEntity = buildOrder("uuid-no-entries");

        when(modelMapper.map(inputDto, Order.class)).thenReturn(orderEntity);
        when(orderRepository.saveAndFlush(any())).thenReturn(orderEntity);
        when(modelMapper.map(orderEntity, OrderDto.class)).thenReturn(new OrderDto());
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(Collections.emptyList());

        orderService.processCheckout(inputDto);

        verifyNoInteractions(orderEntryRepository);
        verify(cartRepository).deleteByIdentifier("9876543210");
    }

    @Test
    @DisplayName("processCheckout — empty entryList: saves order, no entries, deletes cart")
    void processCheckout_emptyEntryList_savesOrderAndDeletesCart() {
        OrderDto inputDto = buildOrderDto(false);
        inputDto.setEntryList(Collections.emptyList());
        Order orderEntity = buildOrder("uuid-empty-entries");

        when(modelMapper.map(inputDto, Order.class)).thenReturn(orderEntity);
        when(orderRepository.saveAndFlush(any())).thenReturn(orderEntity);
        when(modelMapper.map(orderEntity, OrderDto.class)).thenReturn(new OrderDto());
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(Collections.emptyList());

        OrderDto result = orderService.processCheckout(inputDto);

        verify(orderRepository).saveAndFlush(any());
        verifyNoInteractions(orderEntryRepository);
        verify(cartRepository).deleteByIdentifier("9876543210");
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("processCheckout — multiple entries: each gets unique identifier and correct orderIdentifier")
    void processCheckout_multipleEntries_eachGetsUniqueIdentifier() {
        OrderEntryDto e1 = new OrderEntryDto();
        e1.setProduct("Apple");
        OrderEntryDto e2 = new OrderEntryDto();
        e2.setProduct("Mango");

        OrderDto inputDto = buildOrderDto(false);
        inputDto.setEntryList(List.of(e1, e2));

        Order order = buildOrder("order-multi");

        OrderEntry oe1 = new OrderEntry();
        OrderEntry oe2 = new OrderEntry();

        when(modelMapper.map(inputDto, Order.class)).thenReturn(order);
        when(orderRepository.saveAndFlush(any())).thenReturn(order);
        when(modelMapper.map(eq(e1), eq(OrderEntry.class))).thenReturn(oe1);
        when(modelMapper.map(eq(e2), eq(OrderEntry.class))).thenReturn(oe2);
        when(orderEntryRepository.saveAndFlush(oe1)).thenReturn(oe1);
        when(orderEntryRepository.saveAndFlush(oe2)).thenReturn(oe2);
        when(modelMapper.map(oe1, OrderEntryDto.class)).thenReturn(new OrderEntryDto());
        when(modelMapper.map(oe2, OrderEntryDto.class)).thenReturn(new OrderEntryDto());
        when(modelMapper.map(order, OrderDto.class)).thenReturn(new OrderDto());
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(Collections.emptyList());

        orderService.processCheckout(inputDto);

        ArgumentCaptor<OrderEntry> captor = ArgumentCaptor.forClass(OrderEntry.class);
        verify(orderEntryRepository, times(2)).saveAndFlush(captor.capture());

        List<OrderEntry> captured = captor.getAllValues();

        captured.forEach(e -> assertThat(e.getOrderIdentifier()).isNotBlank());
        captured.forEach(e -> assertThat(e.getIdentifier()).isNotBlank());
        assertThat(captured.get(0).getIdentifier()).isNotEqualTo(captured.get(1).getIdentifier());
    }

    @Test
    @DisplayName("processCheckout — each call generates a unique order identifier (UUID)")
    void processCheckout_eachCallGeneratesUniqueOrderUUID() {
        OrderDto dto1 = buildOrderDto(false);
        OrderDto dto2 = buildOrderDto(false);

        Order order1 = new Order();
        Order order2 = new Order();

        when(modelMapper.map(dto1, Order.class)).thenReturn(order1);
        when(modelMapper.map(dto2, Order.class)).thenReturn(order2);
        when(orderRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(new OrderDto());
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(Collections.emptyList());

        orderService.processCheckout(dto1);
        orderService.processCheckout(dto2);

        assertThat(order1.getIdentifier()).isNotEqualTo(order2.getIdentifier());
    }

    @Test
    @DisplayName("getOrderDetails — existing identifier: returns DTO with entries")
    void getOrderDetails_existingIdentifier_returnsDtoWithEntries() {
        String id = "existing-order-id";
        Order order = buildOrder(id);
        OrderEntry entry = buildOrderEntry("entry-1", id);
        OrderDto dto = new OrderDto();
        dto.setInvoiceCode("INV-TEST-1234");

        when(orderRepository.findByIdentifier(id)).thenReturn(order);
        when(orderEntryRepository.findByOrderIdentifier(id)).thenReturn(List.of(entry));
        when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(new OrderEntryDto()));

        OrderDto result = orderService.getOrderDetails(id);

        assertThat(result).isNotNull();
        assertThat(result.getInvoiceCode()).isEqualTo("INV-TEST-1234");
        assertThat(result.getEntryList()).hasSize(1);
        verify(orderRepository).findByIdentifier(id);
        verify(orderEntryRepository).findByOrderIdentifier(id);
    }

    @Test
    @DisplayName("getOrderDetails — non-existent identifier: throws IllegalArgumentException")
    void getOrderDetails_nonExistentIdentifier_throwsException() {
        when(orderRepository.findByIdentifier("ghost-id")).thenReturn(null);

        assertThatThrownBy(() -> orderService.getOrderDetails("ghost-id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Requested invoice does not exist.");

        verifyNoInteractions(orderEntryRepository);
    }

    @Test
    @DisplayName("getOrderDetails — order with no entries: returns DTO with empty entry list")
    void getOrderDetails_orderWithNoEntries_returnsEmptyEntryList() {
        String id = "order-no-entries";
        Order order = buildOrder(id);
        OrderDto dto = new OrderDto();

        when(orderRepository.findByIdentifier(id)).thenReturn(order);
        when(orderEntryRepository.findByOrderIdentifier(id)).thenReturn(Collections.emptyList());
        when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(Collections.emptyList());

        OrderDto result = orderService.getOrderDetails(id);

        assertThat(result.getEntryList()).isEmpty();
    }

    @Test
    @DisplayName("getAllOrdersList — returns mapped list of all orders")
    void getAllOrdersList_returnsAllOrders() {
        Order o1 = buildOrder("id-1");
        Order o2 = buildOrder("id-2");

        OrderDto d1 = new OrderDto();
        d1.setInvoiceCode("INV-001");
        OrderDto d2 = new OrderDto();
        d2.setInvoiceCode("INV-002");

        when(orderRepository.findAll()).thenReturn(List.of(o1, o2));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(d1, d2));

        List<OrderDto> result = orderService.getAllOrdersList();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(OrderDto::getInvoiceCode)
                .containsExactly("INV-001", "INV-002");
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("getAllOrdersList — no orders in DB: returns empty list")
    void getAllOrdersList_noOrders_returnsEmptyList() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(Collections.emptyList());

        List<OrderDto> result = orderService.getAllOrdersList();

        assertThat(result).isEmpty();
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("getAllOrdersList — repository throws: exception propagates")
    void getAllOrdersList_repositoryThrows_propagatesException() {
        when(orderRepository.findAll()).thenThrow(new RuntimeException("DB connection lost"));

        assertThatThrownBy(() -> orderService.getAllOrdersList())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB connection lost");
    }
}