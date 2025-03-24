package pl.skup.vinted.springPackage.services;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.skup.vinted.models.dto.MonthDetailsDTO;
import pl.skup.vinted.models.endpointContraints.OrdersSort;
import pl.skup.vinted.models.vintedresponsemodel.Order;

import java.util.*;
import java.util.stream.Collectors;

import static pl.skup.vinted.models.restAssuredSpec.sendRequest.getAllSold;
import static pl.skup.vinted.springPackage.services.ProductService.baseSpecification;

@Service
@AllArgsConstructor
public class OrdersService {
    private static List<Order> orders;

    public ResponseEntity<?> getAllSoldOrders(int page, int perPage, String status, String type, OrdersSort sortBy) {
        if (Objects.isNull(LogInService.token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad Request: No Cookies.");
        } else {
            orders = getAllOrders(page, perPage, status, type);

            return ResponseEntity.ok(sortOrders(orders, sortBy));
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


    public ResponseEntity<?> calculateSums() {
        checkOrders();

        Map<String, Double> monthlySums = getMonthsAndCash(orders);
        return ResponseEntity.ok(monthlySums);
    }

    private static Map<String, Double> getMonthsAndCash(List<Order> orders) {
        Map<String, Double> monthlySums = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> String.format("%d-%02d", order.getParsedDate().getYear(), order.getParsedDate().getMonthValue()),
                        Collectors.summingDouble(order -> Double.parseDouble(order.getPrice().getAmount()))
                )).entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return monthlySums;
    }

    private static List<Order> getAllOrders(int page, int perPage, String status, String type) {
        Response response = getAllSold(page, perPage, status, type, baseSpecification);
        List<Order> completedOrders = response.jsonPath().getList("my_orders", Order.class);
        return completedOrders;
    }

    public ResponseEntity<?> getMonthsDetails(String month) {
        checkOrders();
        Map<String, Double> monthsInfo = getMonthsAndCash(orders);

        Double totalCash = monthsInfo.get(month);

        if (totalCash == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Brak sprzedaży w miesiącu: " + month);
        }

        List<Order> ordersInMonth = getOrdersIn(month);
        int ordersAmount = ordersInMonth.size();
        double averangePrice = totalCash / ordersAmount;

        MonthDetailsDTO monthDetailsDTO = new MonthDetailsDTO(ordersAmount, ordersInMonth, totalCash, averangePrice);
        return ResponseEntity.ok(monthDetailsDTO);
    }

    private List<Order> getOrdersIn(String month) {
        return orders.stream()
                .filter(order -> order.getDate().contains(month))
                .toList();
    }

    private static void checkOrders() {
        if (Objects.isNull(orders)) {
            orders = getAllOrders(1, 500, "completed", "sell");
        }
    }

}
