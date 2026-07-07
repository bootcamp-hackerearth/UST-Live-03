package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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

    public static final String STOCK_WITH_IDENTIFIER = "Stock with identifier - ";
    private final StockRepository stockRepository;

    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (stock == null) {
            throw new ResourceNotFoundException("Stock with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " already exists");
            if (existingStock.isDeleted()) {
                stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " was deleted , Please Contact the Administrator to add.");
            }
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
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock == null) {
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existingStock);
        setModifiedDetails(existingStock);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        softDelete(stock);
        setModifiedDetails(stock);
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findByIsDeletedFalse(pageable);
        WsDto<StockDto> stockWsDto = new WsDto<>();
        stockWsDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        stockWsDto.setTotalRecords(stockPage.getTotalElements());
        stockWsDto.setTotalPages(stockPage.getTotalPages());
        stockWsDto.setSizePerPage(pageable.getPageSize());
        stockWsDto.setPage(pageable.getPageNumber());

        return stockWsDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock != null) {
            stock.setStatus(!stock.isStatus());
            stockRepository.save(stock);
            setModifiedDetails(stock);
        }
    }
    @Override
    public WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);

        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
