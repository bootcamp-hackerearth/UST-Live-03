package com.ust.pos.modelproduct.service;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface ModelProductService {
    ModelProductDto save(ModelProductDto modelProductDto);

    ModelProductDto update(ModelProductDto modelProductDto);

    ModelProductDto findByIdentifier(String identifier);

    void delete(String identifier);

    WsDto<ModelProductDto> findAll(Pageable pageable);

    void toggleStatus(String identifier);
}
