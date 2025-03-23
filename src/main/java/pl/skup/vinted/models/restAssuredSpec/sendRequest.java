package pl.skup.vinted.models.restAssuredSpec;

import io.restassured.response.Response;

public class sendRequest {
    public static Response getWardrobe(int page, int perPage, String order, BaseSpecification baseSpecification) {
        return baseSpecification.getBaseReqSpec().basePath("/api/v2/wardrobe/91181425/items")
                .queryParam("page", page)
                .queryParam("per_page", perPage)
                .queryParam("order", order)
                .when()
                .get();
    }

    public static Response getAllSold(int page, int perPage, String status, String type, BaseSpecification baseSpecification) {
        return baseSpecification.getBaseReqSpec().basePath("/api/v2/my_orders")
                .queryParam("page", page)
                .queryParam("per_page", perPage)
                .queryParam("status", status)
                .queryParam("type", type)
                .when()
                .get();
    }

}
