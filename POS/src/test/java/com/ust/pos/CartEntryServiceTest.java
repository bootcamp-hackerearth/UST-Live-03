package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
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
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Mock
    private PriceService priceService;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    // SAVE (NEW ENTRY)

    @Test
    void save_NewEntry_Success() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.ONE);
        dto.setDiscount(BigDecimal.ONE);

        PriceDto priceDto = new PriceDto();
        priceDto.setSellingPrice(new BigDecimal("100"));

        CartEntry entity = new CartEntry();

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(null);

        Mockito.when(priceService.findByIdentifier("P1"))
                .thenReturn(priceDto);

        Mockito.doAnswer(invocation -> {
                    CartEntryDto source = invocation.getArgument(0);
                    CartEntry target = invocation.getArgument(1);

                    target.setIdentifier(source.getIdentifier());
                    target.setQuantity(source.getQuantity());
                    target.setUnitPrice(source.getUnitPrice());
                    target.setTotalPrice(source.getTotalPrice());

                    return null;
                }).when(modelMapper)
                .map(Mockito.any(CartEntryDto.class), Mockito.any(CartEntry.class));

        Mockito.when(cartEntryRepository.save(Mockito.any(CartEntry.class)))
                .thenReturn(entity);

        CartEntryDto result = cartEntryService.save(dto);

        Assertions.assertEquals("C1_P1", result.getIdentifier());
        Assertions.assertEquals(new BigDecimal("100"), result.getUnitPrice());

        Mockito.verify(cartEntryRepository).save(Mockito.any(CartEntry.class));
    }

    @Test
    void save_UpdateExistingEntry_Success() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartId("C1");
        dto.setProduct("P1");
        dto.setQuantity(BigDecimal.ONE);
        dto.setDiscount(BigDecimal.ONE);

        CartEntry existing = new CartEntry();
        existing.setIdentifier("C1_P1");
        existing.setQuantity(new BigDecimal("2"));
        existing.setProduct("P1");
        existing.setDiscount(BigDecimal.ONE);

        PriceDto priceDto = new PriceDto();
        priceDto.setSellingPrice(new BigDecimal("100"));

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(existing);

        Mockito.when(priceService.findByIdentifier("P1"))
                .thenReturn(priceDto);

        Mockito.when(cartEntryRepository.save(existing))
                .thenReturn(existing);


        CartEntryDto result = cartEntryService.save(dto);

        Assertions.assertEquals(new BigDecimal("3"), result.getQuantity());
        Assertions.assertNotNull(result.getTotalPrice());

        Mockito.verify(cartEntryRepository).save(existing);
    }

    // UPDATE QUANTITY

    @Test
    void updateQuantity_Delete_WhenZero() {

        CartEntry existing = new CartEntry();
        existing.setIdentifier("C1_P1");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");
        dto.setQuantity(BigDecimal.ZERO);

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(existing);

        CartEntryDto result = cartEntryService.updateQuantity(dto);

        Mockito.verify(cartEntryRepository).delete(existing);

        Assertions.assertEquals("C1_P1", result.getIdentifier());
    }

    @Test
    void updateQuantity_Update_Success() {

        CartEntry existing = new CartEntry();
        existing.setIdentifier("C1_P1");
        existing.setProduct("P1");
        existing.setDiscount(new BigDecimal("5"));

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");
        dto.setQuantity(new BigDecimal("2"));

        PriceDto priceDto = new PriceDto();
        priceDto.setSellingPrice(new BigDecimal("100"));

        CartEntryDto mappedDto = new CartEntryDto();
        mappedDto.setIdentifier("C1_P1");

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(existing);

        Mockito.when(priceService.findByIdentifier("P1"))
                .thenReturn(priceDto);

        Mockito.when(cartEntryRepository.save(existing))
                .thenReturn(existing);

        Mockito.when(modelMapper.map(existing, CartEntryDto.class))
                .thenReturn(mappedDto);

        CartEntryDto result = cartEntryService.updateQuantity(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("C1_P1", result.getIdentifier());

        Mockito.verify(cartEntryRepository).save(existing);
    }

    @Test
    void updateQuantity_WhenNotFound_ShouldReturnSameDto() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(null);

        CartEntryDto result = cartEntryService.updateQuantity(dto);

        Assertions.assertEquals("C1_P1", result.getIdentifier());
    }

    // DELETE ALL

    @Test
    void deleteAll_Success() {

        CartEntry entry = new CartEntry();

        Mockito.when(cartEntryRepository.findByCartId("C1"))
                .thenReturn(List.of(entry));

        cartEntryService.deleteAll("C1");

        Mockito.verify(cartEntryRepository).deleteAll(List.of(entry));
    }

    // UPDATE

    @Test
    void update_Success() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");

        CartEntry existing = new CartEntry();

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(existing);

        Mockito.when(modelMapper.map(dto, CartEntry.class))
                .thenReturn(existing);

        Mockito.when(cartEntryRepository.save(existing))
                .thenReturn(existing);

        CartEntryDto result = cartEntryService.update(dto);

        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void update_WhenNotFound_ShouldFail() {

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("C1_P1");

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(null);

        CartEntryDto result = cartEntryService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    // DELETE

    @Test
    void delete_Success() {

        cartEntryService.delete("C1_P1");

        Mockito.verify(cartEntryRepository)
                .deleteByIdentifier("C1_P1");
    }

    // FIND ALL

    @Test
    void findAll_Success() {

        CartEntry entity = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        Type type = new TypeToken<List<CartEntryDto>>() {
        }.getType();

        Mockito.when(cartEntryRepository.findAll())
                .thenReturn(List.of(entity));

        Mockito.when(modelMapper.map(List.of(entity), type))
                .thenReturn(List.of(dto));

        List<CartEntryDto> result = cartEntryService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // FIND BY ID

    @Test
    void findByIdentifier_Success() {

        CartEntry entity = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        Mockito.when(cartEntryRepository.findByIdentifier("C1_P1"))
                .thenReturn(entity);

        Mockito.when(modelMapper.map(entity, CartEntryDto.class))
                .thenReturn(dto);

        CartEntryDto result = cartEntryService.findByIdentifier("C1_P1");

        Assertions.assertNotNull(result);
    }

    // PAGINATION

    @Test
    void findAll_Pageable_Success() {

        Pageable pageable = PageRequest.of(0, 10);

        CartEntry entity = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        Page<CartEntry> page = new PageImpl<>(List.of(entity));

        Mockito.when(cartEntryRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(List.of(entity),
                        new TypeToken<List<CartEntryDto>>() {
                        }.getType()))
                .thenReturn(List.of(dto));

        List<CartEntryDto> result = cartEntryService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
    }

    // FIND BY CART ID

    @Test
    void findByCartId_Success() {

        CartEntry entity = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        Mockito.when(cartEntryRepository.findByCartId("C1"))
                .thenReturn(List.of(entity));

        Mockito.when(modelMapper.map(List.of(entity),
                        new TypeToken<List<CartEntryDto>>() {
                        }.getType()))
                .thenReturn(List.of(dto));

        List<CartEntryDto> result = cartEntryService.findByCartId("C1");

        Assertions.assertEquals(1, result.size());
    }
}