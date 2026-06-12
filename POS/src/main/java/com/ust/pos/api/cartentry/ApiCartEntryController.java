package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cartentry")
public class ApiCartEntryController extends BaseController {

    @Autowired
    private CartEntryService cartEntryService;

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public CartEntryDto addPost(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.save(cartEntryDto);
    }

    @PostMapping("/list")
    public WsDto<CartEntryDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );
        return cartEntryService.findAll(pageable);
    }

    @GetMapping("/get")
    public CartEntryDto get(@RequestParam("identifier") String identifier) {
        return cartEntryService.findByIdentifier(identifier);
    }
}