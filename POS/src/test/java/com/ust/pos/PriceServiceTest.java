package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
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
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    // ✅ SAVE SUCCESS
    @Test
    void saveTest() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("P1");
        priceDto.setCostPrice(100L);
        priceDto.setSellingPrice(150L);

        Price price = new Price();

        Mockito.when(priceRepository.findByIdentifier("P1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(priceDto, Price.class))
                .thenReturn(price);

        // when
        PriceDto response = priceService.save(priceDto);

        // then
        Assertions.assertEquals("P1", response.getIdentifier());
        Assertions.assertEquals(50L, response.getDifference());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess(), "Success should be true by default");

        Mockito.verify(priceRepository).save(price);
}

    // ✅ SAVE FAILURE
    @Test
    void saveTestFailure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(new Price());

        PriceDto response = priceService.save(priceDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(priceRepository, Mockito.never()).save(Mockito.any());
    }
    @Test
    void findAll_WithPagination_ShouldReturnPriceDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Price> prices = List.of(new Price());
        Page<Price> page = new PageImpl<>(prices);

        List<PriceDto> priceDtos = List.of(new PriceDto());

        Type listType = new TypeToken<List<PriceDto>>() {}.getType();

        Mockito.when(priceRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(prices, listType))
                .thenReturn(priceDtos);

        List<PriceDto> response = priceService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(priceRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(prices, listType);
    }

    // ✅ UPDATE SUCCESS
    @Test
    void updateTest_Success() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("P1");
        dto.setCostPrice(150L);
        dto.setSellingPrice(200L);

        Price existing = new Price();
        existing.setIdentifier("P1");

        Price mappedPrice = new Price();

        Mockito.when(priceRepository.findByIdentifier("P1"))
                .thenReturn(existing);
        Mockito.when(modelMapper.map(dto, Price.class))
                .thenReturn(mappedPrice);

        PriceDto response = priceService.update(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("P1", response.getIdentifier());
        Assertions.assertEquals(50L, response.getDifference());

        Mockito.verify(priceRepository).save(mappedPrice);
    }

    // ✅ UPDATE FAILURE
    @Test
    void updateTestFailure() {
        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        PriceDto response = priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void findByIdentifierTest() {
        Price price = new Price();
        price.setIdentifier("Admin");

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("Admin");

        Mockito.when(priceRepository.findByIdentifier("Admin"))
                .thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(priceDto);

        PriceDto response = priceService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    // ✅ FIND ALL
    @Test
    void findAllTest() {
        Mockito.when(priceRepository.findAll())
                .thenReturn(List.of(new Price()));
        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(java.lang.reflect.Type.class))
        ).thenReturn(List.of(new PriceDto()));

        List<PriceDto> response = priceService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    // ✅ DELETE
    @Test
    void deleteTest() {
        priceService.delete("Admin");

        Mockito.verify(priceRepository)
                .deleteByIdentifier("Admin");
    }
}