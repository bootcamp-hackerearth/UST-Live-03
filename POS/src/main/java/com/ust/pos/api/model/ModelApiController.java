package com.ust.pos.api.model;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.models.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model")
public class ModelApiController extends BaseController {

    @Autowired
    private ModelService modelService;

    @PostMapping("/list")
    public PaginationResponseDto<ModelDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return modelService.findAll(pageable);
    }

    @PostMapping("/add")
    public ModelDto addPost(@RequestBody ModelDto modelDto) {
        return modelService.save(modelDto);
    }

    @PostMapping("/toggle")
    public ModelDto toggleStatus(@RequestBody ModelDto dto) {
        return modelService.updateStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/get")
    public ModelDto update(@RequestParam String identifier) {
        return modelService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ModelDto updatePost(@RequestBody ModelDto modelDto) {
        return modelService.update(modelDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
