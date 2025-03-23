package pl.skup.vinted.springPackage.services;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.skup.vinted.dataBase.ItemTable;
import pl.skup.vinted.models.endpointContraints.ProductSort;
import pl.skup.vinted.models.responsemodel.Item;
import pl.skup.vinted.models.restAssuredSpec.BaseSpecification;
import pl.skup.vinted.springPackage.repositories.ItemRepository;

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
            Response response = getWardrobe(page, perPage, order, baseSpecification);
            List<Item> items = getItemsThatCostMore(minPrice, response);

            return ResponseEntity.ok(sortItems(items, sortBy));
        }

    }

    public ResponseEntity<?> getAllProductsOnStock() {
        return getProductsOnStock(1, 900, "relevance", 0.0, false, ProductSort.PRICE_ASC);
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

    boolean isUpdated;

    public ResponseEntity<?> updateDbToVinted() {
        List<ItemTable> tab = itemRepository.findAll();
        List<String> titleFromDb = tab.stream().map(ItemTable::getTitle).toList();
        List<Item> allItemsFromVinted = getItemsThatCostMore(0., getWardrobe(1, 900, "relevance", baseSpecification));

        allItemsFromVinted.stream().filter(item -> !titleFromDb.contains(item.getTitle()))
                .forEach(item -> {
                    itemRepository.save(new ItemTable(item.getTitle(), "TAK", "BRAK", item.getPrice()));
                    isUpdated = true;
                });
        if (isUpdated) {
            return ResponseEntity.ok("DB updated");
        }
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
}
