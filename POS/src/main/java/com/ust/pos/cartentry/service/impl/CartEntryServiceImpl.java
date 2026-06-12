package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    @Autowired
    private CartService cartService;

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
        Price sellingPrice = priceRepository.findByProductIdentifierAndPriceType(cartEntryDto.getProductIdentifier(), "sellingPrice");
        Price mrp = priceRepository.findByProductIdentifierAndPriceType(cartEntryDto.getProductIdentifier(), "Mrp");
        BigDecimal unitPrice = sellingPrice.getPriceAmount();
        BigDecimal mrpPrice = mrp.getPriceAmount();

        cartEntry.setQuantity(quantity);
        cartEntry.setUnitPrice(unitPrice);
        BigDecimal originalPrice = quantity.multiply(mrpPrice);
        cartEntry.setOriginalPrice(originalPrice);

        BigDecimal discountPerUnit = mrpPrice.subtract(unitPrice);
        BigDecimal totalDiscount = discountPerUnit.multiply(quantity);
        cartEntry.setDiscount(totalDiscount);
        cartEntry.setTotalPrice(unitPrice.multiply(quantity));

        cartEntryRepository.save(cartEntry);
        cartService.recalculate(cartEntry.getCartIdentifier());
        return modelMapper.map(cartEntry, CartEntryDto.class);
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartEntryDto> findAllEntriesForCart(String cartIdentifier) {
        List<CartEntry> cartEntryList = cartEntryRepository.findByCartIdentifier(cartIdentifier);
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryList, listType);
    }

    @Override
    @Transactional
    public void deleteAllByCartIdentifier(String cart) {
        cartEntryRepository.deleteAllByCartIdentifier(cart);
    }

}
