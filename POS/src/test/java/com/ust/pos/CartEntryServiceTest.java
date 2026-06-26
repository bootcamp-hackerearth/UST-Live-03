package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl service;

    @Mock
    private PriceService priceService;

    @Mock
    private CartEntryRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void save_ShouldCreateNewEntry_WhenNotExists() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));
        dto.setDiscount(BigDecimal.ONE);
        PriceDto priceDto = new PriceDto();
        priceDto.setSellingPrice(BigDecimal.TEN);
        Mockito.when(repository.findByIdentifier("C1_P1"))
                .thenReturn(null);
        Mockito.when(priceService.findByIdentifier("P1"))
                .thenReturn(priceDto);
        Mockito.when(repository.save(Mockito.any()))
                .thenAnswer(inv -> inv.getArgument(0));
        CartEntryDto response = service.save(dto);
        assertNotNull(response);
        assertEquals("C1_P1", response.getIdentifier());
        assertEquals(BigDecimal.valueOf(19), response.getTotalPrice());
        Mockito.verify(repository).save(Mockito.any());
    }

    @Test
    void save_ShouldAddQuantity_WhenEntryExists() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));
        dto.setDiscount(BigDecimal.ZERO);
        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.valueOf(3));
        PriceDto priceDto = new PriceDto();
        priceDto.setSellingPrice(BigDecimal.TEN);
        Mockito.when(repository.findByIdentifier("C1_P1"))
                .thenReturn(existing);
        Mockito.when(priceService.findByIdentifier("P1"))
                .thenReturn(priceDto);
        Mockito.when(repository.save(Mockito.any()))
                .thenAnswer(inv -> inv.getArgument(0));
        CartEntryDto response = service.save(dto);
        assertEquals(BigDecimal.valueOf(5), response.getQuantity());
        assertEquals(BigDecimal.valueOf(50), response.getTotalPrice());
        Mockito.verify(repository).save(existing);
    }

    @Test
    void save_ShouldHandleNullDiscount() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.valueOf(2));
        PriceDto priceDto = new PriceDto();
        priceDto.setSellingPrice(BigDecimal.TEN);
        Mockito.when(repository.findByIdentifier("C1_P1"))
                .thenReturn(null);
        Mockito.when(priceService.findByIdentifier("P1"))
                .thenReturn(priceDto);
        Mockito.when(repository.save(Mockito.any()))
                .thenAnswer(inv -> inv.getArgument(0));
        CartEntryDto response = service.save(dto);
        assertEquals(BigDecimal.valueOf(20), response.getTotalPrice());
    }

    @Test
    void update_ShouldFail_WhenNotFound() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("X1");
        Mockito.when(repository.findByIdentifier("X1"))
                .thenReturn(null);
        CartEntryDto response = service.update(dto);
        assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void update_ShouldSucceed_WhenValid() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("X1");
        CartEntry existing = new CartEntry();
        existing.setIdentifier("X1");
        CartEntry mapped = new CartEntry();
        mapped.setIdentifier("X1");
        Mockito.when(repository.findByIdentifier("X1"))
                .thenReturn(existing);
        Mockito.when(mapper.map(dto, CartEntry.class))
                .thenReturn(mapped);
        Mockito.when(repository.save(mapped))
                .thenReturn(mapped);
        CartEntryDto response = service.update(dto);
        assertNotNull(response);
        Mockito.verify(repository).save(mapped);
    }

    @Test
    void delete_ShouldCallRepository() {
        service.delete("X1");
        Mockito.verify(repository).deleteByIdentifier("X1");
    }

    @Test
    void findByIdentifier_ShouldReturnDto() {
        CartEntry entity = new CartEntry();
        CartEntryDto dto = new CartEntryDto();
        Mockito.when(repository.findByIdentifier("X1"))
                .thenReturn(entity);
        Mockito.when(mapper.map(entity, CartEntryDto.class))
                .thenReturn(dto);
        CartEntryDto response = service.findByIdentifier("X1");
        assertNotNull(response);
    }

    @Test
    void findByIdentifier_ShouldReturnNull_WhenNotFound() {
        Mockito.when(repository.findByIdentifier("X1"))
                .thenReturn(null);
        Mockito.when(mapper.map(null, CartEntryDto.class))
                .thenReturn(null);
        assertNull(service.findByIdentifier("X1"));
    }

    @Test
    void findAll_ShouldReturnList() {
        List<CartEntry> entities = List.of(new CartEntry());
        List<CartEntryDto> dtos = List.of(new CartEntryDto());
        Type type = new org.modelmapper.TypeToken<List<CartEntryDto>>() {}.getType();
        Mockito.when(repository.findAll())
                .thenReturn(entities);
        Mockito.when(mapper.map(entities, type))
                .thenReturn(dtos);
        List<CartEntryDto> result = service.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void findAll_WithSearch_ShouldReturnFiltered() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CartEntry> page = new PageImpl<>(List.of(new CartEntry()));
        Mockito.when(repository.findByProductContainingIgnoreCase("P", pageable))
                .thenReturn(page);
        Mockito.when(mapper.map(Mockito.any(CartEntry.class), Mockito.eq(CartEntryDto.class)))
                .thenReturn(new CartEntryDto());
        Page<CartEntryDto> result = service.findAll(pageable, "P");
        assertEquals(1, result.getContent().size());
    }

    @Test
    void findByCartId_ShouldReturnList() {
        List<CartEntry> entities = List.of(new CartEntry());
        List<CartEntryDto> dtos = List.of(new CartEntryDto());
        Type type = new org.modelmapper.TypeToken<List<CartEntryDto>>() {}.getType();
        Mockito.when(repository.findByCartId("C1"))
                .thenReturn(entities);
        Mockito.when(mapper.map(entities, type))
                .thenReturn(dtos);
        List<CartEntryDto> result = service.findByCartId("C1");
        assertEquals(1, result.size());
    }

    @Test
    void decrease_ShouldReduceQuantity() {
        CartEntry entity = new CartEntry();
        entity.setQuantity(BigDecimal.valueOf(5));
        entity.setUnitPrice(BigDecimal.TEN);
        Mockito.when(repository.findByIdentifier("X1"))
                .thenReturn(entity);
        Mockito.when(mapper.map(entity, CartEntryDto.class))
                .thenReturn(new CartEntryDto());
        CartEntryDto response = service.decreaseQuantity("X1");
        assertEquals(BigDecimal.valueOf(4), entity.getQuantity());
        assertEquals(BigDecimal.valueOf(40), entity.getTotalPrice());
        Mockito.verify(repository).save(entity);
        assertNotNull(response);
    }

    @Test
    void decrease_ShouldDelete_WhenQuantityOne() {
        CartEntry entity = new CartEntry();
        entity.setQuantity(BigDecimal.ONE);
        Mockito.when(repository.findByIdentifier("X1"))
                .thenReturn(entity);
        CartEntryDto response = service.decreaseQuantity("X1");
        assertNull(response);
        Mockito.verify(repository).delete(entity);
    }

    @Test
    void decrease_ShouldReturnNull_WhenNotFound() {
        Mockito.when(repository.findByIdentifier("X1"))
                .thenReturn(null);
        CartEntryDto response = service.decreaseQuantity("X1");
        assertNull(response);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }
}