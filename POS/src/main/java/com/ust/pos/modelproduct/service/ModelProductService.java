package com.ust.pos.modelproduct.service;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelProductService {
    ModelProductDto save(ModelProductDto modelProductDto);

    ModelProductDto update(ModelProductDto modelProductDto);

    ModelProductDto findByIdentifier(String identifier);

    List<ModelProductDto> findAll();

    WsDto<ModelProductDto> findAll(Pageable pageable);

    void delete(String identifier);

    Page<ModelProductDto> findAll(String search, Pageable pageable);

    void toggleStatus(String identifier);
}
