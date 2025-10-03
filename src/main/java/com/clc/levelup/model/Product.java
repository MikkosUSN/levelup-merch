package com.clc.levelup.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class Product {
    private Long id;

    @NotBlank
    private String name;

    @Positive
    private Double price;

    @NotBlank
    private String category;

    private String description;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
