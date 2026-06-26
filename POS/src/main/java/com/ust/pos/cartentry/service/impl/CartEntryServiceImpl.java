package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartEntryServiceImpl implements CartEntryService {

    private final CartEntryRepository cartEntryRepository;
    private final CartServiceImpl cartService;
    private final ModelMapper modelMapper;
    private final StockRepository stockRepository;
    private final PriceServiceImpl priceService;

    public CartEntryServiceImpl(CartEntryRepository cartEntryRepository, CartServiceImpl cartService, PriceServiceImpl priceService, StockRepository stockRepository, ModelMapper modelMapper) {
        this.cartEntryRepository = cartEntryRepository;
        this.cartService = cartService;
        this.priceService = priceService;
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

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
        CartEntry cartEntry =
                cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry == null) {
            return null;
        }
        return modelMapper.map(
                cartEntry,
                CartEntryDto.class
        );
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
                cartEntry.getQuantity() == null
                        ? BigDecimal.ZERO
                        : cartEntry.getQuantity();

        BigDecimal updatedQuantity = existingQuantity.add(quantity);

        validateStock(product, updatedQuantity);

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

        if (cartService.findByIdentifier(cartId) == null) {
            cartService.save(cartId);
        }

        CartEntry savedEntry = cartEntryRepository.save(cartEntry);
        cartService.recalculate(cartId);
        return modelMapper.map(savedEntry, CartEntryDto.class);
    }

    @Override
    public CartEntryDto update(CartEntryDto cartEntryDto) {

        String identifier = cartEntryDto.getIdentifier();

        CartEntry cartEntry =
                cartEntryRepository.findByIdentifier(identifier);

        if (cartEntry == null) {
            CartEntryDto dto = new CartEntryDto();
            dto.setSuccess(false);
            dto.setMessage("Entry not found");
            return dto;
        }

        BigDecimal quantity = cartEntryDto.getQuantity();

        validateStock(cartEntry.getProduct(), quantity);

        if (quantity == null ||
                quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        String product = cartEntry.getProduct();

        PriceDto mrpDto =
                priceService.findByIdentifier(product + "Mrp");

        if (mrpDto == null) {
            throw new IllegalArgumentException(
                    "MRP not configured for product: " + product);
        }

        BigDecimal unitPrice = cartEntry.getUnitPrice();
        BigDecimal mrp = mrpDto.getValue();

        BigDecimal totalPrice =
                quantity.multiply(unitPrice);

        BigDecimal originalPrice =
                quantity.multiply(mrp);

        BigDecimal discount =
                originalPrice.subtract(totalPrice);

        cartEntry.setQuantity(quantity);
        cartEntry.setTotalPrice(totalPrice);
        cartEntry.setOriginalPrice(originalPrice);
        cartEntry.setDiscount(discount);

        CartEntry savedEntry =
                cartEntryRepository.save(cartEntry);

        cartService.recalculate(savedEntry.getCart());

        return modelMapper.map(savedEntry, CartEntryDto.class);
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

    private void validateStock(
            String product,
            BigDecimal requiredQuantity) {

        Stock stock =
                stockRepository.findByProduct(product);

        if (stock == null) {
            throw new IllegalArgumentException(
                    "Stock not configured for product: "
                            + product
            );
        }

        if (stock.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Product is out of stock"
            );
        }

        if (BigDecimal.valueOf(stock.getQuantity())
                .compareTo(requiredQuantity) < 0) {

            throw new IllegalArgumentException(
                    "Only " + stock.getQuantity()
                            + " items available in stock"
            );
        }
    }
}
