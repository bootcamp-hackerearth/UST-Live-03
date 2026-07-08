package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

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

    @Test
    @DisplayName("save() should persist a new price with identifier = product_priceType")
    void save_shouldCreatePrice() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");
        dto.setPriceAmount(BigDecimal.valueOf(100));

        PriceDto response = priceService.save(dto);

        Price saved = priceRepository.findByIdentifier("PROD1_MRP");

        assertNotNull(saved, "Price should be persisted");
        assertEquals("PROD1_MRP", saved.getIdentifier());
        assertFalse(saved.isDeleted());
        assertNotNull(response);
        assertEquals("PROD1_MRP", response.getIdentifier());
    }

    @Test
    @DisplayName("save() should fail gracefully when identifier already exists")
    void save_shouldFailWhenDuplicateExists() {
        Price price = new Price();
        price.setIdentifier("PROD1_MRP");
        price.setDeleted(false);
        priceRepository.save(price);

        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        PriceDto response = priceService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Price with identifier - PROD1_MRP already exists",
                response.getMessage()
        );
    }

    @Test
    @DisplayName("save() should return a special message when identifier belongs to a soft-deleted price")
    void save_shouldWarnWhenIdentifierBelongsToDeletedPrice() {
        Price price = new Price();
        price.setIdentifier("PROD1_MRP");
        price.setDeleted(true);
        priceRepository.save(price);

        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");

        PriceDto response = priceService.save(dto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("was deleted"));
    }

    @Test
    @DisplayName("update() should modify the price amount of an existing price")
    void update_shouldUpdatePrice() {
        Price price = new Price();
        price.setIdentifier("PROD1_MRP");
        price.setDeleted(false);
        price.setPriceAmount(BigDecimal.valueOf(100));
        priceRepository.save(price);

        PriceDto dto = new PriceDto();
        dto.setProduct("PROD1");
        dto.setPriceType("MRP");
        dto.setPriceAmount(BigDecimal.valueOf(200));

        PriceDto response = priceService.update(dto);

        assertTrue(response.isSuccess());

        Price updated = priceRepository.findByIdentifier("PROD1_MRP");

        assertEquals(0, BigDecimal.valueOf(200).compareTo(updated.getPriceAmount()),
                "Price amount should be numerically equal to 200");
    }

    @Test
    @DisplayName("update() should fail when price does not exist")
    void update_shouldFailWhenPriceNotFound() {
        PriceDto dto = new PriceDto();
        dto.setProduct("PROD_MISSING");
        dto.setPriceType("MRP");

        PriceDto response = priceService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Price with identifier - PROD_MISSING_MRP not found",
                response.getMessage()
        );
    }


    @Test
    @DisplayName("findByIdentifier() should return the matching price")
    void findByIdentifier_shouldReturnPrice() {
        Price price = new Price();
        price.setIdentifier("PROD1_MRP");
        price.setDeleted(false);
        priceRepository.save(price);

        PriceDto result = priceService.findByIdentifier("PROD1_MRP");

        assertEquals("PROD1_MRP", result.getIdentifier());
    }

    @Test
    @DisplayName("findByIdentifier() should throw when price is not found")
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> priceService.findByIdentifier("PROD1_MRP")
        );
    }

    @Test
    @DisplayName("findByIdentifier() should throw when price exists but is soft-deleted")
    void findByIdentifier_shouldThrowWhenPriceIsSoftDeleted() {
        Price price = new Price();
        price.setIdentifier("PROD1_MRP");
        price.setDeleted(true);
        priceRepository.save(price);

        assertThrows(
                ResourceNotFoundException.class,
                () -> priceService.findByIdentifier("PROD1_MRP")
        );
    }

    @Test
    @DisplayName("findByProductAndPriceType() should return the matching price")
    void findByProductAndPriceType_shouldReturnPrice() {
        Price price = new Price();
        price.setIdentifier("PROD1_MRP");
        price.setProduct("PROD1");
        price.setPriceType("MRP");
        price.setPriceAmount(BigDecimal.valueOf(100));
        price.setDeleted(false);
        priceRepository.save(price);

        PriceDto result = priceService.findByProductAndPriceType("PROD1", "MRP");

        assertNotNull(result);
        assertEquals("PROD1_MRP", result.getIdentifier());
    }

    @Test
    @DisplayName("findAll(Pageable) should return only non-deleted prices")
    void findAll_shouldReturnNonDeletedPrices() {
        Price active = new Price();
        active.setIdentifier("PROD1_MRP");
        active.setDeleted(false);
        priceRepository.save(active);

        Price deleted = new Price();
        deleted.setIdentifier("PROD2_MRP");
        deleted.setDeleted(true);
        priceRepository.save(deleted);

        Pageable pageable = PageRequest.of(0, 10);
        WsDto<PriceDto> result = priceService.findAll(pageable);

        assertEquals(1, result.getTotalRecords());
        assertTrue(result.getDtoList().stream()
                .anyMatch(p -> "PROD1_MRP".equals(p.getIdentifier())));
    }


    @Test
    @DisplayName("delete() should soft-delete the price")
    void delete_shouldSoftDelete() {
        Price price = new Price();
        price.setIdentifier("PROD1_MRP");
        price.setDeleted(false);
        priceRepository.save(price);

        priceService.delete("PROD1_MRP");

        Price deleted = priceRepository.findByIdentifier("PROD1_MRP");
        assertTrue(deleted.isDeleted());
    }
}