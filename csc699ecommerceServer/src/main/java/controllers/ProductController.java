package controllers;

import java.util.Collection;

import models.Product;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repositories.ProductRepo;

@RestController
public class ProductController {

    private final ProductRepo productRepo = new ProductRepo();

    @RequestMapping("/product-catalog/id/{id}")
    public Product product(@PathVariable String id) {
        return productRepo.getProduct(id);
    }

    @RequestMapping("/product-catalog/category/{category}")
    public Collection<Product> productsByCategory(@PathVariable String category) {
        return productRepo.getProductsByCategory(category);
    }

    @RequestMapping("/product-catalog")
    public Collection<Product> allProducts() {
        return productRepo.getAllProducts();
    }

    @RequestMapping("/product-catalog/search/{query}")
    public Collection<Product> searchProducts(@PathVariable String query) {
        return productRepo.searchProducts(query);
    }
}