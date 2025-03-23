package pl.skup.vinted.models.endpointContraints;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter

public enum ProductSort {
    PRICE_DESC("price,desc"),
    PRICE_ASC("price,asc");

    public String value;

    ProductSort(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
