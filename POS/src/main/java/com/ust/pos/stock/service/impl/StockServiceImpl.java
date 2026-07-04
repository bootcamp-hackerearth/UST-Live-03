package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class StockServiceImpl extends BaseService implements StockService {

    private static final String VALIDATION_MESSAGE = "Stock with identifier - ";
    private static final String AVAILABLE = "AVAILABLE";
    private static final String OUT_OF_STOCK = "OUT OF STOCK";
    private static final String LIMITED_STOCK = "LIMITED STOCK";
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {

        Stock stock = stockRepository.findByIdentifier(identifier);

        if (stock == null) {
            throw new ResourseNotFoundException("Data cannot found");
        }

        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public StockDto save(StockDto stockDto) {

        stockDto.setIdentifier(stockDto.getProduct() + stockDto.getWarehouse());
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock != null) {
            stockDto.setMessage(
                    existingStock.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
                            + " already exists."
            );
            stockDto.setSuccess(false);
            return stockDto;
        }

        if (stockDto.getQuantity() < 1) {
            stockDto.setStockStatus(OUT_OF_STOCK);
        } else if (stockDto.getQuantity() <= 5) {
            stockDto.setStockStatus(LIMITED_STOCK);
        } else {
            stockDto.setStockStatus(AVAILABLE);
        }

        Stock stock = modelMapper.map(stockDto, Stock.class);
        setCreatedDetails(stock);
        stockRepository.save(stock);

        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {

        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock == null) {
            stockDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }

        if (stockDto.getQuantity() < 1) {
            stockDto.setStockStatus(OUT_OF_STOCK);
        } else if (stockDto.getQuantity() <= 5) {
            stockDto.setStockStatus(LIMITED_STOCK);
        } else {
            stockDto.setStockStatus(AVAILABLE);
        }

        modelMapper.map(stockDto, existingStock);
        setModifiedDetails(existingStock);
        stockRepository.save(existingStock);

        return stockDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        Stock stock = stockRepository.findByIdentifier(identifier);
        setModifiedDetails(stock);
        softDelete(stock);
    }

    public StockDto updateQuantity(StockDto stockDto) {

        String product = stockDto.getProduct();
        Long quantity = stockDto.getQuantity();
        Stock stock = stockRepository.findByProduct(product);
        if (stock == null) {
            return null;
        }
        Long existingQuantity = stock.getQuantity();
        long reducedQuantity = existingQuantity - quantity;
        stock.setQuantity(reducedQuantity);
        if (reducedQuantity < 1) {
            stock.setStockStatus(OUT_OF_STOCK);
        } else if (reducedQuantity <= 5) {
            stock.setStockStatus(LIMITED_STOCK);
        } else {
            stock.setStockStatus(AVAILABLE);
        }
        return modelMapper.map(stockRepository.save(stock), StockDto.class);
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {

        Page<Stock> stockPage = stockRepository.findByIsDeletedFalse(pageable);

        WsDto<StockDto> stockDto = new WsDto<>();

        List<StockDto> stockDtos = stockPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, StockDto.class))
                .toList();

        stockDto.setContent(stockDtos);
        stockDto.setPage(stockPage.getNumber());
        stockDto.setSizePerPage(stockPage.getSize());
        stockDto.setTotalPages(stockPage.getTotalPages());
        stockDto.setTotalRecords(stockPage.getTotalElements());

        return stockDto;
    }

    @Override
    public WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);

        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}

