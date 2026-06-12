package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
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
public class CartEntryServiceImpl implements CartEntryService {

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PriceServiceImpl priceService;

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(cartEntryRepository.findAll(), listType);
        }
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);
        return modelMapper.map(cartEntryPage.getContent(), listType);
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {

        String product = cartEntryDto.getProduct();
        BigDecimal quantity = cartEntryDto.getQuantity();
        String cartId = cartEntryDto.getCart();
        String identifier = product + "_" + cartId;

        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);

        if (cartEntry == null) {
            cartEntry = new CartEntry();
        }

        BigDecimal existingQuantity =
                cartEntry.getQuantity();

        BigDecimal updatedQuantity = existingQuantity.add(quantity);

        cartEntry.setProduct(product);
        cartEntry.setQuantity(updatedQuantity);
        cartEntry.setCart(cartId);
        cartEntry.setIdentifier(identifier);

        PriceDto sellingPriceDto = priceService.findByIdentifier(product + "Selling");
        PriceDto mrpDto = priceService.findByIdentifier(product + "Mrp");

        if (sellingPriceDto == null || mrpDto == null) {
            throw new IllegalArgumentException("Price not configured for product: " + product);
        }

        BigDecimal sellingPrice = sellingPriceDto.getValue();
        BigDecimal mrp = mrpDto.getValue();

        BigDecimal discount = mrp.subtract(sellingPrice).multiply(updatedQuantity);
        BigDecimal originalPrice = mrp.multiply(updatedQuantity);
        BigDecimal totalPrice = sellingPrice.multiply(updatedQuantity);

        cartEntry.setUnitPrice(sellingPrice);
        cartEntry.setOriginalPrice(originalPrice);
        cartEntry.setDiscount(discount);
        cartEntry.setTotalPrice(totalPrice);

        CartEntry savedEntry = cartEntryRepository.save(cartEntry);

        if (cartService.findByIdentifier(cartId) == null) {
            cartService.save(cartId);
        }

        cartService.recalculate(cartId);

        return modelMapper.map(savedEntry, CartEntryDto.class);
    }

    @Override
    public CartEntryDto update(CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getIdentifier();
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry == null) {
            cartEntryDto.setMessage("Entry not found");
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        } else {
            String product = cartEntry.getProduct();
            BigDecimal quantity = cartEntryDto.getQuantity();
            BigDecimal totalPrice = quantity.multiply(cartEntry.getUnitPrice());
            PriceDto mrpDto = priceService.findByIdentifier(product + "Mrp");
            BigDecimal originalPrice = quantity.multiply(mrpDto.getValue());

            cartEntry.setQuantity(quantity);
            cartEntry.setTotalPrice(totalPrice);
            cartEntry.setOriginalPrice(originalPrice);
            cartEntry.setIdentifier(cartEntryDto.getIdentifier());

            cartService.recalculate(cartEntryDto.getCart());
            return modelMapper.map(cartEntryRepository.save(cartEntry), CartEntryDto.class);
        }
    }

    @Override
    public void delete(String identifier) {
        String cart = findByIdentifier(identifier).getCart();
        cartEntryRepository.deleteByIdentifier(identifier);
        cartService.recalculate(cart);
    }

    @Override
    public List<CartEntryDto> findByCart(String cart) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        List<CartEntry> cartEntryList = cartEntryRepository.findByCart(cart);
        return modelMapper.map(cartEntryList, listType);
    }
}
