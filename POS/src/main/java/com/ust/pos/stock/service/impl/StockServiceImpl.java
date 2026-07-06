package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
import jakarta.persistence.EntityNotFoundException;
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
public class StockServiceImpl extends BaseService implements StockService {
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(
            StockRepository stockRepository,
            ModelMapper modelMapper
    ) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto save(StockDto stockDto) {
        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock != null) {
            if (existingStock.isDeleted()) {
                stockDto.setMessage("Stock with identifier - " + stockDto.getIdentifier() + " has been soft deleted. (Rollback by changing status)");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setMessage("Stock with identifier -" + stockDto.getIdentifier() + "alreay exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        stock.setStockStatus(true);
        setCreatedDetails(stock);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public PaginationResponseDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {}.getType();
        PaginationResponseDto<StockDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Stock> stocks = stockRepository.findAll();
            response.setDtoList(modelMapper.map(stocks, listType));
            response.setTotalRecords(stocks.size());
            response.setTotalPages(1);
            response.setSizePerPage(stocks.size());
            response.setPage(0);
        } else {
            Page<Stock> stockPage = stockRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(stockPage.getContent(), listType));
            response.setTotalRecords(stockPage.getTotalElements());
            response.setTotalPages(stockPage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock == null) {
            stockDto.setMessage("Stock with identifier -" + stockDto.getIdentifier() + "not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existingStock);
        setModifiedDetails(existingStock);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock == null) {
            throw new EntityNotFoundException("Stock not found");
        }
        softDelete(stock);
        setModifiedDetails(stock);
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public StockDto toggleStatus(String identifier,boolean status) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock != null) {
            stock.setStatus(!stock.isStatus());
            setModifiedDetails(stock);
            stockRepository.save(stock);
        }
        return modelMapper.map(stock , StockDto.class);
    }

    @Override
    public PaginationResponseDto<StockDto> findAll(Specification<Stock> example, Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);
        PaginationResponseDto<StockDto> paginationresponse = new PaginationResponseDto<>();
        paginationresponse.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationresponse.setTotalRecords(page.getTotalElements());
        paginationresponse.setTotalPages(page.getTotalPages());
        paginationresponse.setSizePerPage(pageable.getPageSize());
        paginationresponse.setPage(pageable.getPageNumber());
        return paginationresponse;
    }
}