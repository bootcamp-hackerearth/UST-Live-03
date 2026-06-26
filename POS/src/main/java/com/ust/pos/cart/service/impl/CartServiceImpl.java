package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import com.ust.pos.model.Coupon;
import com.ust.pos.model.CouponRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartEntryService cartEntryService;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final ModelMapper modelMapper;

    public CartServiceImpl(
            CartEntryService cartEntryService,
            CartRepository cartRepository,
            CouponRepository couponRepository,
            ModelMapper modelMapper) {
        this.cartEntryService = cartEntryService;
        this.cartRepository = cartRepository;
        this.couponRepository = couponRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDto save(CartDto cartDto) {
        Cart cart = modelMapper.map(cartDto, Cart.class);
        cartRepository.save(cart);
        return findByIdentifier(cart.getIdentifier());
    }

    @Override
    public CartDto recalulateCart(String cartId) {

        Cart cart = cartRepository.findByIdentifier(cartId);

        if (cart == null) {
            cart = new Cart();
            cart.setIdentifier(cartId);
            cartRepository.save(cart);
        }

        List<CartEntryDto> entries = cartEntryService.findByCartId(cartId);
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartEntryDto entry : entries) {
            BigDecimal qty = entry.getQuantity() != null
                    ? entry.getQuantity()
                    : BigDecimal.ZERO;
            BigDecimal price = entry.getUnitPrice() != null
                    ? entry.getUnitPrice()
                    : BigDecimal.ZERO;
            subtotal = subtotal.add(price.multiply(qty));
        }
        String couponCode = cart.getCouponCode();
        BigDecimal discount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isBlank()) {
            discount = calculateCouponDiscount(couponCode, subtotal);
        }
        BigDecimal finalTotal = subtotal.subtract(discount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }
        cart.setDiscount(discount);
        cart.setTotalPrice(finalTotal);
        cartRepository.save(cart);
        CartDto dto = modelMapper.map(cart, CartDto.class);
        dto.setCartEntries(entries);
        return dto;
    }

    private BigDecimal calculateCouponDiscount(String code, BigDecimal total) {
        Coupon coupon = couponRepository.findByCode(code).orElse(null);
        if (coupon == null) return BigDecimal.ZERO;
        if (!coupon.isActive()) return BigDecimal.ZERO;

        if (coupon.getExpiryDate() != null &&
                coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            return BigDecimal.ZERO;
        }
        if (coupon.getUsageLimit() != null &&
                coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return BigDecimal.ZERO;
        }
        if ("PERCENT".equalsIgnoreCase(coupon.getType())) {
            return total.multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100));
        }
        if ("FLAT".equalsIgnoreCase(coupon.getType())) {
            return coupon.getValue();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public CartDto applyCoupon(String cartId, String couponCode) {

        Cart cart = cartRepository.findByIdentifier(cartId);

        if (cart == null) {
            CartDto dto = new CartDto();
            dto.setSuccess(false);
            dto.setMessage("Cart not found");
            return dto;
        }

        Coupon coupon = couponRepository.findByCode(couponCode).orElse(null);

        if (coupon == null) {
            CartDto dto = new CartDto();
            dto.setSuccess(false);
            dto.setMessage("Invalid coupon");
            return dto;
        }

        if (!coupon.isActive()) {
            CartDto dto = new CartDto();
            dto.setSuccess(false);
            dto.setMessage("Coupon is inactive");
            return dto;
        }

        if (coupon.getExpiryDate() != null
                && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            CartDto dto = new CartDto();
            dto.setSuccess(false);
            dto.setMessage("Coupon expired");
            return dto;
        }

        if (coupon.getUsageLimit() != null
                && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            CartDto dto = new CartDto();
            dto.setSuccess(false);
            dto.setMessage("Coupon limit reached");
            return dto;
        }

        cart.setCouponCode(couponCode);
        cartRepository.save(cart);

        CartDto dto = recalulateCart(cartId);
        dto.setSuccess(true);
        dto.setMessage("Coupon applied successfully");
        return dto;
    }

    @Override
    public CartDto removeCoupon(String cartId) {

        Cart cart = cartRepository.findByIdentifier(cartId);
        if (cart == null) {
            CartDto dto = new CartDto();
            dto.setSuccess(false);
            dto.setMessage("Cart not found");
            return dto;
        }
        cart.setCouponCode(null);
        cart.setDiscount(BigDecimal.ZERO);
        cartRepository.save(cart);
        return recalulateCart(cartId);
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            CartDto dto = new CartDto();
            dto.setIdentifier(identifier);
            dto.setTotalPrice(BigDecimal.ZERO);
            dto.setDiscount(BigDecimal.ZERO);
            dto.setCartEntries(List.of());
            return dto;
        }
        CartDto dto = modelMapper.map(cart, CartDto.class);
        List<CartEntryDto> entries = cartEntryService.findByCartId(identifier);
        dto.setCartEntries(entries);
        return dto;
    }

    @Override
    public void delete(String identifier) {
        cartRepository.deleteByIdentifier(identifier);
    }

    @Override
    public CartDto update(CartDto cartDto) {
        Cart existing = cartRepository.findByIdentifier(cartDto.getIdentifier());
        if (existing == null) {
            cartDto.setMessage("Cart not found - " + cartDto.getIdentifier());
            cartDto.setSuccess(false);
            return cartDto;
        }
        existing.setTotalPrice(cartDto.getTotalPrice());
        existing.setDiscount(cartDto.getDiscount());
        cartRepository.save(existing);
        return findByIdentifier(existing.getIdentifier());
    }

    @Override
    public List<CartDto> findAll() {
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        return modelMapper.map(cartRepository.findAll(), listType);
    }

    @Override
    public List<CartDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        Page<Cart> page = cartRepository.findAll(pageable);
        return modelMapper.map(page.getContent(), listType);
    }
}