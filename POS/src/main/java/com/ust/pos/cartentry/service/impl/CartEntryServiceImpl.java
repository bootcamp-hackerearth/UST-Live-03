package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CartEntryServiceImpl implements CartEntryService {
    private final CartEntryRepository cartEntryRepository;
    private final ModelMapper modelMapper;
    private final PriceRepository priceRepository;
    private final CartService cartService;

    public CartEntryServiceImpl(CartEntryRepository cartEntryRepository, ModelMapper modelMapper, PriceRepository priceRepository, CartService cartService){
        this.cartEntryRepository = cartEntryRepository;
        this.cartService = cartService;
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

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

        BigDecimal quantity = cartEntryDto.getQuantity();
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            cartEntryDto.setMessage("Quantity must be greater than 0");
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }
        Price sellingPrice = priceRepository.findByProductAndPriceType(existingCartEntry.getProductIdentifier(), "SELLING PRICE");
        Price mrp = priceRepository.findByProductAndPriceType(existingCartEntry.getProductIdentifier(), "MRP");

        BigDecimal unitPrice = sellingPrice.getAmount();
        BigDecimal mrpPrice = mrp.getAmount();
        existingCartEntry.setQuantity(quantity);
        existingCartEntry.setUnitPrice(unitPrice);

        BigDecimal originalPrice = mrpPrice.multiply(quantity);
        BigDecimal discount = mrpPrice.subtract(unitPrice).multiply(quantity);
        BigDecimal totalPrice = unitPrice.multiply(quantity);

        existingCartEntry.setOriginalPrice(originalPrice);
        existingCartEntry.setDiscount(discount);
        existingCartEntry.setTotalPrice(totalPrice);
        cartEntryRepository.save(existingCartEntry);
        cartService.reCalculate(existingCartEntry.getCartIdentifier());
        return modelMapper.map(existingCartEntry, CartEntryDto.class);
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