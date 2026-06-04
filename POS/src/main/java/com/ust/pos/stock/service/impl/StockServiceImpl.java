package com.ust.pos.stock.service.impl;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
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
public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StockDto findByIdentifier(String identifier) {

        Stock stock = stockRepository.findByIdentifier(identifier);

        if (stock == null) {
            return null;
        }

        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public StockDto save(StockDto stockDto) {

        stockDto.setIdentifier(stockDto.getProduct() + stockDto.getWarehouse());
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock != null) {
            stockDto.setMessage("Stock with identifier - " + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }

        if (stockDto.getQuantity() < 1) {
            stockDto.setStockStatus("OUT OF STOCK");
        } else if (stockDto.getQuantity() <= 5) {
            stockDto.setStockStatus("LIMITED STOCK");
        } else {
            stockDto.setStockStatus("AVAILABLE");
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

        if (stockDto.getQuantity() < 1) {
            stockDto.setStockStatus("OUT OF STOCK");
        } else if (stockDto.getQuantity() <= 5) {
            stockDto.setStockStatus("LIMITED STOCK");
        } else {
            stockDto.setStockStatus("AVAILABLE");
        }

        modelMapper.map(stockDto, existingStock);
        stockRepository.save(existingStock);

        return stockDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        stockRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(pageable);

        return modelMapper.map(stockPage.getContent(), listType);
    }

}

