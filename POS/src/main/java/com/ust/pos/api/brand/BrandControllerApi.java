package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandControllerApi extends BaseController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/list")
    public WsDto<BrandDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return brandService.findAll(pageable);
    }

    @PostMapping("/add")
    public BrandDto add(@RequestBody BrandDto brandDto) {
        return brandService.save(brandDto);
    }

    @GetMapping("/get")
    public BrandDto get(@RequestParam String identifier) {
        return brandService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public BrandDto update(@RequestBody BrandDto brandDto) {
        return brandService.update(brandDto);
    }

    @PostMapping("/delete")
    public BrandDto delete(@RequestBody BrandDto brandDto) {
        BrandDto response = new BrandDto();
        try {
            brandService.delete(brandDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Brand deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }

    @PostMapping("/toggle")
    public BrandDto toggleStatus(@RequestBody BrandDto brandDto) {
        return brandService.toggleStatus(brandDto.getIdentifier());
    }

    @GetMapping("/active")
    public List<BrandDto> active() {
        return brandService.findActiveBrands();
    }
}