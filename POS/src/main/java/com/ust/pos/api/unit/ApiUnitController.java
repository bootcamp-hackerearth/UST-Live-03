package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class ApiUnitController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/unit/list";
    private final UnitService unitService;

    public ApiUnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping("/list")
    public WsDto<UnitDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        return unitService.findAll(pageable);
    }


    @PostMapping("/add")
    public UnitDto addPost(@RequestBody UnitDto unitDto) {

        return unitService.save(unitDto);
    }

    @GetMapping("/get")
    public UnitDto update(@RequestParam String identifier) {

        return unitService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public UnitDto updatePost(@RequestBody UnitDto userDto) {

        return unitService.update(userDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            unitService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public UnitDto toggle(@RequestParam String identifier) {

        return unitService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<UnitDto> findByStatus() {

        return unitService.findIfTrue();
    }

}
