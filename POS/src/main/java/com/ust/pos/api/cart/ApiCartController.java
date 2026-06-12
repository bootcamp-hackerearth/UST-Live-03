package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
public class ApiCartController extends BaseController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartEntryService cartEntryService;

    @PostMapping("/add")
    public CartDto addPost(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/addToCart")
    public CartDto addToCart(@RequestBody CartEntryDto cartEntryDto) {
        return cartService.findByIdentifier(cartEntryDto.getCartIdentifier());
    }

    @PostMapping("/list")
    public WsDto<CartDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );
        return cartService.findAll(pageable);
    }

    @GetMapping("/get")
    public CartDto get(@RequestParam("identifier") String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/clear")
    public CartDto clearCart(@RequestParam String cartIdentifier) {
        cartService.clearCart(cartIdentifier);
        return cartService.findByIdentifier(cartIdentifier);
    }

}

