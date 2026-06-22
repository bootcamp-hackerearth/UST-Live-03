package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class CartEntryServiceImpl implements CartEntryService {

    private final CartEntryRepository cartEntryRepository;

    private final ModelMapper modelMapper;

    private final PriceService priceService;

    private final PriceRepository priceRepository;

    private final CartRepository cartRepository;



    public CartEntryServiceImpl(CartEntryRepository cartEntryRepository, ModelMapper modelMapper, PriceService priceService, PriceRepository priceRepository, CartRepository cartRepository) {
        this.cartEntryRepository = cartEntryRepository;
        this.modelMapper = modelMapper;
        this.priceService = priceService;
        this.priceRepository = priceRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getProduct() + "-" + cartEntryDto.getCart();
        cartEntryDto.setIdentifier(identifier);
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        Price price = priceRepository.findByIdentifier(cartEntryDto.getProduct());
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
    @Transactional
    public void deleteByIdentifier(String identifier) {
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