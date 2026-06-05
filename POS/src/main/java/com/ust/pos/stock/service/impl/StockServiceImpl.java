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
public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StockDto save(StockDto stockDto) {
        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock != null) {
            stockDto.setMessage("Stock with identifier -" + stockDto.getIdentifier() + "alreay exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        stock.setStockStatus(true);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        if(pageable == null){
            return modelMapper.map(stockRepository.findAll(),listType);
        }
        Page<Stock> stockPage = stockRepository.findAll(pageable);
        return modelMapper.map(stockPage.getContent(), listType);
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
        stockRepository.deleteByIdentifier(identifier);
    }

    @Override
    @Transactional
    public StockDto toggleStatus(String identifier,boolean status) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock != null) {
            stock.setStatus(!stock.isStatus());
            stockRepository.save(stock);
        }
        return modelMapper.map(stock , StockDto.class);
    }
}