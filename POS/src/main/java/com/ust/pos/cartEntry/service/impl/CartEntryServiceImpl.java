package com.ust.pos.cartEntry.service.impl;

import com.ust.pos.cartEntry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.*;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class CartEntryServiceImpl implements CartEntryService {
    private final CartEntryRepository cartEntryRepository;
    private final ModelMapper modelMapper;
    private final PriceService priceService;
    private final CartRepository cartRepository;
    private final  ProductRepository productRepository;

    public CartEntryServiceImpl(CartEntryRepository cartEntryRepository, ModelMapper modelMapper, PriceService priceService, CartRepository cartRepository, ProductRepository productRepository) {
        this.cartEntryRepository = cartEntryRepository;
        this.modelMapper = modelMapper;
        this.priceService = priceService;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

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


        try{
            cartEntryDto.setDiscount(getDiscountPriceAmount(getPriceIdentifier(cartEntryDto),cartEntryDto.getQuantity()));
            cartEntryDto.setTotalPrice(getTotalPrice(getPriceIdentifier(cartEntryDto),cartEntryDto.getQuantity()));
            cartEntryDto.setUnitPrice(getSellingPriceAmount(getPriceIdentifier(cartEntryDto)));
            cartEntryDto.setTotalOriginalPrice(cartEntryDto.getTotalPrice().add(cartEntryDto.getDiscount()));
        }catch(IllegalArgumentException e){
            cartEntryDto.setMessage("The product or price Not found");
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }

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

    @Override
    public CartEntryDto updateQuantity(CartEntryDto cartEntryDto) {
        cartEntryDto.setIdentifier(cartEntryDto.getCartId()+"_"+cartEntryDto.getProduct());

        String identifier = cartEntryDto.getIdentifier();
        CartEntry existingCartEntry = cartEntryRepository.findByIdentifier(identifier);

        cartEntryDto.setDiscount(getDiscountPriceAmount(getPriceIdentifier(cartEntryDto),cartEntryDto.getQuantity()));
        cartEntryDto.setTotalPrice(getTotalPrice(getPriceIdentifier(cartEntryDto),cartEntryDto.getQuantity()));
        cartEntryDto.setUnitPrice(getSellingPriceAmount(getPriceIdentifier(cartEntryDto)));
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

    public String getPriceIdentifier(CartEntryDto cartEntryDto){
        Product product = productRepository.findByIdentifier(cartEntryDto.getProduct());
        return  product.getIdentifier()+"-"+product.getName();
    }
}
