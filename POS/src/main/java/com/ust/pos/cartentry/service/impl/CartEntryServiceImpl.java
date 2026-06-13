package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
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
    CartEntryRepository cartEntryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PriceService priceService;

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        String product = cartEntryDto.getProductIdentifier();
        BigDecimal qty = cartEntryDto.getQuantity();
        String cart = cartEntryDto.getCartIdentifier();
        String identifier = product + "_" + cart;
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry == null) {
            cartEntry = new CartEntry();
        }
        BigDecimal existQty = cartEntry.getQuantity();
        BigDecimal updatedQuantity = existQty.add(qty);
        cartEntry.setIdentifier(identifier);
        cartEntry.setProductIdentifier(product);
        cartEntry.setQuantity(updatedQuantity);
        cartEntry.setCartIdentifier(cart);
        PriceDto sellingpriceDto = priceService.findByIdentifier(cartEntry.getProductIdentifier()+"_"+"SELLING_PRICE");
        PriceDto mrpDto = priceService.findByIdentifier(cartEntry.getProductIdentifier()+"_"+"MRP");
        cartEntry.setUnitPrice(BigDecimal.valueOf(sellingpriceDto.getAmount()));
        Long mrp = mrpDto.getAmount();
        cartEntry.setOriginalPrice(BigDecimal.valueOf(mrp).multiply(updatedQuantity));
        cartEntry.setDiscount((BigDecimal.valueOf(mrp).subtract(cartEntry.getUnitPrice())).multiply(updatedQuantity));
        cartEntry.setTotalPrice(cartEntry.getUnitPrice().multiply(updatedQuantity));
        cartEntryRepository.save(cartEntry);
        return modelMapper.map(cartEntry,CartEntryDto.class);
    }

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(cartEntryRepository.findAll(), listType);
        }
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);
        return modelMapper.map(cartEntryPage.getContent(), listType);
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public CartEntryDto update(CartEntryDto cartEntryDto) {
        CartEntry existingCartEntry = cartEntryRepository.findByIdentifier(cartEntryDto.getIdentifier());
        if (existingCartEntry == null) {
            cartEntryDto.setMessage("CartEntry with identifier - " + cartEntryDto.getIdentifier() + "not found");
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }
        modelMapper.map(cartEntryDto, existingCartEntry);
        cartEntryRepository.save(existingCartEntry);
        return cartEntryDto;
    }

    @Override
    public List<CartEntryDto> findByCart(String cartIdentifier) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryRepository.findByCartIdentifier(cartIdentifier),listType);
    }
}