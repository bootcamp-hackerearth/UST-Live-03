package com.ust.pos.api.models;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.models.service.ModelService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/model")
public class ModelControllerApi extends BaseController {

    private final ModelService modelService;

    public ModelControllerApi(ModelService modelService){
        this.modelService=modelService;
    }

    @PostMapping("/list")
    public PageDto<ModelDto> model(@RequestBody PaginationDto paginationDto) {
        Pageable pageable=getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),paginationDto.getSortDirection(),paginationDto.getSortField());
        return modelService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public ModelDto getModelByIdentifier(@RequestParam String identifier) {
        return modelService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    public ModelDto addPost(@RequestBody  ModelDto modelDto) {
        return modelService.save(modelDto);

    }

    @PutMapping("/update")
    public ModelDto updatePost(@RequestBody ModelDto modelDto) {
        return modelService.update(modelDto);

    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;

    }

    @PostMapping("/toggleStatus")
    public void toggleStatus(@RequestParam String identifier) {
        modelService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<ModelDto> findByStatus() {
        return modelService.findActiveModels();
    }
}
