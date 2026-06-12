package com.ust.pos.models.service;

import java.util.List;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface ModelService {

    ModelDto findByIdentifier(String identifier);

    ModelDto save(ModelDto dto);

    ModelDto update(ModelDto dto);

    void delete(String identifier);

    WsDto<ModelDto> findAll(Pageable pageable);

    ModelDto changeToggleStatus(String identifier, boolean status);

    List<ModelDto> findActiveStatus();
}
