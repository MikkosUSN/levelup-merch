package com.clc.levelup.service;

import com.clc.levelup.model.Product;
import java.util.List;

public interface ProductService {
    Product create(Product p);
    List<Product> findAll();
}
