package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class StockServiceImpl extends BaseService implements StockService {

    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            if (existingStock.isDeleted()) {
                stockDto.setMessage("Stock with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setMessage("Stock with identifier - " + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        setCreatedDetails(stock);
        stockRepository.save(stock);
        stockDto.setSuccess(true);
        stockDto.setMessage("Stock created successfully");
        return stockDto;
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        WsDto<StockDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<StockDto> stockDtoList = modelMapper.map(stockRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(stockDtoList);
            wsDto.setTotalRecords(stockDtoList.size());
            return wsDto;
        }
        Page<Stock> stockPage = stockRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(stockPage.getTotalPages());
        wsDto.setTotalRecords(stockPage.getTotalElements());
        return wsDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock == null) {
            stockDto.setMessage("Stock with identifier - " + stockDto.getIdentifier() + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existingStock);
        setModifiedDetails(existingStock);
        stockRepository.save(existingStock);
        stockDto.setSuccess(true);
        stockDto.setMessage("Stock updated successfully");
        return stockDto;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        softDelete(stock);
        setModifiedDetails(stock);
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public StockDto toggleStatus(String identifier, boolean status) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock == null) {
            StockDto response = new StockDto();
            response.setSuccess(false);
            response.setMessage("Stock not found");
            return response;
        }
        stock.setStatus(status);
        setModifiedDetails(stock);
        stockRepository.save(stock);
        StockDto response = modelMapper.map(stock, StockDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    @Override
    public WsDto<StockDto> findAll(Specification<Stock> specification,
                                   Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        Page<Stock> page =
                stockRepository.findAll(specification, pageable);

        WsDto<StockDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}