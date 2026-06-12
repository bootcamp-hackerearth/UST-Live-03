package com.ust.pos.cart.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import com.ust.pos.cart.service.CartService;
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
public class CartServiceImpl implements CartService {

    @Autowired
    private CartEntryService cartEntryService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDto save(CartDto cartDto) {
        Cart cart = modelMapper.map(cartDto, Cart.class);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    public CartDto recalculateCart(String cartId) {
        Cart cart = cartRepository.findByIdentifier(cartId);
        if (cart == null) {
            cart = new Cart();
            cart.setIdentifier(cartId);
        }

        List<CartEntryDto> entries = cartEntryService.findByCartId(cartId);
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal discount = cart.getDiscount() != null
                ? cart.getDiscount()
                : BigDecimal.ZERO;

        for (CartEntryDto entry : entries) {
            if (entry.getTotalPrice() != null) {
                totalPrice = totalPrice.add(entry.getTotalPrice());
            }
        }

        cart.setTotalPrice(totalPrice.subtract(discount));

        cartRepository.save(cart);
        CartDto cartDto1 = modelMapper.map(cart, CartDto.class);
        cartDto1.setCartEntries(entries);
        return cartDto1;
    }

    @Override
    public CartDto update(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);

        if (existingCart == null) {
            cartDto.setMessage("Cart with identifier - " + identifier + " is not found");
            cartDto.setSuccess(false);
            return cartDto;
        }

        Cart cart = modelMapper.map(cartDto, Cart.class);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    public void delete(String identifier) {
        cartRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartDto> findAll() {
        Type listOfType = new TypeToken<List<CartDto>>() {
        }.getType();
        return modelMapper.map(cartRepository.findAll(), listOfType);
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        return modelMapper.map(cartRepository.findByIdentifier(identifier), CartDto.class);
    }

    @Override
    public List<CartDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<CartDto>>() {
        }.getType();
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        return modelMapper.map(cartPage.getContent(), listOfType);
    }
}