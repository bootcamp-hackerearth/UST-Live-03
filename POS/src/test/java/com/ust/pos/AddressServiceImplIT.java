package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AddressServiceImplIT {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void cleanUp() {
        addressRepository.deleteAll();
    }

    @Test
    void save_shouldCreateAddress() {
        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR001");
        dto.setPhoneNo("9876543210");

        AddressDto response = addressService.save(dto);

        assertNotNull(response);
        Address saved = addressRepository.findByIdentifier("ADDR001");
        assertNotNull(saved);
        assertEquals("ADDR001", saved.getIdentifier());
        assertEquals("9876543210", saved.getPhoneNo());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Address address = new Address();
        address.setIdentifier("ADDR001");
        address.setDeleted(false);
        addressRepository.save(address);

        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR001");

        AddressDto response = addressService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Address with identifier - ADDR001 already exists", response.getMessage());
    }

    @Test
    void update_shouldUpdateAddressDetails() {
        Address address = new Address();
        address.setIdentifier("ADDR001");
        address.setPhoneNo("1111111111");
        address.setDeleted(false);
        addressRepository.save(address);

        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR001");
        dto.setPhoneNo("2222222222");

        AddressDto response = addressService.update(dto);
        assertTrue(response.isSuccess());

        Address updated = addressRepository.findByIdentifier("ADDR001");
        assertEquals("2222222222", updated.getPhoneNo());
    }

    @Test
    void update_shouldReturnErrorIfNotFound() {
        AddressDto dto = new AddressDto();
        dto.setIdentifier("NON-EXISTENT");

        AddressDto response = addressService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals("Address with identifier - NON-EXISTENT not found", response.getMessage());
    }

    @Test
    void findByIdentifier_shouldReturnAddress() {
        Address address = new Address();
        address.setIdentifier("ADDR001");
        addressRepository.save(address);

        AddressDto result = addressService.findByIdentifier("ADDR001");
        assertNotNull(result);
        assertEquals("ADDR001", result.getIdentifier());
    }

    @Test
    void findAllByPhoneNo_shouldReturnNonDeletedAddresses() {
        Address address1 = new Address();
        address1.setIdentifier("ADDR001");
        address1.setPhoneNo("9876543210");
        address1.setDeleted(false);
        addressRepository.save(address1);

        Address address2 = new Address();
        address2.setIdentifier("ADDR002");
        address2.setPhoneNo("9876543210");
        address2.setDeleted(true);
        addressRepository.save(address2);

        List<AddressDto> results = addressService.findAllByPhoneNo("9876543210");
        assertEquals(1, results.size());
        assertEquals("ADDR001", results.get(0).getIdentifier());
    }

    @Test
    void delete_shouldSoftDeleteAllAddressesByPhoneNo() {
        Address address1 = new Address();
        address1.setIdentifier("ADDR001");
        address1.setPhoneNo("9876543210");
        address1.setDeleted(false);
        addressRepository.save(address1);

        Address address2 = new Address();
        address2.setIdentifier("ADDR002");
        address2.setPhoneNo("9876543210");
        address2.setDeleted(false);
        addressRepository.save(address2);

        boolean isDeleted = addressService.delete("9876543210");
        assertTrue(isDeleted);

        List<Address> databaseAddresses = addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210");
        assertTrue(databaseAddresses.isEmpty());
    }

    @Test
    void delete_shouldReturnFalseWhenNoAddressesMatchPhoneNo() {
        boolean isDeleted = addressService.delete("NON-EXISTENT");
        assertFalse(isDeleted);
    }

    @Test
    void findAll_shouldReturnPaginatedActiveRecords() {
        Address address = new Address();
        address.setIdentifier("ADDR001");
        address.setDeleted(false);
        addressRepository.save(address);

        Pageable pageable = PageRequest.of(0, 10);
        List<AddressDto> resultPage = addressService.findAll(pageable);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.size());
        assertEquals("ADDR001", resultPage.get(0).getIdentifier());
    }
}