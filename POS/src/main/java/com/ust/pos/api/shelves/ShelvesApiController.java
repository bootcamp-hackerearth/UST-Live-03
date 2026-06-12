package com.ust.pos.api.shelves;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelvesDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.shelves.service.ShelvesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shelves")
public class ShelvesApiController extends BaseController {

    @Autowired
    private ShelvesService shelvesService;

    @PostMapping("/list")
    public WsDto<ShelvesDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return shelvesService.findAll(pageable);
    }

    @PostMapping("/add")
    public ShelvesDto addPost(@RequestBody ShelvesDto shelvesDto) {
        return shelvesService.save(shelvesDto);

    }

    @PostMapping("/get")
    public ShelvesDto update(@RequestBody String identifier) {
        return shelvesService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ShelvesDto updatePost(@RequestBody ShelvesDto shelvesDto) {
        return shelvesService.update(shelvesDto);

    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            shelvesService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}