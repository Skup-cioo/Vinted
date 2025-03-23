package pl.skup.vinted.models.restAssuredSpec;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static pl.skup.vinted.models.restAssuredSpec.headers.baseHeaders.BASE_HEADERS;

public class BaseSpecification {
    public RequestSpecification getBaseReqSpec() {
        return RestAssured.given()
                .baseUri("https://www.vinted.pl")
                .headers(BASE_HEADERS)
                .contentType(ContentType.JSON);
    }
}
