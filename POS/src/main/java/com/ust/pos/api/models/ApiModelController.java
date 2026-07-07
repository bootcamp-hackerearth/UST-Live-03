package com.ust.pos.api.models;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Model;
import com.ust.pos.models.service.ModelService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model")
public class ApiModelController extends BaseController {

    private final ModelService modelService;

    public ApiModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public WsDto<ModelDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable= getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Model> example = buildGlobalSearchSpec(Model.class, paginationDto.getKeyword());
            if (example != null) {
                return modelService.findAll(example, pageable);
            }
        }
        return modelService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ModelDto addModel(@RequestBody ModelDto modelDto) {
        return modelService.save(modelDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ModelDto update(@RequestParam String identifier) {
        return modelService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ModelDto updateModel(@RequestBody ModelDto modelDto) {
        return modelService.update(modelDto);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ModelDto toggle(@RequestBody ModelDto modelDto) {
        return modelService.changeToggleStatus(modelDto.getIdentifier(), modelDto.isStatus());
    }
    @PostMapping("/findActiveStatus")
    public List<ModelDto> findActive() {
        return modelService.findActiveStatus();
    }

}
