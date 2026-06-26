package com.ust.pos.api.cartentry;

import com.ust.pos.base.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartEntry")
@RequiredArgsConstructor
public class CartEntryControllerApi extends BaseController {

    private final CartEntryService cartEntryService;

    @PostMapping("/list")
    public WsDto<CartEntryDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return cartEntryService.findAll(pageable);
    }

    @PostMapping("/add")
    public CartEntryDto addPost(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.save(cartEntryDto);
    }

    @GetMapping("/listByCartId")
    public List<CartEntryDto> listByCartId(@RequestParam String cartId) {
        return cartEntryService.findByCartId(cartId);
    }

    @PostMapping("/update")
    public CartEntryDto update(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.save(cartEntryDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody CartEntryDto cartEntryDto) {
        try {
            cartEntryService.delete(cartEntryDto);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}