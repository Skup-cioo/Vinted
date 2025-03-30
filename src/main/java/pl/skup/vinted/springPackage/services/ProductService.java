package pl.skup.vinted.springPackage.services;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.skup.vinted.dataBase.ItemTable;
import pl.skup.vinted.models.endpointContraints.ProductSort;
import pl.skup.vinted.models.restAssuredSpec.BaseSpecification;
import pl.skup.vinted.models.vintedresponsemodel.Item;
import pl.skup.vinted.springPackage.repositories.ItemRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static pl.skup.vinted.models.restAssuredSpec.sendRequest.getWardrobe;

@Service
@AllArgsConstructor
public class ProductService {
    @Autowired
    ItemRepository itemRepository;

    public static BaseSpecification baseSpecification = new BaseSpecification();

    public ResponseEntity<?> getProductsOnStock(int page, int perPage, String order, Double minPrice, boolean showAll, ProductSort sortBy) {
        if (Objects.isNull(LogInService.token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad Request: No Cookies.");
        } else if (showAll) {
            return getAllProductsOnStock();
        } else {
            List<Item> items = getItems(page, perPage, order, minPrice);

            return ResponseEntity.ok(sortItems(items, sortBy));
        }
    }

    public ResponseEntity<?> getProductsOnStockExcel(int page, int perPage, String order, Double minPrice) {
        if (Objects.isNull(LogInService.token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad Request: No Cookies.");
        } else {
            List<Item> items = getItems(page, perPage, order, minPrice);
            return generateItemsExcel(items);
        }
    }

    private List<Item> getItems(int page, int perPage, String order, Double minPrice) {
        Response response = getWardrobe(page, perPage, order, baseSpecification);
        return getItemsThatCostMore(minPrice, response);
    }

    public ResponseEntity<?> getAllProductsOnStock() {
        return getProductsOnStock(1, 900, "relevance", 0.0, false, ProductSort.PRICE_ASC);
    }

    public ResponseEntity<?> getProductsOnStockExcel() {
        return getProductsOnStockExcel(1, 900, "relevance", 0.0);
    }

    public List<Item> getItemsThatCostMore(Double minPrice, Response response) {
        if (Objects.isNull(minPrice)) {
            return response.jsonPath().getList("items", Item.class);
        } else {
            return response.jsonPath().getList("items", Item.class).stream()
                    .filter(item -> Double.parseDouble(item.getPrice()) > minPrice)
                    .toList();
        }
    }


    public ResponseEntity<?> updateDbToVinted() {
        List<ItemTable> tab = itemRepository.findAll();
        List<String> titleFromDb = tab.stream().map(ItemTable::getTitle).toList();
        List<Item> allItemsFromVinted = getItemsThatCostMore(0., getWardrobe(1, 900, "relevance", baseSpecification));

        allItemsFromVinted.stream().filter(item -> !titleFromDb.contains(item.getTitle()))
                .forEach(item -> {
                    itemRepository.save(new ItemTable(item.getTitle(), "TAK", "BRAK", item.getPrice()));
                });
        return ResponseEntity.ok("There was not anything to update");
    }

    private List<Item> sortItems(List<Item> items, ProductSort sortBy) {
        String[] sortParams = sortBy.getValue().split(",");
        String sortField = sortParams[0];
        boolean isDescending = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]);

        Comparator<Item> comparator;

        switch (sortField) {
            case "price":
                comparator = Comparator.comparing(item -> Double.parseDouble(item.getPrice()));
                break;
            default:
                comparator = Comparator.comparing(Item::getTitle);
        }

        if (isDescending) {
            comparator = comparator.reversed();
        }

        return items.stream().sorted(comparator).toList();
    }

    public ResponseEntity<byte[]> generateItemsExcel(List<Item> items) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Items");

            double sum = items.stream().mapToDouble(item -> Double.parseDouble(item.getPrice())).sum();

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Tytuł", "Cena", "Ścieżka"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Item item : items) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getTitle());
                row.createCell(1).setCellValue(item.getPrice());
                row.createCell(2).setCellValue(item.getPath());
            }

            Row sumRow = sheet.createRow(rowNum);
            sumRow.createCell(0).setCellValue("Łączna kwota:");
            sumRow.createCell(1).setCellValue(sum);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            HttpHeaders headersResponse = new HttpHeaders();
            headersResponse.add("Content-Disposition", "attachment; filename=items.xlsx");
            headersResponse.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(outputStream.toByteArray(), headersResponse, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
