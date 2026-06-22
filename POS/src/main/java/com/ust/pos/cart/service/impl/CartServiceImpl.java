package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import com.ust.pos.model.CustomerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


@Service
@Transactional
public class CartServiceImpl extends CommonService implements CartService {
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CartEntryService cartEntryService;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CustomerRepository customerRepository, CartRepository cartRepository,
                           CartEntryService cartEntryService, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
        this.cartEntryService = cartEntryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            return null;
        }
        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        List<CartEntryDto> cartEntries = cartEntryService.findAllByCartIdentifier(identifier);
        BigDecimal totalPrice = cartEntries.stream()
                .map(CartEntryDto::getTotalPrice).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscount = cartEntries.stream()
                .map(CartEntryDto::getDiscount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cartDto.setCartEntries(cartEntries);
        cartDto.setTotalPrice(totalPrice);
        cartDto.setDiscount(totalDiscount);
        return cartDto;
    }

    @Override
    public CartDto save(CartDto cartDto) {
        String identifier = cartDto.getUsername() != null
                ? cartDto.getUsername()
                : cartDto.getIdentifier();
        cartDto.setIdentifier(identifier);
        cartDto.setUsername(identifier);

        if (customerRepository.findByIdentifier(identifier) == null) {
            cartDto.setMessage("Customer with identifier - " + identifier + " not found");
            cartDto.setSuccess(false);
            return cartDto;
        }

        Cart existingCart = cartRepository.findByIdentifier(identifier);
        if (existingCart != null) {
            if (cartDto.getCartEntries() != null) {
                for (CartEntryDto cartEntryDto : cartDto.getCartEntries()) {
                    cartEntryDto.setCartIdentifier(identifier);
                    cartEntryService.save(cartEntryDto);
                }
            }
            List<CartEntryDto> cartEntries = cartEntryService.findAllByCartIdentifier(identifier);
            BigDecimal totalPrice = cartEntries.stream()
                    .map(CartEntryDto::getTotalPrice).filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalDiscount = cartEntries.stream()
                    .map(CartEntryDto::getDiscount).filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            existingCart.setTotalPrice(totalPrice);
            existingCart.setDiscount(totalDiscount);
            if (cartDto.getCoupon() != null) {
                existingCart.setCoupon(cartDto.getCoupon());
            }
            cartRepository.save(existingCart);
            return findByIdentifier(identifier);
        }

        Cart cart = new Cart();
        cart.setIdentifier(identifier);
        cart.setUsername(identifier);
        cart.setCoupon(cartDto.getCoupon());
        cart.setDiscount(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        if (cartDto.getCartEntries() != null) {
            for (CartEntryDto cartEntryDto : cartDto.getCartEntries()) {
                cartEntryDto.setCartIdentifier(identifier);
                cartEntryService.save(cartEntryDto);
            }
        }

        List<CartEntryDto> cartEntries = cartEntryService.findAllByCartIdentifier(identifier);
        BigDecimal totalPrice = cartEntries.stream()
                .map(CartEntryDto::getTotalPrice).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscount = cartEntries.stream()
                .map(CartEntryDto::getDiscount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(totalPrice);
        cart.setDiscount(totalDiscount);
        cartRepository.save(cart);
        return findByIdentifier(identifier);
    }

    @Override
    public CartDto update(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);
        if (existingCart == null) {
            cartDto.setMessage("Cart with identifier - " + identifier + " not found");
            cartDto.setSuccess(false);
            return cartDto;
        }
        existingCart.setUsername(cartDto.getUsername());
        existingCart.setCoupon(cartDto.getCoupon());
        cartRepository.save(existingCart);

        if (cartDto.getCartEntries() != null) {
            for (CartEntryDto cartEntryDto : cartDto.getCartEntries()) {
                cartEntryDto.setCartIdentifier(identifier);
                if (cartEntryDto.getIdentifier() == null) {
                    cartEntryService.save(cartEntryDto);
                } else {
                    cartEntryService.update(cartEntryDto);
                }
            }
        }

        List<CartEntryDto> cartEntries = cartEntryService.findAllByCartIdentifier(identifier);
        BigDecimal totalPrice = cartEntries.stream()
                .map(CartEntryDto::getTotalPrice).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscount = cartEntries.stream()
                .map(CartEntryDto::getDiscount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        existingCart.setTotalPrice(totalPrice);
        existingCart.setDiscount(totalDiscount);
        cartRepository.save(existingCart);
        return findByIdentifier(identifier);
    }

    @Override
    public boolean delete(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            return false;
        }
        cartEntryService.deleteByCartIdentifier(identifier);
        cartRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public boolean deleteCartEntry(String identifier) {
        CartEntryDto cartEntry = cartEntryService.findByIdentifier(identifier);
        if (cartEntry == null) {
            return false;
        }
        String cartIdentifier = cartEntry.getCartIdentifier();
        cartEntryService.delete(identifier);

        Cart cart = cartRepository.findByIdentifier(cartIdentifier);
        if (cart == null) {
            return false;
        }
        List<CartEntryDto> remainingEntries = cartEntryService.findAllByCartIdentifier(cartIdentifier);
        BigDecimal totalPrice = remainingEntries.stream()
                .map(CartEntryDto::getTotalPrice).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscount = remainingEntries.stream()
                .map(CartEntryDto::getDiscount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(totalPrice);
        cart.setDiscount(totalDiscount);
        cartRepository.save(cart);
        return true;
    }

    @Override
    public List<CartDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        List<CartDto> cartDtos = modelMapper.map(cartPage.getContent(), listType);
        cartDtos.forEach(cartDto -> {
            List<CartEntryDto> cartEntries = cartEntryService.findAllByCartIdentifier(cartDto.getIdentifier());
            BigDecimal totalPrice = cartEntries.stream()
                    .map(CartEntryDto::getTotalPrice).filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalDiscount = cartEntries.stream()
                    .map(CartEntryDto::getDiscount).filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cartDto.setCartEntries(cartEntries);
            cartDto.setTotalPrice(totalPrice);
            cartDto.setDiscount(totalDiscount);
        });
        return cartDtos;
    }
}