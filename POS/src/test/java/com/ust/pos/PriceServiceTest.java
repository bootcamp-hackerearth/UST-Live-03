package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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
        Pageable pageable = Mockito.mock(Pageable.class);


        Page<Price> page = new PageImpl<>(List.of(price));

        when(priceRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                        Mockito.eq(List.of(price)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<PriceDto> result =
                priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());    }

    @Test
    void save_success() {

        PriceDto input = new PriceDto();
        input.setIdentifier("PRICE01");

        when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(null);

        Price entity = new Price();

        when(modelMapper.map(input, Price.class))
                .thenReturn(entity);

        when(priceRepository.save(entity))
                .thenReturn(entity);

        PriceDto result = priceService.save(input);

        assertEquals("PRICE01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        PriceDto input = new PriceDto();
        input.setIdentifier("PRICE01");

        when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(new Price());

        PriceDto result = priceService.save(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Price price = new Price();
        price.setIdentifier("PRICE01");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRICE01");

        when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result = priceService.findByIdentifier("PRICE01");

        assertEquals("PRICE01", result.getIdentifier());
    }

    @Test
    void update_success() {

        PriceDto input = new PriceDto();
        input.setIdentifier("PRICE01");

        Price existing = new Price();

        when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        when(priceRepository.save(existing))
                .thenReturn(existing);

        PriceDto result = priceService.update(input);

        assertEquals("PRICE01", result.getIdentifier());
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

        when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(price);

        when(priceRepository.save(price))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result =
                priceService.changeToggleStatus("PRICE01", true);

        Assertions.assertTrue(price.isStatus());
        assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Price price = new Price();
        price.setStatus(true);

        PriceDto dto = new PriceDto();

        when(priceRepository.findByIdentifier("PRICE01"))
                .thenReturn(price);

        when(priceRepository.save(price))
                .thenReturn(price);

        when(modelMapper.map(price, PriceDto.class))
                .thenReturn(dto);

        PriceDto result =
                priceService.changeToggleStatus("PRICE01", false);

        Assertions.assertFalse(price.isStatus());
        assertNotNull(result);
    }

    @Test
    void testFindActiveStatus() {
        Price active = new Price();
        active.setStatus(true);

        Price inactive = new Price();
        inactive.setStatus(false);

        when(priceRepository.findAll())
                .thenReturn(List.of(active, inactive));

        PriceDto dto = new PriceDto();
        List<PriceDto> expectedDtoList = List.of(dto);

        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        List<PriceDto> result = priceService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}