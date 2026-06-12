package com.ust.pos.cartEntry.service.impl;

import com.ust.pos.cartEntry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class CartEntryServiceImpl implements CartEntryService {
    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PriceService priceService;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        cartEntryDto.setIdentifier(cartEntryDto.getCartId()+"_"+cartEntryDto.getProduct());

        String identifier = cartEntryDto.getIdentifier();
        CartEntry existingCartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (existingCartEntry != null) {
            cartEntryDto.setQuantity(cartEntryDto.getQuantity().add(existingCartEntry.getQuantity()));
        }


        cartEntryDto.setDiscount(getDiscountPriceAmount(cartEntryDto.getProduct(),cartEntryDto.getQuantity()));
        cartEntryDto.setTotalPrice(getTotalPrice(cartEntryDto.getProduct(),cartEntryDto.getQuantity()));
        cartEntryDto.setUnitPrice(getSellingPriceAmount(cartEntryDto.getProduct()));
        cartEntryDto.setTotalOriginalPrice(cartEntryDto.getTotalPrice().add(cartEntryDto.getDiscount()));

        if(existingCartEntry != null){
            modelMapper.map(cartEntryDto, existingCartEntry);
            cartEntryRepository.save(existingCartEntry);
        }else{
            CartEntry cartEntry = modelMapper.map(cartEntryDto, CartEntry.class);
            cartEntryRepository.save(cartEntry);
        }
        recalculate(cartEntryDto.getCartId());
        return cartEntryDto;
    }

    @Override
    @Transactional
    public void delete(String cartId, String product) {
        cartEntryRepository.deleteByCartIdAndProduct(cartId,product);
        recalculate(cartId);
    }

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);
        return modelMapper.map(cartEntryPage.getContent(), listType);
    }

    @Override
    public BigDecimal getSellingPriceAmount(String product) {
        PriceDto sellingPrice = priceService.findByProductAndPriceType(product,"Selling price");
        return sellingPrice.getPriceAmount();
    }

    @Override
    public BigDecimal getDiscountPriceAmount(String product,BigDecimal quantity) {
        PriceDto mrp = priceService.findByProductAndPriceType(product,"MRP");
        PriceDto sellingPrice = priceService.findByProductAndPriceType(product,"Selling price");
        return (mrp.getPriceAmount().subtract(sellingPrice.getPriceAmount())).multiply(quantity);
    }

    @Override
    public BigDecimal getTotalPrice(String product, BigDecimal quantity) {
        return getSellingPriceAmount(product).multiply(quantity);
    }

    @Override
    public void recalculate(String cartId) {
        List<CartEntry> cartEntries = cartEntryRepository.findAllByCartId(cartId);

        BigDecimal cartEntryTotalPrice = new BigDecimal(0);
        BigDecimal cartEntryTotaldiscount = new BigDecimal(0);
        BigDecimal cartEntryTotalOriginalDiscount = new BigDecimal(0);

            for (CartEntry cartEntry : cartEntries){
                cartEntryTotalPrice = cartEntryTotalPrice.add(cartEntry.getTotalPrice());
                cartEntryTotaldiscount = cartEntryTotaldiscount.add(cartEntry.getDiscount());
                cartEntryTotalOriginalDiscount = cartEntryTotalOriginalDiscount.add(cartEntry.getTotalOriginalPrice());
            }

        Cart cart = cartRepository.findByIdentifier(cartId);
        cart.setTotalPrice(cartEntryTotalPrice);
        cart.setDiscount(cartEntryTotaldiscount);
        cart.setTotalOriginalPrice(cartEntryTotalOriginalDiscount);
        cartRepository.save(cart);
    }

    @Override
    public List<CartEntryDto> findByCartId(String cartId) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryRepository.findAllByCartId(cartId), listType);
    }

    @Override
    @Transactional
    public void deleteAllByCartId(String cartId) {
        cartEntryRepository.deleteAllByCartId(cartId);
        recalculate(cartId);
    }
}
