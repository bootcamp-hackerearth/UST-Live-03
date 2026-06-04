package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class ApiBrandController extends BaseController {

    @Autowired
    BrandService brandService;

    @PostMapping("/list")
    public List<BrandDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return brandService.findAll(pageable);
    }

    @PostMapping("/add")
    public BrandDto add(@RequestBody BrandDto brandDto) {
        return brandService.save(brandDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            brandService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    public BrandDto update(@RequestParam String identifier) {
        return brandService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public BrandDto updateCategory(@RequestBody BrandDto brandDto) {
        return brandService.update(brandDto);
    }

    @PostMapping("/toggle")
    public BrandDto toggle(@RequestBody BrandDto brandDto) {
        return brandService.changeToggleStatus(brandDto.getIdentifier(), brandDto.isStatus());
    }
}
