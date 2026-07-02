package com.ust.pos.api.brand;
import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.model.Brand;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/brand")
public class BrandControllerApi extends BaseController {

    private final BrandService brandService;

    public BrandControllerApi(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public PageDto<BrandDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Brand> spec = buildGlobalSearchSpec(Brand.class, paginationDto.getKeyword());
            return brandService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return brandService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public BrandDto getBrandByIdentifier(@RequestParam String identifier) {
        return brandService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public BrandDto addPost(@RequestBody BrandDto brandDto) {
        return brandService.save(brandDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public BrandDto updatePost(@RequestBody BrandDto brandDto) {
        return brandService.update(brandDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            brandService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public void toggleStatus(@RequestParam String identifier) {
        brandService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<BrandDto> findByStatus() {
        return brandService.findActiveBrands();
    }
}
