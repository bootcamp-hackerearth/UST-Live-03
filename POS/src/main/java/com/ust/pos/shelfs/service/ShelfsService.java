package com.ust.pos.shelfs.service;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.model.Shelfs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional
public interface ShelfsService {
    ShelfsDto save(ShelfsDto shelfsDto);

    ShelfsDto update(ShelfsDto shelfsDto);

    boolean delete(String identifier);

    PageDto<ShelfsDto> findAll(Pageable pageable);

    ShelfsDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<ShelfsDto> findActiveShelves();

    PageDto<ShelfsDto> findAll(Specification<Shelfs> spec, Pageable pageable, String keyword);
}
