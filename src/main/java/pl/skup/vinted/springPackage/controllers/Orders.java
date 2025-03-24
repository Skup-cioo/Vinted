package pl.skup.vinted.springPackage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
                                       @RequestParam(defaultValue = "500") int perPage,
                                       @RequestParam(defaultValue = "completed") String status,
                                       @RequestParam(defaultValue = "sell") String type,
                                       @RequestParam(required = false) OrdersSort sortBy) {
        return ordersService.getAllSoldOrders(page, perPage, status, type, sortBy);
    }

    @Operation(summary = "Pobieramy liste miesiecy i kwote jaka udalo nam sie zarobic")
    @GetMapping(path = "/all/months")
    ResponseEntity<?> getAllSoldOrdersLastWeek() {
        return ordersService.calculateSums();
    }

    @Operation(summary = "Detale dla konkretnego miesiace",
            description = "Zwracamy totalną sprzedaż, za ile średnio sprzedawalismy oraz liste orderów w danym miesiącu")
    @GetMapping(path = "/months/{month}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getMonthsDetails(@PathVariable String month) {
        return ordersService.getMonthsDetails(month);
    }
}
