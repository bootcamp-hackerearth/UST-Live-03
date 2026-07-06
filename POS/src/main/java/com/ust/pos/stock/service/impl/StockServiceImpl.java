package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class StockServiceImpl extends BaseService implements StockService {

    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public StockDto save(StockDto stockDto) {
        stockDto.setIdentifier(stockDto.getProduct() + stockDto.getWarehouse());
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            stockDto.setMessage(
                    existingStock.isDeleted()
                            ? " Stock with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " Stock with identifier - " + identifier
                            + " already exists."
            );
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        setCreatedDetails(stock);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingRole = stockRepository.findByIdentifier(identifier);
        if (existingRole == null) {
            stockDto.setMessage("Stock with identifier - " + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existingRole);
        setModifiedDetails(existingRole);
        stockRepository.save(existingRole);
        return stockDto;
    }

    @Transactional
    public void delete(String identifier) {

        Stock stock = stockRepository.findByIdentifier(identifier);
        setModifiedDetails(stock);
        softDelete(stock);
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        Page<Stock> stockPage = stockRepository.findByIsDeletedFalse(pageable);

        List<StockDto> stockDtos = modelMapper.map(
                stockPage.getContent(),
                listType
        );

        WsDto<StockDto> wsDto =
                new WsDto<>();

        wsDto.setContent(stockDtos);
        wsDto.setPage(stockPage.getNumber());
        wsDto.setSizePerPage(stockPage.getSize());
        wsDto.setTotalPages(stockPage.getTotalPages());
        wsDto.setTotalRecords(stockPage.getTotalElements());

        return wsDto;
    }

    @Override
    public WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(example,pageable);
        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(stockPage.getContent(), listType));
        wsDto.setTotalRecords(stockPage.getTotalElements());
        wsDto.setTotalPages(stockPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}