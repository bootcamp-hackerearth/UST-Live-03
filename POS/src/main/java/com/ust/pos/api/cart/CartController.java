package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("cartApiController")
@RequestMapping("/api/cart")
public class CartController extends BaseController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/list")
    public ResponseEntity<List<CartDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<CartDto> carts = cartService.findAll(pageable);
        return ResponseEntity.ok(carts);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<CartDto> getByIdentifier(@PathVariable String identifier) {
        CartDto response = cartService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<CartDto> save(@RequestBody CartDto cartDto) {
        CartDto response = cartService.save(cartDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{identifier}")
    public ResponseEntity<CartDto> update(@PathVariable String identifier, @RequestBody CartDto cartDto) {
        cartDto.setIdentifier(identifier);
        CartDto response = cartService.update(cartDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        boolean response = cartService.delete(identifier);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-entry/{identifier}")
    public ResponseEntity<Boolean> deleteEntry(@PathVariable String identifier) {
        boolean response = cartService.deleteCartEntry(identifier);
        return ResponseEntity.ok(response);
    }
}