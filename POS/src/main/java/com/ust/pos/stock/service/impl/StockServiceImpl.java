package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
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
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class StockServiceImpl extends BaseService implements StockService {

    private final StockRepository stockRepository;

    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        Page<Stock> stockPage = stockRepository.findByIsDeletedFalse(pageable);

        WsDto<StockDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(stockPage.getContent(), listType));
        dto.setTotalRecords(stockPage.getTotalElements());
        dto.setTotalPages(stockPage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public StockDto save(StockDto stockDto) {

        stockDto.setIdentifier(stockDto.getProduct() + stockDto.getWarehouse());
        String identifier = stockDto.getIdentifier();

        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock != null) {
            stockDto.setMessage(
                    existingStock.isDeleted()
                            ? "Stock - " + identifier + " already exists but was deleted, Please contact Administrator"
                            : "Stock - " + identifier + " already exists"
            );
            stockDto.setSuccess(false);
            return stockDto;
        }

        if (stockDto.getQuantity() < 1) {
            stockDto.setStockStatus("OUT_OF_STOCK");
        } else if (stockDto.getQuantity() <= 5) {
            stockDto.setStockStatus("LIMITED_STOCK");
        } else {
            stockDto.setStockStatus("AVAILABLE");
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);

        setCreatedDetails(stock);
        stockRepository.save(stock);

        stockDto.setSuccess(true);
        return stockDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        setModifiedDetails(stock);
        softDelete(stock);
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingProduct = stockRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
            stockDto.setMessage("Stock with product - " + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existingProduct);
        setModifiedDetails(existingProduct);

        if (stockDto.getQuantity() < 1) {
            existingProduct.setStockStatus("OUT_OF_STOCK");
        } else if (stockDto.getQuantity() <= 5) {
            existingProduct.setStockStatus("LIMITED_STOCK");
        } else {
            existingProduct.setStockStatus("AVAILABLE");
        }
        stockRepository.save(existingProduct);
        return stockDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock != null) {
            stock.setStatus(!stock.isStatus());
            stockRepository.save(stock);
        }
    }
}
