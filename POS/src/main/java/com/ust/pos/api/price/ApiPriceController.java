package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.price.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price")
public class ApiPriceController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/price/list";
    @Autowired
    private PriceService priceService;

    @PostMapping("/list")
    public WsDto<PriceDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
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
    public PriceDto updatePost(@RequestBody PriceDto userDto) {

        return priceService.update(userDto);
    }

    @GetMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            priceService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public PriceDto toggle(@RequestParam String identifier) {

        return priceService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<PriceDto> findByStatus() {

        return priceService.findIfTrue();
    }

    @GetMapping("/findByProduct")
    public PriceDto findByProduct(@RequestParam String productIdentifier) {

        return priceService.findByProductIdentifier(productIdentifier);
    }

}
