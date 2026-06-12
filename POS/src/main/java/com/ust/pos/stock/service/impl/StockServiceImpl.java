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
    public StockDto save(StockDto dto) {
        dto.setIdentifier(dto.getProductIdentifier() + "_" + dto.getWarehouseIdentifier());
        if (stockRepository.findByIdentifier(dto.getIdentifier()) != null) {
            dto.setSuccess(false);
            dto.setMessage("Product already exists in this warehouse");
            return dto;
        }
        Stock stock = modelMapper.map(dto, Stock.class);
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
    public StockDto update(StockDto dto) {
        dto.setIdentifier(dto.getProductIdentifier() + "_" + dto.getWarehouseIdentifier());
        Stock existing = stockRepository.findByIdentifier(dto.getIdentifier());
        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(STOCK_NOT_FOUND);
            return dto;
        }
        existing.setAvailableQuantity(dto.getAvailableQuantity());
        existing.setReorderLevel(dto.getReorderLevel());
        existing.setStatus(dto.getStatus());
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
            StockDto dto = new StockDto();
            dto.setSuccess(false);
            dto.setMessage(STOCK_NOT_FOUND);
            return dto;
        }
        StockDto dto = modelMapper.map(stock, StockDto.class);
        dto.setStockState(calculateState(stock));
        dto.setSuccess(true);
        return dto;
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
            StockDto dto = new StockDto();
            dto.setSuccess(false);
            dto.setMessage(STOCK_NOT_FOUND);
            return dto;
        }
        stock.setStatus(!Boolean.TRUE.equals(stock.getStatus()));
        Stock saved = stockRepository.save(stock);
        StockDto dto = modelMapper.map(saved, StockDto.class);
        dto.setStockState(calculateState(saved));
        dto.setSuccess(true);
        dto.setMessage("Stock status updated successfully");
        return dto;
    }
}