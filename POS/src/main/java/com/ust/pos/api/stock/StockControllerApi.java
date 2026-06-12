package com.ust.pos.api.stock;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/stock")
public class StockControllerApi extends BaseController {
    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";

    @Autowired
    private StockService stockService;
    @Autowired
    private WareHouseService wareHouseService;
    @Autowired
    private ProductService productService;

    @PostMapping("/list")
    public PageDto<StockDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable=getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),paginationDto.getSortDirection(),paginationDto.getSortField());
        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    public StockDto addPost(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }

    @GetMapping("/update")
    public StockDto getStockByIdentifier(@RequestParam String identifier) {
        return stockService.findByIdentifier(identifier);
    }


    @PostMapping("/update")
    public StockDto updatePost(@RequestBody StockDto stockDto) {
         return stockService.update(stockDto);

    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            stockService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;

    }
}
