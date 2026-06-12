package com.ust.pos.shelves.service.impl;

import com.ust.pos.dto.ShelvesDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelves;
import com.ust.pos.model.ShelvesRepository;
import com.ust.pos.shelves.service.ShelvesService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ShelvesServiceImpl implements ShelvesService {
    @Autowired
    private ShelvesRepository shelvesRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShelvesDto findByIdentifier(String identifier) {
        return modelMapper.map(shelvesRepository.findByIdentifier(identifier), ShelvesDto.class);
    }

    @Override
    public ShelvesDto save(ShelvesDto shelvesDto) {
        String identifier = shelvesDto.getIdentifier();
        Shelves existingShelves = shelvesRepository.findByIdentifier(identifier);
        if (existingShelves != null) {
            shelvesDto.setMessage("Shelfs with identifier - " + identifier + " already exists");
            shelvesDto.setSuccess(false);
            return shelvesDto;
        }
        Shelves shelves = modelMapper.map(shelvesDto, Shelves.class);
        shelvesRepository.save(shelves);
        return shelvesDto;
    }

    @Override
    public ShelvesDto update(ShelvesDto shelvesDto) {
        String identifier = shelvesDto.getIdentifier();
        Shelves existingShelves = shelvesRepository.findByIdentifier(identifier);
        if (existingShelves == null) {
            shelvesDto.setMessage("Shelves with identifier - " + identifier + " not found");
            shelvesDto.setSuccess(false);
            return shelvesDto;
        }
        modelMapper.map(shelvesDto, existingShelves);
        shelvesRepository.save(existingShelves);
        return shelvesDto;
    }

    @Transactional
    public void delete(String identifier) {
        shelvesRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<ShelvesDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ShelvesDto>>() {
        }.getType();

        Page<Shelves> shelvesPage = shelvesRepository.findAll(pageable);

        List<ShelvesDto> shelvesDtos = modelMapper.map(
                shelvesPage.getContent(),
                listType
        );

        WsDto<ShelvesDto> wsDto =
                new WsDto<>();

        wsDto.setContent(shelvesDtos);
        wsDto.setPage(shelvesPage.getNumber());
        wsDto.setSizePerPage(shelvesPage.getSize());
        wsDto.setTotalPages(shelvesPage.getTotalPages());
        wsDto.setTotalRecords(shelvesPage.getTotalElements());

        return wsDto;
    }

    @Override
    public List<Shelves> findActiveShelves() {
        return shelvesRepository.findByStatus("Active");
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelves shelves = shelvesRepository.findByIdentifier(identifier);
        if (shelves != null) {
            shelves.setStatus(!shelves.isStatus());
            shelvesRepository.save(shelves);
        }
    }
}