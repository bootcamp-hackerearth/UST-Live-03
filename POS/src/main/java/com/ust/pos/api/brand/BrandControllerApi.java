package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
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
    @PreAuthorize("hasAuthority('Admin')")
    public WsDto<BrandDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return brandService.findAll(pageable);

    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public BrandDto saveBrand(@RequestBody BrandDto brandDto) {

        return brandService.save(brandDto);

    }

    @GetMapping("/update")
    public BrandDto showEditPage(@RequestParam String identifier) {

        return brandService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public BrandDto saveEditedBrand(@RequestBody BrandDto brandDto) {

        return brandService.update(brandDto);
    }


    @DeleteMapping("/delete")
    public boolean deleteBrand(@RequestParam String identifier) {
        try {
            brandService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/changeStatus")
    public BrandDto toggle(@RequestBody BrandDto brandDto) {

        return brandService.changeBrandStatus(brandDto.getIdentifier(), brandDto.isStatus());

    }

    @GetMapping("/findAllActive")
    public List<BrandDto> allactive() {
        return brandService.findActiveBrand();
    }
}