package com.ust.pos.stock.service.impl;

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
    ModelMapper modelMapper;

    @Autowired
    StockRepository stockRepository;

    @Override
    public StockDto save(StockDto stockDto) {
        if (stockDto.getQuantity() > 0) {
            stockDto.setStockStatus("Available");
        } else {
            stockDto.setStockStatus("Not Available");
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        if (stockRepository.findByIdentifier(stock.getIdentifier()) != null) {
            stockDto.setMessage("The Product " + stockDto.getIdentifier() + "Already Exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock == null) {
            stockDto.setMessage("Stock with " + stockDto.getIdentifier() + "not found.");
            stockDto.setSuccess(false);
            return stockDto;
        }
        if (stockDto.getQuantity() > 0) {
            stockDto.setStockStatus("Available");
        } else {
            stockDto.setStockStatus("Not Available");
        }
        modelMapper.map(stockDto, existingStock);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public List<StockDto> findAll() {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        return modelMapper.map(stockRepository.findAll(), listType);
    }

    @Override
    public void delete(String identifier) {
        stockRepository.deleteByIdentifier(identifier);
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public void updateStatusOnly(String identifier, boolean status) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        stock.setStatus(status);
        stockRepository.save(stock);
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(pageable);
        return modelMapper.map(stockPage.getContent(), listOfType);
    }
}