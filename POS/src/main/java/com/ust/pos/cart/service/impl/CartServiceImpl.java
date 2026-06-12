package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.CartEntry;
import com.ust.pos.modell.CartEntryRepository;
import com.ust.pos.modell.CartRepository;
import com.ust.pos.modell.Cart;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private PriceService priceService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDto save(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        if (cartRepository.findByIdentifier(identifier) != null) {
            cartDto.setMessage("Cart already exists");
            cartDto.setSuccess(false);
            return cartDto;
        }
        Cart cart = new Cart();
        cart.setIdentifier(identifier);
        cart.setCoupon(cartDto.getCoupon());
        cart.setOriginalPrice(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        cartRepository.save(cart);
        return cartDto;
    }

    @Transactional
    public void delete(String identifier) {
        CartEntry entry = cartEntryRepository.findByIdentifier(identifier);

        if (entry == null) {
            throw new RuntimeException("Cart entry not found");
        }
        cartEntryRepository.delete(entry);
        String cartId = identifier.split("-")[0];
        Cart cart = cartRepository.findByIdentifier(cartId);
        if (cart != null) {
            recalculateAndSave(cart);
        }
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }
        recalculateAndSave(cart);
        CartDto dto = modelMapper.map(cart, CartDto.class);
        List<CartEntry> entries = cartEntryRepository.findByCartIdentifier(identifier);
        List<CartEntryDto> entryDtos = entries.stream()
                .map(e -> modelMapper.map(e, CartEntryDto.class))
                .toList();
        dto.setEntryCart(entryDtos);
        return dto;
    }

    @Override
    public WsDto<CartDto> findAll(Pageable pageable) {
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        List<CartDto> cartDtos = cartPage.getContent().stream().map(cart -> {
            recalculateAndSave(cart);
            CartDto dto = modelMapper.map(cart, CartDto.class);
            List<CartEntry> entries =
                    cartEntryRepository.findByCartIdentifier(cart.getIdentifier());
            dto.setEntryCart(
                    entries.stream()
                            .map(e -> modelMapper.map(e, CartEntryDto.class))
                            .toList()
            );
            return dto;
        }).toList();
        WsDto<CartDto> cartWsDto = new WsDto<>();
        cartWsDto.setDtoList(cartDtos);
        cartWsDto.setTotalRecords(cartPage.getTotalElements());
        cartWsDto.setTotalPage(cartPage.getTotalPages());
        cartWsDto.setSizePerPage(pageable.getPageSize());
        cartWsDto.setPage(pageable.getPageNumber());
        return cartWsDto;
    }

    public void recalculateAndSave(Cart cart) {
        List<CartEntry> entries =
                cartEntryRepository.findByCartIdentifier(cart.getIdentifier());
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;
        for (CartEntry entry : entries) {
            String productId = entry.getProductIdentifier();
            int qty = entry.getQuantity();
            PriceDto selling =
                    priceService.findByIdentifier(productId + "-SELLING");
            PriceDto mrp =
                    priceService.findByIdentifier(productId + "-MRP");
            if (selling == null && mrp == null) {
                throw new RuntimeException("Price not configured");
            }
            if (mrp != null) {
                originalPrice = originalPrice.add(
                        mrp.getPriceAmount().multiply(BigDecimal.valueOf(qty))
                );
            }
            BigDecimal unitPrice;
            if (selling != null) {
                unitPrice = selling.getPriceAmount();
            } else {
                unitPrice = mrp.getPriceAmount();
            }
            totalPrice = totalPrice.add(
                    unitPrice.multiply(BigDecimal.valueOf(qty))
            );
        }
        BigDecimal discount = originalPrice.subtract(totalPrice);
        if ("FLAT10".equalsIgnoreCase(cart.getCoupon())) {
            BigDecimal extra = totalPrice.multiply(BigDecimal.valueOf(10)).divide(BigDecimal.valueOf(100));
            discount = discount.add(extra);
            totalPrice = totalPrice.subtract(extra);
        }
        cart.setOriginalPrice(originalPrice);
        cart.setTotalPrice(totalPrice);
        cart.setDiscount(discount);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(String cartIdentifier) {
        Cart cart = cartRepository.findByIdentifier(cartIdentifier);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }
        cartEntryRepository.deleteByCartIdentifier(cartIdentifier);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setOriginalPrice(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

}

