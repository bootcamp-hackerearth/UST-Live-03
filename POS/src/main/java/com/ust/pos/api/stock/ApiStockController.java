package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class ApiStockController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/stock/list";
    @Autowired
    private StockService stockService;

    @PostMapping("/list")
    public WsDto<StockDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    public StockDto addPost(@RequestBody StockDto stockDto) {

        return stockService.save(stockDto);

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
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            stockService.deleteByIdentifier(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public StockDto toggle(@RequestParam String identifier) {

        return stockService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<StockDto> findByStatus() {

        return stockService.findIfTrue();
    }

}
