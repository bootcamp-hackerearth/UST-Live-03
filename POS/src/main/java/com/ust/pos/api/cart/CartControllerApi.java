package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartControllerApi extends BaseController {

    private final CartService cartService;

    @PostMapping("/list")
    public PaginatedResponseDto<CartDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return cartService.findAll(pageable);
    }

    @PostMapping("/add")
    public CartDto addPost(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody CartDto cartDto) {
        try {
            cartService.delete(cartDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    public CartDto get(@RequestParam String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @PostMapping("/clear")
    public CartDto clearCart(@RequestBody CartDto cartDto) {
        cartService.clearCart(cartDto.getIdentifier());
        return cartService.findByIdentifier(cartDto.getIdentifier());
    }
}
