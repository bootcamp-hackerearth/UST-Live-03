package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.SearchDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandControllerApi extends BaseController {

    private final BrandService brandService;

    public BrandControllerApi(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public WsDto<BrandDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return brandService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority( 'Manager')")
    public BrandDto add(@RequestBody BrandDto brandDto) {
        return brandService.save(brandDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Manager')")
    public BrandDto get(@RequestParam String identifier) {
        return brandService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority( 'Manager')")
    public BrandDto update(@RequestBody BrandDto brandDto) {
        return brandService.update(brandDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Manager')")
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

    @PatchMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public BrandDto toggleStatus(@RequestBody BrandDto brandDto) {
        return brandService.toggleStatus(brandDto.getIdentifier());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public List<BrandDto> active() {
        return brandService.findActiveBrands();
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public WsDto<BrandDto> search(@RequestBody SearchDto searchDto) {
        Pageable pageable = getPageable(searchDto.getPage(), searchDto.getSizePerPage(),
                searchDto.getSortDirection(), searchDto.getSortfield());
        return brandService.search(searchDto.getKeyword(), pageable);
    }
}