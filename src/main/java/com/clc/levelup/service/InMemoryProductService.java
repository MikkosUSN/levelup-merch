package com.clc.levelup.service;

import com.clc.levelup.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryProductService implements ProductService {
    // store products in memory for M3
    private final Map<Long, Product> byId = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(1);

    @Override
    public Product create(Product p) {
        Long id = ids.getAndIncrement();
        p.setId(id);
        byId.put(id, p);
        return p;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(byId.values());
    }
}
