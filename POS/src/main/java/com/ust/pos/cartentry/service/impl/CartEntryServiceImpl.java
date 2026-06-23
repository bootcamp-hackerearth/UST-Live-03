package com.ust.pos.cartentry.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.cartentry.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartEntryServiceImpl extends CommonService implements CartEntryService {

    private final CartEntryRepository cartEntryRepository;

    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    public CartEntryServiceImpl(CartEntryRepository cartEntryRepository,
                                PriceRepository priceRepository,
                                ModelMapper modelMapper) {
        this.cartEntryRepository = cartEntryRepository;
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartEntryDto save(CartEntryDto dto) {

        String identifier = dto.getProduct() + "-" + dto.getCart();
        dto.setIdentifier(identifier);

        Price price = priceRepository.findByProductIdentifier(dto.getProduct());
        if (price == null) {
            throw new IllegalArgumentException("Price not found for product: " + dto.getProduct());
        }

        CartEntry entry = cartEntryRepository.findByIdentifier(identifier);
        BigDecimal qty = dto.getQuantity();

        if (entry != null) {
            qty = entry.getQuantity().add(qty);
        } else {
            entry = new CartEntry();
            entry.setIdentifier(identifier);
            entry.setProduct(dto.getProduct());
            entry.setCart(dto.getCart());
            setAuditFields(entry, true);
        }

        BigDecimal totalPrice = price.getSellingPrice().multiply(qty);
        BigDecimal discountPerUnit = price.getMrp().subtract(price.getSellingPrice());
        BigDecimal totalDiscount = discountPerUnit.multiply(qty);

        dto.setQuantity(qty);
        dto.setDiscount(totalDiscount);
        dto.setTotalPrice(totalPrice);
        dto.setPrice(price.getMrp());
        dto.setSellingPrice(price.getSellingPrice());

        modelMapper.map(dto, entry);
        setAuditFields(entry, false);

        cartEntryRepository.save(entry);

        dto.setSuccess(true);
        dto.setMessage("Cart entry saved successfully");

        return dto;
    }

    @Override
    public List<CartEntryDto> findAllEntriesForCart(String cart) {
        List<CartEntry> list = cartEntryRepository.findByCart(cart);
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(list, listType);
    }

    @Override
    public CartEntryDto update(CartEntryDto dto) {

        CartEntry entry = cartEntryRepository.findByIdentifier(dto.getIdentifier());

        if (entry == null) {
            dto.setSuccess(false);
            dto.setMessage("Cart entry not found");
            return dto;
        }

        Price price = priceRepository.findByProductIdentifier(dto.getProduct());
        if (price == null) {
            throw new IllegalArgumentException("Price not found for product: " + dto.getProduct());
        }

        BigDecimal qty = dto.getQuantity();

        BigDecimal totalPrice = price.getSellingPrice().multiply(qty);
        BigDecimal discountPerUnit = price.getMrp().subtract(price.getSellingPrice());
        BigDecimal totalDiscount = discountPerUnit.multiply(qty);

        dto.setDiscount(totalDiscount);
        dto.setTotalPrice(totalPrice);
        dto.setPrice(price.getMrp());
        dto.setSellingPrice(price.getSellingPrice());

        modelMapper.map(dto, entry);
        setAuditFields(entry, false);

        cartEntryRepository.save(entry);

        dto.setSuccess(true);
        dto.setMessage("Cart entry updated successfully");

        return dto;
    }

    @Override
    public void deleteByIdentifier(String identifier) {

        CartEntry entry = cartEntryRepository.findByIdentifier(identifier);

        if (entry == null) {
            throw new IllegalArgumentException("CartEntry not found");
        }

        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public void deleteAllByCart(String cart) {
        List<CartEntry> list = cartEntryRepository.findByCart(cart);
        if (list.isEmpty()) {
            return;
        }
        cartEntryRepository.deleteAll(list);
    }
}