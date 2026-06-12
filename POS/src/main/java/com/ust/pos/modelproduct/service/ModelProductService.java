package com.ust.pos.modelproduct.service;

import com.ust.pos.dto.ModelProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelProductService {
    ModelProductDto save(ModelProductDto modelProductDto);

    ModelProductDto update(ModelProductDto modelProductDto);

    ModelProductDto findByIdentifier(String identifier);

    List<ModelProductDto> findAll();

    void delete(String identifier);

    void toggleStatus(String identifier);

    List<ModelProductDto> findAll(Pageable pageable);
}