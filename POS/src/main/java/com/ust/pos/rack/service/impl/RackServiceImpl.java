package com.ust.pos.rack.service.impl;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.RackService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RackServiceImpl implements RackService {

    @Autowired
    private RackRepository rackRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PaginationResponseDto<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        if (pageable == null) {

            List<RackDto> rackDtoList =
                    modelMapper.map(
                            rackRepository.findAll(),
                            listType
                    );

            PaginationResponseDto<RackDto> response =
                    new PaginationResponseDto<>();

            response.setDtoList(rackDtoList);
            response.setTotalRecords(rackDtoList.size());

            return response;
        }
        Page<Rack> rackPage = rackRepository.findAll(pageable);
        List<RackDto> rackDtoList = modelMapper.map(rackPage.getContent(), listType);

        PaginationResponseDto<RackDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(rackDtoList);
        paginationResponseDto.setPage(rackPage.getNumber());
        paginationResponseDto.setSizePerPage(rackPage.getSize());
        paginationResponseDto.setTotalPages(rackPage.getTotalPages());
        paginationResponseDto.setTotalRecords(rackPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifier(identifier), RackDto.class);
    }

    public List<RackDto> findActiveRacks() {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(
                rackRepository.findByStatusTrue(),
                listType
        );
    }

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            rackDto.setSuccess(true);
            rackRepository.save(modelMapper.map(rackDto, Rack.class));
            rackDto.setMessage("Successfully added the rack");
            rackDto.setSuccess(true);
        } else {
            rackDto.setMessage("Rack " + identifier + " already exists");
            rackDto.setSuccess(false);
        }
        return rackDto;
    }

    @Override
    public RackDto update(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack == null) {
            rackDto.setMessage("Rack not found");
            rackDto.setSuccess(false);
        } else {
            modelMapper.map(rackDto, existingRack);
            rackRepository.save(existingRack);
            rackDto.setMessage("Rack updated successfully");
            rackDto.setSuccess(true);
        }
        return rackDto;
    }

    @Override
    @Transactional
    public RackDto updateStatus(String identifier, boolean status) {
        RackDto response = new RackDto();

        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            response.setSuccess(false);
            response.setMessage("Rack not found");
            return response;
        }

        rack.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        rackRepository.deleteByIdentifier(identifier);
    }
}
