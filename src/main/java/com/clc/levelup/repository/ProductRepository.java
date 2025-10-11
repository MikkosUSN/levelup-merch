package com.clc.levelup.repository;

import org.springframework.data.repository.CrudRepository;

import com.clc.levelup.model.Product;

/*
 * Team note:
 * Spring Data JDBC provides CRUD for Product. We can add finders later.
 */
public interface ProductRepository extends CrudRepository<Product, Long> { }
