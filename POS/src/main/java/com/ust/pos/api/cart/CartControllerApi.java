package com.ust.pos.api.cart;

import com.ust.pos.base.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartControllerApi extends BaseController {

    private final CartService cartService;

    @PostMapping("/list")
    public WsDto<CartDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return cartService.findAllws(pageable);
    }

    @PostMapping("/add")
    public CartDto addPost(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @GetMapping("/get")
    public CartDto update(@RequestParam String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody CartDto cartDto) {
        try {
            cartService.delete(cartDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}


