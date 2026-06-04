package com.ust.pos.api.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("brandApiController")
@RequestMapping("/api/brands")
public class BrandController extends BaseController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/list")
    public ResponseEntity<List<BrandDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<BrandDto> brands = brandService.findAll(pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<BrandDto> getByIdentifier(@PathVariable String identifier) {
        BrandDto response = brandService.findByIdentifier(identifier);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<BrandDto> save(@RequestBody BrandDto brandDto) {
        BrandDto response = brandService.save(brandDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{identifier}")
    public ResponseEntity<BrandDto> update(@PathVariable String identifier, @RequestBody BrandDto brandDto) {
        brandDto.setIdentifier(identifier);
        BrandDto response = brandService.update(brandDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        try {
            brandService.delete(identifier);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/toggle/{identifier}")
    public ResponseEntity<BrandDto> toggleStatus(@PathVariable String identifier) {
        BrandDto response = brandService.toggleStatus(identifier);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BrandDto>> getActiveBrands() {
        List<BrandDto> activeBrands = brandService.findIfTrue();
        return ResponseEntity.ok(activeBrands);
    }
}