package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Price;
import com.ust.pos.modell.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    public static final String INVALID = "INVALID";
    @InjectMocks
    private PriceServiceImpl service;

    @Mock
    private PriceRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {

        Price price = new Price();
        PriceDto dto = new PriceDto();

        when(repository.findByIdentifierAndDeletedFalse("P-T"))
                .thenReturn(price);

        when(mapper.map(price, PriceDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier("P-T"));

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier(INVALID)
                );

        assertEquals(
                "price with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveTest() {

        PriceDto dto = new PriceDto();
        dto.setProduct("P");
        dto.setType("T");

        Price price = new Price();
        price.setStatus(null);

        when(repository.findByIdentifier("P-T"))
                .thenReturn(null);

        when(mapper.map(dto, Price.class))
                .thenReturn(price);

        PriceDto result = service.save(dto);

        verify(repository).save(price);

        assertTrue(result.isSuccess());
        assertEquals("P-T", result.getIdentifier());
        assertTrue(price.getStatus());

        Price existing = new Price();
        existing.setDeleted(false);

        when(repository.findByIdentifier("P-T"))
                .thenReturn(existing);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Price already exists for product + type",
                result.getMessage()
        );

        existing.setDeleted(true);

        when(repository.findByIdentifier("P-T"))
                .thenReturn(existing);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Price with Identifier P-T already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateTest() {

        PriceDto dto = new PriceDto();
        dto.setIdentifier("OLD");
        dto.setProduct("P");
        dto.setType("T");
        dto.setPriceAmount(BigDecimal.valueOf(100));

        Price existing = new Price();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        existing.setCreatedBy("admin");
        existing.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdentifierAndDeletedFalse("OLD"))
                .thenReturn(existing);

        when(repository.findByIdentifierAndDeletedFalse("P-T"))
                .thenReturn(existing);

        when(mapper.map(existing, PriceDto.class))
                .thenReturn(dto);

        PriceDto result = service.update(dto);

        assertNotNull(result);

        verify(repository).save(existing);

        PriceDto notFoundDto = new PriceDto();
        notFoundDto.setIdentifier(INVALID);

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        result = service.update(notFoundDto);

        assertFalse(result.isSuccess());
        assertEquals("Price not found", result.getMessage());

        PriceDto duplicateDto = new PriceDto();
        duplicateDto.setIdentifier("OLD2");
        duplicateDto.setProduct("P");
        duplicateDto.setType("T");

        Price currentPrice = new Price();
        currentPrice.setId(1L);

        Price duplicatePrice = new Price();
        duplicatePrice.setId(2L);

        when(repository.findByIdentifierAndDeletedFalse("OLD2"))
                .thenReturn(currentPrice);

        when(repository.findByIdentifierAndDeletedFalse("P-T"))
                .thenReturn(duplicatePrice);

        result = service.update(duplicateDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Price already exists for this product and type",
                result.getMessage()
        );
    }

    @Test
    void deleteTest() {

        Price price = new Price();

        when(repository.findByIdentifierAndDeletedFalse("P-T"))
                .thenReturn(price)
                .thenReturn(null);

        service.delete("P-T");

        verify(repository).save(price);

        service.delete("P-T");

        verify(repository, times(1)).save(price);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Price> page =
                new PageImpl<>(
                        List.of(new Price()),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new PriceDto()));

        WsDto<PriceDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        Specification<Price> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<PriceDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());
        assertEquals(1, specResult.getTotalPage());

        verify(repository).findAllByDeletedFalse(pageable);
        verify(repository).findAll(any(Specification.class), eq(pageable));
    }
}