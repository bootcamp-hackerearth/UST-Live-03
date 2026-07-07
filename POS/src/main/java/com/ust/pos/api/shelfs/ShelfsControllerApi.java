package com.ust.pos.api.shelfs;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.shelfs.service.ShelfsService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/shelfs")
public class ShelfsControllerApi extends BaseController {
    public static final String REDIRECT_SHELFS_LIST= "redirect:/shelfs/list";

    private final ShelfsService shelfsService;

    public ShelfsControllerApi(ShelfsService shelfsService){
        this.shelfsService=shelfsService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public PageDto<ShelfsDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Shelfs> spec = buildGlobalSearchSpec(Shelfs.class, paginationDto.getKeyword());
            return shelfsService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return shelfsService.findAll(pageable);
    }


    @GetMapping("/identifier")
    public ShelfsDto getShelfsByIdentifier(@RequestParam String identifier) {
        return shelfsService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public ShelfsDto addPost(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.save(shelfsDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public ShelfsDto updatePost(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.update(shelfsDto);

    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
    public boolean delete(@RequestParam String identifier) {
        try {
            shelfsService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    @PreAuthorize("hasAuthority('Admin')")
    public void toggleStatus(@RequestParam String identifier) {
        shelfsService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<ShelfsDto> findByStatus() {
        return shelfsService.findActiveShelves();
    }
}

