package com.ust.pos.cartentry.service.impl;

import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartEntryServiceImpl implements CartEntryService {
    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PriceRepository priceRepository;

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getCartIdentifier() + "-" + cartEntryDto.getProductIdentifier();
        CartEntry existing = cartEntryRepository.findByIdentifier(identifier);
        BigDecimal quantity = cartEntryDto.getQuantity();
        CartEntry cartEntry;
        if (existing != null) {
            quantity = existing.getQuantity().add(quantity);
            cartEntry = existing;
        } else {
            cartEntry = new CartEntry();
            cartEntry.setIdentifier(identifier);
            cartEntry.setCartIdentifier(cartEntryDto.getCartIdentifier());
            cartEntry.setProductIdentifier(cartEntryDto.getProductIdentifier());
        }
        Price sellingPrice = priceRepository.findByProductAndPriceType(cartEntryDto.getProductIdentifier(), "sellingPrice");
        Price mrp = priceRepository.findByProductAndPriceType(cartEntryDto.getProductIdentifier(), "Mrp");
        BigDecimal unitPrice = sellingPrice.getSumPrice();
        BigDecimal mrpPrice = mrp.getSumPrice();
        cartEntry.setQuantity(quantity);
        cartEntry.setUnitPrice(unitPrice);
        BigDecimal originalPrice = quantity.multiply(unitPrice);
        cartEntry.setOriginalPrice(originalPrice);
        BigDecimal discountPerUnit = mrpPrice.subtract(unitPrice);
        BigDecimal totalDiscount = discountPerUnit.multiply(quantity);
        cartEntry.setDiscount(totalDiscount);
        cartEntry.setTotalPrice(originalPrice.subtract(totalDiscount));
        cartEntryRepository.save(cartEntry);
        return modelMapper.map(cartEntry, CartEntryDto.class);
    }

    @Override
    public CartEntryDto update(CartEntryDto cartentryDto) {
        String identifier = cartentryDto.getIdentifier();
        CartEntry existingCartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (existingCartEntry == null) {
            cartentryDto.setMessage("CartEntry with identifier - " + identifier + " not found");
            cartentryDto.setSuccess(false);
            return cartentryDto;
        }
        modelMapper.map(cartentryDto, existingCartEntry);
        cartEntryRepository.save(existingCartEntry);
        return cartentryDto;
    }

    @Override
    public void delete(String identifier) {
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartentryPage = cartEntryRepository.findAll(pageable);
        return modelMapper.map(cartentryPage.getContent(), listType);
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }

    @Override
    public List<CartEntryDto> findAllCarts(String cart) {
        List<CartEntry> cartEntryList = cartEntryRepository.findByCartIdentifier(cart);
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryList, listType);
    }
}