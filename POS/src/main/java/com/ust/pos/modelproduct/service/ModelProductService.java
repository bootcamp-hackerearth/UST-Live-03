package com.ust.pos.modelproduct.service;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.ModelProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


public interface ModelProductService {
    ModelProductDto save(ModelProductDto modelProductDto);

    ModelProductDto update(ModelProductDto modelProductDto);

    ModelProductDto findByIdentifier(String identifier);

    void delete(String identifier);

    WsDto<ModelProductDto> findAll(Pageable pageable);

    void toggleStatus(String identifier);

    List<ModelProductDto> findAllActive();

    WsDto<ModelProductDto> findAll(Specification<ModelProduct> example, Pageable pageable, String keyword);
}
