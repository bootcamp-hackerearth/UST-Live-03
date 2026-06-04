package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.price.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price")
public class PriceControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/price/list";

    @Autowired
    private PriceService priceService;

    @PostMapping("/list")
    public List<PriceDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return priceService.findAll(pageable);
    }

    @PostMapping("/add")
    public PriceDto addPost(@RequestBody PriceDto priceDto) {
        return priceService.save(priceDto);
    }

    @GetMapping("/get")
    public PriceDto updatePage(@RequestParam String identifier) {
        return priceService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public PriceDto updatePost(@RequestBody PriceDto priceDto) {
        return priceService.update(priceDto);
    }

    @PostMapping("/delete")
    public PriceDto delete(@RequestBody PriceDto priceDto) {
        PriceDto response = new PriceDto();
        try {
            priceService.delete(priceDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Price deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }
}