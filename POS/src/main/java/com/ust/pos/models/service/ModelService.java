package com.ust.pos.models.service;

import java.util.List;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Model;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ModelService {

    ModelDto findByIdentifier(String identifier);

    ModelDto save(ModelDto dto);

    ModelDto update(ModelDto dto);

    void delete(String identifier);

    WsDto<ModelDto> findAll(Pageable pageable);

    WsDto<ModelDto> findAll(Specification<Model> example, Pageable pageable);

    ModelDto changeToggleStatus(String identifier, boolean status);

    List<ModelDto> findActiveStatus();
}
