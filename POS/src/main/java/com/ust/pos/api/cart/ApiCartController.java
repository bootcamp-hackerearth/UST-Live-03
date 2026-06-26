package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ApiCartController extends BaseController {

    private final CartService cartService;

    private final CartEntryService cartEntryService;

    @PostMapping("/add")
    public CartDto addPost(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/addToCart")
    public CartDto addToCart(@RequestBody CartEntryDto cartEntryDto) {
        CartDto cart = cartService.findByIdentifier(cartEntryDto.getCartIdentifier());

        if (cart == null) {
            CartDto newCart = new CartDto();
            newCart.setIdentifier(cartEntryDto.getCartIdentifier());
            cartService.save(newCart);
        }

        cartEntryService.save(cartEntryDto);
        return cartService.findByIdentifier(cartEntryDto.getCartIdentifier());
    }

    @PostMapping("/list")
    public WsDto<CartDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField());
        return cartService.findAll(pageable);
    }

    @GetMapping("/get")
    public CartDto get(@RequestParam("identifier") String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/clear")
    public CartDto clearCart (@RequestParam String cartIdentifier){
        cartService.clearCart(cartIdentifier);
        return cartService.findByIdentifier(cartIdentifier);
    }

}


