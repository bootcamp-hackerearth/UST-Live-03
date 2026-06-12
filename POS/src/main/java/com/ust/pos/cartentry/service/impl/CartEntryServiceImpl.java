package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CartRepository cartRepository;
    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {

        String identifier = cartEntryDto.getProduct() + "-" + cartEntryDto.getCart();
        cartEntryDto.setIdentifier(identifier);
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        Price price = priceRepository.findByProductIdentifier(cartEntryDto.getProduct());
        BigDecimal newQty = cartEntryDto.getQuantity();
        if (cartEntry != null) {
            BigDecimal existingQty = cartEntry.getQuantity();
            newQty = existingQty.add(newQty);
        } else {
            cartEntry = new CartEntry();
        }
        BigDecimal totalPrice = price.getSellingPrice().multiply(newQty);
        BigDecimal discount = price.getMrp().subtract(price.getSellingPrice());
        cartEntryDto.setQuantity(newQty);
        cartEntryDto.setDiscount(discount.multiply(newQty));
        cartEntryDto.setTotalPrice(totalPrice);
        cartEntryDto.setPrice(price.getMrp());
        cartEntryDto.setSellingPrice(price.getSellingPrice());
        modelMapper.map(cartEntryDto, cartEntry);
        cartEntryRepository.save(cartEntry);
        return cartEntryDto;
    }


    @Override
    public List<CartEntryDto> findAllEntriesForCart(String cart) {
        List<CartEntry> cartEntryList = cartEntryRepository.findByCart(cart);
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryList, listType);
    }

    @Override
    public CartEntryDto update(CartEntryDto cartEntryDto) {

        CartEntry cartEntry = cartEntryRepository.findByIdentifier(cartEntryDto.getIdentifier());

        if (cartEntry == null) {
            cartEntryDto.setSuccess(false);
            cartEntryDto.setMessage("Cart entry not found");
            return cartEntryDto;
        }

        Price price = priceRepository.findByIdentifier(cartEntryDto.getProduct());
        BigDecimal qty = cartEntryDto.getQuantity();
        BigDecimal totalPrice = price.getSellingPrice().multiply(qty);
        BigDecimal discount = price.getCostPrice().subtract(price.getSellingPrice());
        cartEntryDto.setDiscount(discount.multiply(qty));
        cartEntryDto.setTotalPrice(totalPrice);
        cartEntryDto.setPrice(price.getCostPrice());
        cartEntryDto.setSellingPrice(price.getSellingPrice());
        modelMapper.map(cartEntryDto, cartEntry);
        cartEntryRepository.save(cartEntry);
        cartEntryDto.setSuccess(true);
        return cartEntryDto;
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry == null) {
            throw new IllegalArgumentException("CartEntry not found");
        }
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public void deleteAllByCart(String cart) {
        List<CartEntry> cartEntries = cartEntryRepository.findByCart(cart);
        if (cartEntries.isEmpty()) {
            return;
        }
        cartEntryRepository.deleteAll(cartEntries);
    }
}
