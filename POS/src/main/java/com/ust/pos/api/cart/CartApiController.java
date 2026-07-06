package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartApiController extends BaseController {

    private final CartService cartService;

    public CartApiController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public List<CartDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return cartService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public CartDto addPost(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto.getIdentifier());
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public CartDto update(@RequestParam String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @PostMapping("/recalculate")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public CartDto recalculate(@RequestBody CartDto cartDto) {
        return cartService.recalculate(cartDto.getIdentifier());
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public CartDto updatePost(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto.getIdentifier());
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
