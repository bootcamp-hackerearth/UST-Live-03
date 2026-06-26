package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private PriceServiceImpl priceService;

    private Price price;
    private PriceDto priceDto;

    @BeforeEach
    void setUp() {
        price = new Price();
        price.setIdentifier("P1");
        price.setStatus(true);
        price.setDeleted(false);

        priceDto = new PriceDto();
        priceDto.setIdentifier("P1");
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> page = new PageImpl<>(Collections.singletonList(price));

        when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(priceDto));

        WsDto<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    // ✅ SAVE - NEW PRICE
    @Test
    void testSave_NewPrice() {
        when(priceRepository.findByIdentifier("P1")).thenReturn(null);
        when(modelMapper.map(priceDto, Price.class)).thenReturn(price);

        doNothing().when(priceService).setAuditFields(price, true);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        verify(priceRepository).save(price);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(priceRepository.findByIdentifier("P1")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ SAVE - SOFT DELETED
    @Test
    void testSave_SoftDeleted() {
        price.setDeleted(true);

        when(priceRepository.findByIdentifier("P1")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    // ✅ DELETE
    @Test
    void testDelete() {
        when(priceRepository.findByIdentifier("P1")).thenReturn(price);

        doNothing().when(priceService).softDelete(price);
        doNothing().when(priceService).setAuditFields(price, false);

        priceService.delete("P1");

        verify(priceRepository).save(price);
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {
        when(priceRepository.findByIdentifier("P1")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto result = priceService.findByIdentifier("P1");

        assertNotNull(result);
    }

    // ✅ UPDATE
    @Test
    void testUpdate() {
        when(priceRepository.findByIdentifier("P1")).thenReturn(price);

        doNothing().when(modelMapper).map(priceDto, price);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        verify(priceRepository).save(price);
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(priceRepository.findByIdentifier("P1")).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        PriceDto result = priceService.changeToggleStatus("P1", false);

        assertNotNull(result);
        assertFalse(price.isStatus());
        verify(priceRepository).save(price);
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        price.setStatus(true);

        Price inactive = new Price();
        inactive.setStatus(false);

        List<Price> prices = List.of(price, inactive);

        when(priceRepository.findAll()).thenReturn(prices);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(priceDto));

        List<PriceDto> result = priceService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}