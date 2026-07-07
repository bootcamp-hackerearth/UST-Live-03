package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Brand;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
public class ApiBrandController extends BaseController {

    private final BrandService brandService;

    @PostMapping("/add")
    public BrandDto addPost(@RequestBody BrandDto brandDto) {
        return brandService.save(brandDto);
    }

    @PostMapping("/list")
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

    @GetMapping("/get")
    public BrandDto get(@RequestParam("identifier") String identifier) {
        return brandService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public BrandDto updatePost(@RequestBody BrandDto brandDto) {
        return brandService.update(brandDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam("identifier") String identifier) {
        try {
            brandService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PatchMapping("/toggle-status")
    public BrandDto toggle(@RequestParam String identifier) {
        return brandService.toggleStatus(identifier);
    }

    @GetMapping("/findallactive")
    public List<BrandDto> findAllActive() {
        return brandService.findAllActive();
    }
}