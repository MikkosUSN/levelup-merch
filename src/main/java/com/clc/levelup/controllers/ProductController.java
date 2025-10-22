package com.clc.levelup.controllers;

import com.clc.levelup.model.Product;
import com.clc.levelup.service.ProductService;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                         BindingResult result,
                         RedirectAttributes ra) {

        // simple required checks for NOT NULL DB columns (keep it lightweight)
        if (p.getManufacturer() == null || p.getManufacturer().isBlank()) {
            result.rejectValue("manufacturer", "NotBlank", "Manufacturer is required.");
        }
        if (p.getCategory() == null || p.getCategory().isBlank()) {
            result.rejectValue("category", "NotBlank", "Category is required.");
        }
        if (p.getPartNumber() == null || p.getPartNumber().isBlank()) {
            result.rejectValue("partNumber", "NotBlank", "Part number is required.");
        }

        if (result.hasErrors()) {
            return "products/new"; // go back to form if errors
        }

        products.create(p); // add product
        ra.addFlashAttribute("success", "Product created.");
        return "redirect:/products/list"; // redirect to list
    }

    // show a single product (details page)
    @GetMapping("/{id}")
    public String details(@PathVariable("id") Long id,
                          Model model,
                          RedirectAttributes ra) {
        return products.findById(id)
                .map(p -> {
                    model.addAttribute("product", p);
                    return "products/details";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Product not found.");
                    return "redirect:/products";
                });
    }

    // show the edit form
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id,
                           Model model,
                           RedirectAttributes ra) {
        return products.findById(id)
                .map(p -> {
                    model.addAttribute("product", p);
                    return "products/edit";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Product not found.");
                    return "redirect:/products";
                });
    }

    // handle edit form submit
    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable("id") Long pathId,
                             @Valid @ModelAttribute("product") Product form,
                             BindingResult result,
                             RedirectAttributes ra) {

        // required fields (same checks as create)
        if (form.getManufacturer() == null || form.getManufacturer().isBlank()) {
            result.rejectValue("manufacturer", "NotBlank", "Manufacturer is required.");
        }
        if (form.getCategory() == null || form.getCategory().isBlank()) {
            result.rejectValue("category", "NotBlank", "Category is required.");
        }
        if (form.getPartNumber() == null || form.getPartNumber().isBlank()) {
            result.rejectValue("partNumber", "NotBlank", "Part number is required.");
        }

        if (result.hasErrors()) {
            return "products/edit";
        }

        try {
            // make sure the path id is used for the update
            form.setId(pathId);
            products.update(form);
            ra.addFlashAttribute("success", "Product updated.");
            return "redirect:/products/" + pathId;
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/products";
        }
    }

    // --- Two-step delete: GET confirm + POST delete ---

    // show delete confirm page (keeps deletes off GET to be safer)
    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable("id") Long id,
                                Model model,
                                RedirectAttributes ra) {
        return products.findById(id)
                .map(p -> {
                    model.addAttribute("product", p);
                    return "products/delete"; // confirm page template
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Product not found.");
                    return "redirect:/products";
                });
    }

    // handle delete submit (from confirm page)
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            products.deleteById(id); // service does existence check
            ra.addFlashAttribute("success", "Product deleted.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/products";
    }
}
