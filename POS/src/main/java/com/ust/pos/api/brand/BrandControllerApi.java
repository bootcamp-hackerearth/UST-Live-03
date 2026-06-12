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
public class BrandControllerApi extends BaseController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/list")
    public List<BrandDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return brandService.findAll(pageable);
    }

    @PostMapping("/add")
    public BrandDto addPost(@RequestBody BrandDto userDto) {
        return brandService.save(userDto);
    }

    @GetMapping("/get")
    public BrandDto update(@RequestParam String identifier) {
        return brandService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public BrandDto updatePost(@RequestBody BrandDto userDto) {
        return brandService.update(userDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            brandService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/getAllActive")
    public List<BrandDto> getAllActive() {
        return brandService.findAllActive();
    }

    @GetMapping("/toggle")
    public boolean toggle(@RequestParam String identifier) {
        try {
            brandService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
