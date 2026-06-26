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

    @Test
    void saveTest() {

        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("P001");
        priceDto.setType("RETAIL");

        String identifier = "P001_RETAIL";

        Mockito.when(
                priceRepository.findByIdentifier(identifier)
        ).thenReturn(null);

        Price price = new Price();

        Mockito.when(
                modelMapper.map(priceDto, Price.class)
        ).thenReturn(price);

        Mockito.when(
                priceRepository.save(price)
        ).thenReturn(price);

        PriceDto response =
                priceService.save(priceDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                identifier,
                response.getIdentifier()
        );

        Assertions.assertEquals(
                "Price created successfully",
                response.getMessage()
        );

        Mockito.verify(priceRepository)
                .save(price);
    }

    @Test
    void saveTestFailureExistingPrice() {

        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("P001");
        priceDto.setType("RETAIL");

        Price existingPrice = new Price();
        existingPrice.setDeleted(false);

        Mockito.when(
                priceRepository.findByIdentifier("P001_RETAIL")
        ).thenReturn(existingPrice);

        PriceDto response =
                priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("already exists")
        );
    }

    @Test
    void saveTestFailureSoftDeletedPrice() {

        PriceDto priceDto = new PriceDto();
        priceDto.setProduct("P001");
        priceDto.setType("RETAIL");

        Price deletedPrice = new Price();
        deletedPrice.setDeleted(true);

        Mockito.when(
                priceRepository.findByIdentifier("P001_RETAIL")
        ).thenReturn(deletedPrice);

        PriceDto response =
                priceService.save(priceDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("soft deleted")
        );
    }

    @Test
    void findAllWithPageableTest() {

        Price price = new Price();
        price.setIdentifier("P001");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P001");

        List<Price> prices =
                List.of(price);

        List<PriceDto> dtos =
                List.of(dto);

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<Price> pricePage =
                new PageImpl<>(
                        prices,
                        pageable,
                        1
                );

        Mockito.when(
                priceRepository.findByDeletedFalse(pageable)
        ).thenReturn(pricePage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(prices),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<PriceDto> response =
                priceService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "P001",
                response.getDtoList()
                        .get(0)
                        .getIdentifier()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Price price = new Price();
        price.setIdentifier("P001");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P001");

        List<PriceDto> dtos =
                List.of(dto);

        Page<Price> pricePage =
                new PageImpl<>(
                        List.of(price)
                );

        Mockito.when(
                priceRepository.findByDeletedFalse(null)
        ).thenReturn(pricePage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(pricePage),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<PriceDto> response =
                priceService.findAll(null);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "P001",
                response.getDtoList()
                        .get(0)
                        .getIdentifier()
        );
    }

    @Test
    void updateTest() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("P001");

        Price existingPrice = new Price();
        existingPrice.setIdentifier("P001");

        Mockito.when(
                priceRepository.findByIdentifier("P001")
        ).thenReturn(existingPrice);

        Mockito.when(
                priceRepository.save(existingPrice)
        ).thenReturn(existingPrice);

        PriceDto response =
                priceService.update(priceDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Price updated successfully",
                response.getMessage()
        );

        Mockito.verify(priceRepository)
                .save(existingPrice);
    }

    @Test
    void updateTestFailure() {

        PriceDto priceDto = new PriceDto();
        priceDto.setIdentifier("P001");

        Mockito.when(
                priceRepository.findByIdentifier("P001")
        ).thenReturn(null);

        PriceDto response =
                priceService.update(priceDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("not found")
        );
    }

    @Test
    void findByIdentifierTest() {

        Price price = new Price();
        price.setIdentifier("P001");

        PriceDto dto = new PriceDto();
        dto.setIdentifier("P001");

        Mockito.when(
                priceRepository.findByIdentifier("P001")
        ).thenReturn(price);

        Mockito.when(
                modelMapper.map(
                        price,
                        PriceDto.class
                )
        ).thenReturn(dto);

        PriceDto response =
                priceService.findByIdentifier("P001");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                "P001",
                response.getIdentifier()
        );
    }

    @Test
    void deleteByIdentifierTest() {

        Price price = new Price();
        price.setIdentifier("P001");

        Mockito.when(
                priceRepository.findByIdentifier("P001")
        ).thenReturn(price);

        priceService.deleteByIdentifier("P001");

        Mockito.verify(priceRepository)
                .findByIdentifier("P001");

        Mockito.verify(priceRepository)
                .save(price);
    }
}