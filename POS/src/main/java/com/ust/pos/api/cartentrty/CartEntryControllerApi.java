package com.ust.pos.api.cartentrty;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartEntry")
public class CartEntryControllerApi extends BaseController {

    @Autowired
    private CartEntryService cartEntryService;

    @Autowired
    private NodeService nodeService;

    @PostMapping("/addEntry")
    public CartEntryDto add(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.save(cartEntryDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartEntryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
