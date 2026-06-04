package com.ust.pos.api.shelfs;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.shelfs.sevice.ShelfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelfs")
public class ApiShelfsController extends BaseController {

    @Autowired
    ShelfsService shelfsService;

    @PostMapping("/list")
    public List<ShelfsDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable= getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortField(), paginationDto.getSortField());
        return shelfsService.findAll(pageable);
    }

    @PostMapping("/add")
    public ShelfsDto add(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.save(shelfsDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try{
            shelfsService.delete(identifier);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    public ShelfsDto update(@RequestParam String identifier) {
        return shelfsService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ShelfsDto updatePrice(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.update(shelfsDto);
    }

    @PostMapping("/toggle")
    public ShelfsDto toggle(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.changeToggleStatus(shelfsDto.getIdentifier(), shelfsDto.isStatus());
    }
}
