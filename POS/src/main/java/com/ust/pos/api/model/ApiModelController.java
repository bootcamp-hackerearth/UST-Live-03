package com.ust.pos.api.model;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model")
public class ApiModelController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/model/list";
    @Autowired
    private ModelService modelService;

    @PostMapping("/list")
    public WsDto<ModelDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
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

    @PostMapping("/update")
    public ModelDto updatePost(@RequestBody ModelDto userDto) {

        return modelService.update(userDto);
    }

    @GetMapping("/delete")
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
