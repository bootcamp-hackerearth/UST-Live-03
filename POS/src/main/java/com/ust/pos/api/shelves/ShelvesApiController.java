package com.ust.pos.api.shelves;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelvesDto;
import com.ust.pos.shelves.service.ShelvesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelves")
public class ShelvesApiController extends BaseController {

    @Autowired
    private ShelvesService shelvesService;

    @PostMapping("/list")
    public List<ShelvesDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return shelvesService.findAll(pageable);
    }

    @PostMapping("/add")
    public ShelvesDto addPost(@RequestBody ShelvesDto shelvesDto) {
        return shelvesService.save(shelvesDto);

    }

    @GetMapping("/get")
    public ShelvesDto update(@RequestParam String identifier) {
        return shelvesService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ShelvesDto updatePost(@RequestBody ShelvesDto shelvesDto) {
        return shelvesService.update(shelvesDto);

    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            shelvesService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}