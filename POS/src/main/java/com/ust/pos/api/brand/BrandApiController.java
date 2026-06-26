package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandApiController extends BaseController {

    private final BrandService brandService;

    BrandApiController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/list")
    public List<BrandDto> list() {
        return brandService.findAll();
    }

    @PostMapping("/list")
    public WsDto<BrandDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortField());
        Page<BrandDto> brand = brandService.findAll(pageable, paginationDto.getSearch());
        WsDto<BrandDto> result = new WsDto<>();
        result.setContent(brand.getContent());
        result.setSizePerPage(brand.getSize());
        result.setPage(brand.getNumber());
        result.setTotalPages(brand.getTotalPages());
        return result;
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

    @PostMapping("/toggleStatus")
    public boolean updateStatus(
            @RequestParam String identifier,
            @RequestParam Boolean status) {
        try {
            brandService.updateStatusOnly(identifier, status);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            brandService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}