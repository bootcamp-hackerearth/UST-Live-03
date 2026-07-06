package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PriceServiceImpl priceService;
    private Price price;
    private PriceDto priceDto;

    @Test
    void findAll_ShouldReturnWsDtoWithPriceDtos() {
        Specification<Price> specification = (root, query, criteriaBuilder) -> null;
        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();
        price.setId(1L);
        price.setIdentifier("PRICE001");

        List<Price> priceList = List.of(price);
        Page<Price> pricePage =
                new PageImpl<>(priceList, pageable, priceList.size());

        PriceDto priceDto = new PriceDto();
        priceDto.setId(1L);
        priceDto.setIdentifier("PRICE001");

        List<PriceDto> priceDtoList = List.of(priceDto);

        when(priceRepository.findAll(specification, pageable))
                .thenReturn(pricePage);

        when(modelMapper.map(eq(priceList), any(Type.class)))
                .thenReturn(priceDtoList);

        WsDto<PriceDto> result = priceService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(priceDtoList, result.getDtoList());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(priceRepository, times(1)).findAll(specification, pageable);
        verify(modelMapper, times(1)).map(eq(priceList), any(Type.class));
    }

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "PROD-001_SELLING_PRICE";

        Price price = new Price();
        price.setIdentifier(identifier);

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier(identifier);

        when(priceRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(price);
        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        PriceDto result = priceService.findByIdentifier(identifier);

        assertNotNull(result);
        assertEquals(identifier, result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_NotFound() {
        String identifier = "PROD-001_SELLING_PRICE";

        when(priceRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(null);

        PriceDto result = priceService.findByIdentifier(identifier);

        assertNull(result);
    }

    @Test
    void testSave_Success() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProductIdentifier("PROD-001");
        priceDto.setPriceType("SELLING_PRICE");
        priceDto.setPriceAmount(BigDecimal.valueOf(100.0));

        Price price = new Price();
        price.setIdentifier("PROD-001_SELLING_PRICE");

        when(priceRepository.findByIdentifier("PROD-001_SELLING_PRICE"))
                .thenReturn(null);
        when(modelMapper.map(priceDto, Price.class))
                .thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertEquals("PROD-001_SELLING_PRICE", result.getIdentifier());
        verify(priceRepository).save(price);
    }

    @Test
    void testSave_AlreadyExists() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProductIdentifier("PROD-001");
        priceDto.setPriceType("SELLING_PRICE");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("PROD-001_SELLING_PRICE");
        existingPrice.setDeleted(false);

        when(priceRepository.findByIdentifier("PROD-001_SELLING_PRICE"))
                .thenReturn(existingPrice);

        PriceDto result = priceService.save(priceDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Price with identifier - PROD-001_SELLING_PRICE already exists",
                result.getMessage()
        );

        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void testSave_DeletedPriceExists() {
        PriceDto priceDto = new PriceDto();
        priceDto.setProductIdentifier("PROD-001");
        priceDto.setPriceType("SELLING_PRICE");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("PROD-001_SELLING_PRICE");
        existingPrice.setDeleted(true);

        when(priceRepository.findByIdentifier("PROD-001_SELLING_PRICE"))
                .thenReturn(existingPrice);

        PriceDto result = priceService.save(priceDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Price with identifier - PROD-001_SELLING_PRICE was deleted and cannot be created again.",
                result.getMessage()
        );

        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void testUpdate_Success() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PROD-001_SELLING_PRICE");
        priceDto.setProductIdentifier("PROD-001");
        priceDto.setPriceType("MRP");
        priceDto.setPriceAmount(BigDecimal.valueOf(150.0));

        Price existingPrice = new Price();
        existingPrice.setId(1L);
        existingPrice.setIdentifier("PROD-001_SELLING_PRICE");
        existingPrice.setProductIdentifier("PROD-001, Product Name");

        when(priceRepository.findByIdentifierAndDeletedFalse("PROD-001_SELLING_PRICE"))
                .thenReturn(existingPrice);

        when(priceRepository.findByIdentifierAndDeletedFalse("PROD-001_MRP"))
                .thenReturn(existingPrice);

        PriceDto result = priceService.update(priceDto);

        assertEquals("PROD-001_MRP", result.getIdentifier());
        assertEquals("MRP", existingPrice.getPriceType());
        assertEquals(BigDecimal.valueOf(150.0), existingPrice.getPriceAmount());
        assertEquals("PROD-001_MRP", existingPrice.getIdentifier());

        verify(priceRepository).save(existingPrice);
    }

    @Test
    void testUpdate_NotFound() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PROD-001_SELLING_PRICE");
        priceDto.setProductIdentifier("PROD-001");
        priceDto.setPriceType("MRP");

        when(priceRepository.findByIdentifierAndDeletedFalse("PROD-001_SELLING_PRICE"))
                .thenReturn(null);

        PriceDto result = priceService.update(priceDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Price not found for Product 'PROD-001' with Price Type 'MRP'",
                result.getMessage()
        );

        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void testUpdate_DuplicateExists() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PROD-001_SELLING_PRICE");
        priceDto.setProductIdentifier("PROD-001");
        priceDto.setPriceType("MRP");

        Price existingPrice = new Price();
        existingPrice.setId(1L);
        existingPrice.setIdentifier("PROD-001_SELLING_PRICE");
        existingPrice.setProductIdentifier("PROD-001");

        Price duplicate = new Price();
        duplicate.setId(2L);
        duplicate.setIdentifier("PROD-001_MRP");

        when(priceRepository.findByIdentifierAndDeletedFalse("PROD-001_SELLING_PRICE"))
                .thenReturn(existingPrice);

        when(priceRepository.findByIdentifierAndDeletedFalse("PROD-001_MRP"))
                .thenReturn(duplicate);

        PriceDto result = priceService.update(priceDto);

        assertFalse(result.isSuccess());
        assertEquals("Another price already exists for this Product + Price Type", result.getMessage());

        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "PROD-001_SELLING_PRICE";

        Price price = new Price();
        price.setIdentifier(identifier);
        price.setDeleted(false);

        when(priceRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(price);

        priceService.delete(identifier);

        assertTrue(price.getDeleted());

        verify(priceRepository).save(price);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();
        price.setIdentifier("PROD-001_SELLING_PRICE");

        List<Price> priceList = List.of(price);
        Page<Price> pricePage = new PageImpl<>(priceList, pageable, priceList.size());

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("PROD-001_SELLING_PRICE");

        List<PriceDto> dtoList = List.of(priceDto);

        when(priceRepository.findAllByDeletedFalse(pageable))
                .thenReturn(pricePage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

}