package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.price.service.PriceService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price")
public class PriceApiController extends BaseController {
    private final PriceService priceService;

    public PriceApiController(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'Cashier')")
    public WsDto<PriceDto> home(@RequestBody PaginationDto paginationDto) throws Exception
    {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        Example<Price> example = buildSearchProbe(Price.class, paginationDto.getSearch());
        Page<PriceDto> pageResult = priceService.findAll(example, pageable);

        WsDto<PriceDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }

    @GetMapping("/list")
    public List<PriceDto> dropdown()
    {
        return priceService.findAll();
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public PriceDto addPost(@RequestBody PriceDto priceDto)
    {
        return priceService.save(priceDto);
    }

    @GetMapping("/get")
    public PriceDto update(@RequestParam String identifier)
    {
        return priceService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public PriceDto doupdate(@RequestBody PriceDto priceDto)
    {
        return priceService.update(priceDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public Boolean delete(@RequestParam String identifier)
    {
        try {
            priceService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
