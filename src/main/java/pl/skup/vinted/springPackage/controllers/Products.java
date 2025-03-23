package pl.skup.vinted.springPackage.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.skup.vinted.springPackage.services.ProductService;

@Slf4j
@RequestMapping(value = "/products")
@AllArgsConstructor
@RestController
public class Products {
    @Autowired
    ProductService productService;

    @GetMapping(path = "/")
    ResponseEntity<?> getAllProductsOnStock(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "50") int perPage,
                                            @RequestParam(defaultValue = "relevance") String order,
                                            @RequestParam(required = false) Double minPrice,

                                            @RequestParam(required = false) boolean showAll,
                                            @RequestParam(required = false, defaultValue = "price,asc") String sortBy


    ) {
        return productService.getProductsOnStock(page, perPage, order, minPrice, showAll, sortBy);
    }

    @GetMapping(path = "/all")
    ResponseEntity<?> getAllProductsOnStock() {
        return productService.getAllProductsOnStock();
    }

    @PostMapping(path = "/db")
    ResponseEntity<?> getAllItemsFromDB() {
        return productService.updateDbToVinted();
    }


}
