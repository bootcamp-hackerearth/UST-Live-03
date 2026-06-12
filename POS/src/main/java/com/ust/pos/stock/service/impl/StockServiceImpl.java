package com.ust.pos.stock.service.impl;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
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
@Transactional

public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public StockDto save(StockDto stockDto) {

        String identifier = stockDto.getProduct() + "_" + stockDto.getWarehouse();
        stockDto.setIdentifier(identifier);

        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock != null) {
            stockDto.setMessage("Stock already exists for Product '"
                    + stockDto.getProduct() + "' in Warehouse '"
                    + stockDto.getWarehouse() + "'");
            stockDto.setSuccess(false);
            return stockDto;
        }

        Stock stock = modelMapper.map(stockDto, Stock.class);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {

        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock == null) {
            stockDto.setMessage("Stock with identifier - " + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }

        modelMapper.map(stockDto, existingStock);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public void delete(String identifier) {
        stockRepository.deleteByIdentifier(identifier);
    }

    @Override
    public PaginatedResponseDto<StockDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(pageable);

        List<StockDto> items = modelMapper.map(stockPage.getContent(), listType);

        PaginatedResponseDto<StockDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(stockPage.getTotalElements());
        response.setTotalPages(stockPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());

        return response;
    }

    @Override
    public List<StockDto> findAllActive() {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        return modelMapper.map(stockRepository.findByStatus(true), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        stock.setStatus(status);
        stockRepository.save(stock);
    }
}
