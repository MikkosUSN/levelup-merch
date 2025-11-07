package com.clc.levelup.controllers;

import com.clc.levelup.model.Product;
import com.clc.levelup.service.ProductService;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles product management pages.
 * Supports viewing, creating, editing, and deleting products.
 * Delegates all business logic to the ProductService.
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService products;

    /**
     * Inject the ProductService dependency.
     * @param products service providing product CRUD operations
     */
    public ProductController(ProductService products) {
        this.products = products;
    }

    /**
     * Display a list of all products.
     * @param model MVC model for the view
     * @return view for product list
     */
    @GetMapping({"", "/list"})
    public String list(Model model) {
        // Fetch all products and send to the view
        model.addAttribute("products", products.findAll());
        return "products/list";
    }

    /**
     * Show the new product creation form.
     * @param model MVC model
     * @return new product form view
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/new";
    }

    /**
     * Handle submission of the new product form.
     * Performs lightweight field validation and delegates persistence to the service.
     * @param p product form data
     * @param result binding result for validation
     * @param ra redirect attributes for success/error messages
     * @return redirect or form view
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("product") Product p,
                         BindingResult result,
                         RedirectAttributes ra) {

        // Field-level checks for required fields
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
            // Re-display form if validation fails
            return "products/new";
        }

        // Save product and redirect to list view
        products.create(p);
        ra.addFlashAttribute("success", "Product created.");
        return "redirect:/products/list";
    }

    /**
     * Show details for a specific product.
     * @param id product ID
     * @param model MVC model
     * @param ra redirect attributes
     * @return details view or redirect if not found
     */
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

    /**
     * Show the edit form for an existing product.
     * @param id product ID
     * @param model MVC model
     * @param ra redirect attributes
     * @return edit form view or redirect if product missing
     */
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

    /**
     * Handle submission of the product edit form.
     * Validates and updates the product.
     * @param pathId path variable ID
     * @param form updated product data
     * @param result validation result
     * @param ra redirect attributes
     * @return redirect to product detail or list
     */
    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable("id") Long pathId,
                             @Valid @ModelAttribute("product") Product form,
                             BindingResult result,
                             RedirectAttributes ra) {

        // Repeat same validation used during creation
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
            // Ensure the product being updated matches the URL path ID
            form.setId(pathId);
            products.update(form);
            ra.addFlashAttribute("success", "Product updated.");
            return "redirect:/products/" + pathId;
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/products";
        }
    }

    // --- Two-step delete: confirmation and final removal ---

    /**
     * Display a confirmation page before deleting a product.
     * @param id product ID
     * @param model MVC model
     * @param ra redirect attributes
     * @return delete confirmation view or redirect if not found
     */
    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable("id") Long id,
                                Model model,
                                RedirectAttributes ra) {
        return products.findById(id)
                .map(p -> {
                    model.addAttribute("product", p);
                    return "products/delete";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Product not found.");
                    return "redirect:/products";
                });
    }

    /**
     * Process confirmed delete requests.
     * @param id product ID
     * @param ra redirect attributes
     * @return redirect to products list after deletion
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            products.deleteById(id);
            ra.addFlashAttribute("success", "Product deleted.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/products";
    }
}
