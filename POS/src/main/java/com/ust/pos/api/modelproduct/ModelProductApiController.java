package com.ust.pos.api.modelproduct;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.modelproduct.service.ModelProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/modelProduct")
public class ModelProductApiController extends BaseController {

    @Autowired
    private ModelProductService modelProductService;

    @PostMapping("/list")
    public List<ModelProductDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return modelProductService.findAll(pageable);
    }

    @PostMapping("/add")
    public ModelProductDto addPost(@RequestBody ModelProductDto modelProductDto) {
        return modelProductService.save(modelProductDto);
    }

    @GetMapping("/get")
    public ModelProductDto update(@RequestParam String identifier) {
        return modelProductService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ModelProductDto updatePost(@RequestBody ModelProductDto modelProductDto) {
        return modelProductService.update(modelProductDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelProductService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public boolean toggleStatus(@RequestBody String identifier) {
        try {
            modelProductService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
