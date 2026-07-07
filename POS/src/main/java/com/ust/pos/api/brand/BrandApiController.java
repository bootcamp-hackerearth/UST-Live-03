package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/brand")
@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
public class BrandApiController extends BaseController {
    private final BrandService brandService;

    public BrandApiController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/list")
    public WsDto<BrandDto> home(@RequestBody PaginationDto paginationDto) throws Exception {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        Example<Brand> example = buildSearchProbe(Brand.class, paginationDto.getSearch());
        Page<BrandDto> pageResult = brandService.findAll(example, pageable);
        WsDto<BrandDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }

    @PostMapping("/add")
    public BrandDto addPost(@RequestBody BrandDto userDto) {
        return brandService.save(userDto);
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
    public Boolean delete(@RequestParam String identifier) {
        try{
            brandService.delete(identifier);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    @GetMapping("/toggleStatus")
    public void toggleStatus(@RequestParam String identifier){
        brandService.toggleStatus(identifier);
    }
}