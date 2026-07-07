package com.ust.pos.api.models;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.model.Model;
import com.ust.pos.models.service.ModelService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('Admin')")
    public PageDto<ModelDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Model> spec = buildGlobalSearchSpec(Model.class, paginationDto.getKeyword());
            return modelService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return modelService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public ModelDto getModelByIdentifier(@RequestParam String identifier) {
        return modelService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public ModelDto addPost(@RequestBody  ModelDto modelDto) {
        return modelService.save(modelDto);

    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public ModelDto updatePost(@RequestBody ModelDto modelDto) {
        return modelService.update(modelDto);

    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
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
    @PreAuthorize("hasAuthority('Admin')")
    public void toggleStatus(@RequestParam String identifier) {
        modelService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<ModelDto> findByStatus() {
        return modelService.findActiveModels();
    }
}
