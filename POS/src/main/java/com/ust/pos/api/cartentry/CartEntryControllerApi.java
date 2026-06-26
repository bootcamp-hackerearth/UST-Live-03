package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartentry")
public class CartEntryControllerApi extends BaseController {
    public static final String REDIRECT_ROLE_LIST = "redirect:/cartentry/list";

    private final CartEntryService cartentryService;

    public CartEntryControllerApi(CartEntryService cartentryService) {
        this.cartentryService = cartentryService;
    }

    @PostMapping("/list")
    public List<CartEntryDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );
        return cartentryService.findAll(pageable);
    }

    @PostMapping("/add")
    public CartEntryDto addPost(@RequestBody CartEntryDto cartentryDto) {
        return cartentryService.save(cartentryDto);
    }

    @GetMapping("/get")
    public CartEntryDto update(@RequestParam String identifier) {
        return cartentryService.findByIdentifier(identifier);
    }

    @PostMapping("/getByCartId")
    public List<CartEntryDto> getByCartId(
            @RequestBody CartEntryDto cartEntryDto
    ) {
        return cartentryService.findAllCarts(
                cartEntryDto.getCartIdentifier()
        );
    }

    @PutMapping("/update")
    public CartEntryDto updatePost(@RequestBody CartEntryDto cartentryDto) {
        return cartentryService.update(cartentryDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartentryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}