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
    public void deleteByIdentifier(String identifier) throws RuntimeException {
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry == null) throw new IllegalArgumentException("CartEntry not found");
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
