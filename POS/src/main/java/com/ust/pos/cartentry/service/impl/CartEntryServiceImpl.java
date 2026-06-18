package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.*;
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
public class CartEntryServiceImpl implements CartEntryService {
    @Autowired
    private PriceService priceService;
    @Autowired
    private CartService cartService;
    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private CartEntryRepository cartEntryRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        String identifier =
                cartEntryDto.getCartId()
                        + "_"
                        + cartEntryDto.getProduct();

        CartEntry existingCartEntry =
                cartEntryRepository.findByIdentifier(identifier);

        BigDecimal quantity =
                cartEntryDto.getQuantity();

        if (existingCartEntry != null) {
            quantity =
                    existingCartEntry
                            .getQuantity()
                            .add(quantity);
        }

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {

            if (existingCartEntry != null) {

                cartEntryRepository.delete(existingCartEntry);

                cartService.recalculateCart(
                        cartEntryDto.getCartId()
                );
            }

            return new CartEntryDto();
        }

        Price price =
                priceRepository.findByIdentifier(
                        cartEntryDto.getProduct()
                );

        if (price == null) {

            cartEntryDto.setSuccess(false);
            cartEntryDto.setMessage(
                    "Price details not found for product '"
                            + cartEntryDto.getProduct()
                            + "'"
            );

            return cartEntryDto;
        }

        BigDecimal unitPrice =
                price.getSellingPrice();

        BigDecimal mrpPrice =
                price.getMrp();

        BigDecimal originalPrice =
                mrpPrice.multiply(quantity);

        BigDecimal totalPrice =
                unitPrice.multiply(quantity);

        BigDecimal discount =
                originalPrice.subtract(totalPrice);

        CartEntry cartEntry;

        if (existingCartEntry != null) {
            cartEntry = existingCartEntry;
        } else {
            cartEntry = new CartEntry();
        }

        cartEntry.setIdentifier(identifier);
        cartEntry.setCartId(cartEntryDto.getCartId());
        cartEntry.setProduct(cartEntryDto.getProduct());
        cartEntry.setQuantity(quantity);
        cartEntry.setUnitPrice(unitPrice);
        cartEntry.setDiscount(discount);
        cartEntry.setTotalPrice(totalPrice);

        CartEntry savedCartEntry =
                cartEntryRepository.save(cartEntry);

        Cart cart =
                cartRepository.findByIdentifier(
                        cartEntryDto.getCartId()
                );

        if (cart == null) {

            cart = new Cart();

            cart.setIdentifier(
                    cartEntryDto.getCartId()
            );

            cartRepository.save(cart);
        }

        cartService.recalculateCart(
                cartEntryDto.getCartId()
        );

        return modelMapper.map(
                savedCartEntry,
                CartEntryDto.class
        );


    }


    @Override
    public CartEntryDto update(
            CartEntryDto cartEntryDto) {

        CartEntry existing =
                cartEntryRepository.findByIdentifier(
                        cartEntryDto.getIdentifier());

        if (existing == null) {
            cartEntryDto.setSuccess(false);
            cartEntryDto.setMessage(
                    "Cart Entry not found");
            return cartEntryDto;
        }

        BigDecimal quantity =
                cartEntryDto.getQuantity() != null
                        ? cartEntryDto.getQuantity()
                        : BigDecimal.ZERO;

        PriceDto priceDto =
                priceService.findByIdentifier(
                        existing.getProduct());

        BigDecimal sellingPrice =
                priceDto.getSellingPrice() != null
                        ? priceDto.getSellingPrice()
                        : BigDecimal.ZERO;

        BigDecimal mrp =
                priceDto.getMrp() != null
                        ? priceDto.getMrp()
                        : BigDecimal.ZERO;

        BigDecimal discountPerUnit =
                mrp.subtract(sellingPrice);

        existing.setQuantity(quantity);

        existing.setUnitPrice(sellingPrice);

        existing.setDiscount(
                discountPerUnit.multiply(quantity));

        existing.setTotalPrice(
                sellingPrice.multiply(quantity));

        cartEntryRepository.save(existing);

        return modelMapper.map(
                existing,
                CartEntryDto.class);
    }

    @Override
    public void delete(String identifier) {
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartEntryDto> findAll() {
        Type listOfType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryRepository.findAll(), listOfType);
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);
        return modelMapper.map(cartEntryPage.getContent(), listOfType);
    }

    @Override
    public List<CartEntryDto> findByCartId(String cart) {
        Type listOfType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        List<CartEntry> cartEntryList = cartEntryRepository.findByCartId(cart);
        return modelMapper.map(cartEntryList, listOfType);
    }
}
