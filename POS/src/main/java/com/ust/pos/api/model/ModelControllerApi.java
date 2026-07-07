package com.ust.pos.api.model;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.model.Model;
import com.ust.pos.models.service.ModelService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model")
@RequiredArgsConstructor
public class ModelControllerApi extends BaseController {

    private final ModelService modelService;

    @PostMapping("/list")
    public PaginatedResponseDto<ModelDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Model> example = buildGlobalSearchSpec(Model.class, paginationDto.getKeyword());
            if (example != null) {
                return modelService.findAll(example, pageable);
            }
        }
        return modelService.findAll(pageable);
    }

    @PostMapping("/add")
    public ModelDto addPost(@RequestBody ModelDto modelDto) {
        return modelService.save(modelDto);
    }

    @GetMapping("/get")
    public ModelDto get(@RequestParam String identifier) {
        return modelService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ModelDto updatePost(@RequestBody ModelDto modelDto) {
        return modelService.update(modelDto);
    }

    @DeleteMapping("/delete")
    public ModelDto delete(@RequestBody ModelDto modelDto) {
        try {
            return modelService.delete(modelDto.getIdentifier());
        } catch (Exception e) {
            ModelDto errorDto = new ModelDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody ModelDto modelDto) {
        try {
            modelService.changeStatus(modelDto.getIdentifier(), modelDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/active")
    public List<ModelDto> getActiveModels() {
        return modelService.findAllActive();
    }
}