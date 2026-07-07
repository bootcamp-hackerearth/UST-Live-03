package com.ust.pos.model.service;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Model;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ModelService {

    ModelDto findByIdentifier(String identifier);

    ModelDto save(ModelDto modelDto);

    ModelDto update(ModelDto modelDto);

    void delete(String identifier);

    WsDto<ModelDto> findAll(Pageable pageable);

    List<ModelDto> findAllActive();

    ModelDto toggleStatus(String identifier);

    WsDto<ModelDto> findAll(Specification<Model> example, Pageable pageable);
}
