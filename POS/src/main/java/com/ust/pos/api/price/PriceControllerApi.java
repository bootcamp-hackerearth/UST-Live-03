package com.ust.pos.api.price;


import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.price.service.PriceService;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/price")
public class PriceControllerApi extends BaseController {

    private final PriceService priceService;

    public PriceControllerApi(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping("/list")
    public WsDto<PriceDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Price> example = buildGlobalSearchSpec(Price.class, paginationDto.getKeyword());
            if (example != null) {
                return priceService.findAll(example, pageable);
            }
        }
        return priceService.findAll(pageable);
    }


    @PostMapping("/add")
    public PriceDto save(@RequestBody PriceDto priceDto) {

        return priceService.save(priceDto);
    }

    @GetMapping("/update")
    public PriceDto update(@RequestParam Long id) {

        return priceService.findById(id);
    }

    @PutMapping("/update")
    public PriceDto update(@RequestBody PriceDto priceDto) {

        return priceService.update(priceDto);
    }

    @DeleteMapping("/delete")
    @Transactional
    public boolean delete(@RequestParam String identifier) {
        try {
            priceService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    @PostMapping("/changeStatus")
    public PriceDto changestatus(@RequestBody PriceDto priceDto) {
        return priceService.changePriceStatus(priceDto.getIdentifier(), priceDto.isStatus());
    }
}