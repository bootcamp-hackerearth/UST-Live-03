package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    private final CartEntryRepository cartEntryRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartRepository cartRepository,
                           CartEntryRepository cartEntryRepository, ModelMapper modelMapper) {
        this.cartEntryRepository = cartEntryRepository;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDto save(
            CartDto cartDto
    ) {

        Cart cart =
                cartRepository.findByIdentifier(
                        cartDto.getIdentifier()
                );

        if (cart == null) {
            cart = new Cart();
        }

        modelMapper.map(
                cartDto,
                cart
        );

        cartRepository.save(cart);

        return modelMapper.map(
                cart,
                CartDto.class
        );
    }

    @Override
    public CartDto recalculateCart(String cartId) {

        Cart cart = cartRepository.findByIdentifier(cartId);

        if (cart == null) {
            cart = new Cart();
            cart.setIdentifier(cartId);
        }

        List<CartEntry> entries =
                cartEntryRepository.findByCartId(cartId);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (CartEntry entry : entries) {

            if (entry.getTotalPrice() != null) {
                subtotal =
                        subtotal.add(
                                entry.getTotalPrice()
                        );
            }

            if (entry.getDiscount() != null) {
                totalDiscount =
                        totalDiscount.add(
                                entry.getDiscount()
                        );
            }
        }

        cart.setDiscount(totalDiscount);

        cart.setTotalPrice(
                subtotal.subtract(totalDiscount)
        );

        cartRepository.save(cart);

        Type listType =
                new TypeToken<List<CartEntryDto>>() {
                }.getType();

        List<CartEntryDto> entryDtos =
                modelMapper.map(entries, listType);

        CartDto cartDto =
                modelMapper.map(
                        cart,
                        CartDto.class
                );

        cartDto.setCartEntries(entryDtos);

        return cartDto;
    }

    @Override
    public void deleteAll() {
        cartRepository.deleteAll();
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
