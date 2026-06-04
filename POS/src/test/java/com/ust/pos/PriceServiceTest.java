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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    @Test
    void findAll_success() {

        Price price = new Price();
        PriceDto dto = new PriceDto();

        Page<Price> page = new PageImpl<>(List.of(price));

        Mockito.when(priceRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(price)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<PriceDto> result =
                priceService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        PriceDto input = new PriceDto();
        input.setIdentifier("PRICE01");

        Mockito.when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(null);

        Price entity = new Price();

        Mockito.when(modelMapper.map(input, Price.class))
                .thenReturn(entity);

        Mockito.when(priceRepository.save(entity))
                .thenReturn(entity);

        PriceDto result = priceService.save(input);

        Assertions.assertEquals("PRICE01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        PriceDto input = new PriceDto();
        input.setIdentifier("PRICE01");

        Mockito.when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(new Price());

        PriceDto result = priceService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Price price = new Price();
        price.setIdentifier("PRICE01");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE01");

        Mockito.when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(price);

        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result = priceService.findByIdentifier("PRICE01");

        Assertions.assertEquals("PRICE01", result.getIdentifier());
    }

    @Test
    void update_success() {

        PriceDto input = new PriceDto();
        input.setIdentifier("PRICE01");

        Price existing = new Price();

        Mockito.when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(priceRepository.save(existing))
                .thenReturn(existing);

        PriceDto result = priceService.update(input);

        Assertions.assertEquals("PRICE01", result.getIdentifier());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(priceRepository).deleteByIdentifier("PRICE01");

        priceService.delete("PRICE01");

        Mockito.verify(priceRepository)
                .deleteByIdentifier("PRICE01");
    }

    @Test
    void changeToggleStatus_enable() {

        Price price = new Price();
        price.setStatus(false);

        PriceDto dto = new PriceDto();

        Mockito.when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(price);

        Mockito.when(priceRepository.save(price))
                .thenReturn(price);

        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result =
                priceService.changeToggleStatus("PRICE01", true);

        Assertions.assertTrue(price.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Price price = new Price();
        price.setStatus(true);

        PriceDto dto = new PriceDto();

        Mockito.when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(price);

        Mockito.when(priceRepository.save(price))
                .thenReturn(price);

        Mockito.when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result =
                priceService.changeToggleStatus("PRICE01", false);

        Assertions.assertFalse(price.isStatus());
        Assertions.assertNotNull(result);
    }
}