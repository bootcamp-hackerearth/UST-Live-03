package com.ust.pos.cart.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl extends CommonService implements CartService {

    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final CartEntryService cartEntryService;

    public CartServiceImpl(CartRepository cartRepository,
                           ModelMapper modelMapper,
                           CartEntryService cartEntryService) {
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
        this.cartEntryService = cartEntryService;
    }

    @Override
    public CartDto save(CartDto cartDto) {

        if (cartDto == null || cartDto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        String identifier = cartDto.getIdentifier();

        if (cartRepository.existsByIdentifier(identifier)) {
            cartDto.setSuccess(false);
            cartDto.setMessage("Cart with identifier '" + identifier + "' already exists");
            return cartDto;
        }

        Cart cart = modelMapper.map(cartDto, Cart.class);
        setAuditFields(cart, true);

        Cart saved = cartRepository.save(cart);

        CartDto response = modelMapper.map(saved, CartDto.class);
        response.setSuccess(true);
        response.setMessage("Cart created successfully");

        return response;
    }

    @Override
    public CartDto recalculate(String identifier) {

        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            CartDto dto = new CartDto();
            dto.setSuccess(false);
            dto.setMessage("Cart not found: " + identifier);
            return dto;
        }

        List<CartEntryDto> entries = cartEntryService.findAllEntriesForCart(identifier);

        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        for (CartEntryDto entry : entries) {
            originalPrice = originalPrice.add(entry.getTotalPrice());
            totalDiscount = totalDiscount.add(entry.getDiscount());
        }

        BigDecimal totalPrice = originalPrice.subtract(totalDiscount);

        cart.setOriginalPrice(originalPrice);
        cart.setTotalDiscount(totalDiscount);
        cart.setTotalPrice(totalPrice);

        setAuditFields(cart, false);

        cartRepository.save(cart);

        CartDto dto = modelMapper.map(cart, CartDto.class);

        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        dto.setEntryList(modelMapper.map(entries, listType));

        dto.setSuccess(true);
        dto.setMessage("Cart recalculated successfully");

        return dto;
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        cartRepository.deleteByIdentifier(identifier);
        cartEntryService.deleteAllByCart(identifier);
    }

    @Override
    public CartDto findByIdentifier(String identifier) {

        Cart cart = cartRepository.findByIdentifier(identifier);

        if (cart == null) {
            CartDto newCart = new CartDto();
            newCart.setIdentifier(identifier);

            return save(newCart);
        }

        CartDto dto = modelMapper.map(cart, CartDto.class);

        dto.setEntryList(
                cartEntryService.findAllEntriesForCart(identifier)
        );

        return dto;
    }
}
