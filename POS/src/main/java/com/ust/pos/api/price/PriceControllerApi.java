package com.ust.pos.api.price;


import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price")
public class PriceControllerApi extends BaseController {
    @Autowired
    PriceService priceService;

    @PostMapping("/list")
    public List<PriceDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
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

    @GetMapping("/delete")
    @Transactional
    public boolean delete(@RequestParam String identifier) {
        try {
            priceService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    @PostMapping("/update")
    public PriceDto update(@RequestBody PriceDto priceDto) {

        return priceService.update(priceDto);
    }
}