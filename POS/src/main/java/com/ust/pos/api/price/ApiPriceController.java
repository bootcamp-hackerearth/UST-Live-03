package com.ust.pos.api.price;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price")
public class ApiPriceController extends BaseController {

    private final PriceService priceService;

    public ApiPriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ACCOUNTANT')")
    public WsDto<PriceDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable= getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Price> example = buildGlobalSearchSpec(Price.class, paginationDto.getKeyword());
            if (example != null) {
                return priceService.findAll(example, pageable);
            }
        }
        return priceService.findAll(pageable);
    }


    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ACCOUNTANT')")
    public PriceDto addprice(@RequestBody PriceDto priceDto) {
        return priceService.save(priceDto);

    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public boolean delete(@RequestParam String identifier) {
        try {
            priceService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ACCOUNTANT')")
    public PriceDto update(@RequestParam String identifier) {
        return priceService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ACCOUNTANT')")
    public PriceDto updatePrice(@RequestBody PriceDto priceDto) {
        return priceService.update(priceDto);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ACCOUNTANT')")
    public PriceDto toggle(@RequestBody PriceDto priceDto) {
        return priceService.changeToggleStatus(priceDto.getIdentifier(), priceDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ACCOUNTANT')")
    public List<PriceDto> findActive() {
        return priceService.findActiveStatus();
    }
}
