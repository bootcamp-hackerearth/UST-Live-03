package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockServiceImpl extends BaseService implements StockService {

    public static final String STOCK_WITH_IDENTIFIER = "Stock with identifier - ";
    private final ModelMapper modelMapper;
    private final StockRepository stockRepository;

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            if (Boolean.TRUE.equals(existingStock.getDeleted())) {
                stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        if (stockDto.getQuantity() > stockDto.getMinimumStock()) {
            stockDto.setStockStatus("Available");
        } else if (stockDto.getQuantity() > 0 && stockDto.getQuantity() <= stockDto.getMinimumStock()) {
            stockDto.setStockStatus("Low Stock");
        } else {
            stockDto.setStockStatus("Out of Stock");
        }
        stockDto.setIdentifier(stockDto.getProduct() + "_" + stockDto.getWarehouse());
        Stock stock = modelMapper.map(stockDto, Stock.class);
        setCreatedDetails(stock);
        stockDto.setSuccess(true);
        stockDto.setMessage("Stock created successfully");
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingStock == null) {
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        if (stockDto.getQuantity() > stockDto.getMinimumStock()) {
            stockDto.setStockStatus("Available");
        } else if (stockDto.getQuantity() > 0 && stockDto.getQuantity() <= stockDto.getMinimumStock()) {
            stockDto.setStockStatus("Low Stock");
        } else {
            stockDto.setStockStatus("Out of Stock");
        }
        modelMapper.map(stockDto, existingStock);
        setModifiedDetails(existingStock);
        stockRepository.save(existingStock);
        stockDto.setSuccess(true);
        stockDto.setMessage("Stock updated successfully");
        return stockDto;
    }

    @Override
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(stock);
        setModifiedDetails(stock);
        stockRepository.save(stock);
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAllByDeletedFalse(pageable);
        WsDto<StockDto> stockDto = new WsDto<>();
        stockDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        stockDto.setTotalRecords(stockPage.getTotalElements());
        stockDto.setTotalPage(stockPage.getTotalPages());
        stockDto.setSizePerPage(pageable.getPageSize());
        stockDto.setPage(pageable.getPageNumber());
        return stockDto;
    }

    @Override
    public WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);
        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifierAndDeletedFalse(identifier), StockDto.class);
    }

}
