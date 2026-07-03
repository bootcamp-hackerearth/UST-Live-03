package com.ust.pos.stock.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
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

@Transactional
@Service
public class StockServiceImpl extends CommonService implements StockService {
    public static final String STOCK_WITH_IDENTIFIER = "Stock with identifier - ";
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            if (!existingStock.isDeleted()) {
                stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " already exists");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        setAuditFields(stock, true);
        stockRepository.save(stock);
        stockDto.setSuccess(true);
        stockDto.setMessage("Stock created successfully");
        return stockDto;
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Page<Stock> stockPage = stockRepository.findByDeletedFalse(pageable);
        Type type = new TypeToken<List<StockDto>>() {
        }.getType();
        WsDto<StockDto> stockWsDto = new WsDto<>();
        stockWsDto.setDtoList(modelMapper.map(stockPage.getContent(), type));
        stockWsDto.setTotalRecords(stockPage.getTotalElements());
        stockWsDto.setTotalPages(stockPage.getTotalPages());
        stockWsDto.setSizePerPage(pageable.getPageSize());
        stockWsDto.setPage(pageable.getPageNumber());
        return stockWsDto;
    }

    @Override
    public WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);
        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock == null) {
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existingStock);
        setAuditFields(existingStock, false);
        stockRepository.save(existingStock);
        stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " Updated");
        stockDto.setSuccess(true);
        return stockDto;
    }

    @Override
    public StockDto toggleStatus(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        stock.setStatus(!stock.isStatus());
        setAuditFields(stock, false);
        stockRepository.save(stock);
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock == null) return false;
        softDelete(stock);
        setAuditFields(stock, false);
        stockRepository.save(stock);
        return true;
    }
}
