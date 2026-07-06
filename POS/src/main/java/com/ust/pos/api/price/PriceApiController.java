package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.price.service.PriceService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/price")
public class PriceApiController extends BaseController {

    private final PriceService priceService;

    public PriceApiController(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public WsDto<PriceDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return priceService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public PriceDto addPost(@RequestBody PriceDto priceDto) {
        return priceService.save(priceDto);
    }

    @GetMapping("/get")
    public PriceDto update(@RequestParam String identifier) {
        return priceService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public PriceDto updatePost(@RequestBody PriceDto priceDto) {
        return priceService.update(priceDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            priceService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
