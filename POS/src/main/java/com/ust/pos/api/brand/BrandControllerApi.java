package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
public class BrandControllerApi extends BaseController {

    private final BrandService brandService;

    @PostMapping("/list")
    public PaginatedResponseDto<BrandDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
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
    public BrandDto delete(@RequestBody BrandDto brandDto) {
        try {
            return brandService.delete(brandDto.getIdentifier());
        } catch (Exception e) {
            BrandDto errorDto = new BrandDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody BrandDto brandDto) {
        try {
            brandService.changeStatus(brandDto.getIdentifier(), brandDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/active")
    public List<BrandDto> getActiveBrands() {
        return brandService.findAllActive();
    }
}