package com.ust.pos.cart.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Cart;
import com.ust.pos.modell.CartEntry;
import com.ust.pos.modell.CartEntryRepository;
import com.ust.pos.modell.CartRepository;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends BaseService implements CartService {

    private final CartRepository cartRepository;
    private final CartEntryRepository cartEntryRepository;
    private final PriceService priceService;
    private final ModelMapper modelMapper;

    @Override
    public CartDto save(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);

        if (existingCart != null) {
            if (Boolean.TRUE.equals(existingCart.getDeleted())) {
                cartDto.setMessage("Cart with Identifier " + identifier + " already exists (Soft-Deleted)");
                cartDto.setSuccess(false);
                return cartDto;
            }
            cartDto.setMessage("Cart already exists");
            cartDto.setSuccess(false);
            return cartDto;
        }
        Cart cart = new Cart();
        cart.setIdentifier(identifier);
        cart.setCustomerIdentifier(cartDto.getCustomerIdentifier());
        cart.setCoupon(cartDto.getCoupon());
        cart.setOriginalPrice(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        if (cart.getStatus() == null) {
            cart.setStatus(true);
        }
        setCreatedDetails(cart);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        CartEntry entry = cartEntryRepository.findByIdentifierAndDeletedFalse(identifier);

        if (entry == null) {
            throw new NoSuchElementException("Cart entry not found");
        }
        softDelete(entry);
        setModifiedDetails(entry);
        cartEntryRepository.save(entry);
        String cartId = identifier.split("-")[0];
        Cart cart = cartRepository.findByIdentifierAndDeletedFalse(cartId);
        if (cart != null) {
            setModifiedDetails(cart);
            recalculateAndSave(cart);
        }
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        Cart cart = cartRepository.findByIdentifierAndDeletedFalse(identifier);

        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }
        recalculateAndSave(cart);
        CartDto dto = modelMapper.map(cart, CartDto.class);
        List<CartEntry> entries = cartEntryRepository.findByCartIdentifierAndDeletedFalse(identifier);
        List<CartEntryDto> entryDtos = entries.stream().map(entry -> modelMapper.map(entry, CartEntryDto.class)).toList();
        dto.setEntryCart(entryDtos);
        return dto;
    }

    @Override
    public WsDto<CartDto> findAll(Pageable pageable) {
        Page<Cart> cartPage = cartRepository.findAllByDeletedFalse(pageable);
        List<CartDto> cartDtos = cartPage.getContent().stream().map(cart -> {
            recalculateAndSave(cart);
            CartDto dto = modelMapper.map(cart, CartDto.class);
            List<CartEntry> entries = cartEntryRepository.findByCartIdentifierAndDeletedFalse(cart.getIdentifier());
            dto.setEntryCart(entries.stream().map(entry -> modelMapper.map(entry, CartEntryDto.class)).toList());
            return dto;
        }).toList();
        WsDto<CartDto> wsDto = new WsDto<>();
        wsDto.setDtoList(cartDtos);
        wsDto.setTotalRecords(cartPage.getTotalElements());
        wsDto.setTotalPage(cartPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    public void recalculateAndSave(Cart cart) {
        List<CartEntry> entries = cartEntryRepository.findByCartIdentifierAndDeletedFalse(cart.getIdentifier());
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        for (CartEntry entry : entries) {
            String productId = entry.getProductIdentifier();
            int qty = entry.getQuantity();
            PriceDto selling = priceService.findByIdentifier(productId + "-SELLING");
            PriceDto mrp = priceService.findByIdentifier(productId + "-MRP");

            if (selling == null && mrp == null) {
                throw new NoSuchElementException("Price not configured");
            }

            if (mrp != null) {
                originalPrice = originalPrice.add(mrp.getPriceAmount().multiply(BigDecimal.valueOf(qty)));
            }

            BigDecimal unitPrice;

            if (selling != null) {
                unitPrice = selling.getPriceAmount();
            } else {
                unitPrice = mrp.getPriceAmount();
            }

            totalPrice = totalPrice.add(unitPrice.multiply(BigDecimal.valueOf(qty)));
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
        setModifiedDetails(cart);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(String cartIdentifier) {
        Cart cart = cartRepository.findByIdentifierAndDeletedFalse(cartIdentifier);

        if (cart == null) {
            throw new NoSuchElementException("Cart not found");
        }

        List<CartEntry> activeEntries = cartEntryRepository.findByCartIdentifierAndDeletedFalse(cartIdentifier);
        for (CartEntry entry : activeEntries) {
            softDelete(entry);
            setModifiedDetails(entry);
            cartEntryRepository.save(entry);
        }

        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setOriginalPrice(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        setModifiedDetails(cart);
        cartRepository.save(cart);
    }

    @Override
    public WsDto<CartDto> findAll(Specification<Cart> example, Pageable pageable) {

        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        Page<Cart> page = cartRepository.findAll(example, pageable);

        WsDto<CartDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}