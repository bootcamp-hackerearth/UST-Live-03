package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.price.service.PriceService;
import org.springframework.data.domain.Page;
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
    public WsDto<PriceDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );

        Page<PriceDto> price = priceService.findAll(pageable, paginationDto.getSearch());

        WsDto<PriceDto> result = new WsDto<>();
        result.setContent(price.getContent());
        result.setPage(price.getNumber());
        result.setSizePerPage(price.getSize());
        result.setTotalPages(price.getTotalPages());

        return result;
    }

    @PostMapping("/add")
    public PriceDto addPost(@RequestBody PriceDto priceDto) {
        return priceService.save(priceDto);
    }

    @GetMapping("/get")
    public PriceDto update(@RequestParam String identifier) {
        return priceService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public PriceDto updatePost(@RequestBody PriceDto priceDto) {
        return priceService.update(priceDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            priceService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}