package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
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

public class ApiBrandController extends BaseController {

    private final BrandService brandService;

    public ApiBrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public WsDto<BrandDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Brand> example = buildGlobalSearchSpec(Brand.class, paginationDto.getKeyword());
            if (example != null) {
                return brandService.findAll(example, pageable);
            }
        }
        return brandService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
        public BrandDto addproduct(@RequestBody BrandDto brandDto) {
        return brandService.save(brandDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public boolean delete(@RequestParam String identifier) {
        try {
            brandService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public BrandDto update(@RequestParam String identifier) {

        return brandService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public BrandDto updateBrand(@RequestBody BrandDto brandDto) {

        return brandService.update(brandDto);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public BrandDto toggle(@RequestBody BrandDto brandDto) {
        return brandService.changeToggleStatus(brandDto.getIdentifier(), brandDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    public List<BrandDto> findActive() {
        return brandService.findActiveStatus();
    }
}
