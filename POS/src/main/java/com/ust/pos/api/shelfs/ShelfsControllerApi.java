package com.ust.pos.api.shelfs;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.shelfs.service.ShelfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelfs")
public class ShelfsControllerApi extends BaseController {

    @Autowired
    private ShelfsService shelfsService;

    @PostMapping("/list")
    public WsDto<ShelfsDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return shelfsService.findAll(pageable);
    }

    @PostMapping("/add")
    public ShelfsDto addPost(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.save(shelfsDto);
    }

    @GetMapping("/get")
    public ShelfsDto update(@RequestParam String identifier) {
        return shelfsService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ShelfsDto updatePost(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.update(shelfsDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            shelfsService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/getAllActive")
    public List<ShelfsDto> getAllActive() {
        return shelfsService.findAllActive();
    }

    @GetMapping("/toggle")
    public boolean toggle(@RequestParam String identifier) {
        try {
            shelfsService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
