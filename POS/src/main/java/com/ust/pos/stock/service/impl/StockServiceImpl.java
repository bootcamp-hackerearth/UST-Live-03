package com.ust.pos.stock.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.StockDto;
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
public class StockServiceImpl extends CommonService implements StockService {
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto save(StockDto stockDto) {
        if (stockDto.getQuantity() > 0) {
            stockDto.setStockStatus("Available");
        } else {
            stockDto.setStockStatus("Not Available");
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        if (stockRepository.findByIdentifierAndIsDeleteFalse(stock.getIdentifier()) != null) {
            stockDto.setMessage("The Product " + stockDto.getIdentifier() + "Already Exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        setAuditFields(stock, true);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        Stock existingStock = stockRepository.findByIdentifierAndIsDeleteFalse(stockDto.getIdentifier());
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
        setAuditFields(existingStock, false);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public List<StockDto> findAll() {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        return modelMapper.map(stockRepository.findByIsDeleteFalse(), listType);
    }

    @Override
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (stock != null) {
            stock.setDelete(true);
            setAuditFields(stock, false);
            stockRepository.save(stock);
        }
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndIsDeleteFalse(identifier);
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public void updateStatusOnly(String identifier, boolean status) {
        Stock stock = stockRepository.findByIdentifierAndIsDeleteFalse(identifier);
        stock.setStatus(status);
        setAuditFields(stock, false);
        stockRepository.save(stock);
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findByIsDeleteFalse(pageable);
        return modelMapper.map(stockPage.getContent(), listOfType);
    }

    @Override
    public Page<StockDto> findAll(Pageable pageable, String search) {
        Page<Stock> stocks;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Stock> specification = buildGlobalSearchSpec(Stock.class, search);
            stocks = stockRepository.findAll(specification, pageable);
        } else {
            stocks = stockRepository.findByIsDeleteFalse(pageable);
        }

        return stocks.map(stock -> modelMapper.map(stock, StockDto.class));
    }
}