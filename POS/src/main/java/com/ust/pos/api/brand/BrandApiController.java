package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/brand")
public class BrandApiController extends BaseController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/list")
    public WsDto<BrandDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return brandService.findAll(pageable);
    }

    @PostMapping("/add")
    public BrandDto addPost(@RequestBody BrandDto brandDto) {
        return brandService.save(brandDto);
    }

    @PostMapping("/get")
    public BrandDto update(@RequestBody String identifier) {
        return brandService.findByIdentifier(identifier);

    }

    @PostMapping("/update")
    public BrandDto updatePost(@RequestBody BrandDto brandDto) {
        return brandService.update(brandDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            brandService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
