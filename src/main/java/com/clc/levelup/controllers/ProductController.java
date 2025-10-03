package com.clc.levelup.controllers;

import com.clc.levelup.model.Product;
import com.clc.levelup.service.ProductService;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService products;

    // connect ProductService to this controller
    public ProductController(ProductService products) {
        this.products = products;
    }

    // show the list of products
    @GetMapping({"", "/list"})
    public String list(Model model) {
        model.addAttribute("products", products.findAll()); // pass products to the view
        return "products/list";
    }

    // show the create form
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product()); // form backing object
        return "products/new";
    }

    // handle create form submit
    @PostMapping
    public String create(@Valid @ModelAttribute("product") Product p,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "products/new"; // go back to form if errors
        }
        products.create(p); // add product in memory
        return "redirect:/products/list"; // redirect to list
    }
}
