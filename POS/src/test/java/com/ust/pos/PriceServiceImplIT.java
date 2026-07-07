package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PriceServiceImplIT {

    @Autowired
    private PriceService priceService;

    @Autowired
    private PriceRepository priceRepository;

    @BeforeEach
    void cleanUp() {
        priceRepository.deleteAll();
    }

    private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        assertNotNull(actual, "BigDecimal value should not be null");
        assertEquals(0, expected.compareTo(actual), "Expected " + expected + " but got " + actual);
    }

    @Test
    void save_shouldCreatePrice() {
        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRC001");
        dto.setMrp(new BigDecimal("100.00"));
        dto.setSellingPrice(new BigDecimal("80.00"));
        dto.setStatus(true);

        PriceDto response = priceService.save(dto);

        assertNotNull(response);
        Price saved = priceRepository.findByIdentifier("PRC001");
        assertNotNull(saved);
        assertEquals("PRC001", saved.getIdentifier());
        assertBigDecimalEquals(new BigDecimal("100.00"), saved.getMrp());
        assertBigDecimalEquals(new BigDecimal("80.00"), saved.getSellingPrice());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Price price = new Price();
        price.setIdentifier("PRC001");
        price.setDeleted(false);
        priceRepository.save(price);

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRC001");

        PriceDto response = priceService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Price with identifier - PRC001 already exists", response.getMessage());
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Price price = new Price();
        price.setIdentifier("PRC001");
        price.setDeleted(true);
        priceRepository.save(price);

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRC001");

        PriceDto response = priceService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Price with identifier PRC001 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void update_shouldUpdatePriceDetails() {
        Price price = new Price();
        price.setIdentifier("PRC001");
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));
        price.setStatus(true);
        price.setDeleted(false);
        priceRepository.save(price);

        PriceDto dto = new PriceDto();
        dto.setIdentifier("PRC001");
        dto.setMrp(new BigDecimal("120.00"));
        dto.setSellingPrice(new BigDecimal("95.00"));

        PriceDto response = priceService.update(dto);
        assertTrue(response.isSuccess());

        Price updated = priceRepository.findByIdentifier("PRC001");
        assertBigDecimalEquals(new BigDecimal("120.00"), updated.getMrp());
        assertBigDecimalEquals(new BigDecimal("95.00"), updated.getSellingPrice());
    }

    @Test
    void findByIdentifier_shouldReturnPrice() {
        Price price = new Price();
        price.setIdentifier("PRC001");
        priceRepository.save(price);

        PriceDto result = priceService.findByIdentifier("PRC001");
        assertNotNull(result);
        assertEquals("PRC001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldReturnNullWhenNotFound() {
        PriceDto result = priceService.findByIdentifier("NON-EXISTENT");
        assertNull(result);
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Price price = new Price();
        price.setIdentifier("PRC001");
        price.setStatus(true);
        priceRepository.save(price);

        priceService.toggleStatus("PRC001");
        Price updated = priceRepository.findByIdentifier("PRC001");
        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Price price = new Price();
        price.setIdentifier("PRC001");
        price.setDeleted(false);
        priceRepository.save(price);

        boolean isDeleted = priceService.delete("PRC001");
        assertTrue(isDeleted);

        Price deleted = priceRepository.findByIdentifier("PRC001");
        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        Price price1 = new Price();
        price1.setIdentifier("PRC001");
        price1.setDeleted(false);
        priceRepository.save(price1);

        Pageable pageable = PageRequest.of(0, 10);
        WsDto<PriceDto> response = priceService.findAll(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {
        Price activePrice = new Price();
        activePrice.setIdentifier("PRC001");
        activePrice.setStatus(true);
        activePrice.setDeleted(false);
        priceRepository.save(activePrice);

        Price inactivePrice = new Price();
        inactivePrice.setIdentifier("PRC002");
        inactivePrice.setStatus(false);
        inactivePrice.setDeleted(false);
        priceRepository.save(inactivePrice);

        List<PriceDto> activeList = priceService.findIfTrue();
        assertEquals(1, activeList.size());
        assertEquals("PRC001", activeList.get(0).getIdentifier());
    }

    @Test
    void findByIdentifierAndDeletedFalse_shouldReturnActivePrice() {
        Price price = new Price();
        price.setIdentifier("PRC001");
        price.setDeleted(false);
        priceRepository.save(price);

        PriceDto result = priceService.findByIdentifierAndDeletedFalse("PRC001");
        assertNotNull(result);
        assertEquals("PRC001", result.getIdentifier());
    }

    @Test
    void findByIdentifierAndDeletedFalse_shouldReturnNullIfDeleted() {
        Price price = new Price();
        price.setIdentifier("PRC001");
        price.setDeleted(true);
        priceRepository.save(price);

        PriceDto result = priceService.findByIdentifierAndDeletedFalse("PRC001");
        assertNull(result);
    }
}