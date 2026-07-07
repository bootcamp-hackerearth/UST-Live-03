package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Cart;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ApiCartController extends BaseController {

    private final CartService cartService;

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
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Cart> example = buildGlobalSearchSpec(Cart.class, paginationDto.getKeyword());
            if (example != null) {
                return cartService.findAll(example, pageable);
            }
        }
        return cartService.findAll(pageable);
    }

    @GetMapping("/get")
    public CartDto get(@RequestParam("identifier") String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartService.delete(identifier);
            return true;
        } catch (Exception _) {
            return false;
        }
    }

    @PostMapping("/clear")
    public CartDto clearCart(@RequestParam String cartIdentifier) {
        cartService.clearCart(cartIdentifier);
        return cartService.findByIdentifier(cartIdentifier);
    }

}

