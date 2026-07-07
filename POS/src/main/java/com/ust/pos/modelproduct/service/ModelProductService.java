package com.ust.pos.modelproduct.service;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.ModelProduct;
import org.springframework.data.domain.Example;
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

    Page<ModelProductDto> findAll(Example<ModelProduct> example, Pageable pageable);

    void toggleStatus(String identifier);
}
