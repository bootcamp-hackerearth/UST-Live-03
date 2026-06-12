package com.ust.pos.api.stocks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StocksDto;
import com.ust.pos.stocks.service.StocksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StocksApiController extends BaseController {

    @Autowired
    private StocksService stocksService;

    @PostMapping("/list")
    public List<StocksDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(), paginationDto.getSortDirection(),
                paginationDto.getSortField());
        return stocksService.findAll(pageable);
    }

    @PostMapping("/add")
    public StocksDto addPost(@RequestBody StocksDto stocksDto) {
        return stocksService.save(stocksDto);
    }

    @GetMapping("/get")
    public StocksDto update(@RequestParam String identifier) {
        return stocksService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public StocksDto updatePost(@RequestBody StocksDto stocksDto) {
        return stocksService.update(stocksDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            stocksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public StocksDto toggle(@RequestParam String identifier) {

        return stocksService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<StocksDto> findByStatus() {

        return stocksService.findIfTrue();
    }
}