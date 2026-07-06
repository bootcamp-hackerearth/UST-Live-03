package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandApiController extends BaseController {

    private final BrandService brandService;

    public BrandApiController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/list")
    public WsDto<BrandDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Brand> example = buildGlobalSearchSpec(Brand.class, paginationDto.getKeyword());
            if (example != null) {
                return brandService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return brandService.findAll(pageable);
    }

    @PostMapping("/add")
    public BrandDto addPost(@RequestBody BrandDto brandDto) {
        return brandService.save(brandDto);
    }

    @GetMapping("/get")
    public BrandDto get(@RequestParam String identifier) {
        return brandService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public BrandDto updatePost(@RequestBody BrandDto brandDto) {
        return brandService.update(brandDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            brandService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/toggleStatus")
    public boolean toggleStatus(@RequestBody BrandDto dto) {
        try {
            brandService.toggleStatus(dto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/getAllActive")
    public List<BrandDto> getAllActive() {
        return brandService.findAllActive();
    }
}