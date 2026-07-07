package com.ust.pos;

import com.ust.pos.adress.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AddressServiceImplIT {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressRepository addressRepository;

    private AddressDto sampleAddressDto;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();

        sampleAddressDto = new AddressDto();
        sampleAddressDto.setIdentifier("ADDR-001");
        sampleAddressDto.setPhoneNo("9876543210");
    }

    @Test
    void testSave_Success() {
        AddressDto result = addressService.save(sampleAddressDto);

        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("added Successfully"));

        Address savedEntity = addressRepository.findByIdentifier("ADDR-001");
        assertNotNull(savedEntity);
        assertFalse(savedEntity.isDeleted());
    }

    @Test
    void testSave_AlreadyExists() {
        addressService.save(sampleAddressDto);

        AddressDto duplicateResult = addressService.save(sampleAddressDto);

        assertFalse(duplicateResult.isSuccess());
        assertTrue(duplicateResult.getMessage().contains("already exists"));
    }

    @Test
    void testSave_PreviouslyDeleted() {
        Address address = new Address();
        address.setIdentifier("ADDR-OLD");
        address.setPhoneNo("9999999999");
        address.setDeleted(true);
        addressRepository.save(address);

        AddressDto deletedDto = new AddressDto();
        deletedDto.setIdentifier("ADDR-OLD");
        deletedDto.setPhoneNo("9999999999");

        AddressDto result = addressService.save(deletedDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        addressService.save(sampleAddressDto);

        sampleAddressDto.setPhoneNo("9111111111");
        AddressDto result = addressService.update(sampleAddressDto);

        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Updated"));

        Address updatedEntity = addressRepository.findByIdentifier("ADDR-001");
        assertEquals("9111111111", updatedEntity.getPhoneNo());
    }

    @Test
    void testUpdate_NotFound() {
        sampleAddressDto.setIdentifier("NON-EXISTENT");
        AddressDto result = addressService.update(sampleAddressDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testDelete_Success() {
        addressService.save(sampleAddressDto);

        boolean isDeleted = addressService.delete("9876543210");
        assertTrue(isDeleted);

        List<Address> addresses = addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210");
        assertTrue(addresses.isEmpty(), "Active address list should be empty after soft-deletion");
    }

    @Test
    void testFindAll_Pagination() {
        addressService.save(sampleAddressDto);

        Pageable pageable = PageRequest.of(0, 10);
        List<AddressDto> results = addressService.findAll(pageable);

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindByIdentifier_Success() {
        addressService.save(sampleAddressDto);

        AddressDto found = addressService.findByIdentifier("ADDR-001");
        assertNotNull(found);
        assertEquals("ADDR-001", found.getIdentifier());
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.findByIdentifier("INVALID-ID");
        });
    }

    @Test
    void testFindAllByPhoneNumber() {
        addressService.save(sampleAddressDto);

        List<AddressDto> results = addressService.findAllByPhoneNumber("9876543210");
        assertNotNull(results);
        assertEquals(1, results.size());
    }
}