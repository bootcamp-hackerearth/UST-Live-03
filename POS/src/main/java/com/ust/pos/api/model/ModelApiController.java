package com.ust.pos.api.model;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.Model;
import com.ust.pos.models.service.ModelService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model")
public class ModelApiController extends BaseController {

    private final ModelService modelService;

    public ModelApiController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping("/list")
    public PaginationResponseDto<ModelDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

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

    @PutMapping("/toggle")
    public ModelDto toggleStatus(@RequestBody ModelDto dto) {
        return modelService.updateStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/get")
    public ModelDto update(@RequestParam String identifier) {
        return modelService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ModelDto updatePost(@RequestBody ModelDto modelDto) {
        return modelService.update(modelDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
