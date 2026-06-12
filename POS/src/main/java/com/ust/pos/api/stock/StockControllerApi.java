package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/stock")
public class StockControllerApi extends BaseController {

    @Autowired
    private StockService stockService;

    @Autowired
    private NodeService nodeService;

    @PostMapping("/list")
    public WsDto<StockDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    public StockDto addPost(@RequestBody StockDto userDto) {
        return stockService.save(userDto);
    }

    @GetMapping("/get")
    public StockDto update(@RequestParam String identifier) {
        return stockService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public StockDto updatePost(@RequestBody StockDto userDto) {
        return stockService.update(userDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            stockService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
