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

    public static final String STOCK_NOT_FOUND = "Stock not found";
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ModelMapper modelMapper;

    private String calculateState(Stock stock) {
        if (Boolean.FALSE.equals(stock.getStatus())) {
            return "DISCONTINUED";
        }
        if (stock.getAvailableQuantity() == null || stock.getAvailableQuantity() <= 0) {
            return "OUT_OF_STOCK";
        }
        if (stock.getReorderLevel() != null && stock.getAvailableQuantity() <= stock.getReorderLevel()) {
            return "LOW_STOCK";
        }
        return "AVAILABLE";
    }

    @Override
    public StockDto save(StockDto stockDto) {
        stockDto.setIdentifier(stockDto.getProductIdentifier() + "_" + stockDto.getWarehouseIdentifier());
        if (stockRepository.findByIdentifier(stockDto.getIdentifier()) != null) {
            stockDto.setSuccess(false);
            stockDto.setMessage("Product already exists in this warehouse");
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        if (stock.getStatus() == null) {
            stock.setStatus(true);
        }
        Stock saved = stockRepository.save(stock);
        StockDto response = modelMapper.map(saved, StockDto.class);
        response.setStockState(calculateState(saved));
        response.setSuccess(true);
        response.setMessage("Stock saved successfully");
        return response;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        stockDto.setIdentifier(stockDto.getProductIdentifier() + "_" + stockDto.getWarehouseIdentifier());
        Stock existing = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existing == null) {
            stockDto.setSuccess(false);
            stockDto.setMessage(STOCK_NOT_FOUND);
            return stockDto;
        }
        existing.setAvailableQuantity(stockDto.getAvailableQuantity());
        existing.setReorderLevel(stockDto.getReorderLevel());
        existing.setStatus(stockDto.getStatus());
        Stock saved = stockRepository.save(existing);
        StockDto response = modelMapper.map(saved, StockDto.class);
        response.setStockState(calculateState(saved));
        response.setSuccess(true);
        response.setMessage("Stock updated successfully");
        return response;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock == null) {
            StockDto stockDto = new StockDto();
            stockDto.setSuccess(false);
            stockDto.setMessage(STOCK_NOT_FOUND);
            return stockDto;
        }
        StockDto stockDto = modelMapper.map(stock, StockDto.class);
        stockDto.setStockState(calculateState(stock));
        stockDto.setSuccess(true);
        return stockDto;
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(pageable);
        List<StockDto> dtoList = modelMapper.map(stockPage.getContent(), listType);
        for (int i = 0; i < dtoList.size(); i++) {
            dtoList.get(i).setStockState(calculateState(stockPage.getContent().get(i)));
        }
        return dtoList;
    }

    @Override
    public void delete(String identifier) {
        stockRepository.deleteByIdentifier(identifier);
    }

    @Override
    public StockDto toggleStatus(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock == null) {
            StockDto stockDto = new StockDto();
            stockDto.setSuccess(false);
            stockDto.setMessage(STOCK_NOT_FOUND);
            return stockDto;
        }
        stock.setStatus(!Boolean.TRUE.equals(stock.getStatus()));
        Stock saved = stockRepository.save(stock);
        StockDto stockDto = modelMapper.map(saved, StockDto.class);
        stockDto.setStockState(calculateState(saved));
        stockDto.setSuccess(true);
        stockDto.setMessage("Stock status updated successfully");
        return stockDto;
    }
}