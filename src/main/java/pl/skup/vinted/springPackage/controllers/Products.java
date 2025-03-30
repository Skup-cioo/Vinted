package pl.skup.vinted.springPackage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.skup.vinted.models.endpointContraints.ProductSort;
import pl.skup.vinted.springPackage.services.ProductService;

@Slf4j
@RequestMapping(value = "/products")
@AllArgsConstructor
@RestController
public class Products {
    @Autowired
    ProductService productService;

    @Operation(summary = "Pobieramy liste produktów Vinted",
            description = "Zwracamy liste wybranych bądź posortowanych produktów")
    @GetMapping(path = "/")
    ResponseEntity<?> getAllProductsOnStock(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "50") int perPage,
                                            @RequestParam(defaultValue = "relevance") String order,
                                            @RequestParam(required = false) Double minPrice,

                                            @RequestParam(required = false) boolean showAll,
                                            @RequestParam(required = false) ProductSort sortBy
    ) {
        return productService.getProductsOnStock(page, perPage, order, minPrice, showAll, sortBy);
    }

    @Operation(summary = "Pobieramy liste wszystkich produktów Vinted",
            description = "Zwracamy liste wszystkich produktów")
    @GetMapping(path = "/all")
    ResponseEntity<?> getAllProductsOnStock() {
        return productService.getAllProductsOnStock();
    }

    @Operation(summary = "Pobieramy liste wszystkich produktów Vinted -> jako Excel",
            description = "Zwracamy liste wszystkich produktów")
    @GetMapping(path = "/all/excel")
    ResponseEntity<?> getAllProductsOnStockExcel() {
        return productService.getProductsOnStockExcel();
    }


    @Operation(summary = "Porównuje itemy z vinted i te która są zapisane na bazie, jeżeli w DB nie ma jakiegoś wyrównuje stan")
    @PostMapping(path = "/db")
    ResponseEntity<?> getAllItemsFromDB() {
        return productService.updateDbToVinted();
    }


}
