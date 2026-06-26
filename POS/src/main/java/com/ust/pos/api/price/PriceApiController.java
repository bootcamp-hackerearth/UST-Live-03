package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.price.service.PriceService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/price")
public class PriceApiController extends BaseController {


    private final PriceService priceService;

    public PriceApiController(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping("/list")
    public WsDto<PriceDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return priceService.findAll(pageable);
    }

    @PostMapping("/add")
    public PriceDto addPost(@RequestBody PriceDto priceDto) {
        return priceService.save(priceDto);
    }

    @PostMapping("/get")
    public PriceDto update(@RequestBody String identifier) {
        return priceService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public PriceDto updatePost(@RequestBody PriceDto priceDto) {
        return priceService.update(priceDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            priceService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
