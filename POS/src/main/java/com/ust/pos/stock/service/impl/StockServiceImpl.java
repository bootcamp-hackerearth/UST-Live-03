package com.ust.pos.stock.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class StockServiceImpl extends CommonService implements StockService {

    private final StockRepository stockRepository;

    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto save(StockDto stockDto) {

        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock != null) {
            if (existingStock.isDeleted()) {
                stockDto.setMessage("Brand with identifier - " + existingStock + "has been soft deleted.(Rollback by changing status");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setSuccess(false);
            stockDto.setMessage("Stock with Identifier " + stockDto.getIdentifier() + " already exists!");
            return stockDto;
        }

        Stock stock = modelMapper.map(stockDto, Stock.class);
        setAuditFields(stock, true);
        Stock savedStock = stockRepository.save(stock);

        StockDto responseDto = modelMapper.map(savedStock, StockDto.class);
        responseDto.setSuccess(true);
        responseDto.setMessage("Stock created successfully");

        return responseDto;
    }


    @Override
    public StockDto update(StockDto stockDto) {

        Stock stock = stockRepository.findById(stockDto.getId())
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        modelMapper.map(stockDto, stock);
        setAuditFields(stock, false);
        stockRepository.save(stock);
        StockDto responseDto = modelMapper.map(stock, StockDto.class);
        responseDto.setSuccess(true);
        responseDto.setMessage("Stock updated successfully");
        return responseDto;
    }

    @Override
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        softDelete(stock);
        setAuditFields(stock, false);
        stockRepository.save(stock);
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findByDeletedFalse(pageable);
        WsDto<StockDto> stockWsDto = new WsDto<>();
        stockWsDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        stockWsDto.setTotalRecords(stockPage.getTotalElements());
        stockWsDto.setTotalPages(stockPage.getTotalPages());
        stockWsDto.setSizePerPage(pageable.getPageSize());
        stockWsDto.setPage(pageable.getPageNumber());

        return stockWsDto;
    }

    @Override
    public WsDto<StockDto> findAll(Specification<Stock> spec, Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {}.getType();
        Page<Stock> page = stockRepository.findAll(spec, pageable);
        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public StockDto findById(Long id) {

        return stockRepository.findById(id)
                .map(stock -> modelMapper.map(stock, StockDto.class))
                .orElseThrow(() -> new RuntimeException("Stock not found"));

    }

    @Override
    public StockDto changeStockStatus(String identifier, boolean status) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        stock.setStatus(status);
        stockRepository.save(stock);
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        return modelMapper.map(stock, StockDto.class);
    }
}