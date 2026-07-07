package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.price.service.PriceService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/price")
@RequiredArgsConstructor
public class PriceControllerApi extends BaseController {

    private final PriceService priceService;

    @PostMapping("/list")
    public PaginatedResponseDto<PriceDto> list(@RequestBody PaginationDto paginationDto) {
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
    public PriceDto addPost(@RequestBody PriceDto priceDto) {
        return priceService.save(priceDto);
    }

    @GetMapping("/get")
    public PriceDto get(@RequestParam String identifier) {
        return priceService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public PriceDto updatePost(@RequestBody PriceDto priceDto) {
        return priceService.update(priceDto);
    }

    @DeleteMapping("/delete")
    public PriceDto delete(@RequestBody PriceDto priceDto) {
        try {
            return priceService.delete(priceDto.getIdentifier());
        } catch (Exception e) {
            PriceDto errorDto = new PriceDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }
}