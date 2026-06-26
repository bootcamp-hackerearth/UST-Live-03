package com.ust.pos.api.modelproduct;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.modelproduct.service.ModelProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model")
public class ModelProductApiController extends BaseController {

    private final ModelProductService modelProductService;

    public ModelProductApiController(ModelProductService modelProductService) {
        this.modelProductService = modelProductService;
    }

    @PostMapping("/list")
    public List<ModelProductDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortField());
        return modelProductService.findAll(pageable);
    }

    @PostMapping("/add")
    public ModelProductDto addModel(@RequestBody ModelProductDto modelProductDto) {
        return modelProductService.save(modelProductDto);
    }

    @GetMapping("/get")
    public ModelProductDto update(@RequestParam String identifier) {
        return modelProductService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ModelProductDto updatePost(@RequestBody ModelProductDto modelProductDto) {
        return modelProductService.update(modelProductDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelProductService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/status")
    public boolean updateStatus(
            @RequestParam String identifier, Boolean status) {
        try {
            modelProductService.updateStatusOnly(identifier, status);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
