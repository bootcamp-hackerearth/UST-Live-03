package com.ust.pos.api.priceapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.price.service.PriceService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price")
public class PriceApiController extends BaseController {

    public PriceApiController(PriceService priceService) {
        this.priceService = priceService;
    }

    private final PriceService priceService;

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('CASHIER','ADMIN','MANAGER','ACCOUNTANT','INVENTORY_MANAGER')")
    public WsDto<PriceDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Price> example = buildGlobalSearchSpec(Price.class, paginationDto.getKeyword());
            if (example != null) {
                return priceService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return priceService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','INVENTORY_MANAGER')")
    public PriceDto addPost(@RequestBody PriceDto priceDto) {
        return priceService.save(priceDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('CASHIER','ADMIN','MANAGER','ACCOUNTANT','INVENTORY_MANAGER')")
    public PriceDto update(@RequestParam String identifier) {
        return priceService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','INVENTORY_MANAGER')")
    public PriceDto updatePost(@RequestBody PriceDto priceDto) {
        return priceService.update(priceDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            priceService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','INVENTORY_MANAGER')")
    public PriceDto toggle(@RequestParam String identifier) {
        return priceService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    @PreAuthorize("hasAnyAuthority('CASHIER','ADMIN','MANAGER','ACCOUNTANT','INVENTORY_MANAGER')")
    public List<PriceDto> findByStatus() {
        return priceService.findIfTrue();
    }
}