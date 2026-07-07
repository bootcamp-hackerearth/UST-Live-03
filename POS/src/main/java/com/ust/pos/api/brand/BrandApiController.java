package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('Admin')")
    public WsDto<BrandDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Brand> example = buildGlobalSearchSpec(Brand.class, paginationDto.getKeyword());
            if (example != null) {
                return brandService.findAll(example, pageable);
            }
        }
        return brandService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public BrandDto add(@RequestBody BrandDto brandDto) {
        return brandService.save(brandDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public BrandDto get(@RequestParam String identifier) {
        return brandService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public BrandDto update(@RequestBody BrandDto brandDto) {
        return brandService.update(brandDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public boolean delete(@RequestBody BrandDto brandDto) {
        try {
            brandService.delete(brandDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/active")
    public List<BrandDto> getActiveBrands() {
        return brandService.findActiveBrands();
    }

    @PostMapping("/toggleStatus")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public BrandDto toggleBrandStatus(@RequestBody BrandDto brandDto) {
        return brandService.toggleStatus(brandDto.getIdentifier(), brandDto.isStatus());
    }
}