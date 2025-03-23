package pl.skup.vinted.springPackage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.skup.vinted.models.endpointContraints.OrdersSort;
import pl.skup.vinted.springPackage.services.OrdersService;

@Slf4j
@RequestMapping(value = "/orders")
@AllArgsConstructor
@RestController
public class Orders {

    @Autowired
    OrdersService ordersService;

    @Operation(summary = "Pobieramy liste wszystkich sprzedanych produktów",
            description = "Zwracamy liste wszystkich sprzedanych produktów")
    @GetMapping(path = "/all")
    ResponseEntity<?> getAllSoldOrders(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "50") int perPage,
                                       @RequestParam(defaultValue = "completed") String status,
                                       @RequestParam(defaultValue = "sell") String type,
                                       @RequestParam(required = false, defaultValue = "date_desc") OrdersSort sortBy) {
        return ordersService.getAllSoldOrders(page, perPage, status, type, sortBy);
    }
}
