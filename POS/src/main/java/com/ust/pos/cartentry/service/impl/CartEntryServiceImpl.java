package com.ust.pos.cartentry.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.*;
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
public class CartEntryServiceImpl extends BaseService implements CartEntryService {

    private final CartEntryRepository cartEntryRepository;

    private final CartRepository cartRepository;

    private final PriceRepository priceRepository;

    private final ModelMapper modelMapper;

    public CartEntryServiceImpl(CartEntryRepository cartEntryRepository, CartRepository cartRepository, PriceRepository priceRepository, ModelMapper modelMapper) {
        this.cartEntryRepository = cartEntryRepository;
        this.cartRepository = cartRepository;
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {

        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);

        if (cartEntry == null) {
            throw new ResourseNotFoundException("Data cannot found");
        }

        return modelMapper.map(cartEntry, CartEntryDto.class);
    }

    @Override
    public BigDecimal getSellingPrice(String product) {

        Price price = priceRepository.
                findByProductAndPriceType(product, "Selling Price");
        return price.getPriceAmount();
    }

    @Override
    public BigDecimal getDiscount(CartEntryDto cartEntryDto) {

        BigDecimal sellingPrice = getSellingPrice(cartEntryDto.getProduct());
        Price price = priceRepository.
                findByProductAndPriceType(cartEntryDto.getProduct(), "MRP");
        BigDecimal mrp = price.getPriceAmount();
        BigDecimal discount = mrp.subtract(sellingPrice);

        return discount.multiply(cartEntryDto.getQuantity());
    }

    private CartEntryDto persistCartEntry(CartEntryDto cartEntryDto,
                                          boolean mergeQuantity) {

        String product = cartEntryDto.getProduct();
        String cartId = cartEntryDto.getCartId();

        cartEntryDto.setIdentifier(product + "_" + cartId);
        cartEntryDto.setUnitPrice(getSellingPrice(product));

        String identifier = cartEntryDto.getIdentifier();

        CartEntry existingCartEntry =
                cartEntryRepository.findByIdentifier(identifier);

        if (mergeQuantity && existingCartEntry != null) {
            cartEntryDto.setQuantity(
                    cartEntryDto.getQuantity()
                            .add(existingCartEntry.getQuantity())
            );
        }

        Price mrpPrice =
                priceRepository.findByProductAndPriceType(product, "MRP");

        BigDecimal mrp = mrpPrice.getPriceAmount();

        cartEntryDto.setDiscount(getDiscount(cartEntryDto));
        cartEntryDto.setOriginalPrice(
                mrp.multiply(cartEntryDto.getQuantity()));

        cartEntryDto.setTotalPrice(
                cartEntryDto.getUnitPrice()
                        .multiply(cartEntryDto.getQuantity()));

        if (existingCartEntry != null) {
            modelMapper.map(cartEntryDto, existingCartEntry);
            setModifiedDetails(existingCartEntry);
            cartEntryRepository.save(existingCartEntry);
        } else {
            CartEntry cartEntry =
                    modelMapper.map(cartEntryDto, CartEntry.class);

            setCreatedDetails(cartEntry);
            cartEntryRepository.save(cartEntry);
        }

        recalculate(cartId);

        return cartEntryDto;
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        return persistCartEntry(cartEntryDto, true);
    }

    @Override
    public CartEntryDto updateQuantity(CartEntryDto cartEntryDto) {
        return persistCartEntry(cartEntryDto, false);
    }

    @Override
    public void recalculate(String cartId) {

        List<CartEntry> cartEntries = cartEntryRepository.findAllByCartId(cartId);

        BigDecimal cartEntryTotalPrice = new BigDecimal(0);
        BigDecimal cartEntryTotaldiscount = new BigDecimal(0);
        BigDecimal cartEntryOriginalPrice = new BigDecimal(0);

        for (CartEntry cartEntry : cartEntries) {
            cartEntryTotalPrice = cartEntryTotalPrice.add(cartEntry.getTotalPrice());
            cartEntryTotaldiscount = cartEntryTotaldiscount.add(cartEntry.getDiscount());
            cartEntryOriginalPrice = cartEntryOriginalPrice.add(cartEntry.getOriginalPrice());
        }

        Cart cart = cartRepository.findByIdentifier(cartId);
        cart.setTotalPrice(cartEntryTotalPrice);
        cart.setDiscount(cartEntryTotaldiscount);
        cart.setOriginalPrice(cartEntryOriginalPrice);

        cartRepository.save(cart);
    }

    @Override
    public List<CartEntryDto> findByCartId(String cartId) {

        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        List<CartEntry> cartEntryList = cartEntryRepository.findByCartId(cartId);

        return modelMapper.map(cartEntryList, listType);
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        String cartId = cartEntryRepository.findByIdentifier(identifier).getCartId();
        cartEntryRepository.deleteByIdentifier(identifier);

        recalculate(cartId);
    }

    @Override
    @Transactional
    public void deleteAllByCartId(String cartId) {
        cartEntryRepository.deleteAllByCartId(cartId);

        recalculate(cartId);
    }

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);

        return modelMapper.map(cartEntryPage.getContent(), listType);
    }
}









