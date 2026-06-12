package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
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
public class CartEntryServiceImpl implements CartEntryService {

    @Autowired
    private CartEntryRepository cartEntryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private CartService cartService;

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }

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

        Price sellingPrice = priceRepository.findByProductAndPriceType(cartEntryDto.getProductIdentifier(), "SELLING PRICE");
        Price mrp = priceRepository.findByProductAndPriceType(cartEntryDto.getProductIdentifier(), "MRP");
        BigDecimal unitPrice = sellingPrice.getAmount();
        BigDecimal mrpPrice = mrp.getAmount();

        cartEntry.setQuantity(quantity);
        cartEntry.setUnitPrice(unitPrice);

        BigDecimal originalPrice = quantity.multiply(mrpPrice);
        cartEntry.setOriginalPrice(originalPrice);

        BigDecimal discountPerUnit = mrpPrice.subtract(unitPrice);
        BigDecimal totalDiscount = discountPerUnit.multiply(quantity);
        cartEntry.setDiscount(totalDiscount);
        cartEntry.setTotalPrice(unitPrice.multiply(quantity));

        if (cartService.findByIdentifier(cartEntry.getCartIdentifier()) == null) {
            CartDto cartDto = new CartDto();
            cartDto.setIdentifier(cartEntry.getCartIdentifier());
            cartService.save(cartDto);
        }
        cartEntryRepository.save(cartEntry);
        cartService.reCalculate(cartEntryDto.getCartIdentifier());
        return modelMapper.map(cartEntry, CartEntryDto.class);
    }

    @Override
    public CartEntryDto update(CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getIdentifier();
        CartEntry existingCartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (existingCartEntry == null) {
            cartEntryDto.setMessage("CartEntry with identifier - " + identifier + " not found");
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }
        modelMapper.map(cartEntryDto, existingCartEntry);
        cartEntryRepository.save(existingCartEntry);
        return cartEntryDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        CartEntry existing = cartEntryRepository.findByIdentifier(identifier);
        if (existing == null) {
            return;
        }
        String cartIdentifier = existing.getCartIdentifier();
        cartEntryRepository.deleteByIdentifier(identifier);
        cartService.reCalculate(cartIdentifier);
    }

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);
        return modelMapper.map(cartEntryPage.getContent(), listType);
    }

    @Override
    public List<CartEntryDto> findByCartIdentifier(String cartIdentifier) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryRepository.findByCartIdentifier(cartIdentifier), listType);
    }
}