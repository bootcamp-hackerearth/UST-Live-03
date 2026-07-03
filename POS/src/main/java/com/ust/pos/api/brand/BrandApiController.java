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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandApiController extends BaseController {
    private final BrandService brandService;

    public BrandApiController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/list")
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

    @GetMapping("/getAllActive")
    public List<BrandDto> getAllActive() {
        return brandService.findAllActive();
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BrandDto add(@RequestPart("identifier") String identifier, @RequestPart("description") String description,
                        @RequestPart(value = "icon", required = false) MultipartFile icon) {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier(identifier);
        brandDto.setDescription(description);
        brandDto.setIcon(icon);
        return brandService.save(brandDto);
    }

    @GetMapping("/get")
    public BrandDto getByIdentifier(@RequestParam String identifier) {
        return brandService.findByIdentifier(identifier);
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BrandDto update(@RequestPart("identifier") String identifier, @RequestPart("description") String description,
                           @RequestPart(value = "icon", required = false) MultipartFile icon) {
        BrandDto brandDto = new BrandDto();
        brandDto.setIdentifier(identifier);
        brandDto.setDescription(description);
        brandDto.setIcon(icon);
        return brandService.update(brandDto);
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

    @PostMapping("/toggle")
    public BrandDto toggleStatus(@RequestParam String identifier) {
        return brandService.toggleStatus(identifier);
    }
}
