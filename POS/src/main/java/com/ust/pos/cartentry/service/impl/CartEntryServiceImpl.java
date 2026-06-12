package com.ust.pos.cartentry.service.impl;

import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private PriceService priceService;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        cartEntryDto.setIdentifier(cartEntryDto.getCartId() + "_" + cartEntryDto.getProduct());
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(cartEntryDto.getIdentifier());
        if (cartEntry == null) {
            cartEntry = new CartEntry();
        }
        BigDecimal existingQty = cartEntry.getQuantity() != null
                ? cartEntry.getQuantity()
                : BigDecimal.ZERO;
        BigDecimal requestQty = cartEntryDto.getQuantity() != null
                ? cartEntryDto.getQuantity()
                : BigDecimal.ZERO;
        cartEntryDto.setQuantity(requestQty.add(existingQty));
        PriceDto priceDto = priceService.findByIdentifier(cartEntryDto.getProduct());
        cartEntryDto.setUnitPrice(priceDto.getSellingPrice());
        BigDecimal discount = cartEntryDto.getDiscount() != null
                ? cartEntryDto.getDiscount()
                : BigDecimal.ZERO;
        cartEntryDto.setTotalPrice(
                cartEntryDto.getUnitPrice()
                        .multiply(cartEntryDto.getQuantity())
                        .subtract(discount)
        );
        modelMapper.map(cartEntryDto, cartEntry);
        cartEntryRepository.save(cartEntry);
        return cartEntryDto;
    }

    @Override
    public CartEntryDto update(CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getIdentifier();
        CartEntry existingCartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (existingCartEntry == null) {
            cartEntryDto.setMessage("CartEntry with identifier - " + identifier + " is not found");
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }
        CartEntry cartEntry = modelMapper.map(cartEntryDto, CartEntry.class);
        cartEntryRepository.save(cartEntry);
        return cartEntryDto;
    }

    @Override
    public void delete(String identifier) {
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartEntryDto> findAll() {
        Type listOfType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
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
        Type listOfType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        List<CartEntry> cartEntryList = cartEntryRepository.findByCartId(cart);
        return modelMapper.map(cartEntryList, listOfType);
    }
}
