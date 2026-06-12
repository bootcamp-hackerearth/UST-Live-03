package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
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
    private PriceService priceService;

    @Autowired
    private PriceRepository priceRepository;

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getProduct() + "-" + cartEntryDto.getCart();
        cartEntryDto.setIdentifier(identifier);
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        BigDecimal newQty = cartEntryDto.getQuantity();
        if (cartEntry != null) {
            BigDecimal existingQty = cartEntry.getQuantity();
            newQty = existingQty.add(newQty);
        } else {
            cartEntry = new CartEntry();
        }
        Price price = priceRepository.findByIdentifier(cartEntryDto.getProduct());
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
    public List<CartEntryDto> findAllCarts(String cart) {
        List<CartEntry> cartEntryList = cartEntryRepository.findByCart(cart);
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryList, listType);
    }

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);
        return modelMapper.map(cartEntryPage.getContent(), listType);
    }

    @Override
    public boolean delete(String product, String cart) {
        String identifier = product + "-" + cart;
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry != null) {
            cartEntryRepository.deleteByIdentifier(identifier);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteAllByCart(String cart) {
        List<CartEntry> entries = cartEntryRepository.findByCart(cart);
        if (entries.isEmpty()) {
            return false;
        }
        cartEntryRepository.deleteAll(entries);
        return true;
    }
}