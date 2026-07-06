package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.price.service.PriceService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/price")
public class PriceApiController extends BaseController {

    private final PriceService priceService;

    public PriceApiController(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public PriceDto addPost(@RequestBody PriceDto priceDto) {
        return priceService.save(priceDto);
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Admin','Seller')")
    public PaginationResponseDto<PriceDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Price> example = buildGlobalSearchSpec(Price.class, paginationDto.getKeyword());
            if (example != null) {
                return priceService.findAll(example, pageable);
            }
        }
        return priceService.findAll(pageable);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('Seller')")
    public PriceDto update(@RequestParam String identifier) {
        return priceService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Seller')")
    public PriceDto updatePost(@RequestBody PriceDto priceDto) {
        return priceService.update(priceDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Seller')")
    public boolean delete(@RequestParam String identifier) {
        try {
            priceService.deleteByIdentifier(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}