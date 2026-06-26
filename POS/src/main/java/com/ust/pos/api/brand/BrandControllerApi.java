package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandControllerApi extends BaseController {
    public static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";

    private final BrandService brandService;

    public BrandControllerApi(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/list")
    public WsDto<BrandDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );
        return brandService.findAll(pageable);
    }

    @PostMapping("/add")
    public BrandDto addPost(@RequestBody BrandDto brandDto) {
        return brandService.save(brandDto);
    }

    @GetMapping("/get")
    public BrandDto update(@RequestParam String identifier) {
        return brandService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public BrandDto updatePost(@RequestBody BrandDto brandDto) {
        return brandService.update(brandDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            brandService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PatchMapping("/toggle")
    public String toggle(
            @RequestParam String identifier,
            @RequestParam boolean status
    ) {
        brandService.updateStatus(identifier, status);
        return "success";
    }

    @PostMapping("/list-active")
    public List<BrandDto> listActive() {
        return brandService.findAllActive();
    }
}