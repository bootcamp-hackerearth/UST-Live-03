package com.ust.pos.cartentry.impl;


import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.cartentry.CartEntryService;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
@Service
@Transactional
public class CartEntryServiceImpl implements CartEntryService {
    private final PriceService priceService;
    private final CartEntryRepository cartEntryRepository;

    private final ModelMapper modelMapper;

    public CartEntryServiceImpl(PriceService priceService, CartEntryRepository cartEntryRepository, ModelMapper modelMapper) {
        this.priceService = priceService;
        this.cartEntryRepository = cartEntryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        cartEntryDto.setIdentifier(cartEntryDto.getCartId() + "_" + cartEntryDto.getProduct());
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(cartEntryDto.getIdentifier());
        if (cartEntry == null) {
            cartEntry = new CartEntry();
        }
        BigDecimal existingQty = cartEntry.getQuantity() != null ? cartEntry.getQuantity() : BigDecimal.ZERO;
        BigDecimal requestQty = cartEntryDto.getQuantity() != null ? cartEntryDto.getQuantity() : BigDecimal.ZERO;
        cartEntryDto.setQuantity(requestQty.add(existingQty));
        PriceDto priceDto = priceService.findByIdentifier(cartEntryDto.getProduct());
        cartEntryDto.setUnitPrice(priceDto.getCostPrice());

        BigDecimal discountPerUnit = priceDto.getDifference() != null ? priceDto.getDifference() : BigDecimal.ZERO;
        BigDecimal totalDiscount = discountPerUnit.multiply(cartEntryDto.getQuantity());
        cartEntryDto.setDiscount(discountPerUnit);
        BigDecimal totalPrice = cartEntryDto.getUnitPrice().multiply(cartEntryDto.getQuantity()).subtract(totalDiscount);
        cartEntryDto.setTotalPrice(totalPrice);

        modelMapper.map(cartEntryDto, cartEntry);
        cartEntryRepository.save(cartEntry);
        return cartEntryDto;
    }

    @Override
    public void clearCart(String cartId)
    {
        cartEntryRepository.deleteByCartId(cartId);
    }

    @Override
    public CartEntryDto update(CartEntryDto cartEntryDto)
    {
        CartEntry existingCartEntry =
                cartEntryRepository.findByIdentifier(
                        cartEntryDto.getIdentifier()
                );

        if(existingCartEntry == null)
        {
            cartEntryDto.setMessage(
                    "CartEntry with identifier - "
                            + cartEntryDto.getIdentifier()
                            + " is not found"
            );
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }

        PriceDto priceDto =
                priceService.findByIdentifier(
                        existingCartEntry.getProduct()
                );

        BigDecimal quantity =
                cartEntryDto.getQuantity();

        BigDecimal discountPerUnit =
                priceDto.getDifference();

        BigDecimal totalDiscount =
                discountPerUnit.multiply(quantity);

        BigDecimal totalPrice =
                priceDto.getCostPrice()
                        .multiply(quantity)
                        .subtract(totalDiscount);

        existingCartEntry.setQuantity(quantity);
        existingCartEntry.setDiscount(discountPerUnit);
        existingCartEntry.setTotalPrice(totalPrice);
        existingCartEntry.setUnitPrice(
                priceDto.getCostPrice()
        );

        cartEntryRepository.save(existingCartEntry);
        return modelMapper.map(
                existingCartEntry,
                CartEntryDto.class
        );
    }

    @Override
    public void delete(String identifier) {
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartEntryDto> findAll() {
        Type listOfType = new TypeToken<List<CartEntryDto>>() {}.getType();
        return modelMapper.map(cartEntryRepository.findAll(), listOfType);
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);
        return modelMapper.map(cartEntryPage.getContent(), listOfType);
    }

    @Override
    public List<CartEntryDto> findByCartId(String cart) {
        Type listOfType = new TypeToken<List<CartEntryDto>>(){
        }.getType();
        List<CartEntry> cartEntryList= cartEntryRepository.findByCartId(cart);
        return modelMapper.map(cartEntryList , listOfType);
    }
}
