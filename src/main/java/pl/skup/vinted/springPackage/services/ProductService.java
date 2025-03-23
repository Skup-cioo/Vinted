package pl.skup.vinted.springPackage.services;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.skup.vinted.dataBase.ItemTable;
import pl.skup.vinted.models.responsemodel.Item;
import pl.skup.vinted.models.restAssuredSpec.BaseSpecification;
import pl.skup.vinted.springPackage.repositories.ItemRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ProductService {
    @Autowired
    ItemRepository itemRepository;

    public static BaseSpecification baseSpecification = new BaseSpecification();

    public ResponseEntity<?> getProductsOnStock(int page, int perPage, String order, Double minPrice, boolean showAll) {
        if (Objects.isNull(LogInService.token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad Request: No Cookies.");
        } else if (showAll) {
            return getAllProductsOnStock();
        } else {
            Response response = getWardrobe(page, perPage, order, baseSpecification);

            return ResponseEntity.ok(getItemsThatCostMore(minPrice, response));
        }
    }

    private static Response getWardrobe(int page, int perPage, String order, BaseSpecification baseSpecification) {
        return baseSpecification.getBaseReqSpec().basePath("/api/v2/wardrobe/91181425/items")
                .queryParam("page", page)
                .queryParam("per_page", perPage)
                .queryParam("order", order)
                .when()
                .get();
    }

    public ResponseEntity<?> getAllProductsOnStock() {
        return getProductsOnStock(1, 900, "relevance", 0.0, true);
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

}
