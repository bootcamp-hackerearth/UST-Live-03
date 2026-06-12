package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.price.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/price")
public class PriceControllerApi extends BaseController {

    @Autowired
    private PriceService priceService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    public PaginatedResponseDto<PriceDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return priceService.findAll(pageable);
    }

    @PostMapping("/add")
    public PriceDto addPost(@RequestBody PriceDto priceDto) {
        return priceService.save(priceDto);
    }

    @GetMapping("/get")
    public PriceDto update(@RequestParam String identifier) {
        return priceService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public PriceDto updatePost(@RequestBody PriceDto priceDto) {
        return priceService.update(priceDto);
    }

    @PostMapping("/delete")
    public Boolean delete(@RequestBody PriceDto priceDto) {
        try {
            priceService.delete(priceDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}