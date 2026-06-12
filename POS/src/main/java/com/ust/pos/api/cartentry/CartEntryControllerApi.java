package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.cartEntry.service.CartEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartEntry")
public class CartEntryControllerApi extends BaseController {
    @Autowired
    private CartEntryService cartEntryService;

    @Autowired
    private NodeService nodeService;

    @PostMapping("/list")
    public List<CartEntryDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return cartEntryService.findAll(pageable);
    }

    @PostMapping("/add")
    public CartEntryDto addPost(@RequestBody CartEntryDto userDto) {
        return cartEntryService.save(userDto);
    }

    @GetMapping("/get")
    public CartEntryDto update(@RequestParam String identifier) {
        return cartEntryService.findByIdentifier(identifier);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String cartId, @RequestParam String product) {
        try {
            cartEntryService.delete(cartId,product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/clearCart")
    public boolean deleteAll(@RequestParam String cartId){
        try {
            cartEntryService.deleteAllByCartId(cartId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
