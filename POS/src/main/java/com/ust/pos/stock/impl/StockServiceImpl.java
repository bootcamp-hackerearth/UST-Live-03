package com.ust.pos.stock.impl;

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
import java.util.Optional;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StockDto save(StockDto stockDto) {

        Stock existing = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existing != null) {
            stockDto.setSuccess(false);
            stockDto.setMessage("Stock with identifier already exists");
            return stockDto;
        }
        stockDto.setIdentifier("STK_" + stockDto.getProductIdentifier() + "_" + stockDto.getWarehouseIdentifier());
        Stock stock = modelMapper.map(stockDto, Stock.class);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {

        Optional<Stock> stockOptional = stockRepository.findById(stockDto.getId());
        if (stockOptional.isEmpty()) {
            stockDto.setSuccess(false);
            stockDto.setMessage("Stock not found");
            return stockDto;
        }

        Stock existingStock = stockOptional.get();

        if (!existingStock.getIdentifier().equalsIgnoreCase(stockDto.getIdentifier())) {
            Stock duplicate = stockRepository.findByIdentifier(stockDto.getIdentifier());
            if (duplicate != null) {
                stockDto.setSuccess(false);
                stockDto.setMessage("Stock identifier already exists");
                return stockDto;
            }
        }

        modelMapper.map(stockDto, existingStock);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    @Transactional
    public void deleteByIdentifier(String identifier) {
        stockRepository.deleteByIdentifier(identifier);

    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> customerPage = stockRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public StockDto toggleStatus(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        stock.setStatus(!stock.isStatus());
        stockRepository.save(stock);
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public List<StockDto> findIfTrue() {
        Type listType = new TypeToken<List<StockDto>>() {

        }.getType();
        return modelMapper.map(stockRepository.findByStatusIsTrue(), listType);
    }
}