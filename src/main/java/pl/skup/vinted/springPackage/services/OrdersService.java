package pl.skup.vinted.springPackage.services;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.skup.vinted.models.endpointContraints.OrdersSort;
import pl.skup.vinted.models.responsemodel.Order;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pl.skup.vinted.models.restAssuredSpec.sendRequest.getAllSold;
import static pl.skup.vinted.springPackage.services.ProductService.baseSpecification;

@Service
@AllArgsConstructor
public class OrdersService {
    public ResponseEntity<?> getAllSoldOrders(int page, int perPage, String status, String type, OrdersSort sortBy) {
        if (Objects.isNull(LogInService.token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad Request: No Cookies.");
        } else {
            Response response = getAllSold(page, perPage, status, type, baseSpecification);
            List<Order> completedOrders = response.jsonPath().getList("my_orders", Order.class);

            return ResponseEntity.ok(sortOrders(completedOrders, sortBy));
        }
    }

    public List<Order> sortOrders(List<Order> orders, OrdersSort sortBy) {
        if (orders == null || orders.isEmpty()) {
            return orders;
        }

        Comparator<Order> dateComparator = Comparator.comparing(Order::getDate);
        Comparator<Order> priceComparator = Comparator.comparing(order -> Double.parseDouble(order.getPrice().getAmount()));
        String sort = sortBy.getValue();
        if ("date_asc".equalsIgnoreCase(sort)) {
            return orders.stream().sorted(dateComparator).collect(Collectors.toList());
        } else if ("date_desc".equalsIgnoreCase(sort)) {
            return orders.stream().sorted(dateComparator.reversed()).collect(Collectors.toList());
        } else if ("price_asc".equalsIgnoreCase(sort)) {
            return orders.stream().sorted(priceComparator).collect(Collectors.toList());
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            return orders.stream().sorted(priceComparator.reversed()).collect(Collectors.toList());
        } else {
            return orders.stream().sorted(dateComparator).collect(Collectors.toList());
        }
    }
}
