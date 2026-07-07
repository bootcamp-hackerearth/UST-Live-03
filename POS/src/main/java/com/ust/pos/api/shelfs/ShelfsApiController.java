package com.ust.pos.api.shelfs;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.Shelfs;
import com.ust.pos.shelfs.service.ShelfsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelfs")
public class ShelfsApiController extends BaseController {

    private final ShelfsService shelfsService;

    public ShelfsApiController(ShelfsService shelfsService) {
        this.shelfsService = shelfsService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public WsDto<ShelfsDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Shelfs> example = buildGlobalSearchSpec(Shelfs.class, paginationDto.getKeyword());
            if (example != null) {
                return shelfsService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return shelfsService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public ShelfsDto addPost(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.save(shelfsDto);
    }

    @GetMapping("/get")
    public ShelfsDto update(@RequestParam String identifier) {
        return shelfsService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public ShelfsDto updatePost(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.update(shelfsDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            shelfsService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    @PostMapping("/toggleStatus")
    public boolean toggleStatus(@RequestBody ShelfsDto dto) {
        try {
            shelfsService.toggleStatus(dto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/getAllActive")
    public List<ShelfsDto> getAllActive() {
        return shelfsService.findAllActive();
    }
}
