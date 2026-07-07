package com.ust.pos.models.service;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Model;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ModelService {

    ModelDto findByIdentifier(String identifier);

    ModelDto save(ModelDto modelDto);

    ModelDto update(ModelDto modelDto);

    void delete(String identifier);

    WsDto<ModelDto> findAll(Pageable pageable);

    WsDto<ModelDto> findAll(Specification<Model> example, Pageable pageable);

    ModelDto toggleStatus(String identifier);

    List<ModelDto> findIfTrue();
}
