package com.clc.levelup.model;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.NumberFormat;

/**
 * Represents a product record mapped to the {@code products} table.
 * This class contains only state and accessor methods—no business logic.
 * Used by the ProductService and ProductController layers.
 */
@Table("products")
public class Product {

    /** Primary key identifier in the database. */
    @Id
    private Long id;

    /** Display name of the product. */
    private String name;

    /** Short description for marketing or catalog display. */
    private String description;

    /** Manufacturer or brand name. */
    private String manufacturer;

    /** Category grouping (e.g., Apparel, Accessories, Collectibles). */
    private String category;

    /** Product part number, mapped explicitly to preserve camel-case column name. */
    @Column("partNumber")
    private String partNumber;

    /** Current stock quantity. */
    private int quantity;

    /** Unit price in USD, formatted to show two decimal places. */
    @NumberFormat(pattern = "0.00")
    private BigDecimal price;

    /** Default constructor required by Spring Data. */
    public Product() { }

    /**
     * Convenience constructor for manual creation, tests, or seeding.
     * @param name product name
     * @param description short description
     * @param manufacturer brand or maker
     * @param category category label
     * @param partNumber internal or vendor part number
     * @param quantity units in stock
     * @param price unit price
     */
    public Product(String name, String description, String manufacturer,
                   String category, String partNumber, int quantity, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.category = category;
        this.partNumber = partNumber;
        this.quantity = quantity;
        this.price = price;
    }

    // ----- Getters and Setters -----

    /** Get the product ID. */
    public Long getId() { return id; }

    /** Set the product ID. */
    public void setId(Long id) { this.id = id; }

    /** Get the product name. */
    public String getName() { return name; }

    /** Set the product name. */
    public void setName(String name) { this.name = name; }

    /** Get the product description. */
    public String getDescription() { return description; }

    /** Set the product description. */
    public void setDescription(String description) { this.description = description; }

    /** Get the manufacturer or brand. */
    public String getManufacturer() { return manufacturer; }

    /** Set the manufacturer or brand. */
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    /** Get the product category. */
    public String getCategory() { return category; }

    /** Set the product category. */
    public void setCategory(String category) { this.category = category; }

    /** Get the product part number. */
    public String getPartNumber() { return partNumber; }

    /** Set the product part number. */
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    /** Get the available quantity. */
    public int getQuantity() { return quantity; }

    /** Set the available quantity. */
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /** Get the unit price. */
    public BigDecimal getPrice() { return price; }

    /** Set the unit price. */
    public void setPrice(BigDecimal price) { this.price = price; }
}
