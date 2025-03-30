package pl.skup.vinted.springPackage.services;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.skup.vinted.models.dto.MonthDetailsDTO;
import pl.skup.vinted.models.endpointContraints.OrdersSort;
import pl.skup.vinted.models.vintedresponsemodel.Order;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public ResponseEntity<byte[]> getMonthlySummeryAsExcel() {
        checkOrders();

        Map<String, Double> monthlySums = getMonthsAndCash(orders);
        return getExcelFile(monthlySums);
    }

    public ResponseEntity<?> getMonthlyDetailsAsExcel(String month) {
        checkOrders();
        MonthDetailsDTO monthDetails;
        try {
            monthDetails = getMonthDetails(month);
        } catch (NullPointerException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Brak sprzedaży w miesiącu: " + month);
        }
        return getMonthDetailsExcelFile(monthDetails, month);
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
        MonthDetailsDTO monthDetailsDTO;
        try {
            monthDetailsDTO = getMonthDetails(month);
        } catch (NullPointerException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Brak sprzedaży w miesiącu: " + month);
        }
        return ResponseEntity.ok(monthDetailsDTO);
    }

    private MonthDetailsDTO getMonthDetails(String month) throws NullPointerException {
        checkOrders();
        Map<String, Double> monthsInfo = getMonthsAndCash(orders);

        Double totalCash = monthsInfo.get(month);

        List<Order> ordersInMonth = getOrdersIn(month);
        int ordersAmount = ordersInMonth.size();
        double averangePrice = 0;
        if (ordersAmount != 0) {
            averangePrice = totalCash / ordersAmount;
        }

        return new MonthDetailsDTO(ordersAmount, ordersInMonth, totalCash, averangePrice);
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

    private ResponseEntity<byte[]> getExcelFile(Map<String, Double> monthlySums) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Monthly Sums");

            // Styl nagłówka
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Tworzenie nagłówków
            Row headerRow = sheet.createRow(0);
            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue("Data");
            headerCell1.setCellStyle(headerStyle);

            Cell headerCell2 = headerRow.createCell(1);
            headerCell2.setCellValue("Kwota");
            headerCell2.setCellStyle(headerStyle);

            // Wypełnianie danymi
            int rowNum = 1;
            for (Map.Entry<String, Double> entry : monthlySums.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            // Auto-size kolumn
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            // Zapis do ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Tworzenie nagłówków HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=monthly_sums.xlsx");
            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private ResponseEntity<byte[]> getMonthDetailsExcelFile(MonthDetailsDTO monthDetailsDTO, String month) {
        {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Orders");

                Row headerRow = sheet.createRow(0);
                String[] headers = {"Tytuł", "Cena", "Pełna Data"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    CellStyle headerStyle = workbook.createCellStyle();
                    Font font = workbook.createFont();
                    font.setBold(true);
                    headerStyle.setFont(font);
                    cell.setCellStyle(headerStyle);
                }

                int rowNum = 1;
                for (Order order : monthDetailsDTO.getOrders()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(order.getTitle());
                    row.createCell(1).setCellValue(order.getPrice().getAmount());
                    row.createCell(2).setCellValue(order.getDate());
                }

                Row summaryRow1 = sheet.createRow(rowNum + 1);
                summaryRow1.createCell(0).setCellValue("Łączna wartość zamówień:");
                summaryRow1.createCell(1).setCellValue(monthDetailsDTO.getTotalCash());

                Row summaryRow2 = sheet.createRow(rowNum + 2);
                summaryRow2.createCell(0).setCellValue("Średnia cena:");
                summaryRow2.createCell(1).setCellValue(monthDetailsDTO.getAveragePrice());

                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);

                String fileName = "orders" + month + ".xlsx";
                HttpHeaders headersResponse = new HttpHeaders();
                headersResponse.add("Content-Disposition", "attachment; filename=" + fileName);
                headersResponse.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                return new ResponseEntity<>(outputStream.toByteArray(), headersResponse, HttpStatus.OK);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

    }
}
