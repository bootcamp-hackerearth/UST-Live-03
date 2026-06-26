package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockControllerApi extends BaseController {

    private final StockService stockService;

    @PostMapping("/list")
    public PaginatedResponseDto<StockDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    public StockDto addPost(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }

    @GetMapping("/get")
    public StockDto get(@RequestParam String identifier) {
        return stockService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public StockDto updatePost(@RequestBody StockDto stockDto) {
        return stockService.update(stockDto);
    }

    @DeleteMapping("/delete")
    public StockDto delete(@RequestBody StockDto stockDto) {
        try {
            return stockService.delete(stockDto.getIdentifier());
        } catch (Exception e) {
            StockDto errorDto = new StockDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }
}