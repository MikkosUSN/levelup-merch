package com.clc.levelup.model;

import java.math.BigDecimal;                         // M4 update: use BigDecimal for money
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.NumberFormat;

/*
 * Team note:
 * Product maps to the products table. Keep this class simple (no business rules).
 *
 * M4 updates:
 *  - We added a real "category" field (kept "manufacturer" as the brand/maker).
 *  - We standardized on "price" and "partNumber" to match the DB and templates.
 *  - "partNumber" is a camelCase DB column; we map it explicitly with @Column.
 *  - Price now uses BigDecimal + @NumberFormat so we show exactly 2 decimals.
 */
@Table("products")
public class Product {

    @Id
    private Long id; // database identity

    private String name;
    private String description;

    /** Brand or maker */
    private String manufacturer;

    /** Store category (e.g., Apparel, Accessories, Collectibles) */
    private String category;

    /** CamelCase column in DB; mapped explicitly so Spring Data uses it verbatim */
    @Column("partNumber")
    private String partNumber;

    /** Units we have in stock */
    private int quantity;

    /** Unit price (USD) */
    @NumberFormat(pattern = "0.00")            // M4 update: ensures two-decimal display/parse
    private BigDecimal price;

    /** Empty constructor required by Spring Data */
    public Product() { }

    /** Convenience constructor for tests/seeds */
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

    // --- Getters/Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /** Product display name. */
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /** Short marketing copy. */
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /** Brand or maker of the item. */
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    /** Store category for filtering and display. */
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    /** Internal or vendor part number. */
    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    /** Inventory count. */
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /** Unit price in USD. */
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
