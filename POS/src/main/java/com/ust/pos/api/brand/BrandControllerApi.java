package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandControllerApi extends BaseController {

    @Autowired
    BrandService brandService;

    @PostMapping("/list")
    public WsDto<BrandDto> listCategories(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return brandService.findAll(pageable);

    }

    @PostMapping("/add")
    public BrandDto saveBrand(@RequestBody BrandDto brandDto) {

        return brandService.save(brandDto);

    }


    @GetMapping("/update")
    public BrandDto showEditPage(@RequestParam String identifier) {

        return brandService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public BrandDto saveEditedBrand(@RequestBody BrandDto brandDto) {

        return brandService.update(brandDto);
    }


    @GetMapping("/delete")
    public boolean deleteBrand(@RequestParam Long id) {
        try {
            brandService.deleteById(id);
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