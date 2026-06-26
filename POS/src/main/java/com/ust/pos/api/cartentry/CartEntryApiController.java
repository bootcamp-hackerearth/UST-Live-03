package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartEntry")
public class CartEntryApiController extends BaseController {

    private final CartEntryService cartEntryService;

    public CartEntryApiController(CartEntryService cartEntryService) {
        this.cartEntryService = cartEntryService;
    }

    @PostMapping("/list")
    public List<CartEntryDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortField());
        return cartEntryService.findAll(pageable);
    }

    @PostMapping("/add")
    public CartEntryDto add(@RequestBody CartEntryDto dto) {
        return cartEntryService.save(dto);
    }

    @GetMapping("/get")
    public CartEntryDto get(@RequestParam String identifier) {
        return cartEntryService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public CartEntryDto update(@RequestBody CartEntryDto dto) {
        return cartEntryService.update(dto);
    }

    @DeleteMapping("/delete")
    public CartEntryDto delete(@RequestBody CartEntryDto dto) {
        CartEntryDto response = new CartEntryDto();
        try {
            cartEntryService.delete(dto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("CartEntry deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }
}