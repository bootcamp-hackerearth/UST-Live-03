package com.ust.pos.api.models;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.models.service.ModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("modelsApiController")
@RequestMapping("/api/models")
public class ModelsController extends BaseController {

    @Autowired
    private ModelsService modelsService;

    @PostMapping("/list")
    public ResponseEntity<List<ModelsDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<ModelsDto> models = modelsService.findAll(pageable);
        return ResponseEntity.ok(models);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<ModelsDto> getByIdentifier(@PathVariable String identifier) {
        ModelsDto response = modelsService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<ModelsDto> save(@RequestBody ModelsDto modelsDto) {
        ModelsDto response = modelsService.save(modelsDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{identifier}")
    public ResponseEntity<ModelsDto> update(@PathVariable String identifier, @RequestBody ModelsDto modelsDto) {
        modelsDto.setIdentifier(identifier);
        ModelsDto response = modelsService.update(modelsDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        try {
            boolean deleted = modelsService.delete(identifier);
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/toggle/{identifier}")
    public ResponseEntity<ModelsDto> toggleStatus(@PathVariable String identifier) {
        ModelsDto response = modelsService.toggleStatus(identifier);
        return ResponseEntity.ok(response);
    }
}