package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
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
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Override
    public List<CartDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(cartRepository.findAll(), listType);
        }
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        return modelMapper.map(cartPage.getContent(), listType);
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);

        if (cart == null) {
            return null;
        }
        return modelMapper.map(cart, CartDto.class);
    }

    @Override
    public CartDto save(String identifier) {
        CartDto cartDto = new CartDto();
        cartDto.setTotalPrice(new BigDecimal(0));
        cartDto.setTotalDiscount(new BigDecimal(0));
        cartDto.setTotalOriginalPrice(new BigDecimal(0));
        cartDto.setIdentifier(identifier);
        cartRepository.save(modelMapper.map(cartDto, Cart.class));
        return cartDto;
    }

    @Override
    public CartDto recalculate(String identifier) {

        Cart cart = cartRepository.findByIdentifier(identifier);

        if (cart == null) {
            CartDto dto = new CartDto();
            dto.setMessage("Cart not found");
            dto.setSuccess(false);
            return dto;
        }

        List<CartEntry> cartEntryList = cartEntryRepository.findByCart(identifier);

        BigDecimal totalOriginalPrice = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartEntry cartEntry : cartEntryList) {
            totalOriginalPrice = totalOriginalPrice.add(cartEntry.getOriginalPrice());
            totalDiscount = totalDiscount.add(cartEntry.getDiscount());
            totalPrice = totalPrice.add(cartEntry.getTotalPrice());
        }

        cart.setTotalOriginalPrice(totalOriginalPrice);
        cart.setTotalDiscount(totalDiscount);
        cart.setTotalPrice(totalPrice);

        Cart saved = cartRepository.save(cart);

        return modelMapper.map(saved, CartDto.class);
    }

    @Override
    public void delete(String identifier) {
        cartEntryRepository.deleteByCart(identifier);
        cartRepository.deleteByIdentifier(identifier);
    }
}
