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

    private static final String NOT_FOUND = " not found";

    private static final String CART_WITH_IDENTIFIER = "Cart with identifier - ";

    private static final String CUSTOMER_WITH_IDENTIFIER = "Customer with identifier - ";

    private static final String CART_ENTRY_WITH_IDENTIFIER = "Cart Entry with identifier - ";

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CartEntryService cartEntryService;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CustomerRepository customerRepository, CartRepository cartRepository, CartEntryService cartEntryService, ModelMapper modelMapper) {

        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
        this.cartEntryService = cartEntryService;
        this.modelMapper = modelMapper;
    }

    private String cartNotFound(String identifier) {
        return CART_WITH_IDENTIFIER + identifier + NOT_FOUND;
    }

    private String customerNotFound(String identifier) {
        return CUSTOMER_WITH_IDENTIFIER + identifier + NOT_FOUND;
    }

    private String cartEntryNotFound(String identifier) {
        return CART_ENTRY_WITH_IDENTIFIER + identifier + NOT_FOUND;
    }

    private void populateCart(CartDto cartDto) {

        List<CartEntryDto> cartEntries = cartEntryService.findAllByCartIdentifier(cartDto.getIdentifier());

        BigDecimal totalPrice = cartEntries.stream().map(CartEntryDto::getTotalPrice).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = cartEntries.stream().map(CartEntryDto::getDiscount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        cartDto.setCartEntries(cartEntries);
        cartDto.setTotalPrice(totalPrice);
        cartDto.setDiscount(totalDiscount);
    }

    @Override
    public CartDto findByIdentifier(String identifier) {

        Cart cart = requireResource(cartRepository.findByIdentifier(identifier), cartNotFound(identifier));

        CartDto cartDto = modelMapper.map(cart, CartDto.class);

        populateCart(cartDto);

        return cartDto;
    }

    @Override
    public CartDto save(CartDto cartDto) {

        String identifier = cartDto.getUsername() != null ? cartDto.getUsername() : cartDto.getIdentifier();

        cartDto.setIdentifier(identifier);
        cartDto.setUsername(identifier);

        requireResource(customerRepository.findByIdentifier(identifier), customerNotFound(identifier));

        Cart existingCart = cartRepository.findByIdentifier(identifier);

        if (existingCart != null) {

            if (cartDto.getCartEntries() != null) {

                for (CartEntryDto cartEntryDto : cartDto.getCartEntries()) {

                    cartEntryDto.setCartIdentifier(identifier);

                    cartEntryService.save(cartEntryDto);
                }
            }

            CartDto existingCartDto = modelMapper.map(existingCart, CartDto.class);

            populateCart(existingCartDto);

            existingCart.setTotalPrice(existingCartDto.getTotalPrice());

            existingCart.setDiscount(existingCartDto.getDiscount());

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

        CartDto savedCart = new CartDto();

        savedCart.setIdentifier(identifier);

        populateCart(savedCart);

        cart.setTotalPrice(savedCart.getTotalPrice());

        cart.setDiscount(savedCart.getDiscount());

        cartRepository.save(cart);

        return findByIdentifier(identifier);
    }

    @Override
    public CartDto update(CartDto cartDto) {

        String identifier = cartDto.getIdentifier();

        Cart existingCart = requireResource(cartRepository.findByIdentifier(identifier), cartNotFound(identifier));

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

        CartDto updatedCart = new CartDto();

        updatedCart.setIdentifier(identifier);

        populateCart(updatedCart);

        existingCart.setTotalPrice(updatedCart.getTotalPrice());

        existingCart.setDiscount(updatedCart.getDiscount());

        cartRepository.save(existingCart);

        return findByIdentifier(identifier);
    }

    @Override
    public boolean delete(String identifier) {

        requireResource(cartRepository.findByIdentifier(identifier), cartNotFound(identifier));

        cartEntryService.deleteByCartIdentifier(identifier);

        cartRepository.deleteByIdentifier(identifier);

        return true;
    }

    @Override
    public boolean deleteCartEntry(String identifier) {

        CartEntryDto cartEntry = cartEntryService.findByIdentifier(identifier);

        requireResource(cartEntry, cartEntryNotFound(identifier));

        String cartIdentifier = cartEntry.getCartIdentifier();

        cartEntryService.delete(identifier);

        Cart cart = requireResource(cartRepository.findByIdentifier(cartIdentifier), cartNotFound(cartIdentifier));

        CartDto cartDto = new CartDto();

        cartDto.setIdentifier(cartIdentifier);

        populateCart(cartDto);

        cart.setTotalPrice(cartDto.getTotalPrice());

        cart.setDiscount(cartDto.getDiscount());

        cartRepository.save(cart);

        return true;
    }

    @Override
    public List<CartDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();

        Page<Cart> cartPage = cartRepository.findAll(pageable);

        List<CartDto> cartDtos = modelMapper.map(cartPage.getContent(), listType);

        cartDtos.forEach(this::populateCart);

        return cartDtos;
    }
}