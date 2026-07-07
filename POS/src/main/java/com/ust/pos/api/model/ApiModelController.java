package com.ust.pos.api.model;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Model;
import com.ust.pos.models.service.ModelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model")
public class ApiModelController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/model/list";

    private final ModelService modelService;

    public ApiModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping("/list")
    public WsDto<ModelDto> list(@RequestBody PaginationDto paginationDto) {

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
    public ModelDto update(@RequestParam String identifier) {

        return modelService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ModelDto updatePost(@RequestBody ModelDto userDto) {

        return modelService.update(userDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            modelService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public ModelDto toggle(@RequestParam String identifier) {

        return modelService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<ModelDto> findByStatus() {

        return modelService.findIfTrue();
    }

    @GetMapping("/findByIdentifier")
    public ModelDto findByIdentifier(@RequestParam String identifier) {

        return modelService.findByIdentifier(identifier);
    }

}
